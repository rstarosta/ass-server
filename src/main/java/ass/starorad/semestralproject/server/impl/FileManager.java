package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IFileManager;
import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpResponse;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager implements IFileManager {

  private Path rootDirectory;
  private ReactiveCache reactiveCache;

  public FileManager(String rootDirectoryPath, ReactiveCache reactiveCache) {
    setRootDirectoryFromPath(rootDirectoryPath);
    this.reactiveCache = reactiveCache;
  }

  public Single<HttpResponseData> getResponseData(IHttpRequest request) {
    Path path = rootDirectory.resolve(request.getPath()).normalize().toAbsolutePath();

    if (checkPath(path)) {
      return reactiveCache.getResponseData(path);
    }
    return Single.just(HttpResponseData.UnauthorizedResponse);
  }

  private boolean checkPath(Path path) {

    if (!isInRootDirectory(path)) {
      return false;
    }

    //if (!htaccessExists(path)) {
    //  return true;
    //}

    //AuthorizationData provided = request.getAuthenticationData();
    //AuthorizationData required = null;
    //try {
    //  required = getHtaccessDataForPath(path);
    //} catch (IOException e) {
    //  e.printStackTrace();
    //  // unable to read htaccess, rather don't serve the file
    //  return false;
    //}

    //return required == null || (provided != null && provided.equals(required));
    return true;
  }

  public void setRootDirectoryFromPath(String path) {
    Path root = Paths.get(path).normalize().toAbsolutePath();
    if (root.toFile().exists() && root.toFile().isDirectory()) {
      rootDirectory = root;
    }
  }

  //private boolean htaccessExists(Path path) {
  //  Path htaccessPath = path.getParent().resolve(".htaccess");

  //  return htaccessPath.toFile().exists();
  //}

  //private AuthorizationData getHtaccessDataForPath(Path path) throws IOException {
  //  return Files.lines(path.getParent().resolve(".htaccess"))
  //      .filter(line -> !line.isEmpty())
  //      .findFirst()
  //      .map(line -> line.split(":"))
  //      .map(parts -> Try.of(() -> new AuthorizationData(parts[0], parts[1])))
  //      .filter(Try::isSuccess)
  //      .map(Try::get)
  //      .orElse(null);
  //}

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
