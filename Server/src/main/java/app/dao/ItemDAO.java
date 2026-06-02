package app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.DatabaseConfig;
import app.functions.GenericItem;
import app.functions.Item;

public class ItemDAO{
  private static final Logger logger = LoggerFactory.getLogger(ItemDAO.class);
  
  private int lastestItemId;

  public void createTable() {
    logger.debug("DEBUG: Initializing item table database...");

    String table = "CREATE TABLE IF NOT EXISTS item(id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL, "
            + "details TEXT, "
            + "startingPrice REAL NOT NULL, "
            + "currentPrice REAL,"
            + "imagePath TEXT,"
            + "username TEXT) ";
    
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        Statement statement = connection.createStatement()) {
          statement.execute(table);
          logger.info("INFO: Successfully created table");
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public boolean insertItem(Item item) {
    logger.debug("DEBUG: Adding new item...");

    String insert = "INSERT INTO item(name, details, startingPrice, currentPrice, imagePath) VALUES(?, ?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

          preStatement.setString(1, item.getName());
          preStatement.setString(2, item.getDetail());
          preStatement.setDouble(3, item.getStartingPrice());
          preStatement.setDouble(4, item.getCurrentPrice());
          preStatement.setString(5, item.getImagePath());

          preStatement.executeUpdate();

          try (ResultSet key = preStatement.getGeneratedKeys()) {
            if (key.next()) {
              this.lastestItemId = key.getInt(1);
            }
          }

          logger.info("INFO: Inserted SQLite.");
          return true;
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public boolean setNewPrice(int id, double newPrice) {
    logger.debug("DEBUG: Setting new price for item_id {}", id);

    String set = "UPDATE item SET currentPrice = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(set)) {
          preStatement.setInt(2, id);
          preStatement.setDouble(1, newPrice);

          preStatement.executeUpdate();

          return true;
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public double getCurrentPrice(int id) {
    String getId = "SELECT currentPrice FROM item WHERE id  = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
    PreparedStatement preStatement = connection.prepareStatement(getId)) {
      preStatement.setInt(1, id);

      try (ResultSet resultSet = preStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getDouble("currentPrice");
        }
      }
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
    return 0;
  }

  public Item getItem(int id) {
    String query = "SELECT name, details, startingPrice, currentPrice, imagePath FROM item WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
      preStatement.setInt(1, id);

      try (ResultSet resultSet = preStatement.executeQuery()) {
        if (resultSet.next()) {
          String name = resultSet.getString("name");
          String details = resultSet.getString("details");
          double startingPrice = resultSet.getDouble("startingPrice");
          double currentPrice = resultSet.getDouble("currentPrice");

          String rawDetail = details;
          if (details != null) {
            int detailMarker = details.indexOf("\nStarting price:");
            if (detailMarker >= 0) {
              rawDetail = details.substring(0, detailMarker);
            }
          }

          String imagePath = resultSet.getString("imagePath");
          Item item = new GenericItem(name, rawDetail, startingPrice);

          item.addId(id);
          item.setNewPrice(currentPrice);
          item.setImagePath(imagePath);
          return item;
        }
      }
    } catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }

    return null;
  }

  public boolean addWinnerToItem(int id, String username) {
    String query = "UPDATE item SET username = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          preStatement.setString(1, username);
          preStatement.setInt(2, id);

          int updatedRows = preStatement.executeUpdate();

          return updatedRows > 0;
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public List<Item> getUserItems(String username) {
    String query = "SELECT * FROM item WHERE username = ?";
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          List<Item> items = new ArrayList<>();
          preStatement.setString(1, username);

          try (ResultSet result = preStatement.executeQuery()) {
            int id = result.getInt("id");

            items.add(getItem(id));
          }

          return items;
    }catch (Exception e) {
      logger.error("ERROR: {}", e.getMessage());
      return new ArrayList<>();
    }
  }

  public boolean updateItemInfo(int id, String name, String detail) {
    String query = "UPDATE item SET name = ? details = ? WHERE id = ?";
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
    PreparedStatement statement = connection.prepareStatement(query);) {
      statement.setString(1, name);
      statement.setString(2, detail);
      statement.setInt(3, id);

      int isUpdated = statement.executeUpdate();

      return isUpdated > 0;
    } catch (SQLException e) {
      logger.error("Error while update item: {}", e.getMessage());
      return false;
    }

  }

  public int getLastestItemId() {
    return lastestItemId;
  }
}