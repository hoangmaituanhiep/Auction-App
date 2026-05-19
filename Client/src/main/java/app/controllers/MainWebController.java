package app.controllers;

import java.io.IOException;
import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.User;
import app.packets.Message;
import app.payload.AuctionTimeoutPayload;
import app.payload.NewAuctionRespond;

import org.slf4j.Logger;

import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
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
  private TextField searchItems;
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

  private static MainWebController instance;

  public static MainWebController getInstance() {
    return instance;
  }

  public void displayAuctionList(int auctionId, String name, String duration) {
    String auctionInfo = String.format("-Auction ID: %d\n-Name: %s\n-Duration: %s", auctionId, name, duration);
    Label auctionLabel = new Label(auctionInfo);
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
    auctionCard.setOnMouseClicked(e -> {
      try {
        FXMLLoader auctionLoader = new FXMLLoader(getClass().getResource("/app/AuctionManager.fxml"));
        Scene auctionScene = new Scene(auctionLoader.load());

        Stage auctionStage = new Stage();
        auctionStage.setScene(auctionScene);
        auctionStage.show();
      } catch (IOException ex) {
        logger.error("ERROR: {}", ex.getMessage());
      }
    });

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

  public void toggleLogedin() {
    logIn.setVisible(false);
    logIn.setManaged(false);
    join.setDisable(false);
    New.setDisable(false);
    searchItems.setDisable(false);
    auctionScrollPane.setDisable(false);
    auctionScrollPane.setVisible(true);
    auctionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    auctionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    logInLabel.setText("Hi " + user.getUserName());
  }

  @FXML
  public void initialize() {
    instance = this;

    logIn.setDisable(true);
    searchItems.setDisable(true);
    join.setDisable(true);
    New.setDisable(true);

    user = User.getInstance();
    if (user.getUserName() != null && !user.getUserName().isEmpty()) {
      toggleLogedin();
    }

    networkClient = NetworkClient.getInstance();
    try {
      networkClient.connect(MainApp.getHost(), MainApp.getPort());
      logger.info("INFO: Connected to server successfully at: {}:{}", MainApp.getHost(), MainApp.getPort());
    } catch (IOException e) {
      logger.error("ERROR: Cannot connect to server. {}", e.getMessage());
    }

    networkClient.addUIListener(Message.WELCOME, packet -> {
      logger.info("INFO: Welcome bro.");
    });

    networkClient.addUIListener(Message.NEW_AUCTION_BROADCAST, packet -> {
      NewAuctionRespond response = (NewAuctionRespond) packet.getPayload();
      if (response.isSuccess()) {
        displayAuctionList(response.getAuctionId(), response.getName(), response.getDuration());
      }
    });

    networkClient.addUIListener(Message.AUCTION_TIMEOUT, packet -> {
      AuctionTimeoutPayload response = (AuctionTimeoutPayload) packet.getPayload();
      boolean isFinished = response.isFinished();

      if (isFinished) {
        disableAuction(String.valueOf(response.getAuctionId()));
      }
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
    } catch (IOException e) {
      logger.error("ERROR: Cannot connect to server. {}", e.getMessage());
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
    }
  }

  @FXML
  public void createAuction() {
    try {
      FXMLLoader auctionLoader = new FXMLLoader(getClass().getResource("/app/createAuction.fxml"));
      Scene auctionScene = new Scene(auctionLoader.load());
      Stage auctionStage = new Stage();
      auctionStage.setScene(auctionScene);
      auctionStage.show();
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  @FXML
  public void openAuctionManager() {
    try {
      FXMLLoader managerLoader = new FXMLLoader(getClass().getResource("/app/AuctionManager.fxml"));
      Scene managerScene = new Scene(managerLoader.load(), 1280, 720);
      Stage managerStage = new Stage();
      managerStage.setScene(managerScene);
      managerStage.show();
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public String getHost() {
    return host;
  }
  // @FXML
  // public void addAuctionItem(Item item) {
  // VBox card = new VBox(10);
  // card.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color:
  // #f9f9f9;");

  // ImageView imageView = new ImageView(item.getImage());
  // imageView.setFitWidth(150);
  // imageView.setFitHeight(150);
  // imageView.setPreserveRatio(true);

  // Label nameLabel = new Label(item.getName());
  // Label priceLabel = new Label("Giá khởi điểm: " + item.getStartingPrice());

  // Button chooseImageBtn = new Button("Chọn ảnh");
  // chooseImageBtn.setOnAction(e -> {
  // FileChooser fileChooser = new FileChooser();
  // fileChooser.setTitle("Chọn ảnh sản phẩm");
  // fileChooser.getExtensionFilters().add(
  // new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
  // File file = fileChooser.showOpenDialog(null);
  // if (file != null) {
  // Image image = new Image(file.toURI().toString());
  // item.setImage(image);
  // imageView.setImage(image);
  // }
  // });

  // card.getChildren().addAll(imageView, nameLabel, priceLabel);
  // auctionPane.getChildren().add(card);

  // // Timeline kiểm tra hết hạn
  // Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
  // if (LocalDateTime.now().isAfter(item.getEndTime())) {
  // auctionPane.getChildren().remove(card);
  // }
  // }));
  // timeline.setCycleCount(Animation.INDEFINITE);
  // timeline.play();
  // }

}
