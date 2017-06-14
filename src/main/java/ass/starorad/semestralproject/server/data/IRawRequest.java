package ass.starorad.semestralproject.server.data;

import io.netty.buffer.ByteBuf;

public interface IRawRequest extends IRequest {
  ByteBuf getRequestData();
}
