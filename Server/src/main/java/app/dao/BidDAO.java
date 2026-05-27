package app.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.DatabaseConfig;

public class BidDAO {
  private static final Logger logger = LoggerFactory.getLogger(BidDAO.class);

  public void createTable() {
    logger.debug("DEBUG: Initializing bid table database...");

    String table = "CREATE TABLE IF NOT EXISTS bidHistory(id INTERGER PRYMARY KEY AUTOINCREMENT, itemId INTERGER NOT NULL, username TEXT NOT NULL, price REAL NOT NULL, timestamps TEXT NOT NULL)";

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
}
