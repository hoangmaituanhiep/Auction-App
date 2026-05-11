package app.controllers;

import java.io.IOException;

import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.Item;
import app.functions.User;
import app.packets.Message;
import app.payload.SellItemRequestPayload;

import org.slf4j.Logger;

import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;


public class MainWebController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);

  private NetworkClient networkClient;
  private User user;

  @FXML
  private BorderPane mainPane;
  @FXML
  private TextField searchItems;
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
  private FlowPane auctionBox;
  @FXML
  private ScrollPane auctionScrollPane;

  private static MainWebController instance;

  public static MainWebController getInstance() {
    return instance;
  }

  public void addItemToAuction(ImageView imageView, String itemInfo) {
    Label itemLabel = new Label(itemInfo);
    itemLabel.setWrapText(true);
    itemLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

    VBox itemBox = new VBox(10);
    itemBox.setAlignment(Pos.TOP_LEFT);
    itemBox.setPadding(new Insets(30));
    itemBox.setStyle(
            "-fx-background-color: #090e13;" + 
            "-fx-background-radius: 20;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(71, 65, 65, 0.3), 10, 0, 0, 5);"
        );
    itemBox.setPrefWidth(220);
    itemBox.getChildren().add(imageView);
    itemBox.getChildren().add(itemLabel);

    auctionBox.getChildren().add(itemBox);
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

    user = User.getInstance();
    networkClient = NetworkClient.getInstance();
    try {
      networkClient.connect("127.0.0.1", MainApp.getPort());
      logger.info("INFO: Connected to server successfully at: " + MainApp.getPort());
    }
    catch (IOException e) {
      logger.error("ERROR: Cannot connect to server. {}", e.getMessage());
    }

    networkClient.addUIListener(Message.WELCOME, packet -> {
      logger.info("INFO: Welcome bro.");
    });

    networkClient.addUIListener(Message.BROADCAST_NEW_ITEM, packet -> {
      SellItemRequestPayload payload = (SellItemRequestPayload) packet.getPayload();
      Item newItem = payload.getItem();

      String itemInfo = String.format("-Name: %s\n-Details: %s\n-Starting Price: %s\n-Duration: %s", newItem.getName(),
                newItem.getDetail(), newItem.getStartingPrice());

      Image image = SellingController.getInstance().getImage();
      ImageView placeholder = new ImageView(image);
      SellingController.getInstance().setImage();

      addItemToAuction(placeholder, itemInfo);
    });
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
