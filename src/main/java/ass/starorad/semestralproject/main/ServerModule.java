package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.transformers.IFileManager;
import ass.starorad.semestralproject.server.transformers.IHttpRequestParser;
import ass.starorad.semestralproject.server.transformers.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.transformers.IRequestHandler;
import ass.starorad.semestralproject.server.transformers.IResponseWriter;
import ass.starorad.semestralproject.server.IServer;
import ass.starorad.semestralproject.server.transformers.impl.FileManager;
import ass.starorad.semestralproject.server.transformers.impl.NettyRequestParser;
import ass.starorad.semestralproject.server.transformers.impl.NettyResponseEncoder;
import ass.starorad.semestralproject.server.transformers.impl.RequestHandler;
import ass.starorad.semestralproject.server.Server;
import ass.starorad.semestralproject.server.transformers.impl.SocketResponseWriter;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.net.SocketAddress;

public class ServerModule extends AbstractModule {

  private final SocketAddress socketAddress;
  private final String documentRootPath;

  public ServerModule(SocketAddress socketAddress, String documentRootPath) {
    this.socketAddress = socketAddress;
    this.documentRootPath = documentRootPath;
  }


  @Override
  protected void configure() {
    bind(SocketAddress.class)
        .annotatedWith(Names.named("Socket address"))
        .toInstance(socketAddress);

    bind(String.class)
        .annotatedWith(Names.named("Document root"))
        .toInstance(documentRootPath);

    bind(IHttpRequestParser.class).to(NettyRequestParser.class);
    bind(IHttpResponseEncoder.class).to(NettyResponseEncoder.class);
    bind(IRequestHandler.class).to(RequestHandler.class);
    bind(IResponseWriter.class).to(SocketResponseWriter.class);
    bind(IFileManager.class).to(FileManager.class);
    bind(IServer.class).to(Server.class);
  }
}
