package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IResponse;
import ass.starorad.semestralproject.server.IResponseWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketResponseWriter implements IResponseWriter, Runnable {

  private int counter = 0;

  // elements are inserted from other thread
  protected ConcurrentLinkedQueue<SocketChannel> queue = new ConcurrentLinkedQueue<>();
  protected Map<SocketChannel, ByteBuffer> bufferMap = new ConcurrentHashMap<>();

  protected final Selector selector;

  private boolean exit = false;

  public SocketResponseWriter() {
    try {
      // prepare selector
      selector = Selector.open();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    // Start single thread that writes responses in non-blocking way using selector
    new Thread(this).start();
  }

  @Override
  public void run() {
    // there could be better stopping condition
    // e.g.: IResponseWriter could inherit from subscriber, than this thread could be stopped on onComplete message
    System.out.println("Started writing thread .. ");

    while (!exit) {
      // register channels that were added to queue
      try {
        registerChannels();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // call blocking selector.select()
      try {
        selector.select();
      } catch (IOException e) {
        e.printStackTrace();
        continue;
      }

      Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
      while (iterator.hasNext()) {
        SelectionKey selKey = iterator.next();

        SocketChannel client = (SocketChannel) selKey.channel();

        // TODO check that key is readable and valid and stuff
        if (!selKey.isValid() || !selKey.isWritable()) {
          System.out.println("Well shit");
          selKey.cancel();
          iterator.remove();
          continue;
        }

        // get buffer from client
        ByteBuffer bufferToWrite = bufferMap.get(client);

        // write what you can; bufferToWrite remembers position, no need to remember what was written
        try {
          client.write(bufferToWrite);
        } catch (IOException e) {
          cleanup(client);
          e.printStackTrace();
        }

        // use bufferToWrite.hasRemaining to check, whether whole buffer was written
        if (!bufferToWrite.hasRemaining()) {
          System.out.println("Finished " + ++counter);
          cleanup(client);
        }

        // cleanup bufferMap of channels that were cancelled or closed by client
        bufferMap.keySet().stream().filter(socketChannel ->
            !socketChannel.isConnected() && !socketChannel.isConnectionPending())
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
      e.printStackTrace();
    }
    bufferMap.remove(client);
  }

  public void registerChannels() throws IOException {
    while (!queue.isEmpty()) {
      SocketChannel toBeRegistered = queue.poll();

      // register channel for write operation
      toBeRegistered.register(selector, SelectionKey.OP_WRITE);
    }
  }

  @Override
  public void accept(IResponse t) throws Exception {
    System.out.println("Registering response for writing ..");
    ByteBuffer dataToWrite = t.getResponseData();
    SocketChannel socketToWriteTo = t.getClient();
    bufferMap.put(socketToWriteTo, dataToWrite);
    queue.add(socketToWriteTo);

    // this wakes up blocking selector.select() so that loop can register sockets added to queue
    selector.wakeup();
  }
}
