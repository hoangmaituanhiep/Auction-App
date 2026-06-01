package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NewAuctionRespondTest {

    @Test
    void successConstructorSetsFields() {
        NewAuctionRespond payload = new NewAuctionRespond(true, 12, "Auction", "15m");

        assertTrue(payload.isSuccess());
        assertEquals(12, payload.getAuctionId());
        assertEquals("Auction", payload.getName());
        assertEquals("15m", payload.getDuration());
        assertNull(payload.getError());
    }

    @Test
    void errorConstructorSetsError() {
        NewAuctionRespond payload = new NewAuctionRespond(false, "bad request");

        assertFalse(payload.isSuccess());
        assertEquals("bad request", payload.getError());
    }

    @Test
    void settersUpdateValues() {
        NewAuctionRespond payload = new NewAuctionRespond(false, "error");
        payload.setSuccess(true);
        payload.setAuctionId(22);
        payload.setName("New Auction");
        payload.setDuration("30m");
        payload.setError("none");

        assertTrue(payload.isSuccess());
        assertEquals(22, payload.getAuctionId());
        assertEquals("New Auction", payload.getName());
        assertEquals("30m", payload.getDuration());
        assertEquals("none", payload.getError());
    }
}
