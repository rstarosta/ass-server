package ass.starorad.semestralproject.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface IResponse {
	SocketChannel getClient();
	ByteBuffer getResponseData();
}
