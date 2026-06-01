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
import app.payload.SellItemRequestPayload;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

class SellingControllerTest {
    private SellingController controller;
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
        controller = new SellingController();
        testClient = new TestNetworkClient();

        setField(controller, "networkClient", testClient);
        setField(controller, "user", User.getInstance());
        setField(controller, "categoryComboBox", new ComboBox<>());
        setField(controller, "getItemName", new TextField());
        setField(controller, "getDetails", new TextField());
        setField(controller, "getStartingPrice", new TextField());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetUserSingleton();
    }

    @Test
    void addSellingItemShouldSendSellItemRequestForVehicle() throws Exception {
        ComboBox<String> categoryComboBox = (ComboBox<String>) getField(controller, "categoryComboBox");
        TextField itemName = (TextField) getField(controller, "getItemName");
        TextField details = (TextField) getField(controller, "getDetails");
        TextField price = (TextField) getField(controller, "getStartingPrice");

        categoryComboBox.getItems().add("Vehicle");
        categoryComboBox.setValue("Vehicle");
        itemName.setText("Car");
        details.setText("Nice car");
        price.setText("99.99");

        controller.addSellingItem();

        PacketMessage packet = testClient.getLastPacket();
        assertNotNull(packet);
        assertEquals(Message.SEND_ITEM_REQUEST, packet.getType());
        assertTrue(packet.getPayload() instanceof SellItemRequestPayload);
        SellItemRequestPayload payload = (SellItemRequestPayload) packet.getPayload();
        assertEquals("Car", payload.getItem().getName());
        assertEquals(99.99, payload.getItem().getStartingPrice());
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
