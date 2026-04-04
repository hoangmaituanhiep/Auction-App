package app;

import java.io.IOException;
import javafx.application.Application;

public class MainApp extends Application {
    private static int port = 0;

    @Override
    public void start(Stage primaryStage) {

        Thread serverThread = new Thread(() -> {
            Server server = Server.getInstance(port);
            server.listen();
        });
        serverThread.setDaemon(true);
        serverThread.start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/signup.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 409, 478); //preheight and prewidth in signup pages

        primaryStage.setScene(scene);
        primaryStage.setTitle("Aution App");
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid port");
            }
        }
        Application.launch(args);
    }
}
