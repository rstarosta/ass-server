package ass.starorad.semestralproject.main;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.fail;

import ass.starorad.semestralproject.server.util.ByteArrayUtil;
import java.io.UnsupportedEncodingException;
import org.testng.annotations.Test;

public class ByteArrayUtilTest {

  @Test
  public void testEndsWith() {
    try {
      byte[] array = "string\r\n".getBytes("UTF-8");
      assertTrue(ByteArrayUtil.endsWith(array, "\r\n"));
    } catch (UnsupportedEncodingException e) {
      fail();
    }
  }

  @Test
  public void testDoesntEndWith() {
    try {
      byte[] array = "string\r\n".getBytes("UTF-8");
      assertFalse(ByteArrayUtil.endsWith(array, "meh\r\n"));
    } catch (UnsupportedEncodingException e) {
      fail();
    }
  }

  @Test
  public void testEndsWithNull() {
    try {
      byte[] array = "string\r\n".getBytes("UTF-8");
      assertFalse(ByteArrayUtil.endsWith(array, null));
      assertFalse(ByteArrayUtil.endsWith(null, "meh"));
    } catch (UnsupportedEncodingException e) {
      fail();
    }
  }

}
