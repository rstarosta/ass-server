package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IResponse;
import ass.starorad.semestralproject.server.IResponseWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketResponseWriter implements IResponseWriter, Runnable {

  // elements are inserted from other thread
  protected ConcurrentLinkedQueue<SocketChannel> queue = new ConcurrentLinkedQueue<>();
  protected Map<SocketChannel, ByteBuffer> bufferMap = new ConcurrentHashMap<>();

  protected final Selector selector;
  private boolean exit = false;

  private static final Logger logger = LoggerFactory.getLogger(SocketResponseWriter.class);

  public SocketResponseWriter() {
    try {
      // prepare selector
      selector = Selector.open();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    logger.info("Selector opened, starting thread");

    // Start single thread that writes responses in non-blocking way using selector
    new Thread(this).start();
  }

  @Override
  public void run() {
    logger.info("Started writing thread");

    while (!exit) {
      // register channels that were added to queue
      registerChannels();

      // call blocking selector.select()
      try {
        selector.select();
      } catch (IOException e) {
        logger.error("Select failed, trying again", e);
        continue;
      }

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey selKey = iterator.next();

        SocketChannel client = (SocketChannel) selKey.channel();

        if (!selKey.isValid() || !selKey.isWritable()) {
          logger.error("Key {} is not writable, cancelling", selKey);
          selKey.cancel();
          cleanup(client);
          iterator.remove();
          continue;
        }

        // get buffer from client
        ByteBuffer bufferToWrite = bufferMap.get(client);

        // write what you can; bufferToWrite remembers position, no need to remember what was written
        try {
          client.write(bufferToWrite);
        } catch (IOException e) {
          logger.error("Unable to write to socket {}, cleaning up", client, e);
          selKey.cancel();
          cleanup(client);
        }

        // use bufferToWrite.hasRemaining to check, whether whole buffer was written
        if (!bufferToWrite.hasRemaining()) {
          logger.info("Completed writing bytes from buffer {}, cleaning up", bufferToWrite);
          selKey.cancel();
          cleanup(client);
        }

        // cleanup bufferMap of channels that were cancelled or closed by client
        bufferMap.keySet().stream().filter(socketChannel -> !socketChannel.isOpen())
            .forEach(this::cleanup);

        iterator.remove();
      }
    }
  }

  public void shutdown() {
    exit = true;
  }

  private void cleanup(SocketChannel client) {
    try {
      client.close();
    } catch (IOException e) {
      logger.error("Unable to close socket {}", client, e);
    }
    bufferMap.remove(client);
  }

  private void registerChannels() {
    while (!queue.isEmpty()) {
      SocketChannel toBeRegistered = queue.poll();

      // register channel for write operation
      try {
        toBeRegistered.register(selector, SelectionKey.OP_WRITE);
      } catch (ClosedChannelException e) {
        logger.error("Unable to register channed {}", toBeRegistered, e);
      }
    }
  }

  @Override
  public void accept(IResponse t) throws Exception {
    logger.info("Registering response {} for writing", t);
    ByteBuffer dataToWrite = t.getResponseData();
    SocketChannel socketToWriteTo = t.getClient();
    bufferMap.put(socketToWriteTo, dataToWrite);
    queue.add(socketToWriteTo);

    // this wakes up blocking selector.select() so that loop can register sockets added to queue
    selector.wakeup();
  }
}
