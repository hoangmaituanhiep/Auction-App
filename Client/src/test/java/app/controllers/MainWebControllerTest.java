package app.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import app.functions.Admin;
import app.functions.User;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

class MainWebControllerTest {
    private MainWebController controller;

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
        controller = new MainWebController();

        setField(controller, "logIn", new Button());
        setField(controller, "join", new Button());
        setField(controller, "New", new Button());
        setField(controller, "searchItems", new TextField());
        setField(controller, "auctionScrollPane", new ScrollPane());
        setField(controller, "logInLabel", new Label());
    }

    @AfterEach
    void tearDown() throws Exception {
        resetUserSingleton();
    }

    @Test
    void toggleLogedinShouldUpdateUiForAdmin() throws Exception {
        setField(controller, "user", Admin.getInstance());
        setField(controller, "logInLabel", new Label());
        setField(controller, "join", new Button());
        setField(controller, "New", new Button());
        setField(controller, "searchItems", new TextField());
        setField(controller, "auctionScrollPane", new ScrollPane());
        setField(controller, "logIn", new Button());

        controller.toggleLogedin();

        Button logIn = (Button) getField(controller, "logIn");
        Button join = (Button) getField(controller, "join");
        Button newButton = (Button) getField(controller, "New");
        TextField searchItems = (TextField) getField(controller, "searchItems");
        ScrollPane auctionScrollPane = (ScrollPane) getField(controller, "auctionScrollPane");
        Label logInLabel = (Label) getField(controller, "logInLabel");

        assertFalse(logIn.isVisible());
        assertFalse(logIn.isManaged());
        assertFalse(join.isDisable());
        assertFalse(newButton.isDisable());
        assertFalse(searchItems.isDisable());
        assertFalse(auctionScrollPane.isDisable());
        assertTrue(auctionScrollPane.isVisible());
        assertEquals("Hi admin1", logInLabel.getText());
        assertEquals("User List", newButton.getText());
    }

    @Test
    void toggleQuitShouldResetJoinLabelAndClearAuction() throws Exception {
        setField(controller, "user", User.getInstance());
        setField(controller, "join", new Button());

        controller.toggleQuit();

        Button join = (Button) getField(controller, "join");
        assertEquals("JOIN", join.getText());
        assertNull(User.getInstance().getCurrentAuction());
    }

    @Test
    void displayAuctionListAddsCardToAuctionBox() throws Exception {
        runOnFxThread(() -> {
            try {
                setField(controller, "auctionBox", new FlowPane());
                setField(controller, "auctionScrollPane", new ScrollPane());
                ScrollPane scrollPane = (ScrollPane) getField(controller, "auctionScrollPane");
                scrollPane.setContent((FlowPane) getField(controller, "auctionBox"));
                new Scene(new StackPane(scrollPane));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        controller.displayAuctionList(1, "Test Auction", "1");
        runOnFxThread(() -> {
        });

        FlowPane auctionBox = (FlowPane) getField(controller, "auctionBox");
        assertEquals(1, auctionBox.getChildren().size());
        assertTrue(auctionBox.getChildren().get(0).getId().startsWith("auction-"));
    }

    @Test
    void disableAuctionShouldDisableExistingAuctionCard() throws Exception {
        final javafx.scene.layout.VBox auctionCard = new javafx.scene.layout.VBox(10);
        auctionCard.setId("auction-7");
        final boolean[] disabled = new boolean[1];

        runOnFxThread(() -> {
            try {
                FlowPane auctionBox = new FlowPane();
                setField(controller, "auctionBox", auctionBox);
                ScrollPane scrollPane = new ScrollPane(auctionBox);
                setField(controller, "auctionScrollPane", scrollPane);
                StackPane root = new StackPane(scrollPane, auctionCard);
                Scene scene = new Scene(root);
                assertNotNull(scene.lookup("#auction-7"),
                        "The auction card should be found in the scene graph before disableAuction");
                controller.disableAuction("7");
                disabled[0] = auctionCard.isDisable();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertTrue(disabled[0], "The auction card should be disabled after calling disableAuction");
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
}
