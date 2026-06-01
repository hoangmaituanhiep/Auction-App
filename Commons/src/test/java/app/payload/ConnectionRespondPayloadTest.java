package app.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionRespondPayloadTest {

    @Test
    void gettersAndSettersReflectPayloadState() {
        ConnectionRespondPayload payload = new ConnectionRespondPayload(false, "invalid credentials");

        assertFalse(payload.isSuccess());
        assertEquals("invalid credentials", payload.getError());

        payload.setSuccess(true);
        payload.setError("ok");

        assertTrue(payload.isSuccess());
        assertEquals("ok", payload.getError());
    }
}
