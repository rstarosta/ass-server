package ass.starorad.semestralproject.server;

import ass.starorad.semestralproject.server.transformers.IRequestHandler;
import ass.starorad.semestralproject.server.transformers.IResponseWriter;
import java.io.IOException;

public interface IServer {
	/**
	 * Runs server with given handler
	 * @param handler - ObservableTransformer that processes requests
	 * @param writer - Consumer that consumes request and writes it to client (close client connection after response is written!)
	 * @param requestTerminator - token that terminates client request
	 */
	void run(IRequestHandler handler, IResponseWriter writer, String requestTerminator) throws IOException;
}
