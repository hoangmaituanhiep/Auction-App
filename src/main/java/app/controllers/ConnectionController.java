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

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import app.dao.UserDAO;
import app.services.ConnectionService;

public class ConnectionController {
  @FXML
  private TextField getUserName;
  @FXML
  private TextField getEmail;
  @FXML
  private PasswordField getPassword;
  @FXML
  private Label status;
  @FXML
  private PasswordField confirmPassword;
  @FXML
  private Button signUpButton;
  @FXML
  private Hyperlink signUpLink;
  @FXML
  private Button signInButton;

  private static final Logger logger = LoggerFactory.getLogger(ConnectionController.class);

  private ConnectionService service;
  private MainWebController mainWebController;
  
  public ConnectionController() {
    UserDAO userDAO = new UserDAO();
    this.service = new ConnectionService(userDAO);

    service.setupDatabase();

    mainWebController = MainWebController.getInstance();
  }

  @FXML
  public void handleLogin() {
    logger.debug("DEBUG: Signing in...");
    // Why put the authenticate outside the if statement, so it can correctly
    // process the authentication without being interrupt
    boolean isSuccess = service.authenticate(getUserName.getText(), getPassword.getText());

    if (isSuccess) {
      logger.info("INFO: Login succeeded. Closing window ...");
      Stage thisStage = (Stage) signInButton.getScene().getWindow();
      thisStage.close();

      mainWebController.toggleLogedin();
    } else {
      logger.error("ERROR: Authentication failed!");
    }
  }

  @FXML
  public void handleSignUp() {
    logger.debug("DEBUG: Signing up...");

    if (service.authenticate(getUserName.getText(), getPassword.getText(), confirmPassword.getText(), getEmail.getText())) {
      
      logger.info("INFO: Signup succeeded. Navigating to login...");

      try {
        logger.debug("DEBUG: Switching to login...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/login.fxml"));
        Scene loginScene = new Scene(loader.load());
        Stage stage = (Stage) signUpButton.getScene().getWindow();
        stage.setScene(loginScene);
      } catch (IOException e) {
        status.setText("Cannot navigate to login scene. \nDon't try again.");
        logger.error("ERROR: " + e.getMessage());
      }
    } else {
      status.setText("Failed to register.");
      logger.error("ERROR: Authentication failed.");
    }
  }

  @FXML
  public void callSignUp() {
    try {
      logger.debug("DEBUG: Opening signup Scene...");

      FXMLLoader signUpLoader = new FXMLLoader(getClass().getResource("/app/signup.fxml"));
      Scene signUpScene = new Scene(signUpLoader.load());
      Stage signUpStage = (Stage) signUpLink.getScene().getWindow();
      signUpStage.setScene(signUpScene);
    } catch (IOException e) {
      logger.error("ERROR: " + e.getMessage());
    }
  }
}
