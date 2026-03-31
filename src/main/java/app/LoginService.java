package app;

import java.sql.*;

public class LoginService {
    private final String databaseUrl = "jdbc:sqlite::resources:database/users.db";

    public boolen authenticate(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try(Connection connection = DriverManager.getConnection(databaseUrl);
        Statement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                String storedPassword = result.getString("password");

                return storedPassword;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
