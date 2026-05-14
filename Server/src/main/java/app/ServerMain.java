package app;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

public class ServerMain {
  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

  private static int port = 8080;
  public static void main(String[] args) {
    if (args.length > 0) {
      logger.debug("DEBUG: Parsing port...");
      try {
        port = Integer.parseInt(args[0]);
      }
      catch (NumberFormatException e) {
        logger.error("ERROR: " + e.getMessage());
      }
    }

    logger.debug("DEBUG: Starting server: " + port);
    Server server = Server.getInstance(port);

    try {
      MediaServer.start(port + 1); // <--- MUST BE STARTED BEFORE server.listen()
      server.listen();
    }
    catch (IOException e){
      logger.error("ERROR: " + e.getMessage());
    }
  }
}
