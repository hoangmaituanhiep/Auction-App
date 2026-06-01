package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CancelAuctionRequestTest {

    @Test
    void constructorSetsAuctionId() {
        CancelAuctionRequest payload = new CancelAuctionRequest(99);

        assertEquals(99, payload.getAuctionId());
    }
}
