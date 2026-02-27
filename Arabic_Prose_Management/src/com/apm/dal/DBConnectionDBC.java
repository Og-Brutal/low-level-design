package com.apm.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionDBC {

    // Static connection instance
    private static Connection connection = null;

    private static final String URL = "jdbc:mysql://localhost:3306/Arabic_Prose_Management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // Default XAMPP MySQL username
    private static final String PASSWORD = "";       // Default password is empty on XAMPP

    // Private constructor — prevents creating objects of this class
    private DBConnectionDBC() {}

    //  Singleton method to get a single shared connection
    public static Connection getConnection() {
        try {
            // Check if connection is null or closed
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection to local MySQL
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to local MySQL (XAMPP) database successfully!");
            }
        } catch (Exception e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
        return connection;
    }

    // Method to close connection gracefully
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
