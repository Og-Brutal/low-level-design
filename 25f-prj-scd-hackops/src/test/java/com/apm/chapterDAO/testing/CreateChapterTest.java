package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;

public class CreateChapterTest {

    private static Connection conn;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private int testBookId;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();  // Real TestDB connection
    }

    @BeforeEach
    void setup() throws Exception {
        try (Statement st = conn.createStatement()) {
            // Clean tables
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
        }

        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);

        // Insert a test book to satisfy foreign key
        bookDAO.createBook("Test Book", -1, "Test Era");
        testBookId = bookDAO.searchBook("Test Book"); // get its generated book_id
    }

    // ---------------------------------------------------------
    // TP1: Successful Insert → rowsInserted > 0 → return true
    // ---------------------------------------------------------
    @Test
    void testCreateChapter_Success() {
        boolean result = chapterDAO.createChapter(testBookId, "Introduction");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP2: Insert Failed → rowsInserted = 0 → return false
    // ---------------------------------------------------------
    @Test
    void testCreateChapter_FailedInsert() {
        // First insert → success
        chapterDAO.createChapter(testBookId, "Preface");
        // Insert same chapter again → may fail due to DB constraints
        boolean result = chapterDAO.createChapter(testBookId, "Preface");
        System.out.println("chapter "+result);
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP3: Simulate SQLException → return false
    // ---------------------------------------------------------
    @Test
    void testCreateChapter_SQLException() throws Exception {
        conn.close(); // Close connection to simulate DB error
        boolean result = chapterDAO.createChapter(testBookId, "Chapter X");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
    }
}
