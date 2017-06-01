package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.channels.SocketChannel;

public class CachedHttpResponse implements IHttpResponse {

  private SocketChannel client;
  private HttpResponseData responseData;

  public CachedHttpResponse(SocketChannel client, HttpResponseData responseData) {
    this.client = client;
    this.responseData = responseData;
  }

  public SocketChannel getClient() {
    return client;
  }

  @Override
  public HttpResponseData getResponseData() {
    return responseData;
  }
}
