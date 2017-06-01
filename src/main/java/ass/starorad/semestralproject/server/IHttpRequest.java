package ass.starorad.semestralproject.server;


import io.netty.handler.codec.http.HttpRequest;

public interface IHttpRequest extends IRequest {
  HttpRequest getHttpRequest();
  String getPath();
}
