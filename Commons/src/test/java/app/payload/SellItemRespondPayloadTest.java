package app.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SellItemRespondPayloadTest {

    @Test
    void constructorWithId_setsFields() {
        SellItemRespondPayload p = new SellItemRespondPayload(true, 42);
        assertTrue(p.isSuccess());
        assertEquals(42, p.getItemId());
        assertNull(p.getError());
    }

    @Test
    void constructorWithError_setsFields() {
        SellItemRespondPayload p = new SellItemRespondPayload(false, "err");
        assertFalse(p.isSuccess());
        assertEquals("err", p.getError());
    }
}
