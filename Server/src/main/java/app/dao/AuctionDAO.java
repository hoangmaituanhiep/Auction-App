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

public class AuctionDAO {
  private static final Logger logger = LoggerFactory.getLogger(AuctionDAO.class);

  private int auctionId;

  public void createTable() {
    String table = "CREATE TABLE IF NOT EXISTS auction(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, duration TEXT NOT NULL, status TEXT NOT NULL)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getAuctionUrl());
        Statement statement = connection.createStatement()) {
          statement.execute(table);
          logger.info("INFO: Create Auction database");
        }
    catch (SQLException e) {
      logger.error("ERROR: Failed to create auction table. {}", e.getMessage());
    }
  }
  
  public boolean addAuction(String name, String duration) {
    String insert = "INSERT INTO auction(name, duration, status) VALUES(?, ?, ?)";


    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getAuctionUrl());
        PreparedStatement preStatement = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
          
          preStatement.setString(1, name);
          preStatement.setString(2, duration);
          preStatement.setString(3, "OPEN");

          int update = preStatement.executeUpdate();

          try (ResultSet generatedKeys = preStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
              this.auctionId = generatedKeys.getInt(1);
            } else {
              this.auctionId = -1;
            }
          }

          return update > 0;
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public boolean changeAuctionStatus(int auctionId, String status) {
    String query = "UPDATE auction SET status = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getAuctionUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          preStatement.setInt(2, auctionId);
          preStatement.setString(1, status);

          int updatedRow = preStatement.executeUpdate();
          return updatedRow > 0;
        }
    catch (SQLException e) {
      logger.error("ERROR: Cannot change auction status in db");
      return false;
    }
  }

  public boolean updateDuration(String duration, int auctionId) {
    String query = "UPDATE auction SET duration = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getAuctionUrl());
        PreparedStatement preStatement = connection.prepareStatement(query)) {
          preStatement.setInt(2, auctionId);
          preStatement.setString(1, duration);

          int update = preStatement.executeUpdate();

          return update > 0;
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public int getLatestAuctionId() {
    return auctionId;
  }
}
