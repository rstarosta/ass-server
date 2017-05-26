package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import java.nio.channels.SocketChannel;

public class CachedHttpResponse implements IHttpResponse {

  private SocketChannel client;
  private FullHttpResponse httpResponse;

  public CachedHttpResponse(SocketChannel client, FullHttpResponse httpResponse) {
    this.client = client;
    this.httpResponse = httpResponse;
  }

  public SocketChannel getClient() {
    return client;
  }

  public FullHttpResponse getHttpResponse() {
    return httpResponse;
  }
}
