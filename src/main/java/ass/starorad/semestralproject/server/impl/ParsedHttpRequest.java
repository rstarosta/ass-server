package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.channels.SocketChannel;

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

  //TODO: Save this into a property?
  @Override
  public String getPath() {
    String path = httpRequest.uri().substring(1);

    return path.isEmpty() ? "index.html" : path;
  }

  @Override
  public String toString() {
    return "ParsedHttpRequest{" +
        "client=" + client +
        ", httpRequest=" + httpRequest +
        '}';
  }
}
