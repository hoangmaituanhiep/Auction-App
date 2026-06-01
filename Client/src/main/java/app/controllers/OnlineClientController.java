package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.KickUser;
import app.payload.OnlineUserResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class OnlineClientController {
  private static final Logger logger = LoggerFactory.getLogger(OnlineClientController.class);

  @FXML
  private ListView<String> clientsListView;
  @FXML
  private Button kick;
  @FXML
  private TextField kickUsername;

  private Alert alert;
  private NetworkClient networkClient;
  ObservableList<String> usernames;

  @FXML
  public void initialize() {
    networkClient = NetworkClient.getInstance();

    networkClient.addUIListener(Message.ONLINE_USER_RESPONSE, packet -> {
      OnlineUserResponse payload = (OnlineUserResponse) packet.getPayload();

      Platform.runLater(() -> {
        usernames = FXCollections.observableArrayList(payload.getOnlineUsernames());
        clientsListView.setItems(usernames);
      });
    });

    try {
      PacketMessage onlineRequest = new PacketMessage(Message.ONLINE_USER_REQUEST, null);
      networkClient.sendPacket(onlineRequest);
    } catch (IOException e) {
      logger.error("ERROR: {}", e);
    }
  }

  @FXML
  public void kickUser() {
    String username = kickUsername.getText();

    if (username.equals(null) || username.isEmpty()) {
      showNotification("ERROR", "Please enter a username");
    }
    else {
      KickUser request = new KickUser(username);
      PacketMessage message = new PacketMessage(Message.KICK_USER_REQUEST, request);

      try {
        networkClient.sendPacket(message);
      }
      catch (IOException e) {
        showNotification("ERROR", "Cannot send Kick request." + e.getMessage());
      }
    }
  }

  private void showNotification(String title, String header) {
    Platform.runLater(() -> {
      alert.setTitle(title);
      alert.setHeaderText(header);
      alert.showAndWait();
    });
  }
}
