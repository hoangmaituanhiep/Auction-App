package app.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class BidDAOTest {

    private final File dbFile = new File("target/test-db/bid.db");
    private final BidDAO dao = new BidDAO();

    @BeforeEach
    void setup() {
        File parent = dbFile.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        if (dbFile.exists())
            dbFile.delete();
        dao.createTable();
    }

    @AfterEach
    void teardown() {
        if (dbFile.exists())
            dbFile.delete();
    }

    @Test
    void insertAndGetWinner() {
        assertTrue(dao.insertNewPrice(1, "alice", 50.0, "t1"));
        assertTrue(dao.insertNewPrice(1, "bob", 60.0, "t2"));

        String winner = dao.getWinner(1);
        assertEquals("bob", winner);
    }

    @Test
    void winnerIsNullWhenNoBidsExist() {
        assertNull(dao.getWinner(2));
    }
}
