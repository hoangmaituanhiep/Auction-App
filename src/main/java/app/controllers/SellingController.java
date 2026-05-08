package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.slf4j.LoggerFactory;
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
    
    private MainWebController mainWebController;
    @FXML
    public void addSellingItem() {
        logger.info("Sell item button clicked");
        Stage stage = (Stage) sellItem.getScene().getWindow();
        mainWebController = MainWebController.getInstance();
        mainWebController.addItemToAuction(String.format("- %s\n- %s\n- %s\n- %s", getItemName.getText()
                                            , getDetails.getText()
                                            , getStartingPrice.getText()
                                            , getDuration.getText()));
        try {
            stage.close();
        } catch (Exception e) {
            logger.error("Error occurred while closing stage", e);
        }
        
    }
}
