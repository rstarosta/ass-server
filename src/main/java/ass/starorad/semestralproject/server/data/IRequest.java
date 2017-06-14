package ass.starorad.semestralproject.server.data;

import java.nio.channels.SocketChannel;

public interface IRequest {
	SocketChannel getClient();
}
