package app.services;

import app.functions.Auction;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.AuctionTimeoutPayload;
import app.Client;
import app.Server;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveAuctionSession {
  private static final Logger logger = LoggerFactory.getLogger(LiveAuctionSession.class);

  private Server server;
  private Auction auction;
  private Timer timer;
  private long remainingTimeMillis;

  public LiveAuctionSession(Auction auction) {
    server = Server.getInstance();
    this.auction = auction;
    this.remainingTimeMillis = parseDuration(auction.getDuration());
  }

  private long parseDuration(String duration) {
    try {
      return Long.parseLong(duration);
    } catch (Exception e) {
      logger.error("ERROR: Invalid duration.");
      return 0;
    }
  }

  public void start() {
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        remainingTimeMillis -= 1000;
        if (remainingTimeMillis <= 0) {
          auction.setStatus("FINISHED");
          timer.cancel();
          // Broadcast finished
          AuctionTimeoutPayload timeoutResponse = new AuctionTimeoutPayload(true, auction.getAuctionId());
          PacketMessage message = new PacketMessage(Message.AUCTION_TIMEOUT, timeoutResponse);

          server.broadcast(message);
        } else {
          // Broadcast tick
        }
      }
    }, 1000, 1000);
  }

  public synchronized boolean placeBid(Client client, double bid) {
    return false;
  }
  
  public Auction getAuction() {
    return auction;
  }
  
  public void addClient(Client client) {
    auction.addClient(client);
  }
}
