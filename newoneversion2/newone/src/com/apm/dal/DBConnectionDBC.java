package com.apm.dal;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBConnectionDBC {

    private static Connection connection = null;

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Static block — loads properties when class is loaded
    static {
        loadDBProperties();
    }

    // Private constructor — prevents creating objects
    private DBConnectionDBC() {}

    // Load database values from properties file
    private static void loadDBProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("DbConfig.properties")) {
            props.load(fis);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
            System.out.println("Database properties loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load database properties.");
            e.printStackTrace();
        }
    }

    // Singleton method to get a shared connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to MySQL database successfully!");
                initializeDatabaseStructure(connection);
            }
        } catch (Exception e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
        
        return connection;
    }

    // Close connection gracefully
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection:");
            e.printStackTrace();
        }
    }
    
    private static void initializeDatabaseStructure(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            // 1️⃣ Author Table
            String createAuthor =
                "CREATE TABLE IF NOT EXISTS Author (" +
                " author_id INT(11) NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(100) NOT NULL," +
                " biography TEXT DEFAULT NULL," +
                " PRIMARY KEY (author_id)" +
                ") ENGINE=InnoDB;";
            stmt.executeUpdate(createAuthor);

            //  Book Table
            String createBook =
                "CREATE TABLE IF NOT EXISTS Book (" +
                " book_id INT(11) NOT NULL AUTO_INCREMENT," +
                " title VARCHAR(200) NOT NULL UNIQUE," +
                " author_id INT(11) DEFAULT NULL," +
                " era VARCHAR(100) DEFAULT NULL," +
                " PRIMARY KEY (book_id)," +
                " KEY (author_id)," +
                " CONSTRAINT Book_ibfk_1 FOREIGN KEY (author_id)" +
                " REFERENCES Author(author_id)" +
                " ON DELETE SET NULL ON UPDATE CASCADE" +
                ") ENGINE=InnoDB;";
            stmt.executeUpdate(createBook);

            //  Chapter Table
            String createChapter =
                "CREATE TABLE IF NOT EXISTS Chapter (" +
                " chapter_id INT(11) NOT NULL AUTO_INCREMENT," +
                " book_id INT(11) NOT NULL," +
                " chapter_name VARCHAR(255) NOT NULL," +
                " PRIMARY KEY (chapter_id)," +
                " CONSTRAINT Chapter_ibfk_1 FOREIGN KEY (book_id)" +
                " REFERENCES Book(book_id)" +
                " ON DELETE CASCADE ON UPDATE CASCADE" +
                ") ENGINE=InnoDB;";
            stmt.executeUpdate(createChapter);

            //  Sentence Table (sentence_number also AUTO_INCREMENT)
            String createSentence =
            	    "CREATE TABLE IF NOT EXISTS Sentence (" +
            	    " sentence_id INT(11) NOT NULL AUTO_INCREMENT," +
            	    " chapter_id INT(11) NOT NULL," +
            	    " sentence_number INT(11) NOT NULL," +  
            	    " text LONGTEXT NOT NULL," +
            	    " text_diacritized LONGTEXT DEFAULT NULL," +
            	    " translation LONGTEXT DEFAULT NULL," +
            	    " notes LONGTEXT DEFAULT NULL," +
            	    " PRIMARY KEY (sentence_id)," +
            	    " KEY (chapter_id)," +
            	    " CONSTRAINT Sentence_ibfk_1 FOREIGN KEY (chapter_id)" +
            	    " REFERENCES Chapter(chapter_id)" +
            	    " ON DELETE CASCADE ON UPDATE CASCADE" +
            	    ") ENGINE=InnoDB AUTO_INCREMENT=1;";
            	stmt.executeUpdate(createSentence);

            //  Trigger for sentence_number (fallback)
            try {
                stmt.executeUpdate("DROP TRIGGER IF EXISTS trg_sentence_number");
                String createTrigger =
                    "CREATE TRIGGER trg_sentence_number " +
                    "BEFORE INSERT ON Sentence " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    " DECLARE max_num INT; " +
                    " SELECT IFNULL(MAX(sentence_number), 0) INTO max_num " +
                    " FROM Sentence WHERE chapter_id = NEW.chapter_id; " +
                    " SET NEW.sentence_number = max_num + 1; " +
                    "END;";
                stmt.executeUpdate(createTrigger);
                System.out.println(" Trigger created/updated: trg_sentence_number");
            } catch (SQLException te) {
                System.err.println(" Trigger creation warning:");
                te.printStackTrace();
            }

            System.out.println(" All tables and triggers verified/created successfully.");

        } catch (SQLException e) {
            System.err.println(" Error initializing database structure:");
            e.printStackTrace();
        }
    }
}
