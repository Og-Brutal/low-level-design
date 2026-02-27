package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.*;
import com.apm.dto.SentenceDTO;

public class GetSentencesByTokenTest {

    private Connection conn;
    private SentenceDAO sentenceDAO;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private TokenDAO tokenDAO;

    @BeforeEach
    void setup() throws Exception {
        conn = DBConnectionDBC.getConnection();

        try (Statement st = conn.createStatement()) {
            // Clear tables
            st.executeUpdate("DELETE FROM SegmentedToken");
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            // Reset auto_increment
            st.executeUpdate("ALTER TABLE SegmentedToken AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);

        // Insert test data
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One");

        sentenceDAO.createSentence(1, "I love Allah.", null, null, null);   // sentenceId = 1
        sentenceDAO.createSentence(1, "We love the Prophet.", null, null, null); // sentenceId = 2

        tokenDAO.addToken(1, 1, "love"); // sentenceId = 1
        tokenDAO.addToken(2, 1, "love"); // sentenceId = 2
    }

    // ---------------------------------------------------------
    // TP1: No matching token → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No matching token → return empty list")
    void getSentencesByToken_NoMatch_ReturnsEmpty() {
        ArrayList<String> result = sentenceDAO.getSentencesByToken("xyz123");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Token appears twice → return list with 2 sentences
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Token appears twice → return list with 2 sentences")
    void getSentencesByToken_TwoMatches_ReturnsTwo() {
        ArrayList<String> result = sentenceDAO.getSentencesByToken("love");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("I love Allah.", result.get(0));
        assertEquals("We love the Prophet.", result.get(1));
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return empty list")
    void getSentencesByToken_DBFailure() throws Exception {
        conn.close();
        ArrayList<String> result = sentenceDAO.getSentencesByToken("love");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }
}
