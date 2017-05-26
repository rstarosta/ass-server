package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IRawRequest;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import java.io.ByteArrayInputStream;
import org.apache.http.HttpRequest;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;

public class HttpCoreRequestParser implements IHttpRequestParser {

  private HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
  private SessionInputBufferImpl buffer = new SessionInputBufferImpl(metrics, 2048);
  private DefaultHttpRequestParser parser = new DefaultHttpRequestParser(buffer);

  @Override
  public ObservableSource<IHttpRequest> apply(Observable<IRawRequest> observable) {
    return observable
        .map(request -> {
          buffer.bind(new ByteArrayInputStream(request.getRequestData().getBytes()));
          HttpRequest httpRequest = parser.parse();
          buffer.clear();

          //return new ParsedHttpRequest(request.getClient(), httpRequest);
          return null;
        });
  }
}
