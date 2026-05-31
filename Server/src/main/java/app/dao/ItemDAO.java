package app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.DatabaseConfig;
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
            + "currentPrice REAL) ";
    
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

    String insert = "INSERT INTO item(name, details, startingPrice, currentPrice) VALUES(?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
          ResultSet key = preStatement.getGeneratedKeys();

          preStatement.setString(1, item.getName());
          preStatement.setString(2, item.getDetail());
          preStatement.setDouble(3, item.getStartingPrice());
          preStatement.setDouble(4, item.getCurrentPrice());

          preStatement.executeUpdate();

          if (key.next()) {
            this.lastestItemId = key.getInt(1);
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
    String query = "SELECT name, details, startingPrice, currentPrice FROM item WHERE id = ?";

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

          Item item = new Item(name, rawDetail, startingPrice) {
            @Override
            public String toString() {
              return getName();
            }
          };

          item.addId(id);
          item.setNewPrice(currentPrice);
          return item;
        }
      }
    } catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }

    return null;
  }

  public int getLastestItemId() {
    return lastestItemId;
  }
}