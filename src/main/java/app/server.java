package app;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class server {
    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ServerSocket server = new ServerSocket(5000);

        while (true) {
            Socket client = server.accept();
            System.out.println("New client: " + client.getInetAddress());

            executor.execute(new clientHandler(client));
        }
    }
}