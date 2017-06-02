package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ParsedHttpRequest implements IHttpRequest {

  private static final String HashingAlgorithm = "SHA1";
  private static final String Salt = "very_secret_salt";

  private SocketChannel client;
  private HttpRequest httpRequest;
  private String path;

  public ParsedHttpRequest(SocketChannel client, HttpRequest httpRequest) {
    this.client = client;
    this.httpRequest = httpRequest;
    this.path = extractPathFromRequest(httpRequest);
  }

  public SocketChannel getClient() {
    return client;
  }

  @Override
  public HttpRequest getHttpRequest() {
    return httpRequest;
  }

  @Override
  public String getPath() {
    return path;
  }

  private String extractPathFromRequest(HttpRequest request) {
    String path = null;
    try {
      path = URLDecoder.decode(request.uri(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

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

    return path.isEmpty() ? "index.html" : path;
  }

  @Override
  public String toString() {
    return "ParsedHttpRequest{" +
        "client=" + client +
        ", httpRequest=" + httpRequest +
        '}';
  }

  public AuthorizationData getAuthorizationData() {
    String authorization = httpRequest.headers().get("Authorization");
    if (authorization != null && authorization.startsWith("Basic")) {
      String base64 = authorization.substring(6);
      try {
        String decoded = new String(Base64.getDecoder().decode(base64), "UTF-8");
        String[] parts = decoded.split(":");

        String hashedPassword = hashPassword(parts[1], Salt, HashingAlgorithm);
        return new AuthorizationData(parts[0], hashedPassword);
      } catch (UnsupportedEncodingException | IndexOutOfBoundsException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  //TODO: Move into helper class?
  public static String hashPassword(String password, String salt, String algorithm) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      Base64.Encoder enc = Base64.getEncoder();

      messageDigest.update(password.getBytes());
      messageDigest.update(salt.getBytes());
      byte[] hash = messageDigest.digest();

      return enc.encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("Not a valid encryption algorithm", e);
    }
  }

  public static void main(String[] args) {
    System.out.println(hashPassword("123456", Salt, HashingAlgorithm));
  }
}
