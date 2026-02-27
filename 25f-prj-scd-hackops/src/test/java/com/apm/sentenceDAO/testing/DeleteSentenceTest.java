package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.SentenceDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.AuthorDAO;

public class DeleteSentenceTest {

    private static Connection conn;
    private SentenceDAO sentenceDAO;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = DBConnectionDBC.getConnection();
        }

        try (Statement st = conn.createStatement()) {
            // Clear all relevant tables
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);

        // Insert base data for testing delete
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One"); // chapterId = 1
        sentenceDAO.createSentence(1, "Sentence One", null, null, null); // sentenceId = 1
        sentenceDAO.createSentence(1, "Sentence Two", null, null, null); // sentenceId = 2
    }

    // ---------------------------------------------------------
    // TP1: Sentence exists → deleted → renumber called → true
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Sentence exists → deleted → true")
    void deleteSentence_Success() {
        // Delete sentenceId = 1 in chapterId = 1
        boolean result = sentenceDAO.deleteSentence(1, 1);
        assertTrue(result);

        // Ensure deletion
        boolean exists = sentenceDAO.deleteSentence(1, 2); // try deleting again
        System.out.println("sentence exist : : :"+ exists);
        assertFalse(exists); // already deleted → false
    }

    // ---------------------------------------------------------
    // TP2: No matching sentence → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: No matching sentence → false")
    void deleteSentence_NoMatch() {
        boolean result = sentenceDAO.deleteSentence(999, 1); // non-existent sentence
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → false")
    void deleteSentence_DBFailure() throws Exception {
        conn.close(); // simulate DB failure
        boolean result = sentenceDAO.deleteSentence(1, 1);
        assertFalse(result);

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
    }
}
