package ass.starorad.semestralproject.server.transformers.impl;

import ass.starorad.semestralproject.server.transformers.IFileManager;
import ass.starorad.semestralproject.server.transformers.IHttpRequestParser;
import ass.starorad.semestralproject.server.transformers.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.data.IRawRequest;
import ass.starorad.semestralproject.server.transformers.IRequestHandler;
import ass.starorad.semestralproject.server.data.IRawResponse;
import com.google.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class RequestHandler implements IRequestHandler {

  private IHttpRequestParser parser;
  private IHttpResponseEncoder encoder;
  private IFileManager fileManager;

  @Inject
  public RequestHandler(IHttpRequestParser parser, IHttpResponseEncoder encoder, IFileManager fileManager) {
    this.parser = parser;
    this.encoder = encoder;
    this.fileManager = fileManager;
  }

  @Override
  public ObservableSource<IRawResponse> apply(Observable<IRawRequest> upstream) {
    return upstream
        .compose(parser)
        .compose(fileManager)
        .compose(encoder);
  }

}
