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
import app.functions.BidTransaction;

public class BidDAO {
  private static final Logger logger = LoggerFactory.getLogger(BidDAO.class);

  public void createTable() {
    logger.debug("DEBUG: Initializing bid table database...");

    String table = "CREATE TABLE IF NOT EXISTS bidHistory(id INTEGER PRIMARY KEY AUTOINCREMENT, itemId INTEGER NOT NULL, username TEXT NOT NULL, price REAL NOT NULL, timestamps TEXT NOT NULL)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getBidUrl());
        Statement statement = connection.createStatement()) {
          statement.execute(table);
          
          logger.info("INFO: Created bid table.");
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public boolean insertNewPrice(int itemId, String username, double price, String timestamp) {
    String query = "INSERT INTO bidHistory(itemId, username, price, timestamps) VALUES(?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getBidUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          preStatement.setString(2, username);
          preStatement.setString(4, timestamp);
          preStatement.setInt(1, itemId);
          preStatement.setDouble(3, price);
          
          return true;
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e);
      return false;
    }
  }

  public String getWinner(int itemId) {
    String query = "SELECT username, price FROM bidHistory WHERE itemId = ? ORDER BY price DESC LIMIT 1";
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getBidUrl());
      PreparedStatement preStatement = connection.prepareStatement(query)) {
        preStatement.setInt(1, itemId);
        try (ResultSet result = preStatement.executeQuery()){
          if (result.next()) {
            return result.getString("username");
          }
        }
      }
    catch(SQLException e) {
      logger.error("ERROR: {}", e);
    }
    return null;
  }

  public List<BidTransaction> getHistory(int itemId) {
    String query = "SELECT username, price, timestamps FROM bidHistory WHERE itemId = ? ORDER BY id ASC";
    List<BidTransaction> history = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getBidUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          preStatement.setInt(1, itemId);
          try (ResultSet result = preStatement.executeQuery()) {
            while (result.next()) {
              String username = result.getString("username");
              double price = result.getDouble("price");
              String ts = result.getString("timestamps");
              history.add(new BidTransaction(username, price, ts));
            }
          }
    } catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
    return history;
  }
}
