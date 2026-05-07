package app.controllers;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.dao.ItemDAO;
import app.dao.UserDAO;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.services.ItemsService;

import org.slf4j.Logger;

import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;


public class MainWebController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);

  private NetworkClient networkClient;
  private ItemsService itemsService;
  private User user;

  @FXML
  private BorderPane mainPane;
  @FXML
  private TextField searchItems;
  @FXML
  private TextField serverIp;
  @FXML
  private Button logIn;
  @FXML
  private Button join;
  @FXML
  private Button sell;
  @FXML
  private Button enterAuction;
  @FXML
  private Label logInLabel;
  @FXML
  private Label connectServerLabel;
  @FXML
  private TilePane auctionPane;
  @FXML
  private ScrollPane auctionScrollPane;

  public MainWebController() {
    ItemDAO itemDAO = new ItemDAO();
    this.itemsService = new ItemsService(itemDAO);
    this.itemsService.setupDatabase();
  }

  private static MainWebController instance;

  public static MainWebController getInstance() {
    return instance;
  }

  public ScrollPane getScrollPane() {
    if (auctionScrollPane == null) {
      HBox hBox = new HBox(10);
      hBox.setStyle("-fx-border-color: red; -fx-min-width: 50; -fx-min-height: 50;");
      auctionScrollPane = new ScrollPane();
      auctionScrollPane.setContent(hBox);
      auctionScrollPane.setFitToHeight(true);
      auctionScrollPane.setFitToWidth(true);
    }
    return auctionScrollPane;
  }

  public void toggleLogedin() {
    logIn.setVisible(false);
    logIn.setManaged(false);
    join.setDisable(false);
    sell.setDisable(false);
    searchItems.setDisable(false);
    auctionScrollPane.setDisable(false);
    auctionScrollPane.setVisible(true);
    auctionScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    auctionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    logInLabel.setText("Hi "+ user.getUserName());
  }

  @FXML
  public void initialize() {
    instance = this;

    searchItems.setDisable(true);
    join.setDisable(true);
    sell.setDisable(true);
    logIn.setDisable(true);

    user = User.getInstance();
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
  public void sellItems() {
    try {
      FXMLLoader sellLoader = new FXMLLoader(getClass().getResource("/app/itemInfo.fxml"));
      Scene sellScene = new Scene(sellLoader.load());

      Stage sellStage = new Stage();
      sellStage.setScene(sellScene);
      sellStage.show();
    }
    catch (IOException e) {
      logger.error("ERROR:" + e.getMessage());
    }
  }

  @FXML
  public void handleEnterAuction() {
    logger.debug("DEBUG: Connecting to server...");
    networkClient = NetworkClient.getInstance();
    try {
      networkClient.connect(serverIp.getText(), MainApp.getPort());
      logger.info("INFO: Connected successfully at: " + MainApp.getPort());
      enterAuction.setVisible(false);
      connectServerLabel.setVisible(false);
      serverIp.setVisible(false);

      enterAuction.setManaged(false);
      connectServerLabel.setManaged(false);
      serverIp.setVisible(false);

      logIn.setDisable(false);

      PacketMessage welcomePacket = networkClient.receivePacket();
      if (welcomePacket != null && welcomePacket.getType() == Message.WELCOME) {
        logger.info("INFO: Welcome!");
      }
    }
    catch (IOException e) {
      logger.error("ERROR: Cannot connect to server. {}", e.getMessage());
    }
    catch (ClassNotFoundException e) {
      logger.error("ERROR: " + e.getMessage());
    }
  }

  // @FXML
  // public void addAuctionItem(Item item) {
  //   VBox card = new VBox(10);
  //   card.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");

  //   ImageView imageView = new ImageView(item.getImage());
  //   imageView.setFitWidth(150);
  //   imageView.setFitHeight(150);
  //   imageView.setPreserveRatio(true);

  //   Label nameLabel = new Label(item.getName());
  //   Label priceLabel = new Label("Giá khởi điểm: " + item.getStartingPrice());

  //   Button chooseImageBtn = new Button("Chọn ảnh");
  //   chooseImageBtn.setOnAction(e -> {
  //     FileChooser fileChooser = new FileChooser();
  //     fileChooser.setTitle("Chọn ảnh sản phẩm");
  //     fileChooser.getExtensionFilters().add(
  //         new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
  //     File file = fileChooser.showOpenDialog(null);
  //     if (file != null) {
  //       Image image = new Image(file.toURI().toString());
  //       item.setImage(image);
  //       imageView.setImage(image);
  //     }
  //   });

  //   card.getChildren().addAll(imageView, nameLabel, priceLabel);
  //   auctionPane.getChildren().add(card);

  //   // Timeline kiểm tra hết hạn
  //   Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
  //     if (LocalDateTime.now().isAfter(item.getEndTime())) {
  //       auctionPane.getChildren().remove(card);
  //     }
  //   }));
  //   timeline.setCycleCount(Animation.INDEFINITE);
  //   timeline.play();
  // }

}
