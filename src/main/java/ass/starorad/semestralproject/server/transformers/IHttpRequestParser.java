package ass.starorad.semestralproject.server.transformers;

import ass.starorad.semestralproject.server.data.IHttpRequest;
import ass.starorad.semestralproject.server.data.IRawRequest;
import io.reactivex.ObservableTransformer;

public interface IHttpRequestParser extends ObservableTransformer<IRawRequest, IHttpRequest> {
}
