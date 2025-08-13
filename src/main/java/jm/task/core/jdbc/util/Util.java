package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    private static final String URL = "jdbc:mysql://localhost:3306/mydbtest";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Util() {
    }

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения к базе данных: " + e.getMessage(), e);
        }
    }
    public static void testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("Соединение с базой данных установлено");
            System.out.println("Database: " + connection.getCatalog());
        } catch (Exception e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
