package ass.starorad.semestralproject.server;

import io.reactivex.ObservableTransformer;

public interface IRequestHandler extends ObservableTransformer<IRawRequest, IResponse> {
}
