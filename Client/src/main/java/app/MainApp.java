package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
  private static int port = 8080;// default port
  private static String host = "127.0.0.1"; // default host

  public static int getPort() {
    return port;
  }
  
  public static String getHost() {
    return host;
  }

  public static void setHost(String newhost) {
    host = newhost;
  }


  @Override
  public void start(Stage primaryStage) throws IOException {
    // Get the FXML file to load on screen
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/MainWeb.fxml"));
    Parent root = loader.load();// Load the scene into ram

    Scene scene = new Scene(root, 1280, 720); // preheight and prewidth in MainWeb pages

    primaryStage.setScene(scene);
    primaryStage.setTitle("Auction App");
    primaryStage.show();
  }

  public static void main(String[] args) throws IOException {
    // get port and host
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.err.println("Invalid port");
      }
    }
    if (args.length > 1) {
        host = args[1];
    }
    Application.launch(args);// call start and pass port indirectly
  }
}
