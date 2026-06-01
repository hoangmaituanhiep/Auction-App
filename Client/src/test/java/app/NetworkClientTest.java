package app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.packets.Message;
import app.packets.PacketMessage;
import app.utils.PacketListener;

class NetworkClientTest {
    private NetworkClient client;

    @BeforeEach
    void setUp() throws Exception {
        Field instanceField = NetworkClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        client = NetworkClient.getInstance();

        Field listenersField = NetworkClient.class.getDeclaredField("listeners");
        listenersField.setAccessible(true);
        listenersField.set(client,
                new java.util.concurrent.ConcurrentHashMap<Message, java.util.List<PacketListener>>());
    }

    @AfterEach
    void tearDown() throws Exception {
        Field instanceField = NetworkClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @Test
    void singletonReturnsSameInstance() {
        NetworkClient first = NetworkClient.getInstance();
        NetworkClient second = NetworkClient.getInstance();

        assertSame(first, second);
    }

    @Test
    void addListenerRoutePacketInvokesListener() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        client.addListener(Message.WELCOME, packet -> called.set(true));

        PacketMessage packet = new PacketMessage(Message.WELCOME, "hello world");

        Method routePacket = NetworkClient.class.getDeclaredMethod("routePacket", PacketMessage.class);
        routePacket.setAccessible(true);
        routePacket.invoke(client, packet);

        assertTrue(called.get(), "Route packet should invoke registered listener");
    }

    @Test
    void sendPacketSerializesPacket() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        Field outField = NetworkClient.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(client, oos);

        PacketMessage packet = new PacketMessage(Message.WELCOME, "payload");
        client.sendPacket(packet);
        oos.flush();

        assertTrue(baos.size() > 0, "The packet should be written into the underlying output stream");
    }

    @Test
    void closeClosesSocketAndOutputStream() throws Exception {
        Socket socket = new Socket();
        Field socketField = NetworkClient.class.getDeclaredField("socket");
        socketField.setAccessible(true);
        socketField.set(client, socket);

        ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
        Field outField = NetworkClient.class.getDeclaredField("out");
        outField.setAccessible(true);
        outField.set(client, oos);

        client.close();
        assertTrue(socket.isClosed(), "Socket should be closed after close()");
    }
}
