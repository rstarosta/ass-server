package ass.starorad.semestralproject.main;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.impl.FileManager;
import ass.starorad.semestralproject.server.impl.HttpResponseData;
import ass.starorad.semestralproject.server.impl.ParsedHttpRequest;
import ass.starorad.semestralproject.server.impl.ReactiveCache;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
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
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/home/index.html");
    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);

    fileManager.getResponseData(request);

    verify(reactiveCache).getResponseData(Paths.get("home/index.html").toAbsolutePath());
  }

  @Test
  public void testSimpleHeadRequest() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.HEAD, "/home/index.html");
    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);

    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/index.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.OK);
  }

  @Test
  public void testSimpleRequestToNonexistantFile() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/meh");

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    // should go to the cache as long as the path is in root directory
    verify(reactiveCache).getResponseData(Paths.get("meh").toAbsolutePath());
  }

  @Test
  public void testSimpleRequestToWrongDirectory() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/../secret");

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("../secret").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.NOT_FOUND);
  }

  @Test
  public void testRequestForHtaccess() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/home/secureFolder/.htaccess");

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/secureFolder/.htaccess").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.NOT_FOUND);
  }

  @Test
  public void testWrongRequestType() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/home/index.html");
    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);

    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/index.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.BAD_REQUEST);
  }

  @Test
  public void testSimpleRequestWithWalks() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/src/main/../../home/index.html");

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    verify(reactiveCache).getResponseData(Paths.get("src/main/../../home/index.html").normalize().toAbsolutePath());
  }

  @Test
  public void testSimpleRequestToProtectedDirectory() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/home/secureFolder/secure.html");

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.UNAUTHORIZED);
  }

  @Test
  public void testAuthenticatedRequestToProtectedDirectory() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/home/secureFolder/secure.html");
    String auth = "test:123456";
    try {
      httpRequest.headers().add("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
    }

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    fileManager.getResponseData(request);

    verify(reactiveCache)
        .getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
  }

  @Test
  public void testWrongAuthenticatedRequestToProtectedDirectory() {
    HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/home/secureFolder/secure.html");
    String auth = "test:asdf";
    try {
      httpRequest.headers().add("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      fail();
    }

    IHttpRequest request = new ParsedHttpRequest(null, httpRequest);
    HttpResponseData data = fileManager.getResponseData(request).blockingGet();

    verify(reactiveCache, never()).getResponseData(Paths.get("home/secureFolder/secure.html").toAbsolutePath());
    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.UNAUTHORIZED);
  }
}
