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
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


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

  private NetworkClient networkClient;
  private User user;
  private final AutoBidThread autoThread = new AutoBidThread();
  private Thread thread;
  private boolean autoBidding = false;

  private int itemId;

  public int getItemId() {
    return (currentItem != null) ? currentItem.getId() : this.itemId;
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
            updateCurrentPrice.setText(String.format("Current highest price: %s", bid));
            currentItem.addHistory(new BidTransaction(user.getUserName(), bid));
          }
          autoThread.setBiddable(false);

        } catch (Exception e) {
          logger.error("Error while bidding: ", e);
        }
      } else {
        logger.error("ERROR: Bidding failed. {}", respone.getError());
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
        autoThread.setBiddable(true);

        if (updateCurrentPrice != null) {
          updateCurrentPrice.setText(String.format("Current highest price: %s", newBid));
        }
      }
    });

    if (user instanceof Seller) {
      submitBidButton.setDisable(true);
      placeBid.setDisable(true);
      placeBid.setPromptText("You are the one sell this bro.");
      autoBidButton.setDisable(true);
      autoBidTextField.setDisable(true);
      autoBidTextField.setPromptText("You are the one sell this bro.");
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
    }
  }

  @FXML
  public void autoBid() {
    logger.info("AutoBid button clicked.");
    if (!autoBidding) {
      if (autoThread.setAmount(Double.parseDouble(autoBidTextField.getText()))) {
        thread = new Thread(autoThread);
        thread.start();
        autoBidding = true;
        logger.info("Start auto bidding succesfully.");

      } else {
        logger.error("Fail to start auto bidding.");
      }
    } else {
      thread.interrupt();
      autoBidding = true;
      logger.info("Close auto bid.");
    }
  }
}
