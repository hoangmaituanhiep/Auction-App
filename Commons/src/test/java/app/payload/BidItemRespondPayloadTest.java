package app.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BidItemRespondPayloadTest {

    @Test
    void constructorsAndGetters() {
        BidItemRespondPayload p1 = new BidItemRespondPayload(5, true);
        assertEquals(5, p1.getId());
        assertTrue(p1.isSuccess());

        BidItemRespondPayload p2 = new BidItemRespondPayload(6, false, "no");
        assertEquals(6, p2.getId());
        assertFalse(p2.isSuccess());
        assertEquals("no", p2.getError());
    }
}
