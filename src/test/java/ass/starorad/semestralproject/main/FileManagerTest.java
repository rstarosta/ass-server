package ass.starorad.semestralproject.main;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IRequest;
import ass.starorad.semestralproject.server.impl.FileManager;
import ass.starorad.semestralproject.server.impl.HttpResponseData;
import ass.starorad.semestralproject.server.impl.ParsedHttpRequest;
import ass.starorad.semestralproject.server.impl.ReactiveCache;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FileManagerTest {

  private ReactiveCache reactiveCache;
  private FileManager fileManager;

  private HttpHeaders emptyHeaders;
  private HttpHeaders wrongAuthHeaders;
  private HttpHeaders correctAuthHeaders;


  @BeforeMethod
  public void setup() {
    reactiveCache = mock(ReactiveCache.class);
    fileManager = new FileManager(".", reactiveCache);

    emptyHeaders = new DefaultHttpHeaders();

    wrongAuthHeaders = new DefaultHttpHeaders();
    try {
      wrongAuthHeaders.add("Authorization", "Basic " + Base64.getEncoder().encodeToString("test:asdf".getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    correctAuthHeaders = new DefaultHttpHeaders();
    try {
      correctAuthHeaders.add("Authorization", "Basic " + Base64.getEncoder().encodeToString("test:123456".getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testSimpleRequest() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/home/index.html");
    when(httpRequest.headers()).thenReturn(emptyHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);

    fileManager.getResponseData(request);

    verify(reactiveCache).getResponseData(Paths.get("home/index.html").toAbsolutePath());
  }

  @Test
  public void testSimpleRequestToNonexistantFile() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/meh");
    when(httpRequest.headers()).thenReturn(emptyHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    // should go to the cache as long as the path is in root directory
    verify(reactiveCache).getResponseData(Paths.get("meh").toAbsolutePath());
  }

  @Test
  public void testSimpleRequestToWrongDirectory() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/../secret");
    when(httpRequest.headers()).thenReturn(emptyHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("../secret").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.NOT_FOUND);
  }

  @Test
  public void testSimpleRequestWithWalks() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/src/main/../../home/index.html");
    when(httpRequest.headers()).thenReturn(emptyHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    verify(reactiveCache).getResponseData(Paths.get("src/main/../../home/index.html").normalize().toAbsolutePath());
  }

  @Test
  public void testSimpleRequestToProtectedDirectory() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/home/secureFolder/secure.html");
    when(httpRequest.headers()).thenReturn(emptyHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.UNAUTHORIZED);
  }

  @Test
  public void testAuthenticatedRequestToProtectedDirectory() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/home/secureFolder/secure.html");
    when(httpRequest.headers()).thenReturn(correctAuthHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    verify(reactiveCache)
        .getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
  }

  @Test
  public void testWrongAuthenticatedRequestToProtectedDirectory() {
    HttpRequest httpRequest = mock(HttpRequest.class);
    when(httpRequest.uri()).thenReturn("/home/secureFolder/secure.html");
    when(httpRequest.headers()).thenReturn(wrongAuthHeaders);
    when(httpRequest.method()).thenReturn(HttpMethod.GET);

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.UNAUTHORIZED);
  }
}
