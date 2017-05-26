package ass.starorad.semestralproject.server;

import io.reactivex.ObservableTransformer;

public interface IHttpRequestParser extends ObservableTransformer<IRawRequest, IHttpRequest> {
}
