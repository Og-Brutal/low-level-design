package com.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionDBC {

    private static Connection connection = null;

    private static final String DB_NAME = "testDB";
    private static final String URL_BASE = "jdbc:mysql://localhost:3306/";
    private static final String URL = URL_BASE + DB_NAME + "?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnectionDBC() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Connect to MySQL without database to ensure creation
                Connection tempConn = DriverManager.getConnection(URL_BASE + "?useSSL=false&serverTimezone=UTC", USER, PASSWORD);

                // Create database if not exists
                try (Statement stmt = tempConn.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                    System.out.println("✅ Database checked/created: " + DB_NAME);
                }

                // Connect to actual database
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to MySQL database successfully!");

                // Initialize tables and triggers
            }

        } catch (Exception e) {
            System.err.println("❌ Database connection or initialization failed:");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println(" Error closing database connection:");
            e.printStackTrace();
        }
    }

   
}
