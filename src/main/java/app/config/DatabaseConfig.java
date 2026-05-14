package app.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {
  private static final Properties properties = new Properties();
  private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

  static {
    try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      if (in != null) {
        properties.load(in);
      }
    }
    catch (Exception e) {
      logger.error("ERROR: " + e.getMessage());
    }
  }

  public static String getItemsUrl() {
    return properties.getProperty("items.url", "jdbc:sqlite:src/main/resources/database/items.db");
  }
  public static String getUsersUrl() {
    return properties.getProperty("users.url", "jdbc:sqlite:src/main/resources/database/user.db");
  }
  public static String getAuctionUrl() {
    return properties.getProperty("auction.url", "jdbc:sqlite:src/main/resources/database/auction.db");
  }
}
