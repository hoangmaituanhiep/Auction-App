package app.payload;

import org.junit.jupiter.api.Test;

import app.functions.GenericItem;

import static org.junit.jupiter.api.Assertions.*;

public class WinnerPayloadTest {

    @Test
    void constructorSetsUsername() {
        WinnerPayload payload = new WinnerPayload("winner1", 1, new GenericItem("ok", "ok", 0));

        assertEquals("winner1", payload.getUsername());
    }
}
