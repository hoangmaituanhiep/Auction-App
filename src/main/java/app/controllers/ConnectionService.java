package app.controllers;

import java.sql.*;

public class ConnectionService {
    private final String databaseUrl = "jdbc:sqlite::resources:database/users.db";

    public boolean authenticate(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try(Connection connection = DriverManager.getConnection(databaseUrl);
        PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String storedPassword = result.getString("password");

                return storedPassword.equals(password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean authenticate(String username, String password, String confirmPassword) {
        if (password.equals(confirmPassword)) {
            String createTable = "CREATE TABLE IF NOT EXISTS users (" + 
            "id INTERGER PRIMARY KEY AUTOINCREMENT, " + 
            "username TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL)";
            
            String insertSql = "INSERT INTO users(username, password) VALUE(?, ?)";

            try (Connection connection = DriverManager.getConnection(databaseUrl);
                Statement status = connection.createStatement()) {
                    status.execute(createTable);
                }
            catch (SQLException e){
                e.printStackTrace();
                return false;
            }

            try (Connection connection = DriverManager.getConnection(databaseUrl);
                PreparedStatement prepareStatement = connection.prepareStatement(insertSql)) {
                    prepareStatement.setString(1, username);
                    prepareStatement.setString(2, password);
                    prepareStatement.executeUpdate();
                    return true;
                }
            catch (SQLException e) {
                if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                    System.out.println("Username existed");
                }
                else {
                    e.printStackTrace();
                }
                return false;
            }
        }
        else {
            System.out.println("Unmatch confirm password");
            return false;
        }
    }
}
