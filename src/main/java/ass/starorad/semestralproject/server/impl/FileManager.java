package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IFileManager;
import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpResponse;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileManager implements IFileManager {

  private Path rootDirectory;
  private ReactiveCache reactiveCache;

  private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

  @Inject
  public FileManager(@Named("Document root") String rootDirectoryPath, ReactiveCache reactiveCache) {
    setRootDirectoryFromPath(rootDirectoryPath);
    this.reactiveCache = reactiveCache;
  }

  public Single<HttpResponseData> getResponseData(IHttpRequest request) {
    Path path = rootDirectory.resolve(request.getPath()).normalize().toAbsolutePath();
    if (!checkPath(path)) {
      logger.info("File with path {} was not found in the root directory");
      return Single.just(HttpResponseData.FileNotFound);
    }

    AuthorizationData authorizationData = request.getAuthorizationData();
    if(!checkAuthorization(path, authorizationData)) {
      logger.info("Access to the file with path {} was unauthorized");
      return Single.just(HttpResponseData.Unauthorized);
    }

    HttpMethod method = request.getHttpRequest().method();
    if(method.equals(HttpMethod.GET)) {
      logger.info("GET request valid, accessing cache");
      return reactiveCache.getResponseData(path);
    } else if(method.equals(HttpMethod.HEAD)) {
      logger.info("HEAD request valid, returning OK");
      return Single.just(HttpResponseData.Ok);
    }

    logger.info("Request type not allowed, returning BAD_REQUEST");
    return Single.just(HttpResponseData.BadRequest);
  }

  private boolean checkAuthorization(Path path, AuthorizationData provided) {
    Path htaccessPath = getClosestHtaccessPath(path);
    if (htaccessPath == null) {
      logger.info("No .htaccess file found, allowing access");
      return true;
    }

    AuthorizationData required = null;
    try {
      required = getAuthorizationDataFromHtaccess(htaccessPath);
    } catch (IOException e) {
      // unable to read htaccess, rather don't serve the file
      logger.error("Unable to read htaccess file at path {}", htaccessPath, e);
      return false;
    } catch(HtaccessParseException e) {
      // htaccess is in wrong format
      logger.error("Unable to parse htaccess file at path {}", htaccessPath, e);
      return false;
    }

    return required == null || (provided != null && provided.equals(required));
  }

  private boolean checkPath(Path path) {
    return isInRootDirectory(path) && !path.endsWith(".htaccess");
  }

  public void setRootDirectoryFromPath(String path) {
    Path root = Paths.get(path).normalize().toAbsolutePath();
    if (root.toFile().exists() && root.toFile().isDirectory()) {
      rootDirectory = root;
    }

    logger.info("Set root directory to {}", path);
  }

  private Path getClosestHtaccessPath(Path path) {
    Path current = path;

    while(!current.equals(rootDirectory)) {
      current = current.getParent();
      Path htaccessPath = current.resolve(".htaccess");

      if(htaccessPath.toFile().exists()) {
        logger.info("Found .htaccess file at {}", htaccessPath);
        return htaccessPath;
      }
    }

    return null;
  }

  private AuthorizationData getAuthorizationDataFromHtaccess(Path path) throws IOException, HtaccessParseException {
    logger.info("Parsing htaccess file");
    return Files.lines(path)
        .filter(line -> !line.isEmpty())
        .findFirst()
        .map(line -> line.split(":"))
        .map(parts -> Try.of(() -> new AuthorizationData(parts[0], parts[1])))
        .filter(Try::isSuccess)
        .map(Try::get)
        .orElseThrow(HtaccessParseException::new);
  }

  private boolean isInRootDirectory(Path path) {
    return path.startsWith(rootDirectory);
  }

  @Override
  public ObservableSource<IHttpResponse> apply(Observable<IHttpRequest> observable) {
    return observable
        .map(request -> new CachedHttpResponse(request.getClient(),
            getResponseData(request).blockingGet()));
  }
}
