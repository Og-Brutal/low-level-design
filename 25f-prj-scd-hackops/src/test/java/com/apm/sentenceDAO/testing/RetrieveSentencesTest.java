package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.*;
import com.apm.dto.SentenceDTO;

public class RetrieveSentencesTest {

    private Connection conn;
    private SentenceDAO sentenceDAO;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;

    @BeforeEach
    void setup() throws Exception {
        conn = DBConnectionDBC.getConnection();

        try (Statement st = conn.createStatement()) {
            // Clear tables
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

        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);

        // Insert test data
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One");

        sentenceDAO.createSentence(1, "Text1", "T1", "Tr1", "N1"); // sentenceId = 1
        sentenceDAO.createSentence(1, "Text2", "T2", "Tr2", "N2"); // sentenceId = 2
    }

    // ---------------------------------------------------------
    // TP1: No sentences → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No sentences → return null")
    void retrieveSentence_NoRows_ReturnsNull() {
        assertNull(sentenceDAO.retrieveSentence(999));
    }

    // ---------------------------------------------------------
    // TP2: Two sentences → return list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Two sentences → return list")
    void retrieveSentence_TwoRows_ReturnsList() {
        ArrayList<SentenceDTO> result = sentenceDAO.retrieveSentence(1);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Text1", result.get(0).getText());
        assertEquals("Text2", result.get(1).getText());
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return null")
    void retrieveSentence_DBFailure() throws Exception {
        conn.close();
        ArrayList<SentenceDTO> result = sentenceDAO.retrieveSentence(1);
        assertNull(result);

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }
}
