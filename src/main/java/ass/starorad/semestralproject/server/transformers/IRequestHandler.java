package ass.starorad.semestralproject.server.transformers;

import ass.starorad.semestralproject.server.data.IRawRequest;
import ass.starorad.semestralproject.server.data.IRawResponse;
import io.reactivex.ObservableTransformer;

public interface IRequestHandler extends ObservableTransformer<IRawRequest, IRawResponse> {
}
