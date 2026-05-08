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

import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.ConnectionRequestPayload;
import app.payload.ConnectionRespondPayload;

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

  private MainWebController mainWebController;
  private NetworkClient networkClient;

  public ConnectionController() {
    mainWebController = MainWebController.getInstance();
    networkClient = NetworkClient.getInstance();
  }

  public void initialize() {
    networkClient.addUIListener(Message.LOGIN_RESPONSE, packet -> {
      ConnectionRespondPayload response = (ConnectionRespondPayload) packet.getPayload();
      if (response.isSuccess() ) {
        try {
          mainWebController.toggleLogedin();
          FXMLLoader mainPage = new FXMLLoader(getClass().getResource("/app/MainWeb.fxml"));
          Scene mainScene = new Scene(mainPage.load());
          Stage mainStage = (Stage) signInButton.getScene().getWindow();
          mainStage.setScene(mainScene);
        }
        catch (IOException e) {
          logger.error("ERROR: {}", e.getMessage());
        }
      }
      else {
        status.setText("Login failed");
        logger.error("Authentication failed");
      }
    });

    networkClient.addUIListener(Message.SIGNUP_RESPONSE, packet -> {
      ConnectionRespondPayload response = (ConnectionRespondPayload) packet.getPayload();
      if (response.isSuccess()) {
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
    });

  }

  @FXML
  public void handleLogin() {
    logger.debug("DEBUG: Request signing in...");
    String username = getUserName.getText();
    String password = getPassword.getText();

    try {
      ConnectionRequestPayload loginData = new ConnectionRequestPayload(username, password);
      PacketMessage packet = new PacketMessage(Message.LOGIN_REQUEST, loginData);
      networkClient.sendPacket(packet);

      logger.info("INFO: Sent login request");
    } catch (IOException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  @FXML
  public void handleSignUp() {
    logger.debug("DEBUG: Signing up...");
    String username = getUserName.getText();
    String password = getPassword.getText();
    String repassword = confirmPassword.getText();
    String email = getEmail.getText();

    try {
      ConnectionRequestPayload signupData = new ConnectionRequestPayload(username, password, repassword, email);
      PacketMessage packet = new PacketMessage(Message.SIGNUP_REQUEST, signupData);
      networkClient.sendPacket(packet);

      logger.info("INFO: Sent signup request");
    } catch (IOException e) {
      logger.error("ERROR {}", e.getMessage());
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
