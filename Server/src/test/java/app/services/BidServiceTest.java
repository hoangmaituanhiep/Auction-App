package app.services;

import app.dao.BidDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BidServiceTest {

    @Test
    void updatePrice_delegatesToDao() {
        BidDAO dao = new BidDAO() {
            public void createTable() {
            }

            public boolean insertNewPrice(int itemId, String username, double price, String timestamp) {
                return true;
            }

            public String getWinner(int itemId) {
                return null;
            }
        };

        BidService svc = new BidService(dao);
        assertTrue(svc.updatePrice(1, "u", 10.0, "t"));
    }

    @Test
    void getWinner_returnsDaoValue() {
        BidDAO dao = new BidDAO() {
            public void createTable() {
            }

            public boolean insertNewPrice(int itemId, String username, double price, String timestamp) {
                return false;
            }

            public String getWinner(int itemId) {
                return "winner";
            }
        };

        BidService svc = new BidService(dao);
        assertEquals("winner", svc.getWinner(5));
    }
}
