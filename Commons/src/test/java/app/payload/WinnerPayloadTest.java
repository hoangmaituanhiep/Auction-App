package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WinnerPayloadTest {

    @Test
    void constructorSetsUsername() {
        WinnerPayload payload = new WinnerPayload("winner1");

        assertEquals("winner1", payload.getUsername());
    }
}
