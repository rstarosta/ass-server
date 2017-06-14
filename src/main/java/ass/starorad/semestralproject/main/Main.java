package ass.starorad.semestralproject.main;

import ass.starorad.semestralproject.server.transformers.IRequestHandler;
import ass.starorad.semestralproject.server.transformers.IResponseWriter;
import ass.starorad.semestralproject.server.IServer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Main class that instantiates the module and runs the server.
 */
public class Main {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    int port = 8080;
    String documentRoot = ".";

    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        logger.warn("Couldn't parse port number, using default port 8080.");
      }
    }

    if (args.length > 1) {
      documentRoot = args[1];
    }

    boolean debug = false;
    if (args.length > 2) {
      debug = Boolean.parseBoolean(args[2]);
    }

    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    if (debug) {
      root.setLevel(Level.INFO);
    }

    Injector injector = Guice
        .createInjector(new ServerModule(new InetSocketAddress("localhost", port), documentRoot));

    IServer server = injector.getInstance(IServer.class);
    IRequestHandler requestHandler = injector.getInstance(IRequestHandler.class);
    IResponseWriter responseWriter = injector.getInstance(IResponseWriter.class);

    logger.info("Starting server on port {} using document root '{}'", port, documentRoot);

    server.run(
        requestHandler,
        responseWriter,
        "\r\n"
    );
  }

}
