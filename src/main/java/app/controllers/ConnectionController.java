package app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javax.script.ScriptEngineManager;

import javafx.event.ActionEvent;

public class ConnectionController {
    @FXML private TextField getUserName;
    @FXML private PasswordField getPassword;
    @FXML private Label status;
    @FXML private PasswordField confirmPassword;
    @FXML private Button signUpButton;

    private final ConnectionService service = new ConnectionService();

    @FXML
    private void handleLogin() {
        if(service.authenticate(getUserName.getText(), getPassword.getText())) {
            status.setText("Successfull!");
        }
        else {
            status.setText("Failed to login.");
        }
    }

    @FXML
    private void handleSignUp() {
        if (service.authenticate(getUserName.getText(), getPassword.getText(), confirmPassword.getText())) {
            status.setText("Successfull!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/login.fxml"));
                Scene loginScene = new Scene(loader.load());
                Stage stage = (Stage) signUpButton.getScene().getWindow();
                stage.setScene(loginScene);
            }
            catch (IOException e) {
                status.setText("Cannot navigate to login scene. \nDon't try again.");
                e.printStackTrace();
            }
        }
        else {
            status.setText("Failed to register.");
        }
    }
}
