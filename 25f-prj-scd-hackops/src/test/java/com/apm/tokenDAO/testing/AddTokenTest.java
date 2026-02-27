package test.java.com.apm.tokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.apm.dal.TokenDAO;

public class AddTokenTest {

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
        authorDAO.createAuthor("Author1", null);          // author_id = 1
        bookDAO.createBook("Book1", 1, null);            // book_id = 1
        chapterDAO.createChapter(1, "Chapter One");      // chapter_id = 1
        sentenceDAO.createSentence(1, "TestSentence", "DiaText", "Translation", "Notes"); // sentence_id = 1
    }

    // ---------------------------------------------------------
    // TP1: Insert success + generated key → return token_id
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Insert success → return token_id")
    void addToken_SuccessWithKey() {
        int tokenId = tokenDAO.addToken(1, 1, "كِتَابٌ");
        assertEquals(1, tokenId); // first inserted token
    }

    // ---------------------------------------------------------
    // TP2: Insert another token → incremented id
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Insert another token → return next token_id")
    void addToken_SecondToken() {
        tokenDAO.addToken(1, 1, "Word1");
        int tokenId = tokenDAO.addToken(1, 1, "Word2");
        assertEquals(2, tokenId);
    }

    // ---------------------------------------------------------
    // TP3: Invalid sentence_id → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: Invalid sentence_id → return -1")
    void addToken_InvalidSentence() {
        int tokenId = tokenDAO.addToken(999, 1, "Invalid");
        assertEquals(-1, tokenId);
    }

    // ---------------------------------------------------------
    // TP4: Null token text → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: Null token text → return -1")
    void addToken_NullTokenText() {
        int tokenId = tokenDAO.addToken(1, 1, null);
        assertEquals(-1, tokenId);
    }

 

    // ---------------------------------------------------------
    // TP6: Multiple inserts → check increment
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP6: Multiple inserts → check increment")
    void addToken_MultipleInserts() {
        int t1 = tokenDAO.addToken(1, 1, "WordA");
        int t2 = tokenDAO.addToken(1, 1, "WordB");
        int t3 = tokenDAO.addToken(1, 1, "WordC");

        assertEquals(t1 + 1, t2);
        assertEquals(t2 + 1, t3);
    }
}
