package app.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterClientPayloadTest {

    @Test
    void constructorAndGetters() {
        RegisterClientPayload p = new RegisterClientPayload(99);
        assertEquals(99, p.getAuctionId());

        p.setAuctionId(10);
        assertEquals(10, p.getAuctionId());
    }
}
