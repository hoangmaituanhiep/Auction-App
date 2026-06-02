package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.NetworkClient;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.NewAuctionRequest;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

class AuctionControllerTest {
    private AuctionController controller;
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
        controller = new AuctionController();
        testClient = new TestNetworkClient();

        setField(controller, "networkClient", testClient);
        setField(controller, "user", User.getInstance());
        setField(controller, "mainWebController", null);

        runOnFxThread(() -> {
            try {
                setField(controller, "auctionName", new TextField());
                setField(controller, "duration", new TextField());
                Button newButton = new Button();
                Stage stage = new Stage();
                stage.setScene(new Scene(newButton));
                setField(controller, "New", newButton);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        resetUserSingleton();
    }

    @Test
    void addAuctionSendsNewAuctionRequest() throws Exception {
        runOnFxThread(() -> {
            try {
                TextField auctionName = (TextField) getField(controller, "auctionName");
                TextField duration = (TextField) getField(controller, "duration");
                auctionName.setText("Test Auction");
                duration.setText("15");

                controller.addAuction();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        PacketMessage packet = testClient.getLastPacket();
        assertNotNull(packet);
        assertEquals(Message.NEW_AUCTION_REQUEST, packet.getType());
        assertTrue(packet.getPayload() instanceof NewAuctionRequest);
        NewAuctionRequest payload = (NewAuctionRequest) packet.getPayload();
        assertEquals("Test Auction", payload.getName());
        assertEquals("15", payload.getDuration());
        assertNotNull(User.getInstance().getCurrentAuction());
        assertEquals("Test Auction", User.getInstance().getCurrentAuction().getName());
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

    private static void resetUserSingleton() throws Exception {
        Field instanceField = User.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    private static void runOnFxThread(Runnable action) throws InterruptedException {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
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
        private PacketMessage lastPacket;

        @Override
        public void sendPacket(PacketMessage packet) throws IOException {
            this.lastPacket = packet;
        }

        PacketMessage getLastPacket() {
            return lastPacket;
        }
    }
}
