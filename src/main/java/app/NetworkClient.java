package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import app.packets.Message;
import app.packets.PacketMessage;
import app.utils.PacketListener;
import javafx.application.Platform;

public class NetworkClient {
  private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);

  private static NetworkClient instance;
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private Map<Message, List<PacketListener>> listeners = new ConcurrentHashMap<>();

  private void routePacket(PacketMessage packet) {
    List<PacketListener> callbacks = listeners.get(packet.getType());

    if (callbacks != null) {
      for (PacketListener callback : callbacks) {
        callback.onReceivingPacket(packet);
      }
    }
  }

  public static NetworkClient getInstance() {
    if (instance == null) instance = new NetworkClient();
    return instance;
  }

  public void addUIListener(Message type, PacketListener listener) {
    addListener(type, packet -> {
      Platform.runLater(() -> {
        listener.onReceivingPacket(packet);
      });
    });
  }

  public void addListener(Message type, PacketListener listener) {
    listeners.computeIfAbsent(type, key -> new ArrayList<>()).add(listener);
  }

  public void startListening() {
    new Thread(() -> {
      try {
        while (true) {
          PacketMessage packet = (PacketMessage) in.readObject();
          routePacket(packet);
        }
      }
      catch(IOException e) {
        logger.debug("DEBUG: Disconnected");
      }
      catch (ClassNotFoundException e) {
        logger.error("ERROR: Cant read packet");
      }
    }).start();
  }

  public void connect(String host, int port) throws IOException{
    socket = new Socket(host, port);
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());

    startListening();
  }

  public void sendPacket(PacketMessage packet) throws IOException {
    if (out != null) {
      out.writeObject(packet);
      out.flush();
    }
  }

  public void close() {
    logger.debug("DEBUG: Closing client...");
    try {
      if (socket != null) socket.close();
      if (in != null) in.close();
      if (out != null) out.close();
    }
    catch (IOException e) {
      logger.error("ERROR: " + e.getMessage());
    }
  }
}
