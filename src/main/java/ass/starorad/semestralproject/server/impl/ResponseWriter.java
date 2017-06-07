package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IResponse;
import ass.starorad.semestralproject.server.IResponseWriter;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//TODO: Implement writing using write only socket
public class ResponseWriter implements IResponseWriter {

  @Override
  public void accept(IResponse t) throws IOException {
    ByteBuf data = t.getResponseData();
    ByteBuffer byteBuffer = ByteBuffer.wrap(data.array());

    SocketChannel client = t.getClient();
    try {
      while(byteBuffer.hasRemaining()) {
          client.write(byteBuffer);
      }
      client.close();
    } catch(IOException exception) {
      exception.printStackTrace();
    }
  }
}
