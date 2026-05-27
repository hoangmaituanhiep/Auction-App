package app.services;

import app.functions.Auction;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.AuctionTimeoutPayload;
import app.payload.WinnerPayload;
import app.Client;
import app.Server;
import app.dao.AuctionDAO;
import app.dao.BidDAO;
import app.dao.ItemDAO;

import java.util.Timer;
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
  private ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> countDown;
  private long remainingTimeS;
  private AuctionService auctionService;
  private BidService bidService;

  public LiveAuctionSession(Auction auction) {
    server = Server.getInstance();
    this.auction = auction;
    this.remainingTimeS = parseDuration(auction.getDuration()) * 60;

    AuctionDAO auctionDAO = new AuctionDAO();
    BidDAO bidDAO = new BidDAO();
    auctionService = new AuctionService(auctionDAO);
    bidService = new BidService(bidDAO);
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
        remainingTimeS--;
      } else {
        logger.info("INFO: Count down finished.");
        auctionService.updateStatus(auction.getAuctionId(), "FINISHED");

        for (int itemId : auction.getItemId()) {
          String winner = bidService.getWinner(itemId);
          if (winner != null) {
            WinnerPayload winnerPayload = new WinnerPayload(winner);
            PacketMessage message = new PacketMessage(Message.WINNER_RESPOND, winnerPayload);

            server.broadcast(message);
          }
        }
        AuctionTimeoutPayload payload = new AuctionTimeoutPayload(true, auction.getAuctionId());
        PacketMessage message = new PacketMessage(Message.AUCTION_TIMEOUT, payload);

        server.broadcast(message);
        server.removeLiveAuction(auction.getAuctionId());

        countDown.cancel(false);
      }
    };

    countDown = schedule.scheduleAtFixedRate(tick, 0, 1, TimeUnit.SECONDS);
  }

  public synchronized boolean placeBid(Client client, int itemId, double bid, String bidderName) {
    if (remainingTimeS <= 0)
      return false;
    ItemsService itemsService = new ItemsService(new ItemDAO());
    boolean bidSuccess = itemsService.setNewPrice(itemId, bid);

    if (bidSuccess) {
      bidService.updatePrice(itemId, bidderName, bid, String.valueOf(System.currentTimeMillis()));

      if (this.remainingTimeS < 10) {
        this.remainingTimeS += 60;
        server.broadcast(new PacketMessage(Message.ANTI_SNIPPING_RESPOND, null));
        logger.info("INFO: Some one bid in the last 10 second...");
      }
    }
    return bidSuccess;
  }

  public Auction getAuction() {
    return auction;
  }

  public void addClient(Client client) {
    auction.addClient(client);
  }
}
