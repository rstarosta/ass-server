package ass.starorad.semestralproject.server.data.impl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ass.starorad.semestralproject.server.data.IRawResponse;

public class EncodedResponse implements IRawResponse {
	protected SocketChannel client;
	protected ByteBuffer responseData;
	
	public EncodedResponse(SocketChannel client, ByteBuffer responseData) {
		this.client = client;
		this.responseData = responseData;
	}
	
	public SocketChannel getClient() {
		return client;
	}
	
	public ByteBuffer getResponseData() {
		return responseData;
	}
}
