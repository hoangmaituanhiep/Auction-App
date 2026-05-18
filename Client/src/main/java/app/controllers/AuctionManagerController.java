package app.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.Item;
import app.functions.User;
import app.packets.Message;
import app.payload.AuctionTimeoutPayload;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AuctionManagerController {
  private static final Logger logger = LoggerFactory.getLogger(AuctionManagerController.class);

  private MainWebController mainWebController;
  private NetworkClient networkClient;

  @FXML
  private ListView<Item> itemListView;
  @FXML
  private Button addNewItem;
  @FXML
  private Button backToMainWeb;

  private User user;

  @FXML
  public void initialize() {
    user = User.getInstance();
    mainWebController = MainWebController.getInstance();
    networkClient = NetworkClient.getInstance();

    networkClient.addUIListener(Message.AUCTION_TIMEOUT, packet -> {
      AuctionTimeoutPayload payload = (AuctionTimeoutPayload) packet.getPayload();
      boolean isFinished = payload.isFinished();

      if (isFinished) {
        backToMainWeb();
      }
    });

    itemListView.setItems(user.getItemsList());
    itemListView.setCellFactory(listView -> new ListCell<>() {
      private final ImageView imageView = new ImageView();
      private final Label text = new Label();
      private final HBox content = new HBox(12, imageView, text);

      {
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        text.setWrapText(true);
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
        text.setText(item.getName() + "\n" + item.getDetail());

        setGraphic(content);
        setText(null);
      }
    });
  }

  @FXML
  public void addNewItem() {
    try {
      FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/app/itemInfo.fxml"));
      Scene itemScene = new Scene(itemLoader.load());
      Stage itemStage = new Stage();
      itemStage.setScene(itemScene);
      itemStage.show();
    }
    catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  @FXML
  public void backToMainWeb() {
    Stage stage = (Stage) backToMainWeb.getScene().getWindow();
    stage.close();
  }
}