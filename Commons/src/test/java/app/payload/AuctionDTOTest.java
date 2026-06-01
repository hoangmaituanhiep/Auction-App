package app.payload;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionDTOTest {

    @Test
    void constructorSetsAllFields() {
        List<Integer> itemIds = List.of(1, 2, 3);
        AuctionDTO dto = new AuctionDTO(10, "AuctionName", "10m", 5.0, "OPEN", itemIds);

        assertEquals(10, dto.getAuctionId());
        assertEquals("AuctionName", dto.getName());
        assertEquals("10m", dto.getDuration());
        assertEquals(5.0, dto.getStep());
        assertEquals("OPEN", dto.getStatus());
        assertSame(itemIds, dto.getItemIds());
    }
}
