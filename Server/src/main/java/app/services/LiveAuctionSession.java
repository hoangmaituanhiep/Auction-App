package app.services;

import app.functions.Auction;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.AuctionTimeoutPayload;
import app.Client;
import app.Server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveAuctionSession {
  private static final Logger logger = LoggerFactory.getLogger(LiveAuctionSession.class);

  private Server server;
  private Auction auction;
  private Timer timer;
  private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> countDown;
  private long remainingTimeS;

  public LiveAuctionSession(Auction auction) {
    server = Server.getInstance();
    this.auction = auction;
    this.remainingTimeS = parseDuration(auction.getDuration())*60;
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
    logger.info("INFO: starting count down at {}", remainingTimeS);

    Runnable tick = () -> {
      if (remainingTimeS > 0) {
        remainingTimeS --;
      }
      else {
        AuctionTimeoutPayload payload = new AuctionTimeoutPayload(false, auction.getAuctionId());
        PacketMessage message = new PacketMessage(Message.AUCTION_TIMEOUT, payload);

        server.broadcast(message);

        countDown.cancel(false);
      }
    };

    countDown = schedule.scheduleAtFixedRate(tick, 0, 1, TimeUnit.SECONDS);
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
