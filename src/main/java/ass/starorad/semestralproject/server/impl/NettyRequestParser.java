package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IRawRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class NettyRequestParser implements IHttpRequestParser {

  @Override
  public ObservableSource<IHttpRequest> apply(Observable<IRawRequest> observable) {
    return observable.map(request -> {
      EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder());

      ch.writeInbound(Unpooled.wrappedBuffer(request.getRequestData()));
      HttpRequest parsedRequest = ch.readInbound();
      ch.close();

      return new ParsedHttpRequest(request.getClient(), parsedRequest);
    });

  }
}
