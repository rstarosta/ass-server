package ass.starorad.semestralproject.server.data;

import ass.starorad.semestralproject.server.data.impl.HttpResponseData;
import java.nio.channels.SocketChannel;

public interface IHttpResponse {

  SocketChannel getClient();

  HttpResponseData getResponseData();
}
