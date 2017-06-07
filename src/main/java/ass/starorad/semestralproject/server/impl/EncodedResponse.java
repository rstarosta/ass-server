package ass.starorad.semestralproject.server.impl;

import io.netty.buffer.ByteBuf;
import java.nio.channels.SocketChannel;

import ass.starorad.semestralproject.server.IResponse;

public class EncodedResponse implements IResponse {
	protected SocketChannel client;
	protected ByteBuf responseData;
	
	public EncodedResponse(SocketChannel client, ByteBuf responseData) {
		this.client = client;
		this.responseData = responseData;
	}
	
	public SocketChannel getClient() {
		return client;
	}
	
	public ByteBuf getResponseData() {
		return responseData;
	}
}
