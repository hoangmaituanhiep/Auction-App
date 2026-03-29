import java.io.*;
import java.net.Socket;

public class clientHandler implements Runnable {
    private Socket clientSocket;

    public clientHandler(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                PrintWriter writer = new PrintWriter(os, true)) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        System.out.println("Testing: " + line);
                        writer.println("Echo: " + line);
                    }
                } 
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}