package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import java.nio.channels.SocketChannel;

public class CachedHttpResponse implements IHttpResponse {

  private SocketChannel client;
  private HttpResponse httpResponse;
  private byte[] content;

  public CachedHttpResponse(SocketChannel client, HttpResponse httpResponse, byte[] content) {
    this.client = client;
    this.httpResponse = httpResponse;
    this.content = content;
  }

  public SocketChannel getClient() {
    return client;
  }

  public HttpResponse getHttpResponse() {
    return httpResponse;
  }

  @Override
  public byte[] getContent() {
    return content;
  }
}
