package test.java.com.apm.tokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class DeleteTokensBySentenceIdTest {

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

        // Create hierarchy
        authorDAO.createAuthor("Author1", null);                  // author_id = 1
        bookDAO.createBook("Book1", 1, null);                    // book_id = 1
        chapterDAO.createChapter(1, "Chapter One");              // chapter_id = 1
        sentenceDAO.createSentence(1, "Sentence1", "Dia1", "Tr1", "N1"); // sentence_id = 1
        sentenceDAO.createSentence(1, "Sentence2", "Dia2", "Tr2", "N2"); // sentence_id = 2

        // Add tokens
        tokenDAO.addToken(1, 1, "TokenA"); // token_id = 1
        tokenDAO.addToken(1, 1, "TokenB"); // token_id = 2
        tokenDAO.addToken(2, 1, "TokenC"); // token_id = 3
    }

    // ---------------------------------------------------------
    // TP1: No tokens → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No tokens → empty list")
    void deleteTokensBySentence_NoTokens() {
        ArrayList<Integer> deleted = tokenDAO.deleteTokensBySentence(999);
        assertTrue(deleted.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Delete tokens for sentence 1 → return IDs
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Two tokens found → deleted and returned")
    void deleteTokensBySentence_TwoTokens() {
        ArrayList<Integer> deleted = tokenDAO.deleteTokensBySentence(1);
        assertEquals(2, deleted.size());
        assertTrue(deleted.contains(1));
        assertTrue(deleted.contains(2));
    }

    // ---------------------------------------------------------
    // TP3: Delete tokens for sentence 2 → return single ID
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: Single token deleted → returned")
    void deleteTokensBySentence_OneToken() {
        ArrayList<Integer> deleted = tokenDAO.deleteTokensBySentence(2);
        assertEquals(1, deleted.size());
        assertTrue(deleted.contains(3));
    }

    // ---------------------------------------------------------
    // TP4: Delete tokens again → no tokens left
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: Deleting again → empty list")
    void deleteTokensBySentence_AlreadyDeleted() {
        tokenDAO.deleteTokensBySentence(1);
        ArrayList<Integer> deleted = tokenDAO.deleteTokensBySentence(1);
        assertTrue(deleted.isEmpty());
    }
}
