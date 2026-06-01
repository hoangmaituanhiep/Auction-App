package app.integration;

import app.MediaServer;
import app.Server;
import app.ServerMain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ServerMainIntegrationTest {

    private static final Path CREATED_ROOT = Paths.get("Server", "src", "main", "resources", "images");

    private Thread mainThread;

    @AfterEach
    void cleanupAfterTest() throws Exception {
        stopServerMain();
        if (mainThread != null && mainThread.isAlive()) {
            mainThread.join(2000);
        }
        MediaServer.stop();
        cleanupCreatedFiles();
        setStaticField(Server.class, "server", null);
        setStaticField(Server.class, "isListening", false);
    }

    @Test
    void serverMainStartsTcpAndMediaEndpoints() throws Exception {
        int port = findFreePort();
        mainThread = new Thread(() -> ServerMain.main(new String[] { String.valueOf(port) }),
                "server-main-thread");
        mainThread.start();

        assertTrue(waitForPort(port, 5000), "TCP server did not start in time");
        assertTrue(waitForPort(port + 1, 5000), "Media server did not start in time");

        try (Socket socket = new Socket("localhost", port)) {
            assertTrue(socket.isConnected());
        }

        URL uploadUrl = new URL("http://localhost:" + (port + 1) + "/upload");
        HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        byte[] payload = "ping".getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(payload.length);
        connection.connect();
        connection.getOutputStream().write(payload);
        assertEquals(200, connection.getResponseCode());
    }

    private static boolean waitForPort(int port, long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            try (Socket ignored = new Socket("localhost", port)) {
                return true;
            } catch (IOException ignored) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void stopServerMain() throws Exception {
        try {
            setStaticField(Server.class, "isListening", false);
            Field socketField = Server.class.getDeclaredField("serverSocket");
            socketField.setAccessible(true);
            Object socket = socketField.get(null);
            if (socket instanceof ServerSocket) {
                ((ServerSocket) socket).close();
            }
        } catch (NoSuchFieldException ignored) {
        } catch (IOException ignored) {
        }
    }

    private static void cleanupCreatedFiles() throws IOException {
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

    private static void cleanupEmptyDirectory(Path path) throws IOException {
        if (path != null && Files.exists(path) && Files.isDirectory(path) && Files.list(path).findAny().isEmpty()) {
            Files.delete(path);
        }
    }

    private static void setStaticField(Class<?> clazz, String name, Object value) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }
}
