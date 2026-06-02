package app.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.dao.ItemDAO;
import app.functions.Item;

public class ItemsService {
  private static final Logger logger = LoggerFactory.getLogger(ItemsService.class);

  private final ItemDAO itemDAO;

  public ItemsService(ItemDAO itemDAO) {
    this.itemDAO = itemDAO;
    setupDatabase();
  }

  public void setupDatabase() {
    itemDAO.createTable();
  }

  public boolean addItems(Item item) {
    if (item.getName() == null || item.getName().isEmpty()) {
      logger.error("ERROR: Item's name must not be null");
      return false;
    }
    if (item.getStartingPrice() < 0) {
      logger.error("ERROR: Starting price must be greater than 0.");
      return false;
    }

    return itemDAO.insertItem(item);
  }

  public boolean setNewPrice(int id, double newPrice) {
    if (itemDAO.getCurrentPrice(id) > newPrice) {
      logger.debug("DEBUG: New price must be higher than current price.");
      return false;
    }

    return itemDAO.setNewPrice(id, newPrice);
  }

  public boolean addWinnerToItem(int id, String username) {
    if (id >= 0 && !username.isEmpty() && !username.equals(null)) {
      return itemDAO.addWinnerToItem(id, username);
    }
    return false;
  }

  public List<Item> getItemByUserName(String username) {
    return itemDAO.getUserItems(username);
  }

  public Item getItemById(int Id) {
    return itemDAO.getItem(Id);
  }

  public int getLastestItemId() {
    return itemDAO.getLastestItemId();
  }
}
