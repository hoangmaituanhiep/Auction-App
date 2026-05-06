package app.controllers;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.dao.ItemDAO;
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


public class MainWebController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);

  private NetworkClient networkClient;
  private ItemsService itemsService;

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
  private TilePane auctionPane;

  public MainWebController() {
    ItemDAO itemDAO = new ItemDAO();
    this.itemsService = new ItemsService(itemDAO);

    this.itemsService.setupDatabase();
  }

  private static MainWebController instance;

  public static MainWebController getInstance() {
    if (instance == null) {
      instance = new MainWebController();
    }
    return instance;
  }

  @FXML
  public void callLogIn() {
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

      Stage sellStage = (Stage) sell.getScene().getWindow();
      sellStage.setScene(sellScene);
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
      enterAuction.setManaged(false);

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
