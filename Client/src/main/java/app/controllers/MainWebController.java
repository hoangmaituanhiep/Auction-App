package app.controllers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.Admin;
import app.functions.Auction;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.AntiSnippingRespondPayload;
import app.payload.AuctionDTO;
import app.payload.CancelAuctionRequest;
import app.payload.CancelAuctionResponse;
import app.payload.FetchDataResponsePayload;
import app.payload.FetchOwnItemsResponse;
import app.payload.KickUser;
import app.payload.NewAuctionRespond;
import app.payload.RegisterClientPayload;
import app.payload.WinnerPayload;

import org.slf4j.Logger;

import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.application.Platform;

public class MainWebController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);

  private NetworkClient networkClient;
  private User user;
  private String host;

  @FXML
  private BorderPane mainPane;
  @FXML
  private Button logIn;
  @FXML
  private Button join;
  @FXML
  private Button New;
  @FXML
  private Button enterAuction;
  @FXML
  private Label logInLabel;
  @FXML
  private FlowPane auctionBox;
  @FXML
  private ScrollPane auctionScrollPane;
  @FXML
  private TextField Ip;
  @FXML
  private Button enter;
  @FXML
  private Button myItems;

  private static MainWebController instance;

  public static MainWebController getInstance() {
    return instance;
  }

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

  public void displayAuctionList(int auctionId, String name, String duration) {
    String dueTime = LocalTime.now().plusMinutes(Integer.parseInt(duration))
        .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    String auctionInfo = String.format("-Auction ID: %d\n-Name: %s\n-Due: %s", auctionId, name, dueTime);
    Label auctionLabel = new Label(auctionInfo);
    auctionLabel.setId("auctionLabel-" + auctionId);
    auctionLabel.setWrapText(true);
    auctionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");

    VBox auctionCard = new VBox(10);
    auctionCard.setId("auction-" + String.valueOf(auctionId));
    auctionCard.setAlignment(Pos.TOP_LEFT);
    auctionCard.setPadding(new Insets(30));
    auctionCard.setStyle(
        "-fx-background-color: #5f0d0d;" +
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(71, 65, 65, 0.3), 10, 0, 0, 5);");
    auctionCard.setPrefWidth(220);
    auctionCard.getChildren().add(auctionLabel);

    if (user instanceof Admin) {
      Button cancel = new Button("x");
      cancel.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

      cancel.setOnAction(e -> {
        e.consume();
        logger.info("INFO: Admin clicked cancel button");

        disableAuction(String.valueOf(auctionId));

        CancelAuctionRequest cancelRequest = new CancelAuctionRequest(auctionId);
        PacketMessage message = new PacketMessage(Message.CANCEL_AUCTION_REQUEST, cancelRequest);

        try {
          networkClient.sendPacket(message);
        } catch (IOException exception) {
          logger.error("ERROR: {}", exception);
          showAlert("Error", "Failed to cancel auction:\n" + exception.getMessage());
        }
      });

      auctionCard.getChildren().add(cancel);
    }

    Platform.runLater(() -> {
      auctionBox.getChildren().add(auctionCard);
    });
  }

  public void disableAuction(String id) {
    Scene currentScene = auctionScrollPane.getScene();
    Node node = currentScene.lookup("#auction-" + id);

    if (node instanceof VBox) {
      VBox auctionCard = (VBox) node;
      auctionCard.setOnMouseClicked(null);
      auctionCard.setStyle("-fx-background-color: #140b0b;" +
          "-fx-background-radius: 20;" +
          "-fx-effect: dropshadow(three-pass-box, rgba(71, 65, 65, 0.3), 10, 0, 0, 5);");
      auctionCard.setDisable(true);
    }
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void toggleLogedin() {
    if (user instanceof Admin) {
      New.setText("User List");
    }
    logIn.setVisible(false);
    logIn.setManaged(false);
    join.setDisable(false);
    New.setDisable(false);
    myItems.setDisable(false);
    auctionScrollPane.setDisable(false);
    auctionScrollPane.setVisible(true);
    auctionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    auctionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    logInLabel.setText("Hi " + user.getUserName());

    try {
      networkClient.sendPacket(new PacketMessage(Message.FETCH_OWN_ITEMS_REQUEST, null));
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public void toggleQuit() {
    join.setText("JOIN");
    user.existAuction();
  }

  @FXML
  public void initialize() {
    instance = this;

    logIn.setDisable(true);
    myItems.setDisable(true);
    join.setDisable(true);
    New.setDisable(true);

    user = User.getInstance();
    if (user.getUserName() != null && !user.getUserName().isEmpty()) {
      toggleLogedin();
    }

    networkClient = NetworkClient.getInstance();

    networkClient.addUIListener(Message.WELCOME, packet -> {
      logger.info("INFO: Welcome bro.");
    });

    networkClient.addUIListener(Message.FETCH_DATA_RESPONSE, packet -> {
      FetchDataResponsePayload fetchPayload = (FetchDataResponsePayload) packet.getPayload();
      for (AuctionDTO auction : fetchPayload.getLiveAuction()) {
        displayAuctionList(auction.getAuctionId(), auction.getName(), auction.getDuration());
      }
    });

    networkClient.addUIListener(Message.NEW_AUCTION_BROADCAST, packet -> {
      NewAuctionRespond response = (NewAuctionRespond) packet.getPayload();
      if (response.isSuccess()) {
        displayAuctionList(response.getAuctionId(), response.getName(), response.getDuration());
      }
    });

    networkClient.addUIListener(Message.CANCEL_AUCTION_RESPONSE, packet -> {
      CancelAuctionResponse payload = (CancelAuctionResponse) packet.getPayload();
      if (payload.isSuccess()) {
        disableAuction(String.valueOf(payload.getAuctionId()));
      }
      if (user.getCurrentAuction() != null && user.getCurrentAuction().getAuctionId() == payload.getAuctionId()) {
        user.existAuction();
        join.setText("JOIN");
      }
    });

    networkClient.addUIListener(Message.ANTI_SNIPPING_RESPOND, packet -> {
      String dueTime = LocalTime.now().plusSeconds(60).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      AntiSnippingRespondPayload snippingPayload = (AntiSnippingRespondPayload) packet.getPayload();
      int id = snippingPayload.getAuctionId();
      String auctionInfo = String.format("-Auction ID: %d\n-Name: %s\n-Due: %s", id, snippingPayload.getName(),
          dueTime);
      Scene currentScene = auctionScrollPane.getScene();
      Node node = currentScene.lookup("#auction-" + id);

      if (node instanceof VBox) {
        VBox auctionCard = (VBox) node;
        Label label = (Label) auctionCard.lookup("#auctionLabel-" + id);
        label.setText(auctionInfo);
      }
    });

    networkClient.addUIListener(Message.JOIN_AUCTION_SUCCEEDED, packet -> {
      AuctionDTO dto = (AuctionDTO) packet.getPayload();

      Auction joinedAuction = new Auction(dto.getName(), dto.getDuration());
      joinedAuction.setAuctionId(dto.getAuctionId());
      joinedAuction.getData(dto);

      user.participate(joinedAuction);

      join.setText(user.getCurrentAuction().getName());
    });

    networkClient.addUIListener(Message.NEW_AUCTION_RESPOND, packet -> {
      NewAuctionRespond respond = (NewAuctionRespond) packet.getPayload();
      if (respond.isSuccess()) {
        user = User.getInstance();
        Auction currentAuction = user.getCurrentAuction();

        if (currentAuction != null && currentAuction.getName().equals(respond.getName())) {
          user.getCurrentAuction().setAuctionId(respond.getAuctionId());
          currentAuction.setAuctionId(respond.getAuctionId());
          join.setText(currentAuction.getName());
        }
      }
    });

    networkClient.addUIListener(Message.KICK_USER_RESPOND, packet -> {
      KickUser kickRespond = (KickUser) packet.getPayload();
      String username = kickRespond.getUsername();

      if (user.getUserName() != null && user.getUserName().equals(username)) {
        Platform.runLater(() -> {
          Stage stage = (Stage) mainPane.getScene().getWindow();
          stage.close();
        });
      }
    });

    networkClient.addUIListener(Message.WINNER_RESPOND, packet -> {
      WinnerPayload winner = (WinnerPayload) packet.getPayload();
      if (user.getCurrentAuction() != null && user.getCurrentAuction().getAuctionId() == winner.getAuctionId()) {
        showNotification("WINNER",
            String.format("The Winner in Auction %d get Item %s", winner.getAuctionId(), winner.getItem().getName()));
      }
    });

    networkClient.addUIListener(Message.FETCH_OWN_ITEMS_RESPOND, packet -> {
      FetchOwnItemsResponse itemsRespond = (FetchOwnItemsResponse) packet.getPayload();
      user.setWonList(itemsRespond.getItems());
    });
  }

  @FXML
  public void enter() {
    try {
      host = Ip.getText();
      networkClient.connect(host, MainApp.getPort());
      logIn.setDisable(false);
      Ip.setVisible(false);
      enter.setVisible(false);
      Ip.setManaged(false);
      enter.setManaged(false);

      networkClient.sendPacket(new PacketMessage(Message.FETCH_DATA_REQUEST, null));
    } catch (IOException e) {
      logger.error("ERROR: Cannot connect to server. {}", e.getMessage());
      showAlert("Connection Error", "Cannot connect to server:\n" + e.getMessage());
    }
  }

  @FXML
  public void callLogIn() {
    logger.debug("DEBUG: Processing Login");
    try {
      FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/app/login.fxml"));
      Scene loginScene = new Scene(loginLoader.load());
      Stage loginStage = new Stage();
      loginStage.setScene(loginScene);
      loginStage.show();
    } catch (IOException e) {
      e.printStackTrace();
      showAlert("Error", "Failed to open login screen:\n" + e.getMessage());
    }
  }

  @FXML
  public void createAuction() {
    if (user instanceof Admin) {
      try {
        FXMLLoader clientLoader = new FXMLLoader(getClass().getResource("/app/onlineClients.fxml"));
        Scene scene = new Scene(clientLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
      } catch (IOException e) {
        logger.error("ERROR: {}", e);
        showAlert("Error", "Failed to open online clients screen:\n" + e.getMessage());
      }
      return;
    }
    try {

      FXMLLoader auctionLoader = new FXMLLoader(getClass().getResource("/app/createAuction.fxml"));
      Scene auctionScene = new Scene(auctionLoader.load());
      Stage auctionStage = new Stage();
      auctionStage.setScene(auctionScene);
      auctionStage.show();
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
      showAlert("Error", "Failed to open create auction screen:\n" + e.getMessage());
    }
  }

  @FXML
  public void joinAuction() {
    if (user.getCurrentAuction() != null) {
      try {
        FXMLLoader managerLoader = new FXMLLoader(getClass().getResource("/app/AuctionManager.fxml"));
        Scene managerScene = new Scene(managerLoader.load(), 1280, 720);
        Stage managerStage = new Stage();
        managerStage.setScene(managerScene);
        managerStage.show();
      } catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
        showAlert("Error", "Failed to open auction manager:\n" + e.getMessage());
      }
      return;
    }
    TextInputDialog getAuctionId = new TextInputDialog();
    getAuctionId.setTitle("Join Auction");
    getAuctionId.setHeaderText("Enter Auction.");
    getAuctionId.setContentText("Auction's ID: ");

    Optional<String> id = getAuctionId.showAndWait();
    id.ifPresent(auctionIdString -> {
      try {
        if (auctionIdString.isEmpty() || auctionIdString.equals(null)) {
          getAuctionId.close();
          return;
        }
        int auctionId = Integer.parseInt(auctionIdString);
        user = user.asBidder();

        RegisterClientPayload payload = new RegisterClientPayload(auctionId);
        PacketMessage message = new PacketMessage(Message.JOIN_AUCTION, payload);
        networkClient.sendPacket(message);
        getAuctionId.close();
        logger.info("INFO: Sent join request");
      } catch (NumberFormatException e) {
        logger.error("ERROR: {}", e.getMessage());
        showAlert("Error", "Invalid Auction ID format:\n" + e.getMessage());
      } catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
        showAlert("Error", "Failed to join auction:\n" + e.getMessage());
      }
    });
  }

  @FXML
  public void openItemsScene() {
    try {
      FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/app/itemsScene.fxml"));
      Scene itemsScene = new Scene(itemLoader.load());
      Stage itemsStage = new Stage();
      itemsStage.setScene(itemsScene);
      itemsStage.show();
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public String getHost() {
    return host;
  }
  //
}
