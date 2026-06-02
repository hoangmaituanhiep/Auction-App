package app.controllers;

import java.io.IOException;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.Item;
import app.functions.Seller;
import app.functions.User;
import app.functions.BidTransaction;
import app.functions.Bidder;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import app.payload.CancelAuctionRequest;
import app.payload.CancelAuctionResponse;
import app.payload.FetchAuctionItemsRequestPayload;
import app.payload.FetchAuctionItemsResponsePayload;
import app.payload.KickUser;
import app.payload.QuitAuctionRequest;
import app.payload.SellItemRequestPayload;
import app.payload.WinnerPayload;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class AuctionManagerController {
  private static final Logger logger = LoggerFactory.getLogger(AuctionManagerController.class);

  private MainWebController mainWebController;
  private NetworkClient networkClient;

  @FXML
  private ListView<Item> itemListView;
  @FXML
  private Button addNewItem;
  @FXML
  private Button quit;

  private User user;
  private Stage stage;

  private void showAlert(String title, String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  private void showNotification(String title, String header) {
    Platform.runLater(() -> {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle(title);
      alert.setHeaderText(header);
      alert.showAndWait();
    });
  }

  @FXML
  public void initialize() {
    user = User.getInstance();
    mainWebController = MainWebController.getInstance();
    networkClient = NetworkClient.getInstance();

    Platform.runLater(() -> {
      stage = (Stage) quit.getScene().getWindow();
    });

    if (user instanceof Bidder) {
      addNewItem.setDisable(true);
    }

    networkClient.addUIListener(Message.NEW_PRICE_BROADCAST, packet -> {
      BidItemRequestPayload globalRequest = (BidItemRequestPayload) packet.getPayload();
      int targetId = globalRequest.getId();

      for (Item item : itemListView.getItems()) {
        if (item.getId() == targetId) {
          item.setNewPrice(globalRequest.getPrice());
          item.addHistory(new BidTransaction(globalRequest.getUserName(), globalRequest.getPrice()));
          itemListView.refresh();
        }
      }
    });

    networkClient.addUIListener(Message.FETCH_AUCTION_ITEMS_RESPONSE, packet -> {
      FetchAuctionItemsResponsePayload payload = (FetchAuctionItemsResponsePayload) packet.getPayload();

      if (user.getCurrentAuction() != null) {
        for (Item item : payload.getItems()) {
          if (item == null) {
            logger.warn("WARNING: null item.");
            continue;
          }
          boolean alreadyExists = false;
          for (Item existingItem : user.getItemsList()) {
            if (existingItem.getId() == item.getId()) {
              alreadyExists = true;
              break;
            }
          }

          if (!alreadyExists) {
            user.addItem(item);
            user.getCurrentAuction().addNewItem(item.getId());
          }
        }
      }
      itemListView.refresh();
    });

    networkClient.addUIListener(Message.BROADCAST_NEW_ITEM, packet -> {
      SellItemRequestPayload payload = (SellItemRequestPayload) packet.getPayload();
      Item item = payload.getItem();
      int auctionId = payload.getAuctionId();

      if (user.getCurrentAuction() != null && user.getCurrentAuction().getAuctionId() == auctionId) {
        boolean alreadyExists = false;
        for (Item existingItem : user.getItemsList()) {
          if (existingItem.getId() == item.getId()) {
            alreadyExists = true;
            break;
          }
        }

        if (!alreadyExists) {
          user.addItem(item);
          user.getCurrentAuction().addNewItem(item.getId());
          itemListView.refresh();
        }
      }
    });

    networkClient.addUIListener(Message.CANCEL_AUCTION_RESPONSE, packet -> {
      CancelAuctionResponse respond = (CancelAuctionResponse) packet.getPayload();
      if (user.getCurrentAuction() != null && user.getCurrentAuction().getAuctionId() == respond.getAuctionId()) {
        stage.close();
      }
    });

    networkClient.addUIListener(Message.KICK_USER_RESPOND, packet -> {
      KickUser kickRespond = (KickUser) packet.getPayload();
      String username = kickRespond.getUsername();

      if (user.getUserName() != null && user.getUserName().equals(username)) {
        Platform.runLater(() -> {
          Stage stage = (Stage) quit.getScene().getWindow();
          stage.close();
        });
      }
    });

    itemListView.setItems(user.getItemsList());
    if (user.getCurrentAuction() != null) {
      int auctionId = user.getCurrentAuction().getAuctionId();
      FetchAuctionItemsRequestPayload itemsRequest = new FetchAuctionItemsRequestPayload(auctionId);
      PacketMessage fetchItemsRequest = new PacketMessage(Message.FETCH_AUCTION_ITEMS_REQUEST, itemsRequest);
      try {
        networkClient.sendPacket(fetchItemsRequest);
      }
      catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
        showAlert("Error", "Failed to fetch items:\n" + e.getMessage());
      }
    }
    itemListView.setCellFactory(listView -> new ListCell<>() {
      private final ImageView imageView = new ImageView();
      private final Label text1 = new Label();
      private final HBox content = new HBox(12, imageView, text1);

      {
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        text1.setWrapText(true);

        content.setOnMouseClicked(e -> {
          try {
            FXMLLoader biddingLoader = new FXMLLoader(getClass().getResource("/app/bidScreen.fxml"));
            Parent root = biddingLoader.load();

            BiddingController controller = biddingLoader.getController();
            controller.setItem(getItem());

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
          } catch(IOException exception) {
            logger.error("Error while open Bidding screen: {}", exception.getMessage());
            showAlert("Error", "Error while open Bidding screen:\n" + exception.getMessage());
          }
        });
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
      showAlert("Error", "Failed to add new item:\n" + e.getMessage());
    }
  }

  @FXML
  public void quitAuction() {
    if (user instanceof Seller) {
      CancelAuctionRequest cancelRequest = new CancelAuctionRequest(user.getCurrentAuction().getAuctionId());
      PacketMessage cancelMessage = new PacketMessage(Message.CANCEL_AUCTION_REQUEST, cancelRequest);

      try {
        networkClient.sendPacket(cancelMessage);
      }
      catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
        showAlert("Error", "Failed to cancel auction:\n" + e.getMessage());
      }
    }
    QuitAuctionRequest quitRequest = new QuitAuctionRequest(user.getCurrentAuction().getAuctionId());
    PacketMessage quitMessage = new PacketMessage(Message.QUIT_ACTION, quitRequest);
    try {
      networkClient.sendPacket(quitMessage);
    }
    catch(IOException e) {
      logger.error("ERROR: {}", e.getMessage());
      showAlert("Error", "Failed to quit auction:\n" + e.getMessage());
    }

    mainWebController.toggleQuit();

    Stage stage = (Stage) quit.getScene().getWindow();
    stage.close();
  }
}