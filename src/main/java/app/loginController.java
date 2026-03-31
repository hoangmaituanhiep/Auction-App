package app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

public class loginController {
    @FXML private TextField getUserName;
    @FXML private PasswordField getPassword;
    @FXML private Label status;

    private LoginService service = new LoginService();

    @FXML
    private void handleLogin() {
        if(service.authenticate(getUserName.getText(), getPassword.getText())) {
            status.setText("Successfull!");
        }
        else {
            status.setText("Failed to login.");
        }
    }
}
