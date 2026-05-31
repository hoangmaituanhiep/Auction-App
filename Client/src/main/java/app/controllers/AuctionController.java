package app.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.NetworkClient;
import app.functions.Auction;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.NewAuctionRequest;
import app.payload.NewAuctionRespond;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuctionController {
  private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

  private NetworkClient networkClient;
  private User user;
  private MainWebController mainWebController;

  @FXML
  private TextField auctionName;
  @FXML
  private TextField duration;
  @FXML
  private Button addItem;
  @FXML
  private Button New;

  @FXML
  public void initialize() {
    networkClient = NetworkClient.getInstance();
    user = User.getInstance();
    mainWebController = MainWebController.getInstance();
  }


  @FXML
  public void addItem() {
    try {
      FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/app/itemInfo.fxml"));
      Scene itemScene = new Scene(itemLoader.load());
      Stage itemStage = new Stage();
      itemStage.setScene(itemScene);
      itemStage.show();
    }
    catch(IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  @FXML
  public void addAuction() {
    if (user.getCurrentAuction()==null) {
      String name = auctionName.getText();
      String dur = duration.getText();

      user = user.asSeller();

      Auction auction = new Auction(name, dur);
      user.participate(auction);

      if (mainWebController != null) {
        mainWebController.setUser(user);
      }

      NewAuctionRequest request = new NewAuctionRequest(name, dur);
      PacketMessage message = new PacketMessage(Message.NEW_AUCTION_REQUEST, request);
      try {
        networkClient.sendPacket(message);
      }
      catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
      }
    }
    else {
      logger.error("ERROR: User is already in an auction bro");
    }

    Stage stage = (Stage) New.getScene().getWindow();
    stage.close();
  }
}
