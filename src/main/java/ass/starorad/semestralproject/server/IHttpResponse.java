package ass.starorad.semestralproject.server;

import io.netty.handler.codec.http.FullHttpResponse;
import java.nio.channels.SocketChannel;

public interface IHttpResponse {
  SocketChannel getClient();
  FullHttpResponse getHttpResponse();
}
