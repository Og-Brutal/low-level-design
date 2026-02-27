package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;

public class UpdateChapterTest {

    private static Connection conn;
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
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");

            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);

        // Insert default data
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Introduction"); // chapterId = 1
    }

    // ---------------------------------------------------------
    // TP1: Successful Update → return true
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Successful Update → return true")
    void testUpdateChapter_Success() {
        boolean result = chapterDAO.updateChaper(1, "Introduction", "Getting Started");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP2: Update non-existent chapter → return false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Update non-existent chapter → return false")
    void testUpdateChapter_FailedUpdate() {
        boolean result = chapterDAO.updateChaper(999, "NonExistent", "New Name");
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return false")
    void testUpdateChapter_DBFailure() throws Exception {
        conn.close(); // simulate DB failure
        boolean result = chapterDAO.updateChaper(1, "Introduction", "Changed Name");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);
    }
}
