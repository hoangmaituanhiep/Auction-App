package app.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.config.DatabaseConfig;

public class UserDAO {
  private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

  public void createTable() {
    logger.debug("DEBUG: Initializing user table database...");

    String table = "CREATE TABLE IF NOT EXISTS user(username TEXT NOT NULL PRIMARY KEY, email TEXT NOT NULL, password TEXT NOT NULL)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getUsersUrl());
        Statement statement = connection.createStatement()) {
          statement.execute(table);
          
          logger.info("INFO: Created user table.");
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
  }

  public boolean insertUser(String username, String password, String email) {
    logger.debug("DEBUG: Inserting new user");

    String insert = "INSERT INTO user(username, email, password) VALUES(?, ?, ?)";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getUsersUrl());
        PreparedStatement preStatement = connection.prepareStatement(insert)) {
          preStatement.setString(1, username);
          preStatement.setString(2, email);
          preStatement.setString(3, password);
          
          preStatement.executeUpdate();
          
          logger.info("INFO: Successfully created new user.");
          
          return true;
        }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
      return false;
    }
  }

  public boolean userExists(String username) {
    logger.debug("DEBUG: Checking if user exists: {}", username);
    String query = "SELECT count(username) FROM user WHERE username = ?";
    
    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getUsersUrl());
         PreparedStatement preStatement = connection.prepareStatement(query)) {
      
      preStatement.setString(1, username);
      
      try (ResultSet resultSet = preStatement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getInt(1) > 0;
        }
      }
    } catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
    return false;
  }

  public String getPassword(String username) {
    logger.debug("DEBUG: Fetching password for user: {}", username);

    String query = "SELECT password FROM user WHERE username = ?";

    try (Connection connection = DriverManager.getConnection(DatabaseConfig.getUsersUrl());
      PreparedStatement preStatement = connection.prepareStatement(query)) {
        preStatement.setString(1, username);

        try (ResultSet resultSet = preStatement.executeQuery()) {
          if (resultSet.next()) {
            return resultSet.getString("password");
          }
        }
    }
    catch (SQLException e) {
      logger.error("ERROR: {}", e.getMessage());
    }
    return null;
  }
}