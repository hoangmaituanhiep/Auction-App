package app.controllers;

import java.io.IOException;

import app.controllers.*;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MainWebController {
    @FXML private TextField searchItems;
    @FXML private Button logIn;
    @FXML private Button join;
    
    @FXML
    public void callLogIn() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/app/login.fxml"));
            Scene loginScene = new Scene(loginLoader.load());
            Stage loginStage = (Stage) logIn.getScene().getWindow();
            loginStage.setScene(loginScene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
