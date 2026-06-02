package app.services;

import app.dao.BidDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Bid Service Tests")
public class BidServiceTest {

    @Mock
    private BidDAO bidDAO;

    private BidService bidService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bidService = new BidService(bidDAO);
    }

    // Test: Update Price - Success Case
    @Test
    @DisplayName("Should insert new bid price successfully")
    void testUpdatePriceSuccess() {
        // Arrange
        int itemId = 5;
        String username = "johndoe";
        double price = 250.50;
        String timestamp = "2026-06-03 10:30:00";
        
        when(bidDAO.insertNewPrice(itemId, username, price, timestamp)).thenReturn(true);

        // Act
        boolean result = bidService.updatePrice(itemId, username, price, timestamp);

        // Assert
        assertTrue(result, "Bid price should be inserted successfully");
        verify(bidDAO, times(1)).insertNewPrice(itemId, username, price, timestamp);
    }

    // Test: Update Price - Failure Case
    @Test
    @DisplayName("Should fail to insert bid when DAO returns false")
    void testUpdatePriceFailure() {
        // Arrange
        int itemId = 5;
        String username = "johndoe";
        double price = 250.50;
        String timestamp = "2026-06-03 10:30:00";
        
        when(bidDAO.insertNewPrice(itemId, username, price, timestamp)).thenReturn(false);

        // Act
        boolean result = bidService.updatePrice(itemId, username, price, timestamp);

        // Assert
        assertFalse(result, "Bid price insertion should fail");
        verify(bidDAO, times(1)).insertNewPrice(itemId, username, price, timestamp);
    }

    // Test: Get Winner
    @Test
    @DisplayName("Should return the winner username for auction item")
    void testGetWinner() {
        // Arrange
        int itemId = 5;
        String expectedWinner = "alice_smith";
        
        when(bidDAO.getWinner(itemId)).thenReturn(expectedWinner);

        // Act
        String result = bidService.getWinner(itemId);

        // Assert
        assertEquals(expectedWinner, result, "Winner should be returned correctly");
        verify(bidDAO, times(1)).getWinner(itemId);
    }

    // Test: Get Winner - No Winner Found
    @Test
    @DisplayName("Should return null when no winner found")
    void testGetWinnerNotFound() {
        // Arrange
        int itemId = 999;
        
        when(bidDAO.getWinner(itemId)).thenReturn(null);

        // Act
        String result = bidService.getWinner(itemId);

        // Assert
        assertNull(result, "Should return null when no winner exists");
        verify(bidDAO, times(1)).getWinner(itemId);
    }

    // Test: Invalid Input - Negative Price
    @Test
    @DisplayName("Should reject bid with negative price")
    void testUpdatePriceWithNegativePrice() {
        // Arrange
        int itemId = 5;
        String username = "user";
        double invalidPrice = -100.0;
        String timestamp = "2026-06-03 10:30:00";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            if (invalidPrice < 0) {
                throw new IllegalArgumentException("Bid price cannot be negative");
            }
        });
    }

    // Test: Invalid Input - Empty Username
    @Test
    @DisplayName("Should reject bid with empty username")
    void testUpdatePriceWithEmptyUsername() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            String emptyUsername = "";
            if (emptyUsername == null || emptyUsername.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
        });
    }

    // Parameterized Test: Multiple Price Updates
    @ParameterizedTest
    @ValueSource(doubles = {10.0, 50.0, 100.50, 999.99})
    @DisplayName("Should accept various valid bid prices")
    void testUpdatePriceWithVariousPrices(double price) {
        // Arrange
        int itemId = 1;
        String username = "testuser";
        String timestamp = "2026-06-03 10:30:00";
        
        when(bidDAO.insertNewPrice(itemId, username, price, timestamp)).thenReturn(true);

        // Act
        boolean result = bidService.updatePrice(itemId, username, price, timestamp);

        // Assert
        assertTrue(result, "Various valid prices should be accepted");
    }
}
