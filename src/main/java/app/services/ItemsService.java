package app.services;

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
    if (item.getDuration() < 0) {
      logger.error("ERROR: Timer must be greater than 0 and be an int");
    }

    return itemDAO.insertItem(item);
  }
}
