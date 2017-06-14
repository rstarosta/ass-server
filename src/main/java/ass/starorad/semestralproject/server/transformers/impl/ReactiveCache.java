package ass.starorad.semestralproject.server.transformers.impl;

import ass.starorad.semestralproject.server.data.impl.HttpResponseData;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javaslang.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores response data that was already accessed during the server runtime.
 */
public class ReactiveCache {

  private ConcurrentHashMap<Path, SoftReference<HttpResponseData>> cache = new ConcurrentHashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(ReactiveCache.class);

  /**
   * Retrieves the appropriate response data for the requested path. First checks the contents of
   * the cache, otherwise it reads the file from disk and stores it.
   */
  public Single<HttpResponseData> getResponseData(Path path) {
    return Observable.concat(
        Observable.just(path)
            .filter(cache::containsKey)
            .map(cache::get)
            .map(softReference -> Optional.ofNullable(softReference.get()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .doOnNext(responseData -> logger.info("Found cached response data {}", responseData)),
        Observable.just(path)
            .map(p -> Try.of(() -> Files.readAllBytes(p)))
            .filter(Try::isSuccess)
            .map(Try::get)
            .map(bytes -> new HttpResponseData(
                new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK),
                Unpooled.wrappedBuffer(bytes)
            ))
            .doOnNext(responseData -> {
                  logger.info("Read file from disk, caching response data {}", responseData);
                  cache.put(path, new SoftReference<>(responseData));
                }
            )
    ).first(HttpResponseData.FileNotFound);
  }
}
