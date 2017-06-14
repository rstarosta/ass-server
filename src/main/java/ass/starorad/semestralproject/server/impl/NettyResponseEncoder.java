package ass.starorad.semestralproject.server.impl;

import ass.starorad.semestralproject.server.IHttpResponse;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyResponseEncoder implements IHttpResponseEncoder {

  private static final Logger logger = LoggerFactory.getLogger(NettyResponseEncoder.class);

  @Override
  public ObservableSource<IResponse> apply(Observable<IHttpResponse> observable) {
    return observable.map(response -> {
      EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseEncoder());

      ByteBuf content = response.getResponseData().getContent();
      ch.writeOutbound(response.getResponseData().getHttpResponse());

      ByteBuf byteBuf = ch.readOutbound();
      ch.close();

      if(content != null) {
        byteBuf.writeBytes(content);
        content.resetReaderIndex();
      }

      ByteBuffer byteBuffer = byteBuf.nioBuffer();
      byteBuf.release();

      logger.info("Successfully encoded response {}", response);
      return new EncodedResponse(response.getClient(), byteBuffer);
    });
  }
}
