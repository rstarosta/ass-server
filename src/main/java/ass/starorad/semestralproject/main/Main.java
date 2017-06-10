package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.IFileManager;
import ass.starorad.semestralproject.server.IHttpRequestParser;
import ass.starorad.semestralproject.server.IHttpResponseEncoder;
import ass.starorad.semestralproject.server.IRequest;
import ass.starorad.semestralproject.server.IRequestHandler;
import ass.starorad.semestralproject.server.IResponseWriter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.InetSocketAddress;

import ass.starorad.semestralproject.server.IServer;
import ass.starorad.semestralproject.server.impl.RequestHandler;


public class Main {

  public static void main(String[] args) throws IOException {
    int port = 8080;
    String documentRoot = ".";

    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Couldn't parse port number, using default port 8080.");
      }
    }

    if(args.length > 1) {
      documentRoot = args[1];
    }

    Injector injector = Guice.createInjector(new ServerModule(new InetSocketAddress("localhost", port), documentRoot));

    IServer server = injector.getInstance(IServer.class);
    IRequestHandler requestHandler = injector.getInstance(IRequestHandler.class);
    IResponseWriter responseWriter = injector.getInstance(IResponseWriter.class);

    server.run(
        requestHandler,
        responseWriter,
        "\r\n"
    );
  }

}
