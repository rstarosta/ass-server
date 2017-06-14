package ass.starorad.semestralproject.main;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;

import ass.starorad.semestralproject.server.impl.AuthorizationData;
import ass.starorad.semestralproject.server.impl.AuthorizationUtil;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import org.testng.annotations.Test;

public class AuthorizationUtilTest {

  @Test
  public void testHashMatchesForSamePasswords() {
    assertEquals(AuthorizationUtil.hashPassword("password"),
        AuthorizationUtil.hashPassword("password"));
  }

  @Test
  public void testHashDiffersForDifferentPasswords() {
    assertNotEquals(AuthorizationUtil.hashPassword("password"),
        AuthorizationUtil.hashPassword("different"));
  }

  @Test
  public void testGetAuthorizationDataForRequest() throws UnsupportedEncodingException {
    HttpHeaders headers = new DefaultHttpHeaders();
    headers.add("Authorization", "Basic " + Base64.encode("user:pass".getBytes("UTF-8")));

    HttpRequest request = mock(HttpRequest.class);
    when(request.headers()).thenReturn(headers);

    assertEquals(AuthorizationUtil.getAuthorizationDataForRequest(request),
        new AuthorizationData("user", AuthorizationUtil.hashPassword("pass")));
  }


}
