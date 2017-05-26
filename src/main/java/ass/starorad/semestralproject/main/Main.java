package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.impl.HttpCoreRequestParser;
import java.io.IOException;
import java.net.InetSocketAddress;

import ass.starorad.semestralproject.server.IServer;
import ass.starorad.semestralproject.server.impl.RequestHandler;
import ass.starorad.semestralproject.server.impl.ResponseWriter;
import ass.starorad.semestralproject.server.impl.Server;


public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    int port = 8080;
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Couldn't parse port number, using default port 8080.");
      }
    }

    IServer server = new Server(new InetSocketAddress("localhost", port));
    server.run(
        new RequestHandler(
            new HttpCoreRequestParser()
        ),
        new ResponseWriter(),
        "\r\n"
    );
  }

}
