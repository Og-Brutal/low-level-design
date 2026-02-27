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
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;
import com.apm.dal.SentenceDAO;
import com.apm.dal.TokenDAO;
import com.apm.dto.TokenDTO;

public class GetTokensByLemmaTest {

    private Connection conn;
    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private TokenDAO tokenDAO;

    @BeforeEach
    void setup() throws SQLException {
        conn = DBConnectionDBC.getConnection();

        // Clear all relevant tables and reset auto-increment
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("DELETE FROM Root");

            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        // Initialize DAOs
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        tokenDAO = new TokenDAO(conn);

        // Create Author → Book → Chapter → Sentence
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One");
        sentenceDAO.createSentence(1, "Text1", "T1", "Tr1", "N1");

        // Create Root → Lemma → Tokens
        rootDAO.addRoots("كتب");          // root_id = 1
        lemmaDAO.addLemmas(1,"كِتَابٌ"); // lemma_id = 1, linked to root_id 1
        tokenDAO.addToken(1, 1, "كِتَابٌ"); // token_id = 1
        tokenDAO.addToken(1, 1, "الْكِتَابِ"); // token_id = 2
    }

    // ---------------------------------------------------------
    // TP1: No tokens for lemma → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No tokens for lemma → empty list")
    void getTokensByLemma_NoTokens_ReturnsEmpty() {
        ArrayList<TokenDTO> result = tokenDAO.getTokensByLemma(999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Lemma has 2 tokens → return list with 2 DTOs
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Lemma has 2 tokens → return 2 DTOs")
    void getTokensByLemma_TwoTokens_ReturnsTwoDTOs() {
        ArrayList<TokenDTO> result = tokenDAO.getTokensByLemma(1);

        assertEquals(2, result.size());
        assertEquals("كِتَابٌ", result.get(0).getToken());
        assertEquals("الْكِتَابِ", result.get(1).getToken());
    }

    // ---------------------------------------------------------
    // TP3: Simulate prepareStatement failure → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: prepareStatement fails → empty list")
    void getTokensByLemma_PrepareFails() throws SQLException {
        conn.close(); // simulate DB failure
        ArrayList<TokenDTO> result = tokenDAO.getTokensByLemma(1);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }

    // ---------------------------------------------------------
    // TP4: executeQuery fails → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: executeQuery fails → empty list")
    void getTokensByLemma_ExecuteQueryFails() throws SQLException {
        conn.close(); // executeQuery will fail
        ArrayList<TokenDTO> result = tokenDAO.getTokensByLemma(1);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }

    // ---------------------------------------------------------
    // TP5: SQLException inside loop → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP5: exception inside loop → empty list")
    void getTokensByLemma_LoopException() throws SQLException {
        conn.close(); // simulate exception inside loop
        ArrayList<TokenDTO> result = tokenDAO.getTokensByLemma(1);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }
}
