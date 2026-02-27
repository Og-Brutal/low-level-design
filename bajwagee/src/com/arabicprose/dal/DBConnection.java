package com.arabicprose.dal;

import java.sql.*;

public class DBConnection {

    private static final String DB_NAME = "arabic_prose_db";
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    // ONE STATIC SHARED CONNECTION
    private static Connection conn;

    // ---------------------------
    //      GET CONNECTION
    // ---------------------------
    public static Connection getConnection() throws SQLException {

        try {
            // Reuse existing connection
            if (conn != null && !conn.isClosed()) {
                return conn;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1) TEMP connection (no DB)
            Connection tempConn = DriverManager.getConnection(BASE_URL, USER, PASS);
            System.out.println("✅ Connected to MySQL server.");

            // 2) Create DB if needed
            try (Statement stmt = tempConn.createStatement()) {
                stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS " + DB_NAME +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
                );
                System.out.println("📘 Database ensured: " + DB_NAME);
            }

            tempConn.close();

            // 3) REAL connection to your DB
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 4) Ensure tables & indexes
            createTablesAndIndexes(conn);
            System.out.println("✅ Tables/indexes ensured.");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver missing: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ DB connection error: " + e.getMessage());
            throw e;
        }

        return conn;
    }

    // -----------------------------------------------------
    //       CREATE ALL TABLES + INDEXES (RUN ONCE)
    // -----------------------------------------------------
    private static void createTablesAndIndexes(Connection conn) {

        String createAuthor =
            "CREATE TABLE IF NOT EXISTS Author (" +
            "author_id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(255) NOT NULL," +
            "biography TEXT" +
            ");";

        String createBook =
            "CREATE TABLE IF NOT EXISTS Book (" +
            "book_id INT AUTO_INCREMENT PRIMARY KEY," +
            "title VARCHAR(255) NOT NULL," +
            "author_id INT NOT NULL," +
            "era VARCHAR(50)," +
            "FOREIGN KEY (author_id) REFERENCES Author(author_id) " +
            "ON DELETE CASCADE ON UPDATE CASCADE" +
            ");";

        String createChapter =
            "CREATE TABLE IF NOT EXISTS Chapter (" +
            "chapter_id INT AUTO_INCREMENT PRIMARY KEY," +
            "book_id INT NOT NULL," +
            "chapter_name VARCHAR(255) NOT NULL," +
            "chapter_order INT NOT NULL," +
            "description TEXT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE ON UPDATE CASCADE," +
            "UNIQUE KEY unique_book_chapter (book_id, chapter_name)" +
            ");";

        String createSentence =
            "CREATE TABLE IF NOT EXISTS Sentence (" +
            "sentence_id INT AUTO_INCREMENT PRIMARY KEY," +
            "book_id INT NOT NULL," +
            "chapter_id INT," +
            "sentence_number INT NOT NULL," +
            "`text` TEXT NOT NULL," +
            "text_diacritized TEXT," +
            "translation TEXT," +
            "notes TEXT," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "FOREIGN KEY (book_id) REFERENCES Book(book_id) ON DELETE CASCADE ON UPDATE CASCADE," +
            "FOREIGN KEY (chapter_id) REFERENCES Chapter(chapter_id) ON DELETE SET NULL ON UPDATE CASCADE," +
            "UNIQUE KEY unique_book_sentence (book_id, sentence_number)" +
            ");";

        String[] indexQueries = new String[] {
            "CREATE INDEX idx_chapter_book_id ON Chapter(book_id);",
            "CREATE INDEX idx_chapter_order ON Chapter(chapter_order);",
            "CREATE INDEX idx_chapter_name ON Chapter(chapter_name);",
            "CREATE INDEX idx_sentence_chapter_id ON Sentence(chapter_id);"
        };

        // Morphological tables
        String createAnalysisResult =
            "CREATE TABLE IF NOT EXISTS AnalysisResult (" +
            "analysis_id INT AUTO_INCREMENT PRIMARY KEY," +
            "sentence_id INT NOT NULL," +
            "original_text TEXT NOT NULL," +
            "analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY (sentence_id) REFERENCES Sentence(sentence_id) ON DELETE CASCADE" +
            ");";

        String createTokenization =
            "CREATE TABLE IF NOT EXISTS Tokenization (" +
            "token_id INT AUTO_INCREMENT PRIMARY KEY," +
            "analysis_id INT NOT NULL," +
            "token VARCHAR(255) NOT NULL," +
            "position INT NOT NULL," +
            "FOREIGN KEY (analysis_id) REFERENCES AnalysisResult(analysis_id) ON DELETE CASCADE" +
            ");";

        String createLemmatization =
            "CREATE TABLE IF NOT EXISTS Lemmatization (" +
            "lemma_id INT AUTO_INCREMENT PRIMARY KEY," +
            "token_id INT NOT NULL," +
            "lemma VARCHAR(255) NOT NULL," +
            "confidence DOUBLE DEFAULT 0.0," +
            "FOREIGN KEY (token_id) REFERENCES Tokenization(token_id) ON DELETE CASCADE" +
            ");";

        String createSegmentation =
            "CREATE TABLE IF NOT EXISTS Segmentation (" +
            "segmentation_id INT AUTO_INCREMENT PRIMARY KEY," +
            "token_id INT NOT NULL," +
            "prefix VARCHAR(100)," +
            "stem VARCHAR(255)," +
            "suffix VARCHAR(100)," +
            "FOREIGN KEY (token_id) REFERENCES Tokenization(token_id) ON DELETE CASCADE" +
            ");";

        String createRoot =
            "CREATE TABLE IF NOT EXISTS Root (" +
            "root_id INT AUTO_INCREMENT PRIMARY KEY," +
            "token_id INT NOT NULL," +
            "root VARCHAR(255) NOT NULL," +
            "confidence DOUBLE DEFAULT 0.0," +
            "FOREIGN KEY (token_id) REFERENCES Tokenization(token_id) ON DELETE CASCADE" +
            ");";

        // --------------------------------------------------
        //   Execute all table creation safely
        // --------------------------------------------------
        try (Statement stmt = conn.createStatement()) {

            // Core tables
            stmt.executeUpdate(createAuthor);
            stmt.executeUpdate(createBook);
            stmt.executeUpdate(createChapter);
            stmt.executeUpdate(createSentence);

            // Morphology tables
            stmt.executeUpdate(createAnalysisResult);
            stmt.executeUpdate(createTokenization);
            stmt.executeUpdate(createLemmatization);
            stmt.executeUpdate(createSegmentation);
            stmt.executeUpdate(createRoot);

            // Indexes
            for (String q : indexQueries) {
                try {
                    stmt.executeUpdate(q);
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 1061) {
                        // Index already exists
                    } else {
                        System.out.println("⚠️ Index error: " + ex.getMessage());
                    }
                }
            }

            System.out.println("✅ All tables & indexes ensured.");

        } catch (SQLException e) {
            System.out.println("⚠️ Error creating tables/indexes: " + e.getMessage());
        }
    }
}
