package app.services;

import app.dao.UserDAO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionServiceTest {

    @Test
    void authenticate_returnsFalse_whenUsernameMissing() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertFalse(svc.authenticate(null, "pw"));
        assertFalse(svc.authenticate("", "pw"));
    }

    @Test
    void authenticate_returnsFalse_whenPasswordMissing() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertFalse(svc.authenticate("u", null));
        assertFalse(svc.authenticate("u", ""));
    }

    @Test
    void authenticate_returnsTrue_whenUserExistsAndPasswordMatches() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }

            public boolean userExists(String username) {
                return "u".equals(username);
            }

            public String getPassword(String username) {
                return "secret";
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertTrue(svc.authenticate("u", "secret"));
    }

    @Test
    void authenticate_returnsFalse_whenUserDoesNotExist() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }

            public boolean userExists(String username) {
                return false;
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertFalse(svc.authenticate("x", "any"));
    }

    @Test
    void register_returnsFalse_whenUserExists() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }

            public boolean userExists(String username) {
                return true;
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertFalse(svc.authenticate("u", "p", "p", "e@x.com"));
    }

    @Test
    void register_returnsFalse_whenPasswordsDoNotMatch() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }

            public boolean userExists(String username) {
                return false;
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertFalse(svc.authenticate("u", "p", "q", "e@x.com"));
    }

    @Test
    void register_returnsTrue_whenInsertSucceeds() {
        UserDAO userDAO = new UserDAO() {
            public void createTable() {
            }

            public boolean userExists(String username) {
                return false;
            }

            public boolean insertUser(String username, String password, String email, String role) {
                return true;
            }
        };
        ConnectionService svc = new ConnectionService(userDAO);

        assertTrue(svc.authenticate("new", "p", "p", "e@x.com"));
    }
}
