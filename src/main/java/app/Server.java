package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Map<Integer, ClientHandler> clientHandlers;
    private static boolean isListening;
    private static boolean isAutioning;
    private static ExecutorService executors = Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;
    private static Server server;

    Server() {
        isAutioning=false;
        isListening=false;

        try {
            serverSocket = new ServerSocket(5000);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        clientHandlers = new HashMap<Integer, ClientHandler>();
    }
    Server(int port) {
        isAutioning=false;
        isListening=false;

        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        clientHandlers = new HashMap<Integer, ClientHandler>();
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }
    public static Server getInstance(int port) {
        if (server == null) {
            server = new Server(port);
        }
        return server;
    }

    public void listen() throws IOException{
        isListening = true;
        isAutioning = true;

        while(isListening) {
            Socket clientSocket = serverSocket.accept();
            Client client = new Client(clientSocket);
            executors.execute(new ClientHandler(client));
        }
    }
}