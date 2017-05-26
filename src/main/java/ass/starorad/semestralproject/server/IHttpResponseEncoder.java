package ass.starorad.semestralproject.server;

import io.reactivex.ObservableTransformer;

public interface IHttpResponseEncoder extends ObservableTransformer<IHttpResponse, IResponse> {

}
