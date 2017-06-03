package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IFileManager;
import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpResponse;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javaslang.control.Try;

public class FileManager implements IFileManager {

  private Path rootDirectory;
  private ReactiveCache reactiveCache;

  public FileManager(String rootDirectoryPath, ReactiveCache reactiveCache) {
    setRootDirectoryFromPath(rootDirectoryPath);
    this.reactiveCache = reactiveCache;
  }

  public Single<HttpResponseData> getResponseData(IHttpRequest request) {
    Path path = rootDirectory.resolve(request.getPath()).normalize().toAbsolutePath();
    if (!checkPath(path)) {
      return Single.just(HttpResponseData.FileNotFound);
    }

    AuthorizationData authorizationData = request.getAuthorizationData();
    if(!checkAuthorization(path, authorizationData)) {
      return Single.just(HttpResponseData.Unauthorized);
    }

    HttpMethod method = request.getHttpRequest().method();
    if(method.equals(HttpMethod.GET)) {
      return reactiveCache.getResponseData(path);
    } else if(method.equals(HttpMethod.HEAD)) {
      return Single.just(HttpResponseData.Ok);
    }

    return Single.just(HttpResponseData.BadRequest);
  }

  private boolean checkAuthorization(Path path, AuthorizationData provided) {
    Path htaccessPath = getClosestHtaccessPath(path);
    if (htaccessPath == null) {
      return true;
    }

    AuthorizationData required = null;
    try {
      required = getAuthorizationDataFromHtaccess(htaccessPath);
    } catch (IOException e) {
      e.printStackTrace();
      // unable to read htaccess, rather don't serve the file
      return false;
    } catch(ParseException e) {
      e.printStackTrace();
      // htaccess is in wrong format
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
  }

  private Path getClosestHtaccessPath(Path path) {
    Path current = path;

    while(!current.equals(rootDirectory)) {
      current = current.getParent();
      Path htaccessPath = current.resolve(".htaccess");

      if(htaccessPath.toFile().exists()) {
        return htaccessPath;
      }
    }

    return null;
  }

  private AuthorizationData getAuthorizationDataFromHtaccess(Path path) throws IOException, ParseException {
    return Files.lines(path)
        .filter(line -> !line.isEmpty())
        .findFirst()
        .map(line -> line.split(":"))
        .map(parts -> Try.of(() -> new AuthorizationData(parts[0], parts[1])))
        .filter(Try::isSuccess)
        .map(Try::get)
        .orElseThrow(ParseException::new);
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
