package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IRawRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponse;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class RequestHandler implements IRequestHandler {

  private IHttpRequestParser parser;

  public RequestHandler(IHttpRequestParser parser) {
    this.parser = parser;
  }

  @Override
  public ObservableSource<IResponse> apply(Observable<IRawRequest> upstream) {
    //return upstream.map(request -> {
    //  String requestData = request.getRequestData();
    //  String responseData =
    //      (requestData.length() % 2 == 0) ? requestData.toUpperCase() : requestData.toLowerCase();

    //  return new ClientResponse(request.getClient(), responseData);
    //});

    return upstream
        .compose(parser).map(httpRequest -> new ClientResponse(httpRequest.getClient(), httpRequest.getHttpRequest().toString()));
  }

}
