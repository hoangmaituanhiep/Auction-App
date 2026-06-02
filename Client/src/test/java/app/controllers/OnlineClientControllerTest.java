package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.OnlineUserResponse;
import app.utils.PacketListener;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

class OnlineClientControllerTest {
    private OnlineClientController controller;
    private TestNetworkClient testClient;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new OnlineClientController();
        testClient = new TestNetworkClient();
        setStaticNetworkClientInstance(testClient);
        setField(controller, "clientsListView", new ListView<>());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetNetworkClientSingleton();
    }

    @Test
    void initializeShouldSendOnlineUserRequest() throws Exception {
        controller.initialize();

        assertNotNull(testClient.lastPacket);
        assertEquals(Message.ONLINE_USER_REQUEST, testClient.lastPacket.getType());
    }

    @Test
    void initializeShouldUpdateClientListWhenOnlineUserResponseArrives() throws Exception {
        controller.initialize();

        OnlineUserResponse response = new OnlineUserResponse(List.of("alice", "bob"));
        assertNotNull(testClient.lastListener);

        CountDownLatch latch = new CountDownLatch(1);
        runOnFxThread(() -> {
            testClient.lastListener.onReceivingPacket(new PacketMessage(Message.ONLINE_USER_RESPONSE, response));
            Platform.runLater(latch::countDown);
        });
        latch.await();

        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getField(controller, "clientsListView");
        ObservableList<String> items = listView.getItems();
        assertEquals(2, items.size());
        assertTrue(items.contains("alice"));
        assertTrue(items.contains("bob"));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setStaticNetworkClientInstance(NetworkClient client) throws Exception {
        Field instanceField = NetworkClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, client);
    }

    private static void resetNetworkClientSingleton() throws Exception {
        Field instanceField = NetworkClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private static void runOnFxThread(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    private static class TestNetworkClient extends NetworkClient {
        PacketListener lastListener;
        PacketMessage lastPacket;

        @Override
        public void addUIListener(Message type, PacketListener listener) {
            lastListener = listener;
        }

        @Override
        public void sendPacket(PacketMessage packet) throws IOException {
            lastPacket = packet;
        }
    }
}
