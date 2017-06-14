package ass.starorad.semestralproject.server.transformers;

import ass.starorad.semestralproject.server.data.IHttpResponse;
import ass.starorad.semestralproject.server.data.IRawResponse;
import io.reactivex.ObservableTransformer;

public interface IHttpResponseEncoder extends ObservableTransformer<IHttpResponse, IRawResponse> {

}
