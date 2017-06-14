package ass.starorad.semestralproject.main;

import static org.testng.Assert.assertEquals;

import ass.starorad.semestralproject.server.util.PathUtil;
import org.testng.annotations.Test;

public class PathUtilTest {

  @Test
  public void testExtractPathSimple() {
    assertEquals(PathUtil.extractPath("/home/image.jpg"), "home/image.jpg");
  }

  @Test
  public void testExtractPathWithParameters() {
    assertEquals(PathUtil.extractPath("/home/image.jpg?param=123&another=asdf"), "home/image.jpg");
  }

  @Test
  public void testExtractPathWithAnchor() {
    assertEquals(PathUtil.extractPath("/home/image.jpg#meh"), "home/image.jpg");
  }

  @Test
  public void testExtractPathWithParametersAndAnchor() {
    assertEquals(PathUtil.extractPath("/home/image.jpg?param=123&another=asdf#meh"),
        "home/image.jpg");
  }

  @Test
  public void testEmptyPathGoesToIndex() {
    assertEquals(PathUtil.extractPath("/"), "index.html");
  }

}
