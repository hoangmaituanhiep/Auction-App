package app.controllers;

import app.MainApp;
import app.NetworkClient;
import app.functions.Item;
import app.functions.User;
import app.packets.Message;
import app.payload.WinnerPayload;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class itemsSceneController {

  private MainWebController mainWebController;
  private NetworkClient networkClient;
  private User user;

  @FXML
  private ListView<Item> itemsView;
  private ObservableList<Item> ownItems;

  @FXML
  public void initialize() {
    mainWebController = MainWebController.getInstance();
    networkClient = NetworkClient.getInstance();
    user = User.getInstance();

    networkClient.addUIListener(Message.WINNER_RESPOND, packet -> {
      WinnerPayload winner = (WinnerPayload) packet.getPayload();
      if (user.getUserName().equals(winner.getUsername())) {
          itemsView.refresh();
      }
    });

    ownItems = user.getWonLists();
    itemsView.setItems(ownItems);
    itemsView.setCellFactory(listView -> new ListCell<>() {
      private final ImageView imageView = new ImageView();
      private final Label text1 = new Label();
      private final HBox content = new HBox(12, imageView, text1);

      {
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        text1.setWrapText(true);
      }
      @Override
      protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
          return;
        }
        
        Image img = item.getImage();
        if (img == null && item.getImagePath() != null) {
          String path = item.getImagePath();
          String url = path.startsWith("http")
              ? path
              : "http://" + mainWebController.getHost() + ":" + (MainApp.getPort() + 1) + path;
          img = new Image(url, true);
        }
        imageView.setImage(img);
        text1.setText(item.getName() + "\n" + item.getDetail());

        setGraphic(content);
        setText(null);
      }
    });
  }
}
