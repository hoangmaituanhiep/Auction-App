package app.payload;

import app.functions.GenericItem;
import app.functions.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SellItemRequestPayloadTest {

    @Test
    void constructorWithItemSetsItemAndDefaultAuctionId() {
        Item item = new GenericItem("item", "desc", 15.0);
        SellItemRequestPayload payload = new SellItemRequestPayload(item);

        assertSame(item, payload.getItem());
        assertEquals(-1, payload.getAuctionId());
    }

    @Test
    void constructorWithItemAndAuctionIdSetsBoth() {
        Item item = new GenericItem("item", "desc", 15.0);
        SellItemRequestPayload payload = new SellItemRequestPayload(item, 34);

        assertSame(item, payload.getItem());
        assertEquals(34, payload.getAuctionId());
    }
}
