package app.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private final File dbFile = new File("target/test-db/user.db");
    private final UserDAO dao = new UserDAO();

    @BeforeEach
    void setup() {
        if (dbFile.exists())
            dbFile.delete();
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
    void insertAndQueryUser() {
        boolean inserted = dao.insertUser("u", "p", "e@x.com", "BIDDER");
        assertTrue(inserted);

        assertTrue(dao.userExists("u"));
        assertEquals("p", dao.getPassword("u"));
    }

    @Test
    void nonExistingUserReturnsNullPassword() {
        assertFalse(dao.userExists("missing"));
        assertNull(dao.getPassword("missing"));
    }
}
