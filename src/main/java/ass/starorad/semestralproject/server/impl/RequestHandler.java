package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.ICache;
import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IRawRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponse;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class RequestHandler implements IRequestHandler {

  private IHttpRequestParser parser;
  private IHttpResponseEncoder encoder;
  private ICache cache;

  public RequestHandler(IHttpRequestParser parser, IHttpResponseEncoder encoder, ICache cache) {
    this.parser = parser;
    this.encoder = encoder;
    this.cache = cache;
  }

  @Override
  public ObservableSource<IResponse> apply(Observable<IRawRequest> upstream) {
    return upstream
        .compose(parser)
        .compose(cache)
        .compose(encoder);
  }

}
