package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.impl.NettyRequestParser;
import ass.starorad.semestralproject.server.impl.NettyResponseEncoder;
import ass.starorad.semestralproject.server.impl.ReactiveCache;
import ass.starorad.semestralproject.server.impl.FileManager;
import ass.starorad.semestralproject.server.impl.SocketResponseWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

import ass.starorad.semestralproject.server.IServer;
import ass.starorad.semestralproject.server.impl.RequestHandler;
import ass.starorad.semestralproject.server.impl.ResponseWriter;
import ass.starorad.semestralproject.server.impl.Server;


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

    IServer server = new Server(new InetSocketAddress("localhost", port));
    server.run(
        new RequestHandler(
            new NettyRequestParser(),
            new NettyResponseEncoder(),
            new FileManager(
                documentRoot,
                new ReactiveCache()
            )
        ),
        new SocketResponseWriter(),
        "\r\n"
    );
  }

}
