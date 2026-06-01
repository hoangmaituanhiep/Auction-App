package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.NetworkClient;
import app.functions.Admin;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.ConnectionRequestPayload;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

class ConnectionControllerTest {
    private ConnectionController controller;
    private TestNetworkClient testClient;

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException ignored) {
            // Toolkit already initialized by a previous test or runtime environment.
        }
    }

    private static class TestNetworkClient extends NetworkClient {
        private PacketMessage lastPacket;

        @Override
        public void sendPacket(PacketMessage packet) {
            this.lastPacket = packet;
        }

        PacketMessage getLastPacket() {
            return lastPacket;
        }
    }

    private static class TestMainWebController extends MainWebController {
        boolean toggleLogedinCalled = false;
        boolean setUserCalled = false;

        @Override
        public void setUser(app.functions.User user) {
            setUserCalled = true;
            super.setUser(user);
        }

        @Override
        public void toggleLogedin() {
            toggleLogedinCalled = true;
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        controller = new ConnectionController();
        testClient = new TestNetworkClient();

        setField(controller, "networkClient", testClient);
        setField(controller, "mainWebController", null);
        setField(controller, "user", User.getInstance());
        setField(controller, "getUserName", new TextField());
        setField(controller, "getPassword", new PasswordField());
        setField(controller, "confirmPassword", new PasswordField());
        setField(controller, "getEmail", new TextField());

        Button signInButton = new Button();
        runOnFxThread(() -> {
            Scene scene = new Scene(new StackPane(signInButton));
            Stage stage = new Stage();
            stage.setScene(scene);
        });
        setField(controller, "signInButton", signInButton);
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
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

    @Test
    void handleLoginSendsLoginRequestForRegularUser() throws Exception {
        setField(controller, "getUserName", new TextField("user1"));
        setField(controller, "getPassword", new PasswordField());
        ((PasswordField) getField(controller, "getPassword")).setText("secret");

        controller.handleLogin();

        PacketMessage packet = testClient.getLastPacket();
        assertNotNull(packet);
        assertEquals(Message.LOGIN_REQUEST, packet.getType());
        assertTrue(packet.getPayload() instanceof ConnectionRequestPayload);
        ConnectionRequestPayload payload = (ConnectionRequestPayload) packet.getPayload();
        assertEquals("user1", payload.getUsername());
        assertEquals("secret", payload.getPassword());
    }

    @Test
    void handleSignUpSendsSignupRequest() throws Exception {
        setField(controller, "getUserName", new TextField("newuser"));
        setField(controller, "getPassword", new PasswordField());
        setField(controller, "confirmPassword", new PasswordField());
        setField(controller, "getEmail", new TextField("new@example.com"));

        ((PasswordField) getField(controller, "getPassword")).setText("abc123");
        ((PasswordField) getField(controller, "confirmPassword")).setText("abc123");

        controller.handleSignUp();

        PacketMessage packet = testClient.getLastPacket();
        assertNotNull(packet);
        assertEquals(Message.SIGNUP_REQUEST, packet.getType());
        assertTrue(packet.getPayload() instanceof ConnectionRequestPayload);
        ConnectionRequestPayload payload = (ConnectionRequestPayload) packet.getPayload();
        assertEquals("newuser", payload.getUsername());
        assertEquals("abc123", payload.getPassword());
        assertEquals("abc123", payload.getConfirmPassword());
        assertEquals("new@example.com", payload.getEmail());
    }

    @Test
    void handleLoginUsesAdminPathWhenAdminCredentialsAreProvided() throws Exception {
        setField(controller, "getUserName", new TextField("admin1"));
        setField(controller, "getPassword", new PasswordField());
        ((PasswordField) getField(controller, "getPassword")).setText("toidongtinh");
        TestMainWebController mainWebController = new TestMainWebController();
        setField(controller, "mainWebController", mainWebController);

        runOnFxThread(controller::handleLogin);

        assertTrue(mainWebController.toggleLogedinCalled);
        assertTrue(mainWebController.setUserCalled);
        assertTrue(User.getInstance() instanceof Admin);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
