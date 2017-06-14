package ass.starorad.semestralproject.server.util;

import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtil {

  private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

  public static String decodeUri(String uri) {
    try {
      return URLDecoder.decode(uri, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.error("Unable to decode URI {}", uri, e);
      return null;
    }
  }

  public static String extractPathFromRequest(HttpRequest request) {
    String decoded = decodeUri(request.uri());
    if(decoded == null) {
      logger.error("Unable to extract path from {}", request);
      return null;
    }

    return extractPath(decoded);
  }

  public static String extractPath(String path) {
    int paramIndex = path.indexOf('?');
    int anchorIndex = path.indexOf('#');

    if (paramIndex != -1) { // params present, must be first
      path = path.substring(1, paramIndex);
    } else {
      if (anchorIndex != -1) { // only anchor
        path = path.substring(1, anchorIndex);
      } else { // nothing
        path = path.substring(1);
      }
    }

    if (path.isEmpty()) {
      logger.info("Path is empty, trying index.html");
      return "index.html";
    }
    return path;
  }
}
