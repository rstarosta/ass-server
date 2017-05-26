package ass.starorad.semestralproject.server;

import java.nio.channels.SocketChannel;

public interface IRequest {
	SocketChannel getClient();
}
