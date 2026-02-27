package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;

public class SearchChapterTest {

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
    }

    // ---------------------------------------------------------
    // TP1: Chapter exists → return chapterId
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Chapter exists → return chapterId")
    void testSearchChapter_Found() throws Exception {
        // 1. Create Author, Book, Chapter
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Introduction");

        // 2. Search chapter
        int chapterId = chapterDAO.searchChapter("Introduction");

        Assertions.assertEquals(1, chapterId); // First inserted chapterId = 1
    }

    // ---------------------------------------------------------
    // TP2: Chapter does not exist → return 0
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Chapter does not exist → return 0")
    void testSearchChapter_NotFound() {
        int chapterId = chapterDAO.searchChapter("NonExistingChapter");
        Assertions.assertEquals(0, chapterId);
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return 0
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return 0")
    void testSearchChapter_DBFailure() throws Exception {
        conn.close(); // Simulate DB failure
        int chapterId = chapterDAO.searchChapter("AnyChapter");
        Assertions.assertEquals(0, chapterId);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);
    }
}
