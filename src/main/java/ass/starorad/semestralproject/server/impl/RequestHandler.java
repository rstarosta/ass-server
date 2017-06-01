package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IFileManager;
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
  private IFileManager fileManager;

  public RequestHandler(IHttpRequestParser parser, IHttpResponseEncoder encoder, IFileManager fileManager) {
    this.parser = parser;
    this.encoder = encoder;
    this.fileManager = fileManager;
  }

  @Override
  public ObservableSource<IResponse> apply(Observable<IRawRequest> upstream) {
    return upstream
        .compose(parser)
        .compose(fileManager)
        .compose(encoder);
  }

}
