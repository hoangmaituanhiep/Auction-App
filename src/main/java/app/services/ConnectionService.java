package app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.dao.UserDAO;

public class ConnectionService {
  private static final Logger logger = LoggerFactory.getLogger(ConnectionService.class);

  private UserDAO userDAO;

  public ConnectionService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  public void setupDatabase() {
    userDAO.createTable();
  }

  public boolean authenticate(String username, String password) {
    if (username == null || username.isEmpty()) {
      logger.error("ERROR: Enter username pls.");
      return false;
    }
    if (password == null || password.isEmpty()) {
      logger.error("ERROR: Enter password pls.");
      return false;
    }
    
    if (userDAO.userExists(username)) {
      String storedPassword = userDAO.getPassword(username);

      return password.equals(storedPassword);
    }
    return false;
  }

  public boolean authenticate(String username, String password, String confirmPassword) {
    logger.debug("DEBUG: Registering...");

    if (userDAO.userExists(username)){
      logger.error("ERROR: User existed.");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      logger.error("ERROR: Unmatched password");
      return false;
    }

    userDAO.insertUser(username, confirmPassword);

    logger.info("INFO: Successfully created new user");
    return true;
  }
}
