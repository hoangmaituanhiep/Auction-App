package app.controllers;

import app.NetworkClient;
import app.packets.Message;
import app.payload.OnlineUserResponse;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class OnlineClientController {
  @FXML
  private ListView<String> clientList;

  private NetworkClient networkClient;
  ObservableList<String> usernames;

  @FXML
  public void initialize() {
    networkClient = NetworkClient.getInstance();

    networkClient.addUIListener(Message.ONLINE_USER_RESPONSE, packet -> {
      OnlineUserResponse payload = (OnlineUserResponse) packet.getPayload();

      usernames = (ObservableList<String>) payload.getOnlineUsernames();

      clientList.setItems(usernames);
    });
  }
}
