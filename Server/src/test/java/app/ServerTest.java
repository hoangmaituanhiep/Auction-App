package app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.functions.Auction;
import app.services.LiveAuctionSession;

import app.packets.PacketMessage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        setStaticField(Server.class, "server", null);
    }

    @AfterEach
    void cleanup() throws Exception {
        setStaticField(Server.class, "server", null);
    }

    @Test
    void getInstanceCreatesSingleton() {
        Server server = Server.getInstance(0);
        assertNotNull(server);
        assertSame(server, Server.getInstance());
    }

    @Test
    void broadcastWithNoClientsDoesNotThrow() {
        Server server = Server.getInstance(0);
        assertDoesNotThrow(() -> server.broadcast(new PacketMessage(null, null)));
    }

    @Test
    void addAndRemoveLiveAuctionUpdatesMapAndCancelsSession() {
        Server server = Server.getInstance(0);

        Auction auction = new Auction("Test Auction", "1");
        auction.setAuctionId(101);
        LiveAuctionSession session = new LiveAuctionSession(auction);

        server.addLiveAuction(session);
        assertTrue(server.getLiveAuction().containsKey(101));

        server.removeLiveAuction(101);
        assertFalse(server.getLiveAuction().containsKey(101));
        assertEquals("CANCELED", auction.getStatus());
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }
}
