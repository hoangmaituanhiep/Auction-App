package app.services;

import app.dao.BidDAO;

public class BidService {
  private BidDAO bidDAO;

  public BidService(BidDAO bidDAO) {
    this.bidDAO = bidDAO;
    setupDatabase();
  }

  public void setupDatabase() {
    bidDAO.createTable();
  }

  public boolean updatePrice(int titemId, String username, double price, String timestamp) {
    return bidDAO.insertNewPrice(titemId, username, price, timestamp);
  }
}
