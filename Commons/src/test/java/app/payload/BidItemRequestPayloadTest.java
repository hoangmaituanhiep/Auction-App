package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BidItemRequestPayloadTest {

    @Test
    void payloadStoresRequestDetails() {
        BidItemRequestPayload payload = new BidItemRequestPayload(1024, "bidder1", 123.5);

        assertEquals(1024, payload.getId());
        assertEquals("bidder1", payload.getUserName());
        assertEquals(123.5, payload.getPrice());
    }
}
