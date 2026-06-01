package app.packets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PacketMessageTest {

    @Test
    void constructorAndGettersWork() {
        PacketMessage msg = new PacketMessage(Message.WELCOME, "payload");

        assertEquals(Message.WELCOME, msg.getType());
        assertEquals("payload", msg.getPayload());
    }

    @Test
    void settersWork() {
        PacketMessage msg = new PacketMessage(Message.WELCOME, null);
        msg.setType(Message.QUIT_ACTION);
        msg.setPayload(123);

        assertEquals(Message.QUIT_ACTION, msg.getType());
        assertEquals(123, msg.getPayload());
    }
}
