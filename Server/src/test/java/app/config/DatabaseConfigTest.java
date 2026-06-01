package app.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConfigTest {

    @Test
    void returnsDefaultJdbcUrls() {
        assertTrue(DatabaseConfig.getItemsUrl().contains("jdbc:sqlite"));
        assertTrue(DatabaseConfig.getUsersUrl().contains("jdbc:sqlite"));
        assertTrue(DatabaseConfig.getAuctionUrl().contains("jdbc:sqlite"));
        assertTrue(DatabaseConfig.getBidUrl().contains("jdbc:sqlite"));
    }
}
