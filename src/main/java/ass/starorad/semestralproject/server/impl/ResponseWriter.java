package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IResponse;
import ass.starorad.semestralproject.server.IResponseWriter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//TODO: Implement writing using write only socket
public class ResponseWriter implements IResponseWriter {

  @Override
  public void accept(IResponse t) throws Exception {
    byte[] data = t.getResponseData();
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);

    SocketChannel client = t.getClient();
    while(byteBuffer.hasRemaining()) {
      client.write(byteBuffer);
    }

    client.close();
  }

}
