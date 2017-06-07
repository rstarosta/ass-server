package ass.starorad.semestralproject.server.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseData {

  public static final HttpResponseData Ok = new HttpResponseData(
      new DefaultHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.OK
      ));

  public static final HttpResponseData FileNotFound = new HttpResponseData(
      new DefaultHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.NOT_FOUND
      ));

  public static final HttpResponseData Unauthorized = new HttpResponseData(
        new DefaultHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.UNAUTHORIZED
        ));

  static {
    Unauthorized.getHttpResponse().headers().add("WWW-Authenticate", "Basic");
  }
  
  public static final HttpResponseData BadRequest = new HttpResponseData(
      new DefaultHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.BAD_REQUEST
      ));

  private HttpResponse httpResponse;
  private ByteBuf content;

  public HttpResponseData(HttpResponse httpResponse, ByteBuf content) {
    this.httpResponse = httpResponse;
    this.content = content;
  }

  public HttpResponseData(HttpResponse httpResponse) {
    this.httpResponse = httpResponse;
    this.content = new EmptyByteBuf(UnpooledByteBufAllocator.DEFAULT);
  }

  public HttpResponse getHttpResponse() {
    return httpResponse;
  }

  public ByteBuf getContent() {
    return content;
  }
}
