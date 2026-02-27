package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.ChapterDAO;
import com.apm.dal.SentenceDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class CreateSentenceTest {

    private static Connection conn;
    private SentenceDAO sentenceDAO;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real DB connection
    }

    @BeforeEach
    void setup() throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = DBConnectionDBC.getConnection();
        }

        try (Statement st = conn.createStatement()) {
            // Clear dependent tables
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

        // Insert base data for sentences
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One"); // chapterId = 1
    }

    // ---------------------------------------------------------
    // TP1: All fields filled → success
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: All fields filled → success")
    void createSentence_AllFieldsFilled_ReturnsTrue() {
        boolean result = sentenceDAO.createSentence(
            1, "Hello World", "Hēllō", "Hi", "Important note"
        );
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP2: Optional fields null/empty → success
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Optional fields null/empty → success")
    void createSentence_AllOptionalEmpty_ReturnsTrue() {
        boolean result = sentenceDAO.createSentence(1, "Text", "", null, "   ");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP3: Mixed null/non-null → success
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: Mixed null/non-null → success")
    void createSentence_MixedNullAndFilled_ReturnsTrue() {
        boolean result = sentenceDAO.createSentence(1, "Text", "Diacritics", null, "Note");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP4: Simulate DB failure → return false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: DB failure → return false")
    void createSentence_DBFailure_ReturnsFalse() throws Exception {
        conn.close(); // simulate DB failure
        boolean result = sentenceDAO.createSentence(1, "Any", "Any", "Any", "Any");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
    }
}
