package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import java.nio.channels.SocketChannel;
import org.apache.http.HttpRequest;

public class ParsedHttpRequest implements IHttpRequest {

  private SocketChannel client;
  private HttpRequest httpRequest;

  public ParsedHttpRequest(SocketChannel client, HttpRequest httpRequest) {
    this.client = client;
    this.httpRequest = httpRequest;
  }

  public SocketChannel getClient() {
    return client;
  }

  @Override
  public HttpRequest getHttpRequest() {
    return httpRequest;
  }

  @Override
  public String toString() {
    return "ParsedHttpRequest{" +
        "client=" + client +
        ", httpRequest=" + httpRequest +
        '}';
  }
}
