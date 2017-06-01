package ass.starorad.semestralproject.server;


import ass.starorad.semestralproject.server.impl.AuthorizationData;
import io.netty.handler.codec.http.HttpRequest;

public interface IHttpRequest extends IRequest {
  HttpRequest getHttpRequest();
  String getPath();
  AuthorizationData getAuthorizationData();
}
