package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CancelAuctionResponseTest {

    @Test
    void successConstructorSetsAuctionId() {
        CancelAuctionResponse payload = new CancelAuctionResponse(true, 55);

        assertTrue(payload.isSuccess());
        assertEquals(55, payload.getAuctionId());
        assertNull(payload.getError());
    }

    @Test
    void errorConstructorSetsError() {
        CancelAuctionResponse payload = new CancelAuctionResponse(false, "not allowed");

        assertFalse(payload.isSuccess());
        assertEquals("not allowed", payload.getError());
        assertEquals(0, payload.getAuctionId());
    }
}
