package app;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private static int port = 0;//default port

    @Override
    public void start(Stage primaryStage) throws IOException{

        Thread serverThread = new Thread(() -> {
            Server server = Server.getInstance(port);
            try {
                server.listen();
            } catch (IOException e) {
                System.err.println("Failed to start server: " + e.getMessage());
            }
        });
        //Must mark a thread daemon before start, only daemon threads run when VTM start
        serverThread.setDaemon(true);//VTM exists when all threads running are deamon threads
        serverThread.start();

        //Get the FXML file to load on screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/signup.fxml"));
        Parent root = loader.load();//Load the scene into ram

        Scene scene = new Scene(root, 409, 478); //preheight and prewidth in signup pages

        primaryStage.setScene(scene);
        primaryStage.setTitle("Aution App");
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException {
        //get port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid port");
            }
        }
        Application.launch(args);//call start and pass port indirectly
    }
}
