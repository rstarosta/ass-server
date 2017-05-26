package ass.starorad.semestralproject.server;

import org.apache.http.HttpRequest;

public interface IHttpRequest extends IRequest {
  HttpRequest getHttpRequest();
}
