package app.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.LoggerFactory;

import app.MainApp;
import app.NetworkClient;
import app.functions.Art;
import app.functions.Electronics;
import app.functions.Item;
import app.functions.User;
import app.functions.Vehicle;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.CancelAuctionResponse;
import app.payload.KickUser;
import app.payload.SellItemRequestPayload;
import app.payload.SellItemRespondPayload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;

import org.slf4j.Logger;

public class SellingController {
  private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
  private static SellingController instance;

  private Image selectedImage;
  private byte[] selectedImageBytes;

  @FXML
  private Button sellItem;
  @FXML
  private TextField getItemName;
  @FXML
  private TextField getDetails;
  @FXML
  private TextField getStartingPrice;
  @FXML
  private TextField getDuration;
  @FXML
  private ComboBox<String> categoryComboBox;
  @FXML
  private Button uploadFileButton;
  @FXML
  private ImageView imageView;

  private MainWebController mainWebController;
  private NetworkClient networkClient;
  private User user;

  public static SellingController getInstance() {
    return instance;
  }

  @FXML
  public void initialize() {
    instance = this;
    mainWebController = MainWebController.getInstance();
    networkClient = NetworkClient.getInstance();
    user = User.getInstance();

    networkClient.addUIListener(Message.SEND_ITEM_RESPOND, packet -> {
      SellItemRespondPayload response = (SellItemRespondPayload) packet.getPayload();
      if (response.isSuccess()) {
        try {
          user.addItemId(response.getItemId());
          user.getCurrentAuction().addNewItem(response.getItemId());
          Stage stage = (Stage) sellItem.getScene().getWindow();
          stage.close();
        } catch (Exception e) {
          logger.error("Error occurred while closing stage", e);
        }
      }
    });

    networkClient.addUIListener(Message.KICK_USER_RESPOND, packet -> {
      KickUser kickRespond = (KickUser) packet.getPayload();
      String username = kickRespond.getUsername();

      if (user.getUserName() != null && user.getUserName().equals(username)) {
        Platform.runLater(() -> {
          Stage stage = (Stage) sellItem.getScene().getWindow();
          stage.close();
        });
      }
    });

    networkClient.addUIListener(Message.CANCEL_AUCTION_RESPONSE, packet -> {
      CancelAuctionResponse respond = (CancelAuctionResponse) packet.getPayload();
      if (user.getCurrentAuction() == null || user.getCurrentAuction().getAuctionId() == respond.getAuctionId()) {
        Platform.runLater(() -> {
          Stage stage = (Stage) sellItem.getScene().getWindow();
          stage.close();
        });
      }
    });
  }

  @FXML
  public void addSellingItem() {
    logger.info("Sell item button clicked");

    Item item = null;

    if (categoryComboBox.getValue() == null) {
      logger.warn("WARN: No category selected");
      return;
    }

    switch (categoryComboBox.getValue()) {
      case "Vehicle":
        item = new Vehicle(getItemName.getText(), getDetails.getText(), Double.parseDouble(getStartingPrice.getText()));
        break;

      case "Art":
        item = new Art(getItemName.getText(), getDetails.getText(), Double.parseDouble(getStartingPrice.getText()));
        break;

      case "Electronics":
        item = new Electronics(getItemName.getText(), getDetails.getText(),
            Double.parseDouble(getStartingPrice.getText()));
        break;
      default:
        logger.warn("WARN: Invalid category");
        return;
    }

    if (item != null) {
      if (selectedImageBytes != null) {
        String uploadedPath = uploadImageToServer(selectedImageBytes);
        item.setImagePath(uploadedPath);
      }

      int auctionId = -1;
      if (user.getCurrentAuction() != null) {
        auctionId = user.getCurrentAuction().getAuctionId();
      }

      SellItemRequestPayload payload = new SellItemRequestPayload(item, auctionId);
      PacketMessage message = new PacketMessage(Message.SEND_ITEM_REQUEST, payload);

      try {
        networkClient.sendPacket(message);
        logger.info("INFO: Item info sent");
      } catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
      }
    }

  }

  @FXML
  public void handleUploadFile() {
    logger.info("Upload file button clicked");
    FileChooser fileChooser = new FileChooser();

    // Set extension filter
    FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
    FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
    fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

    // Show open file dialog
    File file = fileChooser.showOpenDialog(null);

    if (file != null) {
      try {
        selectedImageBytes = Files.readAllBytes(file.toPath());
      } catch (IOException e) {
        logger.error("ERROR: Failed to read image bytes", e);
      }
      selectedImage = new Image(file.toURI().toString());
      imageView.setImage(selectedImage);
    }
  }

  public Image getImage() {
    return this.selectedImage;
  }

  public void clearImage() {
    this.selectedImage = null;
  }

  public String uploadImageToServer(byte[] imageBytes) {
    try {
      int port = MainApp.getPort() + 1;

      URL url = new URI("http://" + MainApp.getHost() + ":" + port + "/upload").toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

      try (OutputStream os = connection.getOutputStream()) {
        os.write(imageBytes);
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        return reader.readLine();
      }
    } catch (Exception e) {
      logger.error("ERROR: Failed to upload image to server. {}", e.getMessage());
      return null;
    }
  }
}
