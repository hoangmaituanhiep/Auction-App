package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FetchAuctionItemsRequestPayloadTest {

    @Test
    void constructorSetsAuctionId() {
        FetchAuctionItemsRequestPayload payload = new FetchAuctionItemsRequestPayload(42);

        assertEquals(42, payload.getAuctionId());
    }
}
