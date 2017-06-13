package ass.starorad.semestralproject.main;

import static org.junit.Assert.assertEquals;

import ass.starorad.semestralproject.server.impl.HttpResponseData;
import ass.starorad.semestralproject.server.impl.ReactiveCache;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ReactiveCacheTest {

  private ReactiveCache cache;

  @BeforeMethod
  public void setup() {
    cache = new ReactiveCache();
  }

  @Test
  public void testSingleRequest() {
    Path path = Paths.get("home/index.html").toAbsolutePath();
    HttpResponseData data = cache.getResponseData(path).blockingGet();

    assertEquals(data.getHttpResponse().status(), HttpResponseStatus.OK);
  }

  @Test
  public void testSameRequestCaching() {
    Path path = Paths.get("home/index.html").toAbsolutePath();

    HttpResponseData firstResponse = cache.getResponseData(path).blockingGet();
    HttpResponseData secondResponse = cache.getResponseData(path).blockingGet();

    // not guaranteed with SoftReferences, but expected with only two requests
    assertEquals(firstResponse, secondResponse);
  }

  @Test
  public void testFileNotFound() {
    Path path = Paths.get("meh").toAbsolutePath();
    HttpResponseData responseData = cache.getResponseData(path).blockingGet();

    assertEquals(responseData, HttpResponseData.FileNotFound);
  }
}
