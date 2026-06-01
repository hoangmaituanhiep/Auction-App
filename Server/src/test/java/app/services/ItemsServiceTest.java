package app.services;

import app.dao.ItemDAO;
import app.functions.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemsServiceTest {

    static class SimpleItem extends Item {
        public SimpleItem(String name, String detail, double startingPrice) {
            super(name, detail, startingPrice);
        }

        public String toString() {
            return getName();
        }
    }

    @Test
    void addItems_returnsFalse_whenInvalidNameOrPrice() {
        ItemDAO dao = new ItemDAO() {
            public void createTable() {
            }

            public boolean insertItem(Item item) {
                return true;
            }

            public double getCurrentPrice(int id) {
                return 0;
            }

            public boolean setNewPrice(int id, double newPrice) {
                return true;
            }

            public Item getItem(int Id) {
                return null;
            }

            public int getLastestItemId() {
                return 0;
            }
        };

        ItemsService svc = new ItemsService(dao);

        SimpleItem badName = new SimpleItem(null, "d", 10);
        assertFalse(svc.addItems(badName));

        SimpleItem badPrice = new SimpleItem("n", "d", -1);
        assertFalse(svc.addItems(badPrice));
    }

    @Test
    void addItems_delegatesToDao() {
        ItemDAO dao = new ItemDAO() {
            public void createTable() {
            }

            public boolean insertItem(Item item) {
                return true;
            }

            public double getCurrentPrice(int id) {
                return 0;
            }

            public boolean setNewPrice(int id, double newPrice) {
                return true;
            }

            public Item getItem(int Id) {
                return new SimpleItem("n", "d", 5);
            }

            public int getLastestItemId() {
                return 7;
            }
        };

        ItemsService svc = new ItemsService(dao);
        SimpleItem item = new SimpleItem("n", "d", 5);
        assertTrue(svc.addItems(item));
        assertEquals(item.getName(), svc.getItemById(1).getName());
        assertEquals(7, svc.getLastestItemId());
    }

    @Test
    void setNewPrice_checksCurrentPrice() {
        ItemDAO dao = new ItemDAO() {
            public void createTable() {
            }

            public boolean insertItem(Item item) {
                return true;
            }

            public double getCurrentPrice(int id) {
                return 100.0;
            }

            public boolean setNewPrice(int id, double newPrice) {
                return true;
            }

            public Item getItem(int Id) {
                return null;
            }

            public int getLastestItemId() {
                return 0;
            }
        };

        ItemsService svc = new ItemsService(dao);
        assertFalse(svc.setNewPrice(1, 50.0));
        assertTrue(svc.setNewPrice(1, 150.0));
    }
}
