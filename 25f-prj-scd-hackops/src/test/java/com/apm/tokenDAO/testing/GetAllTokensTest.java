package test.java.com.apm.tokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dal.SentenceDAO;
import com.apm.dal.TokenDAO;
import com.apm.dto.TokenDTO;

public class GetAllTokensTest {

    private Connection conn;
    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;

    @BeforeEach
    void setup() throws SQLException {
        conn = DBConnectionDBC.getConnection();

        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);

        try (Statement st = conn.createStatement()) {
            // Clear all tables
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            // Reset auto_increment
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        // Create hierarchy: Author → Book → Chapter → Sentence
        authorDAO.createAuthor("Author1", null);                     // author_id = 1
        bookDAO.createBook("Book1", 1, null);                        // book_id = 1
        chapterDAO.createChapter(1, "Chapter One");                  // chapter_id = 1
        sentenceDAO.createSentence(1, "Sentence1", "Dia1", "Tr1", "N1"); // sentence_id = 1
        sentenceDAO.createSentence(1, "Sentence2", "Dia2", "Tr2", "N2"); // sentence_id = 2

        // Add tokens (assuming lemma_id = 1 exists)
        tokenDAO.addToken(1, 1, "TokenA"); // token_id = 1
        tokenDAO.addToken(1, 1, "TokenB"); // token_id = 2
        tokenDAO.addToken(2, 1, "TokenC"); // token_id = 3
    }

    // ---------------------------------------------------------
    // TP1: No tokens in DB → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No tokens in DB → return empty list")
    void getAllTokens_NoTokens() throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Token");
        }

        ArrayList<TokenDTO> result = tokenDAO.getAllTokens();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Tokens exist → return all DTOs (2 token)
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Tokens exist → return all DTOs")
    void getAllTokens_WithTokens() {
        ArrayList<TokenDTO> result = tokenDAO.getAllTokens();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.get(0).getTokenId());
        assertEquals("TokenA", result.get(0).getToken());

        assertEquals(2, result.get(1).getTokenId());
        assertEquals("TokenB", result.get(1).getToken());

        assertEquals(3, result.get(2).getTokenId());
        assertEquals("TokenC", result.get(2).getToken());
    }

    // ---------------------------------------------------------
    // TP3: simulate SQLException in prepareStatement → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: simulate prepareStatement failure → return empty")
    void getAllTokens_PrepareFails() throws SQLException {
        conn.close(); // close connection to simulate prepareStatement failure

        ArrayList<TokenDTO> result = tokenDAO.getAllTokens();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }

    // ---------------------------------------------------------
    // TP4: simulate SQLException in executeQuery → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: simulate executeQuery failure → return empty")
    void getAllTokens_ExecuteQueryFails() throws SQLException {
        conn.close(); // executeQuery will fail

        ArrayList<TokenDTO> result = tokenDAO.getAllTokens();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Reopen connection
        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }


}
