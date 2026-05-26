package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.OnlineUserResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class OnlineClientController {
  private static final Logger logger = LoggerFactory.getLogger(OnlineClientController.class);

  @FXML
  private ListView<String> clientsListView;

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
}
