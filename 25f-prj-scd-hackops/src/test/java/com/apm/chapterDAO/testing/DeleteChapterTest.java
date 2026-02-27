package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.ChapterDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;

public class DeleteChapterTest {

    private static Connection conn;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real TestDB connection
    }

    @BeforeEach
    void setup() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
        }
        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);

        // Insert a book to satisfy foreign key constraint
        bookDAO.createBook("Test Book", 1, "Era");
    }

    // ---------------------------------------------------------
    // TP1: Successful Delete → rowsDeleted > 0 → return true
    // ---------------------------------------------------------
    @Test
    void deleteChapter_Success_ReturnsTrue() {
        chapterDAO.createChapter(1, "Introduction");

        boolean result = chapterDAO.deleteChapter(1, "Introduction");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP2: Delete Failed → rowsDeleted = 0 → return false
    // ---------------------------------------------------------
    @Test
    void deleteChapter_FailedDelete_ReturnsFalse() {
        // No chapter inserted
        boolean result = chapterDAO.deleteChapter(1, "NonExistingChapter");
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP3: Simulate SQLException → return false
    // ---------------------------------------------------------
    @Test
    void deleteChapter_SQLException_ReturnsFalse() throws Exception {
        chapterDAO.createChapter(1, "Chapter X");

        conn.close(); // Close connection to simulate failure
        boolean result = chapterDAO.deleteChapter(1, "Chapter X");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);
    }
}
