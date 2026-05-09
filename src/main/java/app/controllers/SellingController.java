package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.LoggerFactory;

import app.NetworkClient;
import app.functions.Art;
import app.functions.Electronics;
import app.functions.Item;
import app.functions.User;
import app.functions.Vehicle;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.SellItemRequestPayload;
import app.payload.SellItemRespondPayload;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

public class SellingController {
    private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
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

    @FXML
    public void initialize() {
      mainWebController = MainWebController.getInstance();
      networkClient = NetworkClient.getInstance();
      user = User.getInstance();

      networkClient.addUIListener(Message.SEND_ITEM_RESPOND, packet -> {
        SellItemRespondPayload response = (SellItemRespondPayload) packet.getPayload();
        if (response.isSuccess()) {
          //TODO: Need logic
        }
      });
    }
    @FXML
    public void addSellingItem() {
        logger.info("Sell item button clicked");
        Stage stage = (Stage) sellItem.getScene().getWindow();
        mainWebController.addItemToAuction(imageView, String.format("-Name: %s\n-Details: %s\n-Starting Price: %s\n-Duration: %s", getItemName.getText()
                                            , getDetails.getText()
                                            , getStartingPrice.getText()
                                            , getDuration.getText()));
      
        Item item = null;  

        if (categoryComboBox.getValue() == null) {
            logger.warn("WARN: No category selected");
            return;
        }

        switch (categoryComboBox.getValue()) {
          case "Vehicle":
            item = new Vehicle(getItemName.getText(), getDetails.getText(), Integer.parseInt(getDuration.getText()), Double.parseDouble(getStartingPrice.getText()));
            break;

          case "Art":
            item = new Art(getItemName.getText(), getDetails.getText(), Integer.parseInt(getDuration.getText()), Double.parseDouble(getStartingPrice.getText()));
            break;
          
          case "Electronics":
            item = new Electronics(getItemName.getText(), getDetails.getText(), Integer.parseInt(getDuration.getText()), Double.parseDouble(getStartingPrice.getText()));
            break;
          default:
            logger.warn("WARN: Invalid category");
            return;
        }

        if (item != null) {
          SellItemRequestPayload payload = new SellItemRequestPayload(item);
          PacketMessage message = new PacketMessage(Message.SEND_ITEM_REQUEST, payload);

          try {
            networkClient.sendPacket(message);
            logger.info("INFO: Item info sent");
          }
          catch (IOException e) {
            logger.error("ERROR: {}", e.getMessage());
          }
        }

        try {
            stage.close();
        } catch (Exception e) {
            logger.error("Error occurred while closing stage", e);
        }
    }
    @FXML
    public void handleUploadFile() {
        logger.info("Upload file button clicked");
                FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }
}
