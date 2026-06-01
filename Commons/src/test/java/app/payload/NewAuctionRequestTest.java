package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NewAuctionRequestTest {

    @Test
    void constructorSetsFields() {
        NewAuctionRequest payload = new NewAuctionRequest("AuctionName", "20m");

        assertEquals("AuctionName", payload.getName());
        assertEquals("20m", payload.getDuration());
    }
}
