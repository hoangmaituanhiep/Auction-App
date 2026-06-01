package app.payload;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionRequestPayloadTest {

    @Test
    void constructorTwoArgs_setsFields() {
        ConnectionRequestPayload p = new ConnectionRequestPayload("user", "pass");

        assertEquals("user", p.getUsername());
        assertEquals("pass", p.getPassword());
        assertNull(p.getConfirmPassword());
        assertNull(p.getEmail());
    }

    @Test
    void constructorFourArgs_setsAllFields() {
        ConnectionRequestPayload p = new ConnectionRequestPayload("u", "p", "cp", "e@x.com");

        assertEquals("u", p.getUsername());
        assertEquals("p", p.getPassword());
        assertEquals("cp", p.getConfirmPassword());
        assertEquals("e@x.com", p.getEmail());
    }
}
