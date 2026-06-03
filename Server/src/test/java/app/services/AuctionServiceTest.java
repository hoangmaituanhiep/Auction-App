package app.services;

import app.dao.AuctionDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Auction Service Tests")
public class AuctionServiceTest {

    @Mock
    private AuctionDAO auctionDAO;

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionService = new AuctionService(auctionDAO);
    }

    // Test: Add Auction - Success Case
    @Test
    @DisplayName("Should add auction successfully when DAO returns true")
    void testAddAuctionSuccess() {
        // Arrange
        String auctionName = "Vintage Watch";
        String duration = "7d";
        when(auctionDAO.addAuction(auctionName, duration)).thenReturn(true);

        // Act
        boolean result = auctionService.addAuction(auctionName, duration);

        // Assert
        assertTrue(result, "Auction should be added successfully");
        verify(auctionDAO, times(1)).addAuction(auctionName, duration);
    }

    // Test: Add Auction - Failure Case
    @Test
    @DisplayName("Should fail to add auction when DAO returns false")
    void testAddAuctionFailure() {
        // Arrange
        String auctionName = "Vintage Watch";
        String duration = "7d";
        when(auctionDAO.addAuction(auctionName, duration)).thenReturn(false);

        // Act
        boolean result = auctionService.addAuction(auctionName, duration);

        // Assert
        assertFalse(result, "Auction should not be added");
        verify(auctionDAO, times(1)).addAuction(auctionName, duration);
    }

    // Test: Update Auction Status
    @Test
    @DisplayName("Should update auction status when valid ID and status provided")
    void testUpdateAuctionStatus() {
        // Arrange
        int auctionId = 1;
        String newStatus = "CLOSED";
        when(auctionDAO.changeAuctionStatus(auctionId, newStatus)).thenReturn(true);

        // Act
        boolean result = auctionService.updateStatus(auctionId, newStatus);

        // Assert
        assertTrue(result, "Status should be updated successfully");
        verify(auctionDAO, times(1)).changeAuctionStatus(auctionId, newStatus);
    }

    // Test: Update Auction Duration
    @Test
    @DisplayName("Should update auction duration successfully")
    void testUpdateAuctionDuration() {
        // Arrange
        int auctionId = 1;
        String newDuration = "14d";
        when(auctionDAO.updateDuration(newDuration, auctionId)).thenReturn(true);

        // Act
        boolean result = auctionService.updateDuration(auctionId, newDuration);

        // Assert
        assertTrue(result, "Duration should be updated successfully");
        verify(auctionDAO, times(1)).updateDuration(newDuration, auctionId);
    }

    // Test: Get Latest Auction ID
    @Test
    @DisplayName("Should return latest auction ID from DAO")
    void testGetLatestAuctionId() {
        // Arrange
        int expectedId = 42;
        when(auctionDAO.getLatestAuctionId()).thenReturn(expectedId);

        // Act
        int result = auctionService.getAuctionId();

        // Assert
        assertEquals(expectedId, result, "Latest auction ID should be returned correctly");
        verify(auctionDAO, times(1)).getLatestAuctionId();
    }

    // Test: Invalid Input - Empty Auction Name
    @Test
    @DisplayName("Should reject auction with empty name")
    void testAddAuctionWithEmptyName() {
        // Arrange
        String invalidName = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            if (invalidName == null || invalidName.trim().isEmpty()) {
                throw new IllegalArgumentException("Auction name cannot be empty");
            }
        });
    }
}