package ass.starorad.semestralproject.server.transformers.impl;

import ass.starorad.semestralproject.server.data.IRawResponse;
import ass.starorad.semestralproject.server.transformers.IResponseWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ResponseWriter implements IResponseWriter {

  @Override
  public void accept(IRawResponse t) throws IOException {
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
