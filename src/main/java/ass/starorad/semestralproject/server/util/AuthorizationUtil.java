package ass.starorad.semestralproject.server.util;

import ass.starorad.semestralproject.server.data.impl.AuthorizationData;
import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationUtil {

  private static final String HashingAlgorithm = "SHA1";
  private static final String Salt = "very_secret_salt";

  private static final Logger logger = LoggerFactory.getLogger(AuthorizationUtil.class);

  /**
   * Returns a String of the hashed the password using a given hashing algorithm.
   *
   * @param password password to be hashed
   * @return hashed password
   */
  public static String hashPassword(String password) {
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance(HashingAlgorithm);
    } catch (NoSuchAlgorithmException e) {
      logger.error("Wrong hashing algorithm", e);
      return null;
    }

    try {
      messageDigest.update(password.getBytes("UTF-8"));
      messageDigest.update(Salt.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      logger.error("Unable to convert strings, wrong encoding?", e);
      return null;
    }
    byte[] hash = messageDigest.digest();

    Base64.Encoder enc = Base64.getEncoder();
    return enc.encodeToString(hash);
  }

  /**
   * Decodes the Authorization header from an HttpRequest and constructs AuthorizationData object
   * with the hashed password.
   *
   * @param httpRequest request with the Authorization header
   * @return authorization data
   */
  public static AuthorizationData getAuthorizationDataForRequest(HttpRequest httpRequest) {
    String authorization = httpRequest.headers().get("Authorization");
    if (authorization != null && authorization.startsWith("Basic")) {
      String base64 = authorization.substring(6);
      try {
        String decoded = new String(Base64.getDecoder().decode(base64), "UTF-8");
        String[] parts = decoded.split(":");

        String hashedPassword = AuthorizationUtil.hashPassword(parts[1]);
        return new AuthorizationData(parts[0], hashedPassword);
      } catch (UnsupportedEncodingException e) {
        logger.error("Unable to convert strings, wrong encoding?", e);
      }
    }

    return null;
  }

}
