package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import com.apm.dto.SentenceDTO;

public class SentenceByNumbersTest {

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
            // Clear all related tables
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            // Reset AUTO_INCREMENT
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        // Insert Author → Book → Chapter
        authorDAO.createAuthor("Author1", null);           // author_id = 1
        bookDAO.createBook("Book1", 1, null);             // book_id = 1
        chapterDAO.createChapter(1, "Chapter One");       // chapter_id = 1

        // Insert sentences
        sentenceDAO.createSentence(1, "Hello", "Hellō", "Hi", "Note");       // sentence_id = 1
        sentenceDAO.createSentence(1, "Second", "Secōnd", "Second text", "Note2"); // sentence_id = 2
        sentenceDAO.createSentence(1, null, null, null, null);              // sentence_id = 3
    }

    // ---------------------------------------------------------
    // TP1: Sentence exists → return filled DTO
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Sentence exists → return filled DTO")
    void sentenceByNumbers_Found_ReturnsDto() {
        SentenceDTO dto = sentenceDAO.sentenceByNumbers(1, 1);

        assertNotNull(dto);
        assertEquals(1, dto.getSentenceId());
        assertEquals(1, dto.getSentenceNumber());
        assertEquals("Hello", dto.getText());
    }

    // ---------------------------------------------------------
    // TP2: No sentence matches → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: No sentence matches → return null")
    void sentenceByNumbers_NotFound_ReturnsNull() {
        SentenceDTO dto = sentenceDAO.sentenceByNumbers(99, 999);
        assertNull(dto);
    }

    // ---------------------------------------------------------
    // TP3: SQLException in prepareStatement → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB connection closed → return null")
    void sentenceByNumbers_PrepareFails_ReturnsNull() throws SQLException {
        conn.close(); // simulate DB failure
        SentenceDTO dto = sentenceDAO.sentenceByNumbers(1, 1);
        assertNull(dto);
    }

    // ---------------------------------------------------------
    // TP4: SQLException in executeQuery → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: executeQuery fails → return null")
    void sentenceByNumbers_ExecuteQueryFails_ReturnsNull() throws SQLException {
        conn.close(); // executeQuery will fail
        SentenceDTO dto = sentenceDAO.sentenceByNumbers(1, 2);
        assertNull(dto);
    }

    // ---------------------------------------------------------
    // TP5: SQLException during setInt → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP5: setInt fails → return null")
    void sentenceByNumbers_SetIntFails_ReturnsNull() {
        // Use extremely large values to simulate failure
        SentenceDTO dto = sentenceDAO.sentenceByNumbers(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertNull(dto);
    }
}
