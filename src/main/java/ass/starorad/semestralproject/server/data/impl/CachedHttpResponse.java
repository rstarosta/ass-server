package ass.starorad.semestralproject.server.data.impl;

import ass.starorad.semestralproject.server.data.IHttpResponse;
import java.nio.channels.SocketChannel;

/**
 * Data object carrying the constructed HttpResponseData, sent to the encoder.
 */
public class CachedHttpResponse implements IHttpResponse {

  private SocketChannel client;
  private HttpResponseData responseData;

  public CachedHttpResponse(SocketChannel client, HttpResponseData responseData) {
    this.client = client;
    this.responseData = responseData;
  }

  public SocketChannel getClient() {
    return client;
  }

  @Override
  public HttpResponseData getResponseData() {
    return responseData;
  }
}
