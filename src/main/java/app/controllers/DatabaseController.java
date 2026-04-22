package app.controllers;

import app.functions.*;

import java.sql.*;

public class DatabaseController {
    private static final String itemsPath = "jdbc:sqlite:src/main/resources/database/items.db";
    
    public static void initialize() {
        String createTable = "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, startingPrice REAL NOT NULL, detail TEXT)";

        try (Connection connection = DriverManager.getConnection(itemsPath);
            Statement statement = connection.createStatement();) {
                statement.execute(createTable);
                System.out.println("Database ready");
            }
        catch (SQLTimeoutException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String insertSql = "INSERT INTO items(name, startingPrice, detail) VALUES(?, ?, ?)";

    public static boolean insertDB(Item item) {
        try (Connection connection = DriverManager.getConnection(itemsPath);
            PreparedStatement add = connection.prepareStatement(insertSql)) {
                String itemName = item.getName();
                double itemStartingPrice = item.getStartingPrice();
                String itemDetail = item.getDetail();

                add.setString(1, itemName);
                add.setDouble(2, itemStartingPrice);
                add.setString(3, itemDetail);

                return true;
            }
        catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("NULL")) {
                System.out.println("Please add starting value");
            }
            else {
                e.printStackTrace();
            }
            return false;
        }
    }
}
