package com.cakelicious.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/cakelicious_db";
    // IMPORTANT: Update these credentials with your local MySQL setup
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnector() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // The new driver class name for MySQL Connector/J 8.0+
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error connecting to the database", e);
        }
        return connection;
    }
}