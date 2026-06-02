package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.services.LiveAuctionSession;
import app.functions.Auction;
import app.packets.PacketMessage;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  
  private static Map<String, ClientHandler> clientHandlers;
  private static Map<Integer, LiveAuctionSession> liveAuctions;
  private static boolean isListening;
  private static ExecutorService executors = Executors.newFixedThreadPool(10);
  private static ServerSocket serverSocket;
  private static Server server;

  Server() {
    isListening = false;

    try {
      serverSocket = new ServerSocket(0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    clientHandlers = new ConcurrentHashMap<String, ClientHandler>();
    liveAuctions = new HashMap<Integer, LiveAuctionSession>();
  }

  Server(int port) {
    isListening = false;

    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    clientHandlers = new HashMap<String, ClientHandler>();
    liveAuctions = new HashMap<Integer, LiveAuctionSession>();
  }

  public Map<String, ClientHandler> getClientHanlders() {
    return clientHandlers;
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

  public void listen() throws IOException {
    isListening = true;

    while (isListening) {
      Socket clientSocket = serverSocket.accept();
      Client client = new Client(clientSocket);
      ClientHandler clientThread = new ClientHandler(client);
      clientHandlers.put(client.getSocket().getRemoteSocketAddress().toString(), clientThread);
      executors.execute(clientThread);
    }
  }

  public void broadcast(PacketMessage packet) {
    for (ClientHandler handler : clientHandlers.values()) {
      try {
        handler.sendPacket(packet);
      } catch (IOException e) {
        logger.error("ERROR: {}", e.getMessage());
      }
    }
  }

  public void removeClient(Client client) throws IOException {
    if (client == null || client.getSocket() == null) {
      logger.error("ERROR: Cannot remove null client or client with null socket");
      return;
    }
    String clientID = client.getSocket().getRemoteSocketAddress().toString();

    if (clientHandlers.containsKey(clientID)) {
      clientHandlers.get(clientID).stopRunning();
    } else {
      logger.error("ERROR: Cannot find clients");
    }
  }

  public void joinAution(int auctionId, Client client) {
    if (liveAuctions.containsKey(auctionId)) {
      liveAuctions.get(auctionId).addClient(client);
    } else {
      logger.error("ERROR: Already in the auction.");
    }
  }

  public void quitAuction(int auctionId, Client client) {
    if (liveAuctions.containsKey(auctionId)) {
      liveAuctions.get(auctionId).removeClient(client);
    }
  }

  public void addLiveAuction(LiveAuctionSession session) {
    liveAuctions.put(session.getAuction().getAuctionId(), session);
  }

  public void removeLiveAuction(int auctionId) {
    LiveAuctionSession session = liveAuctions.get(auctionId);
    if (session != null) {
      session.stop();
      session.getAuction().setStatus("CANCELED");
      liveAuctions.remove(auctionId);
    }
  }

  public Map<Integer, LiveAuctionSession> getLiveAuction() {
    return liveAuctions;
  }

  public Auction getAuction(int id) {
    return liveAuctions.get(id).getAuction();
  }

  public Client findClientByUsername(String username) {
    for (ClientHandler client : clientHandlers.values()) {
      if (client.getClient() != null && 
          client.getClient().getUser() != null && 
          client.getClient().getUser().getUserName() != null && 
          client.getClient().getUser().getUserName().equals(username)) {
        return client.getClient();
      }
    }
    return null;
  }
}