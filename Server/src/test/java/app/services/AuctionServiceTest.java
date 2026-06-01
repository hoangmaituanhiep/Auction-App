package app.services;

import app.dao.AuctionDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionServiceTest {

    @Test
    void addAuction_returnsTrue_whenDaoSucceeds() {
        AuctionDAO dao = new AuctionDAO() {
            public void createTable() {
            }

            public boolean addAuction(String name, String duration) {
                return true;
            }

            public boolean changeAuctionStatus(int id, String status) {
                return false;
            }

            public boolean updateDuration(String duration, int auctionId) {
                return false;
            }

            public int getLatestAuctionId() {
                return 0;
            }
        };

        AuctionService svc = new AuctionService(dao);
        assertTrue(svc.addAuction("n", "d"));
    }

    @Test
    void addAuction_returnsFalse_whenDaoFails() {
        AuctionDAO dao = new AuctionDAO() {
            public void createTable() {
            }

            public boolean addAuction(String name, String duration) {
                return false;
            }

            public boolean changeAuctionStatus(int id, String status) {
                return false;
            }

            public boolean updateDuration(String duration, int auctionId) {
                return false;
            }

            public int getLatestAuctionId() {
                return 0;
            }
        };

        AuctionService svc = new AuctionService(dao);
        assertFalse(svc.addAuction("n", "d"));
    }

    @Test
    void updateStatus_delegatesToDao() {
        AuctionDAO dao = new AuctionDAO() {
            public void createTable() {
            }

            public boolean addAuction(String name, String duration) {
                return false;
            }

            public boolean changeAuctionStatus(int id, String status) {
                return true;
            }

            public boolean updateDuration(String duration, int auctionId) {
                return false;
            }

            public int getLatestAuctionId() {
                return 0;
            }
        };

        AuctionService svc = new AuctionService(dao);
        assertTrue(svc.updateStatus(1, "OPEN"));
    }

    @Test
    void updateDuration_delegatesToDao() {
        AuctionDAO dao = new AuctionDAO() {
            public void createTable() {
            }

            public boolean addAuction(String name, String duration) {
                return false;
            }

            public boolean changeAuctionStatus(int id, String status) {
                return false;
            }

            public boolean updateDuration(String duration, int auctionId) {
                return true;
            }

            public int getLatestAuctionId() {
                return 123;
            }
        };

        AuctionService svc = new AuctionService(dao);
        assertTrue(svc.updateDuration(5, "10m"));
        assertEquals(123, svc.getAuctionId());
    }
}
