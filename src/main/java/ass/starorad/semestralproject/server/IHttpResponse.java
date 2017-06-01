package ass.starorad.semestralproject.server;

import ass.starorad.semestralproject.server.impl.HttpResponseData;
import io.netty.handler.codec.http.HttpResponse;
import java.nio.channels.SocketChannel;

public interface IHttpResponse {
  SocketChannel getClient();
  HttpResponseData getResponseData();
}
