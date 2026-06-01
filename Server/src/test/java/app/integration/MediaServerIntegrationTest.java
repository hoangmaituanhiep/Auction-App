package app.integration;

import app.MediaServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

public class MediaServerIntegrationTest {

    private static final Path CREATED_ROOT = Paths.get("Server", "src", "main", "resources", "images");

    @AfterEach
    void cleanupCreatedFiles() throws IOException {
        app.MediaServer.stop();
        Path clientsDir = CREATED_ROOT.resolve("clients");
        if (Files.exists(clientsDir)) {
            Files.walk(clientsDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }

        cleanupEmptyDirectory(CREATED_ROOT.resolve("clients"));
        cleanupEmptyDirectory(CREATED_ROOT);
        cleanupEmptyDirectory(CREATED_ROOT.getParent());
        cleanupEmptyDirectory(CREATED_ROOT.getParent().getParent());
        cleanupEmptyDirectory(CREATED_ROOT.getParent().getParent().getParent());
    }

    @Test
    void mediaServerUploadsAndServesImageSuccessfully() throws Exception {
        int port = findFreePort();
        MediaServer.start(port);

        byte[] content = "test-image-bytes".getBytes(StandardCharsets.UTF_8);
        URL uploadUrl = new URL("http://localhost:" + port + "/upload");
        HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(content.length);
        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(content);
        }

        assertEquals(200, connection.getResponseCode());
        String responsePath;
        try (InputStream inputStream = connection.getInputStream()) {
            responsePath = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertNotNull(responsePath);
        assertTrue(responsePath.startsWith("/images/"));

        URL imageUrl = new URL("http://localhost:" + port + responsePath);
        HttpURLConnection imageConnection = (HttpURLConnection) imageUrl.openConnection();
        assertEquals(200, imageConnection.getResponseCode());

        try (InputStream imageStream = imageConnection.getInputStream()) {
            byte[] actual = imageStream.readAllBytes();
            assertArrayEquals(content, actual);
        }
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void cleanupEmptyDirectory(Path path) throws IOException {
        if (path != null && Files.exists(path) && Files.isDirectory(path) && Files.list(path).findAny().isEmpty()) {
            Files.delete(path);
        }
    }
}
