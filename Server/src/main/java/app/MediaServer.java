package app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.sun.net.httpserver.HttpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaServer {
  private static final String IMAGE_DIR = "Server/src/main/resources/images/clients/";
  private static final Logger logger = LoggerFactory.getLogger(MediaServer.class);
  private static HttpServer httpServer;

  public static void start(int port) throws IOException {
    Files.createDirectories(Paths.get(IMAGE_DIR));
    httpServer = HttpServer.create(new InetSocketAddress(port), 0);

    httpServer.createContext("/upload", exchange -> {
      if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        String filename = UUID.randomUUID().toString() + "png"; // Just get a random name
        Path filePath = Paths.get(IMAGE_DIR, filename);

        try (InputStream is = exchange.getRequestBody();
            OutputStream os = Files.newOutputStream(filePath)) {
          is.transferTo(os);
        }

        String reponsePath = "/images/" + filename;
        exchange.sendResponseHeaders(200, reponsePath.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(reponsePath.getBytes());
        }
      } else {
        exchange.sendResponseHeaders(405, -1);
      }
    });

    httpServer.createContext("/images/", exchange -> {
      if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        String filename = exchange.getRequestURI().getPath().substring("/images/".length());
        Path filePath = Paths.get(IMAGE_DIR, filename);

        if (Files.exists(filePath)) {
          byte[] bytes = Files.readAllBytes(filePath);
          exchange.getResponseHeaders().set("Content-Type", "image/png");
          exchange.sendResponseHeaders(200, bytes.length);
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
          }
        } else {
          exchange.sendResponseHeaders(404, -1);
        }
      }
    });

    httpServer.setExecutor(null);
    httpServer.start();
    logger.info("Media Server started at: {}", port);
  }

  public static void stop() {
    if (httpServer != null) {
      httpServer.stop(0);
      httpServer = null;
    }
  }
}
