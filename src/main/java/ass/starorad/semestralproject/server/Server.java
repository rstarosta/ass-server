package ass.starorad.semestralproject.server;

import ass.starorad.semestralproject.server.data.impl.ClientRequest;
import ass.starorad.semestralproject.server.util.ByteArrayUtil;
import ass.starorad.semestralproject.server.data.IRawRequest;
import ass.starorad.semestralproject.server.transformers.IRequestHandler;
import ass.starorad.semestralproject.server.transformers.IResponseWriter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements IServer {

  private static int BufferSize = 8096;

  /**
   * Address that server will bind to
   */
  private SocketAddress address;
  private boolean exit = false;

  private ByteBuffer byteBuffer = ByteBuffer.allocate(BufferSize);
  private Map<SocketChannel, ByteBuf> buffers = new ConcurrentHashMap<>();
  private ExecutorService executorService = Executors.newFixedThreadPool(30);
  private PublishSubject<IRawRequest> requests = PublishSubject.create();

  private static final Logger logger = LoggerFactory.getLogger(Server.class);


  @Inject
  public Server(@Named("Socket address") SocketAddress address) {
    this.address = address;
  }

  /**
   * Runs the server.
   *
   * @param handler - ObservableTransformer that processes requests
   * @param writer - Consumer that consumes request and writes it to client
   * @param delimiter - terminates requests
   */
  public void run(IRequestHandler handler, IResponseWriter writer, String delimiter) {
    // open, bind and configure ServerSocketChannel
    ServerSocketChannel serverChannel = null;
    try {
      serverChannel = ServerSocketChannel.open();
    } catch (IOException e) {
      logger.error("Unable to open server socket channel");
      return;
    }

    try {
      serverChannel.bind(address);
      serverChannel.configureBlocking(false);
    } catch (IOException e) {
      logger.error("Unable to bind address", e);
      return;
    }

    logger.info("Server started at address {}", address);

    // open selector and register serverSocketChannel to it
    Selector sel = null;
    try {
      sel = Selector.open();
    } catch (IOException e) {
      logger.error("Unable to open selector", e);
      return;
    }

    try {
      serverChannel.register(sel, serverChannel.validOps());
    } catch (ClosedChannelException e) {
      logger.error("Unable to register server socket channel", e);
      return;
    }

    // handle requests using the RequestHandler ran on a thread pool
    Disposable handlerDisposable = requests
        .subscribeOn(Schedulers.from(executorService))
        .compose(handler)
        .subscribe(writer);

    // start non-blocking loop
    while (!exit) {
      Observable<SelectionKey> validKeys = selectValidKeys(sel);

      acceptAndRegister(sel, validKeys);
      readAndBuildRequests(delimiter, validKeys);
    }

    try {
      serverChannel.close();
    } catch (IOException e) {
      logger.error("Unable to close server socket channel");
    }

    handlerDisposable.dispose();
  }

  /**
   * Shuts down the server
   */
  public void shutdown() {
    exit = true;
  }

  private Observable<SelectionKey> selectValidKeys(Selector sel) {
    try {
      sel.select();
    } catch (IOException e) {
      logger.error("Unable to select on selector {}", sel, e);
      return Observable.empty();
    }

    Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();

    return Observable.<SelectionKey>create((emittor) -> {
      while (iterator.hasNext()) {
        SelectionKey next = iterator.next();
        emittor.onNext(next);
        iterator.remove();    // this is important, as selector only adds but does not remove
      }
      emittor.onComplete();
    }).filter(SelectionKey::isValid)
        .cache();
  }

  private void acceptAndRegister(Selector sel, Observable<SelectionKey> validKeys) {
    validKeys
        .filter(SelectionKey::isValid)
        .filter(SelectionKey::isAcceptable)
        .forEach(key -> {
          SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
          clientChannel.configureBlocking(false);
          clientChannel.register(sel, SelectionKey.OP_READ);
          logger.info("Accepted and registered for reading {}", clientChannel);
        }).dispose();
  }

  /**
   * Reads data from the socket into ByteBuf and constructs requests upon reaching the given
   * delimiter.
   *
   * @param delimiter request terminator
   * @param validKeys Observable with valid keys
   */
  private void readAndBuildRequests(String delimiter, Observable<SelectionKey> validKeys) {
    validKeys
        .filter(SelectionKey::isValid)
        .filter(SelectionKey::isReadable)
        .forEach(key -> {
          SocketChannel socketChannel = (SocketChannel) key.channel();
          ByteBuf byteBuf = getOrCreateMessageBuilder(socketChannel);

          byte[] bytes;
          try {
            bytes = ByteArrayUtil.readFromChannel(byteBuffer, socketChannel);
            byteBuf.writeBytes(bytes);
            logger.info("Successfully read from channel");
          } catch (IOException e) {
            logger.error("Unable to read from channel {}, closing", socketChannel, e);
            socketChannel.close();
            buffers.remove(socketChannel);
            byteBuf.release();
            key.cancel();
            return;
          }

          if (ByteArrayUtil.endsWith(bytes, delimiter)) {
            logger.info("Reached delimiter, creating request object");
            buffers.remove(socketChannel);
            key.cancel();
            requests.onNext(new ClientRequest(socketChannel, byteBuf));
          }
        }).dispose();
  }

  private ByteBuf getOrCreateMessageBuilder(SocketChannel socketChannel) {
    return buffers.computeIfAbsent(socketChannel, c -> Unpooled.buffer(BufferSize));
  }

}
