package ass.starorad.semestralproject.server.data;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IRawResponse {

  SocketChannel getClient();

  ByteBuffer getResponseData();
}
