package ass.starorad.semestralproject.server.data.impl;

import ass.starorad.semestralproject.server.data.IHttpRequest;
import ass.starorad.semestralproject.server.util.PathUtil;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.channels.SocketChannel;

/**
 * Data class carrying the parsed HttpRequest and requested file path, sent to the file manager.
 */
public class ParsedHttpRequest implements IHttpRequest {

  private SocketChannel client;
  private HttpRequest httpRequest;
  private String path;

  public ParsedHttpRequest(SocketChannel client, HttpRequest httpRequest) {
    this.client = client;
    this.httpRequest = httpRequest;
    this.path = PathUtil.extractPathFromRequest(httpRequest);
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

}
