package ass.starorad.semestralproject.server.transformers;

import ass.starorad.semestralproject.server.data.IHttpRequest;
import ass.starorad.semestralproject.server.data.IHttpResponse;
import io.reactivex.ObservableTransformer;

public interface IFileManager extends ObservableTransformer<IHttpRequest, IHttpResponse> {

}
