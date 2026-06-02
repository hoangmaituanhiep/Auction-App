package app.services;

import java.util.List;

import app.dao.BidDAO;
import app.functions.BidTransaction;

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

  public String getWinner(int itemId) {
    return bidDAO.getWinner(itemId);
  }

  public List<BidTransaction> getHistory(int itemId) {
    return bidDAO.getHistory(itemId);
  }
}
