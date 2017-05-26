package ass.starorad.semestralproject.server;

import java.nio.channels.SocketChannel;

public interface IResponse {
	SocketChannel getClient();
	byte[] getResponseData();
}
