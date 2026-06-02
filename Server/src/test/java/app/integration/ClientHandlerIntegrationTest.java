package app.integration;

import app.Client;
import app.ClientHandler;
import app.Server;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.ConnectionRequestPayload;
import app.payload.ConnectionRespondPayload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHandlerIntegrationTest {

    @BeforeEach
    void resetServerSingleton() throws Exception {
        setStaticField(Server.class, "server", null);
        setStaticField(Server.class, "isListening", false);
    }

    @AfterEach
    void cleanupServerSingleton() throws Exception {
        setStaticField(Server.class, "server", null);
        setStaticField(Server.class, "isListening", false);
    }

    @Test
    void clientHandlerStartsAndSendsWelcomeMessage() throws Exception {
        try (ServerSocket serverListener = new ServerSocket(0);
                Socket clientSocket = new Socket("localhost", serverListener.getLocalPort());
                Socket serverSocket = serverListener.accept()) {

            Constructor<Client> clientConstructor = Client.class.getDeclaredConstructor(Socket.class);
            clientConstructor.setAccessible(true);
            Client serverClient = clientConstructor.newInstance(serverSocket);
            ClientHandler handler = new ClientHandler(serverClient);
            Thread handlerThread = new Thread(handler, "client-handler-thread");
            handlerThread.start();

            try (ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream())) {
                clientOut.flush();

                PacketMessage welcome = (PacketMessage) clientIn.readObject();
                assertNotNull(welcome);
                assertEquals(Message.WELCOME, welcome.getType());
                assertNull(welcome.getPayload());

                PacketMessage loginRequest = new PacketMessage(
                        Message.LOGIN_REQUEST,
                        new ConnectionRequestPayload("unknown-user", "wrong-password"));
                clientOut.writeObject(loginRequest);
                clientOut.flush();

                PacketMessage loginResponse = (PacketMessage) clientIn.readObject();
                assertNotNull(loginResponse);
                assertEquals(Message.LOGIN_RESPONSE, loginResponse.getType());
                assertTrue(loginResponse.getPayload() instanceof ConnectionRespondPayload);
                ConnectionRespondPayload responsePayload = (ConnectionRespondPayload) loginResponse.getPayload();
                assertFalse(responsePayload.isSuccess());
            } finally {
                clientSocket.close();
                serverSocket.close();
                handlerThread.join(2000);
            }
        }
    }

    private static void setStaticField(Class<?> clazz, String name, Object value) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }
}
