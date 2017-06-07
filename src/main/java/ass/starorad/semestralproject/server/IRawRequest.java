package ass.starorad.semestralproject.server;

import io.netty.buffer.ByteBuf;

public interface IRawRequest extends IRequest {
  ByteBuf getRequestData();
}
