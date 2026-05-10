package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.functions.Auction;
import app.packets.PacketMessage;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  
  private static Map<String, ClientHandler> clientHandlers;
  private static Map<Integer, Auction> auctions;
  private static boolean isListening;
  private static boolean isAutioning;
  private static ExecutorService executors = Executors.newFixedThreadPool(10);
  private static ServerSocket serverSocket;
  private static Server server;

  Server() {
    isAutioning = false;
    isListening = false;

    try {
      serverSocket = new ServerSocket(0);
    } catch (IOException e) {
      e.printStackTrace();
    }

    clientHandlers = new HashMap<String, ClientHandler>();
    auctions = new HashMap<Integer, Auction>();
  }

  Server(int port) {
    isAutioning = false;
    isListening = false;

    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }

    clientHandlers = new HashMap<String, ClientHandler>();
    auctions = new HashMap<Integer, Auction>();
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
    isAutioning = true;

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
    String clientID = client.getInetSocketAddress().getHostName();

    if (clientHandlers.containsKey(clientID)) {
      clientHandlers.get(clientID).stopRunning();
    } else {
      System.err.println("Cannot find clients");
    }
  }

  public void joinAution(int auctionId, Client client) {
    if (auctions.containsKey(auctionId)) {
      auctions.get(auctionId).addClient(client);
    } else {
      System.err.println("Already in the auction.");
    }
  }
}