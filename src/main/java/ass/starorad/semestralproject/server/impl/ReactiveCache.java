package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.ICache;
import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javaslang.control.Try;

public class ReactiveCache implements ICache {

  private ConcurrentHashMap<String, SoftReference<HttpResponseData>> cache = new ConcurrentHashMap<>();

  //TODO: Fix soft references, can return null
  //TODO: Add base directory resolution

  private Single<HttpResponseData> getResponseData(IHttpRequest request) {
    return Observable.concat(
        Observable.just(request)
            .map(IHttpRequest::getPath)
            .filter(cache::containsKey)
            .map(cache::get)
            .map(SoftReference::get)
            .filter(Objects::nonNull),
        Observable.just(request)
            .map(IHttpRequest::getPath)
            .map(path -> Try.of(() -> Files.readAllBytes(Paths.get(".").resolve(path))))
            .filter(Try::isSuccess)
            .map(Try::get)
            .map(bytes ->
                new HttpResponseData(
                    new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK),
                    bytes
                )
            )
            .doOnNext(responseData ->
                    cache.put(request.getPath(), new SoftReference<HttpResponseData>(responseData))
                //cache.put(request.getKey(), new SoftReference<CachedFileResponse>(null))
            )
    )
        .first(HttpResponseData.FileNotFoundResponse);
  }


  //TODO: Blocking get problem?
  @Override
  public ObservableSource<IHttpResponse> apply(Observable<IHttpRequest> observable) {
    return observable.map(
        iHttpRequest -> new CachedHttpResponse(iHttpRequest.getClient(),
            getResponseData(iHttpRequest).blockingGet()));
  }
}
