package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AntiSnippingRespondPayloadTest {

    @Test
    void constructorSetsFields() {
        AntiSnippingRespondPayload payload = new AntiSnippingRespondPayload(7, "auction-name");

        assertEquals(7, payload.getAuctionId());
        assertEquals("auction-name", payload.getName());
    }
}
