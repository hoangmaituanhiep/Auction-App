package app.payload;

import app.functions.GenericItem;
import app.functions.Item;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FetchAuctionItemsResponsePayloadTest {

    @Test
    void constructorSetsItemsList() {
        List<Item> items = List.of(new GenericItem("item1", "desc", 10.0));
        FetchAuctionItemsResponsePayload payload = new FetchAuctionItemsResponsePayload(items);

        assertSame(items, payload.getItems());
        assertEquals(1, payload.getItems().size());
        assertEquals("item1", payload.getItems().get(0).getName());
    }
}
