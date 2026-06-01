package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import app.NetworkClient;
import app.functions.Bidder;
import app.functions.Item;
import app.functions.User;
import app.functions.Vehicle;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import javafx.application.Platform;
import javafx.scene.control.TextField;

class BiddingControllerTest {
    private BiddingController controller;
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
        controller = new BiddingController();
        testClient = new TestNetworkClient();

        setField(controller, "networkClient", testClient);
        setField(controller, "user", User.getInstance());
        setField(controller, "placeBid", new TextField());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetUserSingleton();
    }

    @Test
    void bidActionShouldSendNewPriceRequest() throws Exception {
        Item item = new Vehicle("Painting", "Nice", 10.0);
        Field currentItemField = BiddingController.class.getDeclaredField("currentItem");
        currentItemField.setAccessible(true);
        currentItemField.set(controller, item);
        setField(controller, "itemId", item.getId());
        TextField placeBid = (TextField) getField(controller, "placeBid");
        placeBid.setText("50.0");

        User user = User.getInstance();
        user.setUserName("bidder1");
        setField(controller, "user", user);

        controller.bidAction();

        PacketMessage packet = testClient.getLastPacket();
        assertNotNull(packet);
        assertEquals(Message.NEW_PRICE_REQUEST, packet.getType());
        assertTrue(packet.getPayload() instanceof BidItemRequestPayload);
        BidItemRequestPayload payload = (BidItemRequestPayload) packet.getPayload();
        assertEquals(item.getId(), payload.getId());
        assertEquals("bidder1", payload.getUserName());
        assertEquals(50.0, payload.getPrice());
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
