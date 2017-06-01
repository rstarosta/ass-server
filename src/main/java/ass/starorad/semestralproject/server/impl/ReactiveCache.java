package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javaslang.control.Try;

public class ReactiveCache {

  private ConcurrentHashMap<Path, SoftReference<HttpResponseData>> cache = new ConcurrentHashMap<>();

  //TODO: Fix soft references, can return null
  public Single<HttpResponseData> getResponseData(Path path) {
    return Observable.concat(
        Observable.just(path)
            .filter(cache::containsKey)
            .map(cache::get)
            .map(SoftReference::get)
            .filter(Objects::nonNull),
        Observable.just(path)
            .map(p -> Try.of(() -> Files.readAllBytes(p)))
            .filter(Try::isSuccess)
            .map(Try::get)
            .map(bytes ->
                new HttpResponseData(
                    new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK),
                    bytes
                )
            )
            .doOnNext(responseData ->
                    cache.put(path, new SoftReference<HttpResponseData>(responseData))
                //cache.put(request.getKey(), new SoftReference<CachedFileResponse>(null))
            )
    )
        .first(HttpResponseData.FileNotFoundResponse);
  }
}
