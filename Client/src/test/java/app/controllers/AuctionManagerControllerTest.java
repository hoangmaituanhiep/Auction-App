package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.NetworkClient;
import app.functions.Auction;
import app.functions.Seller;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

class AuctionManagerControllerTest {
    private AuctionManagerController controller;
    private TestNetworkClient testClient;
    private TestMainWebController testMainWebController;

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
        controller = new AuctionManagerController();
        testClient = new TestNetworkClient();
        testMainWebController = new TestMainWebController();

        setStaticNetworkClientInstance(testClient);
        setField(controller, "networkClient", testClient);
        setField(controller, "mainWebController", testMainWebController);

        Seller seller = new Seller();
        seller.setUserName("seller1");
        Auction auction = new Auction("TestAuction", "10");
        auction.setAuctionId(99);
        seller.participate(auction);
        setField(controller, "user", seller);

        runOnFxThread(() -> {
            try {
                Button quit = new Button();
                Stage stage = new Stage();
                stage.setScene(new Scene(quit));
                setField(controller, "quit", quit);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        resetUserSingleton();
        resetNetworkClientSingleton();
    }

    @Test
    void quitAuctionShouldSendCancelAndQuitPackets() throws Exception {
        runOnFxThread(() -> {
            try {
                controller.quitAuction();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(2, testClient.sentPackets.size());
        assertEquals(Message.CANCEL_AUCTION_REQUEST, testClient.sentPackets.get(0).getType());
        assertEquals(Message.QUIT_ACTION, testClient.sentPackets.get(1).getType());
        assertTrue(testMainWebController.toggleQuitCalled);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void setStaticNetworkClientInstance(NetworkClient client) throws Exception {
        Field instanceField = NetworkClient.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, client);
    }

    private static void resetUserSingleton() throws Exception {
        Field instanceField = User.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
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
        private final List<PacketMessage> sentPackets = new ArrayList<>();

        @Override
        public void sendPacket(PacketMessage packet) throws IOException {
            sentPackets.add(packet);
        }
    }

    private static class TestMainWebController extends MainWebController {
        boolean toggleQuitCalled = false;

        @Override
        public void toggleQuit() {
            toggleQuitCalled = true;
        }
    }
}
