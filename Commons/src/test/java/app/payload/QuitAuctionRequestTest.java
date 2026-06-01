package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuitAuctionRequestTest {

    @Test
    void constructorSetsAuctionId() {
        QuitAuctionRequest payload = new QuitAuctionRequest(77);

        assertEquals(77, payload.getAuctionId());
    }
}
