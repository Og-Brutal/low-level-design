package main.java.com.apm.dal;

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

            // 1️⃣ Make sure DB exists
            createDatabaseIfNotExists();

            // 2️⃣ Now connect to database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL database successfully!");

            // 3️⃣ Create tables if not exist
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

        /* ------------------------------------------------------------------
         *  1️⃣ Author Table
         * ------------------------------------------------------------------ */
        String createAuthor =
            "CREATE TABLE IF NOT EXISTS Author (" +
            " author_id INT NOT NULL AUTO_INCREMENT," +
            " name VARCHAR(100) NOT NULL," +
            " biography TEXT DEFAULT NULL," +
            " PRIMARY KEY (author_id)" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createAuthor);

        /* ------------------------------------------------------------------
         *  2️⃣ Book Table
         * ------------------------------------------------------------------ */
        String createBook =
            "CREATE TABLE IF NOT EXISTS Book (" +
            " book_id INT NOT NULL AUTO_INCREMENT," +
            " title VARCHAR(200) NOT NULL UNIQUE," +
            " author_id INT DEFAULT NULL," +
            " era VARCHAR(100) DEFAULT NULL," +
            " PRIMARY KEY (book_id)," +
            " KEY (author_id)," +
            " CONSTRAINT Book_ibfk_1 FOREIGN KEY (author_id)" +
            "   REFERENCES Author(author_id)" +
            "   ON DELETE SET NULL ON UPDATE CASCADE" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createBook);

        /* ------------------------------------------------------------------
         *  3️⃣ Chapter Table
         * ------------------------------------------------------------------ */
        String createChapter =
            "CREATE TABLE IF NOT EXISTS Chapter (" +
            " chapter_id INT NOT NULL AUTO_INCREMENT," +
            " book_id INT NOT NULL," +
            " chapter_name VARCHAR(255) NOT NULL," +
            " PRIMARY KEY (chapter_id)," +
            " CONSTRAINT Chapter_ibfk_1 FOREIGN KEY (book_id)" +
            "   REFERENCES Book(book_id)" +
            "   ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createChapter);

        /* ------------------------------------------------------------------
         *  4️⃣ Sentence Table
         * ------------------------------------------------------------------ */
        String createSentence =
            "CREATE TABLE IF NOT EXISTS Sentence (" +
            " sentence_id INT NOT NULL AUTO_INCREMENT," +
            " chapter_id INT NOT NULL," +
            " sentence_number INT NOT NULL," +
            " text LONGTEXT NOT NULL," +
            " text_diacritized LONGTEXT DEFAULT NULL," +
            " translation LONGTEXT DEFAULT NULL," +
            " notes LONGTEXT DEFAULT NULL," +
            " PRIMARY KEY (sentence_id)," +
            " KEY (chapter_id)," +
            " CONSTRAINT Sentence_ibfk_1 FOREIGN KEY (chapter_id)" +
            "   REFERENCES Chapter(chapter_id)" +
            "   ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1;";
        stmt.executeUpdate(createSentence);

        /* ------------------------------------------------------------------
         *  5️⃣ Root Table  (COMMENTED TABLE #1)
         * ------------------------------------------------------------------ */
        String createRoot =
            "CREATE TABLE IF NOT EXISTS Root (" +
            " root_id INT NOT NULL AUTO_INCREMENT," +
            " root VARCHAR(50) NOT NULL UNIQUE," +
            " PRIMARY KEY (root_id)" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createRoot);

        /* ------------------------------------------------------------------
         *  6️⃣ Lemma Table  (COMMENTED TABLE #2)
         * ------------------------------------------------------------------ */
        String createLemma =
            "CREATE TABLE IF NOT EXISTS Lemma (" +
            " lemma_id INT NOT NULL AUTO_INCREMENT," +
            " root_id INT NOT NULL," +
            " lemma VARCHAR(100) NOT NULL UNIQUE," +
            " PRIMARY KEY (lemma_id)," +
            " CONSTRAINT Lemma_ibfk_1 FOREIGN KEY (root_id)" +
            "   REFERENCES Root(root_id)" +
            "   ON DELETE RESTRICT ON UPDATE CASCADE" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createLemma);

        /* ------------------------------------------------------------------
         *  7️⃣ Token Table  (COMMENTED TABLE #3)
         * ------------------------------------------------------------------ */
        String createToken =
            "CREATE TABLE IF NOT EXISTS Token (" +
            " token_id INT NOT NULL AUTO_INCREMENT," +
            " sentence_id INT NOT NULL," +
            " lemma_id INT NOT NULL," +
            " token VARCHAR(100) NOT NULL," +
            " PRIMARY KEY (token_id)," +
            " KEY (sentence_id)," +
            " KEY (lemma_id)," +
            " CONSTRAINT Token_fk_sentence FOREIGN KEY (sentence_id)" +
            "   REFERENCES Sentence(sentence_id)" +
            "   ON DELETE CASCADE ON UPDATE CASCADE," +
            " CONSTRAINT Token_fk_lemma FOREIGN KEY (lemma_id)" +
            "   REFERENCES Lemma(lemma_id)" +
            "   ON DELETE RESTRICT ON UPDATE CASCADE" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createToken);

        /* ------------------------------------------------------------------
         *  8️⃣ SegmentedToken Table  (COMMENTED TABLE #4)
         * ------------------------------------------------------------------ */
        String createSegmentedToken =
            "CREATE TABLE IF NOT EXISTS SegmentedToken (" +
            " segmentedTokenId INT NOT NULL AUTO_INCREMENT," +
            " tokenId INT NOT NULL," +
            " prefix VARCHAR(50)," +
            " stem VARCHAR(100) NOT NULL," +
            " lemma VARCHAR(100)," +
            " root VARCHAR(10)," +
            " PRIMARY KEY (segmentedTokenId)," +
            " CONSTRAINT SegToken_fk_token FOREIGN KEY (tokenId)" +
            "   REFERENCES Token(token_id)" +
            "   ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB;";
        stmt.executeUpdate(createSegmentedToken);

        /* ------------------------------------------------------------------
         *  9️⃣ Trigger for sentence_number auto increment per chapter
         * ------------------------------------------------------------------ */
        try {
            stmt.executeUpdate("DROP TRIGGER IF EXISTS trg_sentence_number");
            String createTrigger =
                "CREATE TRIGGER trg_sentence_number " +
                "BEFORE INSERT ON Sentence " +
                "FOR EACH ROW " +
                "BEGIN " +
                " DECLARE max_num INT; " +
                " SELECT IFNULL(MAX(sentence_number), 0) INTO max_num " +
                "   FROM Sentence WHERE chapter_id = NEW.chapter_id; " +
                " SET NEW.sentence_number = max_num + 1; " +
                "END;";
            stmt.executeUpdate(createTrigger);
            System.out.println("Trigger created/updated: trg_sentence_number");
        } catch (SQLException te) {
            System.err.println("Trigger creation failed:");
            te.printStackTrace();
        }

        System.out.println("All tables (including Token, Lemma, Root, SegmentedToken) created successfully.");

    } catch (SQLException e) {
        System.err.println("Error initializing database structure:");
        e.printStackTrace();
    }
}
   private static void createDatabaseIfNotExists() {
	    try {
	        // Extract raw DB name from URL
	        String rawDb = URL.substring(URL.lastIndexOf("/") + 1);

	        // Remove parameters (anything after '?')
	        String dbName = rawDb.contains("?")
	                ? rawDb.substring(0, rawDb.indexOf("?"))
	                : rawDb;

	        // Base URL WITHOUT database name
	        String baseUrl = URL.substring(0, URL.lastIndexOf("/"));

	        try (Connection conn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
	             Statement stmt = conn.createStatement()) {

	            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
	            System.out.println("Database created or verified: " + dbName);
	        }

	    } catch (Exception e) {
	        System.err.println("Failed to create database automatically.");
	        e.printStackTrace();
	    }
	}



}


