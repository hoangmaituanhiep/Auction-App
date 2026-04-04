package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.script.ScriptEngineManager;

import javafx.event.ActionEvent;

public class ConnectionController {
    @FXML private TextField getUserName;
    @FXML private PasswordField getPassword;
    @FXML private Label status;
    @FXML private PasswordField confirmPassword;
    @FXML private Button signUpButton;

    private ConnectionService service = new ConnectionService();

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

            FXMLLoader loader = new FXMLLoader.getResource("/app/login.fxml");
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.setScene(loginScene);
        }
        else {
            status.setText("Failed to register.");
        }
    }
}
