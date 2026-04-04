package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class server {
    private static Map<Integer, clientHandler> clientHandlers;
    private static boolean isListening;
    private static boolean isAutioning;
    private static ExecutorService executors = new Executors.newFixedThreadPool(10);
    private static ServerSocket serverSocket;

    server() {
        isAutioning=false;
        isListening=false;

        try {
            serverSocket = new ServerSocket(5000);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        clientHandlers = new HashMap<Integer, clientHandler>();
    }

    public server getInstance() {
        if (server == null) {
            server = new server();
        }
        return server;
    }
    public server getInstance(int port) {
        if (server == null) {
            server = new server(port);
        }
        return server;
    }
}