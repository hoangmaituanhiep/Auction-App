package app;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import app.dao.AuctionDAO;
import app.dao.BidDAO;
import app.dao.ItemDAO;
import app.dao.UserDAO;
import app.functions.Auction;
import app.functions.Seller;
import app.functions.User;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.AuctionDTO;
import app.payload.BidItemRequestPayload;
import app.payload.BidItemRespondPayload;
import app.payload.CancelAuctionRequest;
import app.payload.CancelAuctionResponse;
import app.payload.ConnectionRequestPayload;
import app.payload.ConnectionRespondPayload;
import app.payload.FetchDataResponsePayload;
import app.payload.NewAuctionRequest;
import app.payload.NewAuctionRespond;
import app.payload.OnlineUserResponse;
import app.payload.RegisterClientPayload;
import app.payload.SellItemRequestPayload;
import app.payload.SellItemRespondPayload;
import app.services.AuctionService;
import app.services.BidService;
import app.services.ConnectionService;
import app.services.ItemsService;
import app.services.LiveAuctionSession;

public class ClientHandler implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

  private Client client;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;
  private ConnectionService connectionService;
  private ItemsService itemsService;
  private AuctionService auctionService;
  private BidService bidService;
  private boolean isRunning;

  public ClientHandler(Client client) {
    UserDAO userDAO = new UserDAO();
    ItemDAO itemDAO = new ItemDAO();
    AuctionDAO auctionDAO = new AuctionDAO();
    BidDAO bidDAO = new BidDAO();

    this.bidService = new BidService(bidDAO);
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
            client.setUser(client.getUser().asSeller());
            boolean auctionSuccess = auctionService.addAuction(payload.getName(), payload.getDuration());

            NewAuctionRespond response;

            if (auctionSuccess) {
              int newId = auctionService.getAuctionId();

              // 1. Create the data object
              Auction newAuctionData = new Auction(payload.getName(),
                  payload.getDuration());
              newAuctionData.setAuctionId(newId);

              // 2. Wrap it in a Live Session
              LiveAuctionSession liveSession = new LiveAuctionSession(newAuctionData);

              // 3. Start the timer and add to Server
              liveSession.start();
              server.addLiveAuction(liveSession);

              response = new NewAuctionRespond(auctionSuccess, newId, payload.getName(), payload.getDuration());
              client.setUser(client.getUser().asSeller());
            } else {
              response = new NewAuctionRespond(auctionSuccess, "Failed to create new Auction");
            }
            PacketMessage message = new PacketMessage(Message.NEW_AUCTION_RESPOND, response);
            sendPacket(message);
            PacketMessage serverMessage = new PacketMessage(Message.NEW_AUCTION_BROADCAST, response);
            server.broadcast(serverMessage);
            break;

          case JOIN_AUCTION:
            client.setUser(client.getUser().asBidder());
            try {
              RegisterClientPayload registerClientPayload = (RegisterClientPayload) packetMessage.getPayload();

              server.joinAution(registerClientPayload.getAuctionId(), client);
              Auction auction = server.getAuction(registerClientPayload.getAuctionId());
              AuctionDTO auctionDTO = new AuctionDTO(auction.getAuctionId(), auction.getName(), auction.getDuration(), auction.getStep(), auction.getStatus(), auction.getItemId());
              sendPacket(new PacketMessage(Message.JOIN_AUCTION_SUCCEEDED, auctionDTO));
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
              } else {
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
                client.setUser(User.createNewUser(loginData.getUsername()));
              } else {
                responseData = new ConnectionRespondPayload(isSuccess, "Authentication failed.");
              }

              PacketMessage responsePacket = new PacketMessage(Message.LOGIN_RESPONSE, responseData);
              sendPacket(responsePacket);
            } catch (IOException e) {
              logger.error("ERROR: {}", e.getMessage());
            }
            break;

          case SIGNUP_REQUEST:

            ConnectionRequestPayload signUpData = (ConnectionRequestPayload) packetMessage.getPayload();
            boolean isSuccess = connectionService.authenticate(signUpData.getUsername(), signUpData.getPassword(),
                signUpData.getConfirmPassword(), signUpData.getEmail());

            ConnectionRespondPayload responseData;
            if (isSuccess) {
              responseData = new ConnectionRespondPayload(isSuccess);
              client.setUser(User.createNewUser(signUpData.getUsername()));
            } else {
              responseData = new ConnectionRespondPayload(isSuccess, "Authentication failed");
            }

            PacketMessage responsePacket = new PacketMessage(Message.SIGNUP_RESPONSE, responseData);
            try {
              sendPacket(responsePacket);
            }
            catch (IOException e) {
              logger.error("ERROR: {}", e);
            }

            break;

          case ONLINE_USER_REQUEST:
            logger.info("INFO: Fetching user's data");
            List<String> onlineUsers = new ArrayList<>();
            for (ClientHandler clientHandlerItem : server.getClientHanlders().values()) {
              User clientUser = clientHandlerItem.getClient().getUser();
              if (clientUser != null) {
                String name = clientUser.getUserName();
                if (name != null && !name.isEmpty()) {
                  onlineUsers.add(name);
                }
              }
            }
            OnlineUserResponse onlineResponse = new OnlineUserResponse(onlineUsers);
            PacketMessage onlineMessage = new PacketMessage(Message.ONLINE_USER_RESPONSE, onlineResponse);
            try {
              sendPacket(onlineMessage);
            } catch (IOException e) {
              logger.error("ERROR: {}", e);
            }
            break;

          case NEW_PRICE_REQUEST:
            try {
              if (client.getUser() instanceof Seller) {
                logger.warn("WARNING: Seller bided???");
                break;
              }
              BidItemRequestPayload request = (BidItemRequestPayload) packetMessage.getPayload();
              int Id = request.getId();
              String bidderName = request.getUserName();

              LiveAuctionSession session = null;
              for (LiveAuctionSession s : server.getLiveAuction().values()) {
                if (s.getAuction().getItemId().contains(Id)) {
                  session = s;
                  break;
                }
              }

              boolean bidSuccess;
              if (session != null) {
                bidSuccess = session.placeBid(client, Id, request.getPrice(), bidderName);
              }
              else {
                bidSuccess = itemsService.setNewPrice(Id, request.getPrice());
              }
              
              BidItemRespondPayload respond;

              if (bidSuccess) {
                respond = new BidItemRespondPayload(Id, bidSuccess);
                BidItemRequestPayload broadcastRespond = new BidItemRequestPayload(Id, bidderName,  request.getPrice());
                PacketMessage globalMess = new PacketMessage(Message.NEW_PRICE_BROADCAST, broadcastRespond);

                server.broadcast(globalMess);
              } else {
                respond = new BidItemRespondPayload(Id, bidSuccess, "The new price offer failed.");
              }

              PacketMessage packet = new PacketMessage(Message.NEW_PRICE_RESPOND, respond);
              sendPacket(packet);
            } catch (IOException e) {
              logger.error("ERROR: {}", e.getMessage());
            }
            break;
          
          case CANCEL_AUCTION_REQUEST:
            CancelAuctionRequest request = (CancelAuctionRequest) packetMessage.getPayload();
            int id = request.getAuctionId();
            server.removeLiveAuction(id);
            boolean cancelSuccess = auctionService.updateStatus(id, "CANCELED");

            CancelAuctionResponse cancelResponse;
            if (cancelSuccess) {
              cancelResponse = new CancelAuctionResponse(cancelSuccess, id);
            }
            else {
              cancelResponse = new CancelAuctionResponse(cancelSuccess, "ERROR: failed to cancel auction");
            }
            PacketMessage cancelMessage = new PacketMessage(Message.CANCEL_AUCTION_RESPONSE, cancelResponse);

            server.broadcast(cancelMessage);
            break;

          case FETCH_DATA_REQUEST:
            List<LiveAuctionSession> liveSessions = new ArrayList<>(server.getLiveAuction().values());
            List<AuctionDTO> ongoingAuctions = new ArrayList<>();
            for (LiveAuctionSession liveSession : liveSessions) {
              Auction auction = liveSession.getAuction();
              ongoingAuctions.add(new AuctionDTO(auction.getAuctionId(), auction.getName(), auction.getDuration(), auction.getStep(), auction.getStatus(), auction.getItemId()));
            }

            FetchDataResponsePayload fetchResponse = new FetchDataResponsePayload(ongoingAuctions);
            PacketMessage fectchMessage = new PacketMessage(Message.FETCH_DATA_RESPONSE, fetchResponse);

            try {
              sendPacket(fectchMessage);
            }
            catch (IOException exception) {
              logger.error("ERROR: {}", exception);
            }
            break;

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

  public synchronized void sendPacket(PacketMessage message) throws IOException {
    objectOutputStream.writeObject(message);
    objectOutputStream.flush();
  }
}