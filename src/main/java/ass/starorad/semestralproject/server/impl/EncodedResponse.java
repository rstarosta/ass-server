package ass.starorad.semestralproject.server.impl;

import java.nio.channels.SocketChannel;

import ass.starorad.semestralproject.server.IResponse;

public class EncodedResponse implements IResponse {
	protected SocketChannel client;
	protected byte[] responseData;
	
	public EncodedResponse(SocketChannel client, byte[] responseData) {
		this.client = client;
		this.responseData = responseData;
	}
	
	public SocketChannel getClient() {
		return client;
	}
	
	public byte[] getResponseData() {
		return responseData;
	}
}