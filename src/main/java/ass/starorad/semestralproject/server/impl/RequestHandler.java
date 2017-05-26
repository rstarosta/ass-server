package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IRawRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponse;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class RequestHandler implements IRequestHandler {

  private IHttpRequestParser parser;
  private IHttpResponseEncoder encoder;

  public RequestHandler(IHttpRequestParser parser, IHttpResponseEncoder encoder) {
    this.parser = parser;
    this.encoder = encoder;
  }

  @Override
  public ObservableSource<IResponse> apply(Observable<IRawRequest> upstream) {
    return upstream
        .compose(parser)
        .map(httpRequest -> {
          FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
          response.content().writeBytes("hai bois".getBytes());

          return new CachedHttpResponse(httpRequest.getClient(), response);
        })
        .compose(encoder);
  }

}
