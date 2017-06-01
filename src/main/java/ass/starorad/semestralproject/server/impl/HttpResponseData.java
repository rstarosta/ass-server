package ass.starorad.semestralproject.server.impl;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseData {

  public static HttpResponseData FileNotFoundResponse = new HttpResponseData(
      new DefaultHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.NOT_FOUND
      ));

  public static HttpResponseData UnauthorizedResponse = new HttpResponseData(
      new DefaultHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.UNAUTHORIZED
      ));

  private HttpResponse httpResponse;
  private byte[] content;

  public HttpResponseData(HttpResponse httpResponse, byte[] content) {
    this.httpResponse = httpResponse;
    this.content = content;
  }

  public HttpResponseData(HttpResponse httpResponse) {
    this.httpResponse = httpResponse;
    this.content = null;
  }

  public HttpResponse getHttpResponse() {
    return httpResponse;
  }

  public byte[] getContent() {
    return content;
  }
}
