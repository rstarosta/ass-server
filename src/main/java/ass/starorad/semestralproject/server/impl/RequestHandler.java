package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IRawRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponse;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import java.nio.file.Files;
import java.nio.file.Paths;

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
          HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
          byte[] content = Files.readAllBytes(Paths.get("dude.jpg"));

          return new CachedHttpResponse(httpRequest.getClient(), response, content);
        })
        .compose(encoder);
  }

}
