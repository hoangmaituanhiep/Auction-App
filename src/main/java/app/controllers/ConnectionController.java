package app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import app.functions.User;
import app.dao.*;


public class ConnectionController {
    @FXML private TextField getUserName;
    @FXML private PasswordField getPassword;
    @FXML private Label status;
    @FXML private PasswordField confirmPassword;
    @FXML private Button signUpButton;
    @FXML private Hyperlink signUpLink;
    @FXML private Button signInButton;

    private final ConnectionService service = new ConnectionService();

    private loginListener listener;
    public void setLoginListener(loginListener listener) {this.listener=listener;}

    @FXML
    public void handleLogin() {
        boolean isSuccess = service.authenticate(getUserName.getText(), getPassword.getText());

        if (isSuccess) {

            if (listener != null) {
                User.getInstance().setUserName(getUserName.getText());

                listener.loginSucceeded(getUserName.getText());

                Stage thisStage = (Stage) signInButton.getScene().getWindow();
                thisStage.close();
            }
        }
    }

    @FXML
    public void handleSignUp() {
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
    @FXML
    public void callSignUp() {
        try {
            FXMLLoader signUpLoader = new FXMLLoader(getClass().getResource("/app/signup.fxml"));
            Scene signUpScene = new Scene(signUpLoader.load());
            Stage signUpStage = (Stage) signUpLink.getScene().getWindow();
            signUpStage.setScene(signUpScene);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

