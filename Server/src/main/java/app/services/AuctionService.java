package app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.dao.AuctionDAO;

public class AuctionService {
  private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

  private AuctionDAO auctionDAO;

  public AuctionService(AuctionDAO auctionDAO) {
    this.auctionDAO = auctionDAO;
    setupDatabase();
  }

  public void setupDatabase() {
    auctionDAO.createTable();
  }

  public boolean addAuction(String name, String duration) {
    if (name.equals(null) || name.isEmpty()) {
      logger.error("ERROR: Pls add Auction's name");
    }
    if (duration.equals(null) || duration.isEmpty()) {
      logger.error("ERROR: Add duration pls");
    }

    if (auctionDAO.addAuction(name, duration)) {
      return true;
    }
    return false;
  }

  public boolean updateStatus(int id, String status) {
    return auctionDAO.changeAuctionStatus(id, status);
  }

  public boolean updateDuration(int auctionId, String duration) {
    if (auctionId<0) {
      logger.error("ERROR: Invalid id");
    } 
    if (duration.equals(null) || duration.isEmpty()) {
      logger.error("ERROR: Invalid duration");
    }

    if (auctionDAO.updateDuration(duration, auctionId)) {
      return true;
    }
    return false;
  }
  
  public int getAuctionId() {
    return auctionDAO.getLatestAuctionId();
  }
}
