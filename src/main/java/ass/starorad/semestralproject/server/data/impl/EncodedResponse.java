package ass.starorad.semestralproject.server.data.impl;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import ass.starorad.semestralproject.server.data.IRawResponse;

/**
 * Data object carrying the response encoded to raw bytes, send to the writer.
 */
public class EncodedResponse implements IRawResponse {

  private SocketChannel client;
  private ByteBuffer responseData;

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
