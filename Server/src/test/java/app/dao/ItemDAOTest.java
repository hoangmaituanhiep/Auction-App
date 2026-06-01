package app.dao;

import app.functions.GenericItem;
import app.functions.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDAOTest {

    private final File dbFile = new File("target/test-db/item.db");
    private final ItemDAO dao = new ItemDAO();

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
    void insertAndRetrieveItem() {
        Item item = new GenericItem("n", "d", 10.0);
        boolean ok = dao.insertItem(item);
        assertTrue(ok);

        int id = dao.getLastestItemId();
        assertTrue(id > 0);

        double price = dao.getCurrentPrice(id);
        assertEquals(10.0, price);

        Item fetched = dao.getItem(id);
        assertNotNull(fetched);
        assertEquals("n", fetched.getName());
    }

    @Test
    void setNewPrice_updatesPrice() {
        Item item = new GenericItem("n", "d", 10.0);
        dao.insertItem(item);
        int id = dao.getLastestItemId();

        assertTrue(dao.setNewPrice(id, 20.0));
        assertEquals(20.0, dao.getCurrentPrice(id));
    }

    @Test
    void getItemReturnsNullForMissingId() {
        assertNull(dao.getItem(9999));
    }
}
