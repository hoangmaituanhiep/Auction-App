package app.controllers;

import app.NetworkClient;
import app.packets.Message;
import app.payload.OnlineUserResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class OnlineClientController {
  @FXML
  private ListView<String> clientsListView;

  private NetworkClient networkClient;
  ObservableList<String> usernames;

  @FXML
  public void initialize() {
    networkClient = NetworkClient.getInstance();

    networkClient.addUIListener(Message.ONLINE_USER_RESPONSE, packet -> {
      OnlineUserResponse payload = (OnlineUserResponse) packet.getPayload();

      javafx.application.Platform.runLater(() -> {
        usernames = FXCollections.observableArrayList(payload.getOnlineUsernames());
        clientsListView.setItems(usernames);
      });
    });

    try {
      app.packets.PacketMessage onlineRequest = new app.packets.PacketMessage(Message.ONLINE_USER_REQUEST, null);
      networkClient.sendPacket(onlineRequest);
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
  }
}
