package ass.starorad.semestralproject.server.data.impl;

import ass.starorad.semestralproject.server.data.IRawRequest;
import io.netty.buffer.ByteBuf;
import java.nio.channels.SocketChannel;

public class ClientRequest implements IRawRequest {
	protected SocketChannel client;
	protected ByteBuf requestData;

	public ClientRequest(SocketChannel clientAddress, ByteBuf requestData) {
		this.client = clientAddress;
		this.requestData = requestData;
	}

	public SocketChannel getClient() {
		return client;
	}

	public ByteBuf getRequestData() {
		return requestData;
	}
}
