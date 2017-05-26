package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IRawRequest;
import java.nio.channels.SocketChannel;

public class ClientRequest implements IRawRequest {
	protected SocketChannel client;
	protected byte[] requestData;

	public ClientRequest(SocketChannel clientAddress, byte[] requestData) {
		this.client = clientAddress;
		this.requestData = requestData;
	}

	public SocketChannel getClient() {
		return client;
	}

	public byte[] getRequestData() {
		return requestData;
	}

	@Override
	public String toString() {
		return "ClientRequest [clientAddress=" + client + ", requestData=" + requestData + "]";
	}
}
