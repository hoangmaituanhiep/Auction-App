package app;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import app.dao.AuctionDAO;
import app.dao.ItemDAO;
import app.dao.UserDAO;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.ConnectionRequestPayload;
import app.payload.ConnectionRespondPayload;
import app.payload.NewAuctionRequest;
import app.payload.NewAuctionRespond;
import app.payload.OnlineUserResponse;
import app.payload.RegisterClientPayload;
import app.payload.SellItemRequestPayload;
import app.payload.SellItemRespondPayload;
import app.services.AuctionService;
import app.services.ConnectionService;
import app.services.ItemsService;

public class ClientHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  private Client client;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;
  private ConnectionService connectionService;
  private ItemsService itemsService;
  private AuctionService auctionService;
  private boolean isRunning;

  public ClientHandler(Client client) {
    UserDAO userDAO = new UserDAO();
    ItemDAO itemDAO = new ItemDAO();
    AuctionDAO auctionDAO = new AuctionDAO();

    this.connectionService = new ConnectionService(userDAO);
    this.itemsService = new ItemsService(itemDAO);
    this.auctionService = new AuctionService(auctionDAO);

    this.client = client;
    isRunning = true;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public ObjectInputStream getObjectInputStream() {
    return objectInputStream;
  }

  public void setObjectInputStream(ObjectInputStream objectInputStream) {
    this.objectInputStream = objectInputStream;
  }

  public ObjectOutputStream getObjectOutputStream() {
    return objectOutputStream;
  }

  public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
    this.objectOutputStream = objectOutputStream;
  }

  public boolean status() {
    return isRunning;
  }

  @Override
  public void run() {
    isRunning = true;
    Server server = Server.getInstance();

    try {
      objectOutputStream = new ObjectOutputStream(client.getSocket().getOutputStream());
      objectOutputStream.flush(); // Flush the output stream immediately
      objectInputStream = new ObjectInputStream(client.getSocket().getInputStream());
      sendPacket((new PacketMessage(Message.WELCOME, null)));
    } catch (IOException e) {
      isRunning = false;
      e.printStackTrace();
    }

    while (isRunning) {
      try {
        PacketMessage packetMessage = (PacketMessage) objectInputStream.readObject();

        switch (packetMessage.getType()) {
          case NEW_AUCTION_REQUEST:
            NewAuctionRequest payload = (NewAuctionRequest) packetMessage.getPayload();
            boolean auctionSuccess = auctionService.addAuction(payload.getName(), payload.getDuration());

            NewAuctionRespond response;
            
            if (auctionSuccess) {
              int newId = auctionService.getAuctionId();
              
              // 1. Create the data object
              app.functions.Auction newAuctionData = new app.functions.Auction(payload.getName(), payload.getDuration());
              newAuctionData.setAuctionId(newId);
              
              // 2. Wrap it in a Live Session
              app.services.LiveAuctionSession liveSession = new app.services.LiveAuctionSession(newAuctionData);
              
              // 3. Start the timer and add to Server
              liveSession.start();
              server.addLiveAuction(liveSession);

              response = new NewAuctionRespond(auctionSuccess, newId, payload.getName(), payload.getDuration());
            }
            else {
              response = new NewAuctionRespond(auctionSuccess, "Failed to create new Auction");
            }
            PacketMessage message = new PacketMessage(Message.NEW_AUCTION_RESPOND, response);
            sendPacket(message);
            PacketMessage serverMessage = new PacketMessage(Message.NEW_AUCTION_BROADCAST, response);
            server.broadcast(serverMessage);
            break;

          case JOIN_AUCTION:
            try {
              joinAution(packetMessage);
            } catch (Exception e) {
              logger.error("ERROR: Unrecognized Packet");
            }
            break;

          case SEND_ITEM_REQUEST:
            try {
              SellItemRequestPayload sellData = (SellItemRequestPayload) packetMessage.getPayload();
              boolean addSuccess = itemsService.addItems(sellData.getItem());

              SellItemRespondPayload respondPayload;
              if (addSuccess) {
                 respondPayload = new SellItemRespondPayload(addSuccess, itemsService.getLastestItemId());
                 SellItemRequestPayload globalRequest = new SellItemRequestPayload(sellData.getItem());
                 PacketMessage globalMessage = new PacketMessage(Message.BROADCAST_NEW_ITEM, globalRequest);

                 server.broadcast(globalMessage);
                 logger.info("INFO: Notice other online clients");
              }
              else {
                respondPayload = new SellItemRespondPayload(addSuccess, "ERROR: Cannot insert Item into DB");
              }

              PacketMessage respondMessage = new PacketMessage(Message.SEND_ITEM_RESPOND, respondPayload);
              sendPacket(respondMessage);
            } catch (Exception e) {
              logger.error("ERROR: Unrecognized Packet");
            }
            break;

          case LOGIN_REQUEST:
            try {
              ConnectionRequestPayload loginData = (ConnectionRequestPayload) packetMessage.getPayload();
              boolean isSuccess = connectionService.authenticate(loginData.getUsername(), loginData.getPassword());
              
              ConnectionRespondPayload responseData;
              if (isSuccess) {
                responseData = new ConnectionRespondPayload(isSuccess);
              }
              else {
                responseData = new ConnectionRespondPayload(isSuccess, "Authentication failed.");
              }

              PacketMessage responsePacket = new PacketMessage(Message.LOGIN_RESPONSE, responseData);
              objectOutputStream.writeObject(responsePacket);
            }
            catch (IOException e) {
              logger.error("ERROR: {}", e.getMessage());
            }
            break;

          case SIGNUP_REQUEST:
            try {
              ConnectionRequestPayload signUpData = (ConnectionRequestPayload) packetMessage.getPayload();
              boolean isSuccess = connectionService.authenticate(signUpData.getUsername(), signUpData.getPassword(), signUpData.getConfirmPassword(), signUpData.getEmail());

              ConnectionRespondPayload responseData;
              if (isSuccess) {
                responseData = new ConnectionRespondPayload(isSuccess);
              }
              else {
                responseData = new ConnectionRespondPayload(isSuccess, "Authentication failed");
              }

              PacketMessage responsePacket = new PacketMessage(Message.SIGNUP_RESPONSE, responseData);
              objectOutputStream.writeObject(responsePacket);
            } catch (IOException e) {
              logger.error("ERROR: {}", e.getMessage());
            }
            break;

          case ONLINE_USER_REQUEST:
            logger.info("INFO: Fetching user's data");
            List<String> onlineUsers = new ArrayList<>();
            for (ClientHandler clientHandler : server.getClientHanlders().values()) {
              onlineUsers.add(clientHandler.getClient().getUser().getUserName());
            }
            OnlineUserResponse onlineResponse = new OnlineUserResponse(onlineUsers);
            PacketMessage onlineMessage = new PacketMessage(Message.ONLINE_USER_RESPONSE, onlineResponse);
            try {
              sendPacket(onlineMessage);
            }
            catch (IOException e) {
              logger.error("ERROR: {}", e);
            }
            

          default:
            break;
        }
      } catch (ClassNotFoundException | IOException e) {
        logger.info("INFO: Client disconnected or connection lost.");
        try {
          stopRunning();
        } catch (IOException ex) {
          logger.error("ERROR: Failed to clean up client connection.");
        }
        break;
      }
    }
  }

  public void stopRunning() throws IOException {
    isRunning = false;

    client.getSocket().close();

    Server server = Server.getInstance();
    server.getClientHanlders().remove(getClient().getSocket().getRemoteSocketAddress().toString());
  }

  public void sendPacket(PacketMessage message) throws IOException {
    objectOutputStream.writeObject(message);
    objectOutputStream.flush();
  }

  public void joinAution(PacketMessage packetMessage) throws IOException {
    if (packetMessage.getPayload() instanceof RegisterClientPayload) {
      Server server = Server.getInstance();
      RegisterClientPayload registerClientPayload = (RegisterClientPayload) packetMessage.getPayload();

      server.joinAution(registerClientPayload.getAuctionId(), client);
    } else {
      logger.error("ERROR: Expected RegisterClientPayload");
    }
  }
}