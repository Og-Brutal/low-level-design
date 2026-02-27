package test.java.com.apm.tokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

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

public class GetAllTokensByBookTest {

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

        // Clear all tables and reset AUTO_INCREMENT
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
        sentenceDAO.createSentence(1, "Text2", "T2", "Tr2", "N2");

        // Create Root → Lemma → Tokens
        rootDAO.addRoots("كتب");          // root_id = 1
        rootDAO.addRoots("رجُل");         // root_id = 2

        lemmaDAO.addLemmas(1,"كِتَابٌ"); // lemma_id = 1
        lemmaDAO.addLemmas(2,"الرَّجُلُ"); // lemma_id = 2

        tokenDAO.addToken(1, 1, "كِتَابٌ");     // sentence_id = 1
        tokenDAO.addToken(2, 2, "الرَّجُلُ");  // sentence_id = 2
    }

    // ---------------------------------------------------------
    // TP1: No tokens → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No tokens in book → return empty list")
    void getAllTokensByBook_NoTokens_ReturnsEmpty() throws SQLException {
        ArrayList<String> result = tokenDAO.getAllTokensByBook(999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Two distinct tokens → return list with 2 strings
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Book has 2 distinct tokens → return list with 2 strings")
    void getAllTokensByBook_TwoTokens_ReturnsList() throws SQLException {
        ArrayList<String> result = tokenDAO.getAllTokensByBook(1); // book_id = 1
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("كِتَابٌ"));
        assertTrue(result.contains("الرَّجُلُ"));
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure in prepareStatement → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException in prepareStatement → return null")
    void getAllTokensByBook_PrepareFails_ReturnsNull() throws SQLException {
        conn.close();
        ArrayList<String> result = tokenDAO.getAllTokensByBook(1);
        assertNull(result);

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }
}
