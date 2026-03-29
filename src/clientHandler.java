import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.file.*;

public class clientHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path indexPath = Paths.get("../webapp/index.html");

        String response = "";
        int statusCode = 200;

        try {
            String indexContent = Files.readString(indexPath);

            String newDiv = "<div id=\"testSite\">"
                          + "<h2>Server testing...</h2>";
            
            response = indexContent.replace("<div id=\"testSite\">", newDiv);
        }
        catch (IOException e){
            statusCode = 404;
            response = "<html><body><h1>404 - Cannot find index.html file</h1></body></html>";
            System.out.println("Error reading file: "+e.getMessage());
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}