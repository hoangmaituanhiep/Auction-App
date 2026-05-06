package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class SellingController {
    private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
    @FXML
    private Button sellItem;
    
    @FXML
    public void addSellingItem() {
        logger.info("Sell item button clicked");
        Stage stage = (Stage) sellItem.getScene().getWindow();
        try {
            stage.close();
        } catch (Exception e) {
            logger.error("Error occurred while closing stage", e);
        }
        
    }
}
