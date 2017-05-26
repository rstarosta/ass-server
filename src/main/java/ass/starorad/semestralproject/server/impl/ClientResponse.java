package ass.starorad.semestralproject.server.impl;

import java.nio.channels.SocketChannel;

import ass.starorad.semestralproject.server.IResponse;

public class ClientResponse implements IResponse {
	protected SocketChannel client;
	protected String responseData;
	
	public ClientResponse(SocketChannel client, String responseData) {
		this.client = client;
		this.responseData = responseData;
	}
	
	public SocketChannel getClient() {
		return client;
	}
	
	public String getResponseData() {
		return responseData;
	}
}
