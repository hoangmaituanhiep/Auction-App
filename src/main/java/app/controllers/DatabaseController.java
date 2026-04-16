package app.controllers;

import app.functions.*;

import java.sql.*;

public class DatabaseController {
    private static final String itemsPath = "jdbc:sqlite:database/items.db";

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
