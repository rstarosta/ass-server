package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IResponse;
import ass.starorad.semestralproject.server.IResponseWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ResponseWriter implements IResponseWriter {

  @Override
  public void accept(IResponse t) throws IOException {
    ByteBuffer data = t.getResponseData();

    SocketChannel client = t.getClient();
    try {
      while(data.hasRemaining()) {
          client.write(data);
      }
      client.close();
    } catch(IOException exception) {
      exception.printStackTrace();
    }
  }
}
