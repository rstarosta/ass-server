package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.util.Base64;

public class ParsedHttpRequest implements IHttpRequest {

  private SocketChannel client;
  private HttpRequest httpRequest;
  private String path;

  public ParsedHttpRequest(SocketChannel client, HttpRequest httpRequest) {
    this.client = client;
    this.httpRequest = httpRequest;
    this.path = extractPathFromRequest(httpRequest);
  }

  public SocketChannel getClient() {
    return client;
  }

  @Override
  public HttpRequest getHttpRequest() {
    return httpRequest;
  }

  @Override
  public String getPath() {
    return path;
  }

  private String extractPathFromRequest(HttpRequest request) {
    String path = request.uri();

    int paramIndex = path.indexOf('?');
    int anchorIndex = path.indexOf('#');

    if (paramIndex != -1) { // params present, must be first
      path = path.substring(1, paramIndex);
    } else {
      if (anchorIndex != -1) { // only anchor
        path = path.substring(1, anchorIndex);
      } else { // nothing
        path = path.substring(1);
      }
    }

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
