package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpRequest;
import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IRawRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class NettyRequestParser implements IHttpRequestParser {

  private static int MaxInitialLineLength = 8192;
  private static int MaxHeaderSize = 8192;
  private static int MaxChunkSize = 8192;

  @Override
  public ObservableSource<IHttpRequest> apply(Observable<IRawRequest> observable) {
    return observable.map(request -> {
      EmbeddedChannel ch = new EmbeddedChannel(new HttpRequestDecoder(MaxInitialLineLength, MaxHeaderSize, MaxChunkSize));

      ByteBuf byteBuf = request.getRequestData();
      ch.writeInbound(byteBuf);
      HttpRequest parsedRequest = ch.readInbound();

      ch.close();

      return new ParsedHttpRequest(request.getClient(), parsedRequest);
    });

  }
}
