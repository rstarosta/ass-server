package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.ByteBufferUtil;
import ass.starorad.semestralproject.server.IRawRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponseWriter;
import ass.starorad.semestralproject.server.IServer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server implements IServer {

  /**
   * Address that server will bind to
   */
  protected SocketAddress address;
  protected boolean exit = false;

  private ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
  private Map<SocketChannel, ByteArrayOutputStream> attachments = new HashMap<>();

  /*
   * Use RxJava Subject
   * You can use PublishSubject, which is instantiated by PublishSubject.create()
   */
  protected PublishSubject<IRawRequest> requests = PublishSubject.create();

  public Server(SocketAddress address) {
    this.address = address;
  }

  public void run(IRequestHandler handler, IResponseWriter writer, String delimiter)
      throws IOException {
    // open, bind and configure ServerSocketChannel
    ServerSocketChannel serverChannel = ServerSocketChannel.open();
    serverChannel.bind(address);
    serverChannel.configureBlocking(false);

    System.out.println("Server started on address:" + address);

    // open selector and register serverSocketChannel to it
    Selector sel = Selector.open();
    serverChannel.register(sel, serverChannel.validOps());

    // use compose operator on RxJava Observable requests to handle requests using provided handler
    // use response writer to write response to client
    Disposable handlerDisposable = requests.compose(handler).subscribe(writer);

    // start non-blocking loop
    while (!exit) {
      Observable<SelectionKey> validKeys = selectValidKeys(sel);

      acceptAndRegister(sel, validKeys);
      readAndBuildRequests(delimiter, validKeys);
    }

    serverChannel.close();
    handlerDisposable.dispose();
  }

  public void shutdown() {
    exit = true;
  }

  private Observable<SelectionKey> selectValidKeys(Selector sel) throws IOException {
    sel.select();
    Iterator<SelectionKey> iterator = sel.selectedKeys().iterator();

    return Observable.<SelectionKey>create((emittor) -> {
      while (iterator.hasNext()) {
        SelectionKey next = iterator.next();
        emittor.onNext(next);
        iterator.remove();    // this is important, as selector only adds but does not remove
      }
      emittor.onComplete();
    })
    .filter(SelectionKey::isValid)
    .cache();
  }

  // using valid keys observable:
  // 1) clientChannel <- accept on acceptable key
  // 2) clientChannel.configureBlocking(false);
  // 3) clientChannel.register(selector, SelectionKey.OP_READ);
  private void acceptAndRegister(Selector sel, Observable<SelectionKey> validKeys) {
   validKeys
        .filter(SelectionKey::isValid)
        .filter(SelectionKey::isAcceptable)
        .forEach(key -> {
          SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
          clientChannel.configureBlocking(false);
          clientChannel.register(sel, SelectionKey.OP_READ);
        }).dispose();
  }

  // 1) (SocketChannel)key.channel() on readable key
  // 2) read from channel (you can use ByteBufferUtil)
  // 3) append data to some kind of buffer (ByteBuffer, ByteArrayOutputStream, StringBuilder, ...)
  //		this might have tremendous impact on performance, but beware of premature optimization
  //		note that speed is not primary objective of this homework
  // 4) construct request object when whole message is stored in buffer (message is terminated by terminator string)
  // 5) emit request object into observable
  //	(hint: using PublishSubject is convenient)
  private void readAndBuildRequests(String delimiter, Observable<SelectionKey> validKeys) {
    validKeys
        .filter(SelectionKey::isValid)
        .filter(SelectionKey::isReadable)
        .forEach(key -> {
          SocketChannel socketChannel = (SocketChannel) key.channel();
          ByteArrayOutputStream byteArrayOutputStream = getOrCreateMessageBuilder(socketChannel);

          byte[] bytes =  ByteBufferUtil.readFromChannel(byteBuffer, socketChannel);
          byteArrayOutputStream.write(bytes);

          if(byteArrayEndsWith(bytes, delimiter)) {
            requests.onNext(new ClientRequest(socketChannel, byteArrayOutputStream.toByteArray()));
            byteArrayOutputStream.close();
            attachments.remove(socketChannel);
          }
        }).dispose();
    }

  private ByteArrayOutputStream getOrCreateMessageBuilder(SocketChannel socketChannel) {
    return attachments.computeIfAbsent(socketChannel, c -> new ByteArrayOutputStream());
  }

  private boolean byteArrayEndsWith(byte[] bytes, String string) {
    if(string == null || bytes.length < string.length()) {
      return false;
    }

    byte[] end;
    try {
      end = string.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return false;
    }
    for(int i = 0; i < end.length; i++) {
      if(bytes[bytes.length - 1 - i] != end[end.length - 1 - i]) {
        return false;
      }
    }

    return true;
  }
}
