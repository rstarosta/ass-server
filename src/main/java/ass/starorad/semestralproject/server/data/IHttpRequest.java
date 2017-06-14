package ass.starorad.semestralproject.server.data;


import io.netty.handler.codec.http.HttpRequest;

public interface IHttpRequest extends IRequest {

  HttpRequest getHttpRequest();

  String getPath();
}
