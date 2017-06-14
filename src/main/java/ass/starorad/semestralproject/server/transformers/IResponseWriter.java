package ass.starorad.semestralproject.server.transformers;

import ass.starorad.semestralproject.server.data.IRawResponse;
import io.reactivex.functions.Consumer;

public interface IResponseWriter extends Consumer<IRawResponse> {

}
