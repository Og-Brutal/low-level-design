package test.java.com.apm.tokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;
import com.apm.dal.SentenceDAO;
import com.apm.dal.TokenDAO;

public class GetTokenTest {

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

        // Clear all relevant tables and reset AUTO_INCREMENT
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

        // Create Root → Lemma → Token
        rootDAO.addRoots("كتب");          // root_id = 1
        lemmaDAO.addLemmas(1,"كِتَابٌ"); // lemma_id = 1
        tokenDAO.addToken(1, 1, "كِتَابٌ"); // token_id = 1
    }

    // ---------------------------------------------------------
    // TP1: Token exists → return token text
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Token exists → return token text")
    void getTokenText_Found_ReturnsText() {
        String result = tokenDAO.getTokenText(1);
        assertEquals("كِتَابٌ", result);
    }

    // ---------------------------------------------------------
    // TP2: Token not found → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Token not found → return null")
    void getTokenText_NotFound_ReturnsNull() {
        String result = tokenDAO.getTokenText(999);
        assertNull(result);
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return null
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: simulate DB failure → return null")
    void getTokenText_DBFailure_ReturnsNull() throws SQLException {
        conn.close();
        String result = tokenDAO.getTokenText(1);
        assertNull(result);

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        tokenDAO = new TokenDAO(conn);
    }
}
