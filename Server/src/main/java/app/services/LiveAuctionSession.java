package app.services;

import app.functions.Auction;
import app.Client;

import java.util.Timer;
import java.util.TimerTask;

public class LiveAuctionSession {
  private Auction auction;
  private Timer timer;
  private long remainingTimeMillis;

  public LiveAuctionSession(Auction auction) {
    this.auction = auction;
    this.remainingTimeMillis = parseDuration(auction.getDuration());
  }

  // Basic utility to parse a duration string like "2h", "30m", or fallback
  private long parseDuration(String duration) {
    try {
      if (duration.endsWith("m")) {
        return Long.parseLong(duration.replace("m", "")) * 60 * 1000;
      } else if (duration.endsWith("h")) {
        return Long.parseLong(duration.replace("h", "")) * 60 * 60 * 1000;
      }
      return Long.parseLong(duration) * 1000; // Assume seconds if no suffix
    } catch (Exception e) {
      return 60 * 60 * 1000; // Default 1 hour
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
