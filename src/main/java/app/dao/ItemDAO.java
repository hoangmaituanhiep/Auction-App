package app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.DatabaseConfig;
import app.functions.Item;

public class ItemDAO{
  private static final Logger logger = LoggerFactory.getLogger(ItemDAO.class);

  public void createTable() {
    logger.debug("DEBUG: Initializing item table database...");

    String table = "CREATE TABLE IF NOT EXISTS items(id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL, "
            + "details TEXT, "
            + "startingPrice REAL NOT NULL, "
            + "currentPrice REAL, "
            + "duration INTEGER NOT NULL)";
    
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

    String insert = "INSERT INTO items(name, details, startingPrice, currentPrice, duration) VALUES(?, ?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getItemsUrl());
        PreparedStatement preStatement = connection.prepareStatement(insert)) {
          preStatement.setString(1, item.getName());
          preStatement.setString(2, item.getDetail());
          preStatement.setDouble(3, item.getStartingPrice());
          preStatement.setDouble(4, item.getCurrentPrice());
          preStatement.setInt(5, item.getDuration());

          logger.info("INFO: Inserted SQLite.");

          int rowsAffected = preStatement.executeUpdate(); //number of rows affected

          return rowsAffected > 0;
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

}