package app;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Client client;
    private boolean isRunning;

    public ClientHandler(Client client) {
        this.client = client;
        isRunning = true;
    }

    @Override
    public void run() {
        
    }
}