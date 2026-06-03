package app;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        // This redirects the startup flow to your actual JavaFX MainApp
        try {
          MainApp.main(args);
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}