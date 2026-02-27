package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dal.SentenceDAO;

public class UpdateSentenceTest {

    private Connection conn;
    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;

    @BeforeEach
    void setup() throws SQLException {
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);

        try (Statement st = conn.createStatement()) {
            // Clear all tables
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            // Reset auto_increment
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        // Create hierarchy: Author → Book → Chapter
        authorDAO.createAuthor("Author1", null);          // author_id = 1
        bookDAO.createBook("Book1", 1, null);            // book_id = 1
        chapterDAO.createChapter(1, "Chapter One");      // chapter_id = 1

        // Insert test sentence
        sentenceDAO.createSentence(1, "OldText", "OldDia", "OldTrans", "OldNote"); // sentence_id = 1
    }

    // ---------------------------------------------------------
    // TP1: Row updated → true
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Row updated → true")
    void tp1_rowUpdated() {
        boolean updated = sentenceDAO.updateSenetence(1, 1, "NewText", "NewDia", "NewTrans", "NewNote");
        assertTrue(updated);
    }

    // ---------------------------------------------------------
    // TP2: No row matches → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: No row matches → false")
    void tp2_noRow() {
        boolean updated = sentenceDAO.updateSenetence(99, 999, "X", "X", "X", "X");
        assertFalse(updated);
    }

    // ---------------------------------------------------------
    // TP3: Simulate prepare failure → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB connection closed → false")
    void tp3_prepareFails() throws SQLException {
        conn.close(); // simulate DB failure
        boolean updated = sentenceDAO.updateSenetence(1, 1, "X", "X", "X", "X");
        assertFalse(updated);

        // Reconnect for other tests
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }

    // ---------------------------------------------------------
    // TP4: Simulate set parameter failure → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: Extremely large values → false")
    void tp4_setFails() {
        // Use extremely large values to simulate failure during binding
        boolean updated = sentenceDAO.updateSenetence(Integer.MAX_VALUE, Integer.MAX_VALUE, "X", "X", "X", "X");
        assertFalse(updated);
    }

    // ---------------------------------------------------------
    // TP5: Simulate executeUpdate failure → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP5: ExecuteUpdate fails → false")
    void tp5_executeFails() throws SQLException {
        conn.close(); // executeUpdate will fail
        boolean updated = sentenceDAO.updateSenetence(1, 1, "X", "X", "X", "X");
        assertFalse(updated);

        // Reconnect for future tests
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }
}
