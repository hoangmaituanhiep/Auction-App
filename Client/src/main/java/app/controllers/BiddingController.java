package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;

import app.functions.User;
import app.functions.Item;
import app.functions.Seller;
import app.functions.BidTransaction;
import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import app.payload.BidItemRespondPayload;
import app.payload.CancelAuctionResponse;
import app.payload.ChangeItemInfoRequest;
import app.payload.DeleteItemRequest;
import app.payload.KickUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class BiddingController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
  @FXML
  private ImageView itemImage;
  @FXML
  LineChart<String, Number> priceChart;
  private XYChart.Series<String, Number> priceSeries;
  private Item currentItem;
  @FXML
  private Label updateCurrentPrice;
  @FXML
  private TextField placeBid;
  @FXML
  private Button submitBidButton;
  @FXML
  private TextField autoBidTextField;
  @FXML
  private Button autoBidButton;
  @FXML
  private Button editItemButton;
  @FXML
  private TextField editedNameField;
  @FXML
  private TextField editedDetailField;

  private NetworkClient networkClient;
  private User user;
  private final AutoBidThread autoThread = new AutoBidThread();
  private Thread thread;

  private int itemId;

  public int getItemId() {
    return (currentItem != null) ? currentItem.getId() : this.itemId;
  }

  private void showAlert(String title, String message) {
    javafx.application.Platform.runLater(() -> {
      javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    });
  }

  public void setItem(Item item) {
    this.currentItem = item;
    this.itemId = item.getId();

    autoThread.setItem(currentItem);
    if (priceSeries == null) {
      priceSeries = new XYChart.Series<>();
    }
    priceSeries.setName("Bidding History");
    priceSeries.getData().clear();

    for (BidTransaction transaction : item.getHistory()) {
      String timeStamps = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      priceSeries.getData().add(new XYChart.Data<>(timeStamps, transaction.getPrice()));
    }

    if (priceChart != null && !priceChart.getData().contains(priceSeries)) {
      priceChart.getData().add(priceSeries);
    }

    if (updateCurrentPrice != null) {
      updateCurrentPrice.setText(String.format("Current highest bid: %s", item.getCurrentPrice()));
    }
  }

  @FXML
  public void initialize() {
    networkClient = NetworkClient.getInstance();
    user = User.getInstance();
    priceSeries = new XYChart.Series<>();

    networkClient.addUIListener(Message.NEW_PRICE_RESPOND, packet -> {
      BidItemRespondPayload respone = (BidItemRespondPayload) packet.getPayload();
      if (respone.isSuccess()) {
        try {
          if (currentItem != null) {
            double bid = Double.parseDouble(placeBid.getText());
            currentItem.addHistory(new BidTransaction(user.getUserName(), bid));
          }
          autoThread.setBiddable(false);

        } catch (Exception e) {
          logger.error("Error while bidding: ", e);
          showAlert("Error", "Error while bidding:\n" + e.getMessage());
        }
      } else {
        logger.error("ERROR: Bidding failed. {}", respone.getError());
        showAlert("Bidding Failed", respone.getError());
      }
    });

    networkClient.addUIListener(Message.NEW_PRICE_BROADCAST, packet -> {
      BidItemRequestPayload newPrice = (BidItemRequestPayload) packet.getPayload();

      if (currentItem != null && newPrice.getId() == this.itemId) {
        String timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        double newBid = newPrice.getPrice();

        if (priceSeries == null) {
          priceSeries = new XYChart.Series<>();
          priceSeries.setName("Bidding History");
          if (priceChart != null && !priceChart.getData().contains(priceSeries)) {
            priceChart.getData().add(priceSeries);
          }
        }

        priceSeries.getData().add(new XYChart.Data<>(timeStamp, newBid));
        if (!newPrice.getUserName().equals(user.getUserName())){
          autoThread.setBiddable(false);
        }

        if (updateCurrentPrice != null) {
          updateCurrentPrice.setText(String.format("Current highest price: %s", newBid));
        }
      }
    });

    networkClient.addUIListener(Message.CANCEL_AUCTION_RESPONSE, packet -> {
      CancelAuctionResponse respond = (CancelAuctionResponse) packet.getPayload();
      if (user.getCurrentAuction() != null && user.getCurrentAuction().getAuctionId() == respond.getAuctionId()) {
        Stage stage = (Stage) submitBidButton.getScene().getWindow();
        stage.close();
      }
    });

    networkClient.addUIListener(Message.KICK_USER_RESPOND, packet -> {
      KickUser kickRespond = (KickUser) packet.getPayload();
      String username = kickRespond.getUsername();

      if (user.getUserName() != null && user.getUserName().equals(username)) {
        Platform.runLater(() -> {
          Stage stage = (Stage) placeBid.getScene().getWindow();
          stage.close();
        });
      }
    });

    networkClient.addUIListener(Message.DELETE_ITEM_BROADCAST, packet -> {
      Stage stage = (Stage) placeBid.getScene().getWindow();
      stage.close();
    });

    if (user instanceof Seller) {
      submitBidButton.setDisable(true);
      placeBid.setDisable(true);
      placeBid.setPromptText("You are the one sell this bro.");
      autoBidButton.setDisable(true);
      autoBidTextField.setDisable(true);
      autoBidTextField.setPromptText("You are the one sell this bro.");
      editItemButton.setVisible(true);
      editItemButton.setDisable(false);
    }
  }

  @FXML
  public void bidAction() {
    logger.info("New price is bidded");

    BidItemRequestPayload payload = new BidItemRequestPayload(this.getItemId(), user.getUserName(),
        Double.parseDouble(placeBid.getText()));
    PacketMessage mesage = new PacketMessage(Message.NEW_PRICE_REQUEST, payload);

    try {
      networkClient.sendPacket(mesage);
      logger.info("New price is sent.");
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
      showAlert("Error", "Failed to send bid:\n" + e.getMessage());
    }
  }

  @FXML
  public void autoBid() {
    logger.info("AutoBid button clicked.");
    boolean autoBidding = currentItem.isAutoBidding();
    if (!autoBidding) {
      if (autoThread.setAmount(Double.parseDouble(autoBidTextField.getText()))) {
        thread = new Thread(autoThread);
        thread.start();
        currentItem.setAutoBid(true);;
        logger.info("Start auto bidding succesfully.");

      } else {
        logger.error("Fail to start auto bidding.");
        showAlert("Error", "Fail to start auto bidding.");
      }
    } else {
      thread.interrupt();
      currentItem.setAutoBid(false);;
      logger.info("Close auto bid.");
    }
  }

  @FXML
  public void edit() {
    boolean isApply = false;

    editItemButton.setText("APPLY");
    if (!isApply) {
      editedDetailField.setDisable(false);
      editedDetailField.setVisible(true);
      editedNameField.setDisable(false);
      editedNameField.setVisible(true);
      isApply = true;
    } else {
      ChangeItemInfoRequest infoRequest = new ChangeItemInfoRequest(itemId, editedNameField.getText(), editedDetailField.getText(), user.getCurrentAuction().getAuctionId());
      PacketMessage packet = new PacketMessage(Message.CHANGE_INFO_REQUEST, infoRequest);

      try {
        networkClient.sendPacket(packet);
        editItemButton.setText("EDIT");
        isApply = false;
      } catch (IOException e) {
        logger.error("Error to send ChangeInfo packet: {}", e.getMessage());
      }
    }
  }

  @FXML
  public void delete() {
    DeleteItemRequest request = new DeleteItemRequest(user.getCurrentAuction().getAuctionId(), itemId);
    PacketMessage packetMessage = new PacketMessage(Message.DELETE_ITEM_REQUEST, request);

    try {
      networkClient.sendPacket(packetMessage);
    } catch (IOException e) {
      logger.error("ERROE: {}", e.getMessage());
    }
  }
}
