package ass.starorad.semestralproject.server;

import io.netty.handler.codec.http.HttpResponse;
import java.nio.channels.SocketChannel;

public interface IHttpResponse {
  SocketChannel getClient();
  HttpResponse getHttpResponse();
  byte[] getContent();
}
