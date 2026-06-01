package app.payload;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FetchDataResponsePayloadTest {

    @Test
    void constructorSetsLiveAuctionList() {
        AuctionDTO dto = new AuctionDTO(1, "A", "10m", 2.0, "OPEN", List.of(1, 2));
        FetchDataResponsePayload payload = new FetchDataResponsePayload(List.of(dto));

        assertEquals(1, payload.getLiveAuction().size());
        assertSame(dto, payload.getLiveAuction().get(0));
    }
}
