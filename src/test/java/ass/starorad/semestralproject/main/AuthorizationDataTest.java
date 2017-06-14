package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.impl.AuthorizationData;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class AuthorizationDataTest {

  @Test
  public void testSameDataEquals() {
    AuthorizationData first = new AuthorizationData("user", "pass");
    AuthorizationData second = new AuthorizationData("user", "pass");

    assertEquals(first, second);
  }

  @Test
  public void testDifferentUserNotEquals() {
    AuthorizationData first = new AuthorizationData("user", "pass");
    AuthorizationData second = new AuthorizationData("different", "pass");

    assertNotEquals(first, second);
  }

  @Test
  public void testDifferentPasswordNotEquals() {
    AuthorizationData first = new AuthorizationData("user", "pass");
    AuthorizationData second = new AuthorizationData("user", "different");

    assertNotEquals(first, second);
  }
}
