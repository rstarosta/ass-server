package ass.starorad.semestralproject.server;

import io.netty.buffer.ByteBuf;
import java.nio.channels.SocketChannel;

public interface IResponse {
	SocketChannel getClient();
	ByteBuf getResponseData();
}
