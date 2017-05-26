package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpResponse;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class NettyResponseEncoder implements IHttpResponseEncoder {

  @Override
  public ObservableSource<IResponse> apply(Observable<IHttpResponse> observable) {
    return observable.map(response -> {
      EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseEncoder());
      ch.writeOutbound(response.getHttpResponse());

      ByteBuf byteBuf = ch.readOutbound();
      byte[] bytes = new byte[byteBuf.readableBytes()];
      byteBuf.readBytes(bytes);

      return new EncodedResponse(response.getClient(), bytes);
    });
  }
}
