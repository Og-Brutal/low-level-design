package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.*;
import com.apm.dto.ChapterDTO;

public class RetrieveChapterTest {

    private static Connection conn;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real test DB connection
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

    @Test
    @DisplayName("TP1: No chapters → 0 loop iterations")
    void retrieveChapters_NoChapters() {
        ArrayList<ChapterDTO> result = chapterDAO.retrieveChapters(999); // non-existent bookId
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP2: Two chapters → returns correct ChapterDTO list")
    void retrieveChapters_TwoChapters() throws Exception {
        // 1. Create Author and Book
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);

        // 2. Create Chapters
        chapterDAO.createChapter(1, "First Chapter");
        chapterDAO.createChapter(1, "Second Chapter");

        // Call DAO function
        ArrayList<ChapterDTO> result = chapterDAO.retrieveChapters(1);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("First Chapter", result.get(0).getChapterName());
        assertEquals("Second Chapter", result.get(1).getChapterName());
    }

    @Test
    @DisplayName("TP3: Simulate DB failure → empty list")
    void retrieveChapters_DBFailure() throws Exception {
        conn.close(); // Simulate DB failure
        ArrayList<ChapterDTO> result = chapterDAO.retrieveChapters(1);
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);
    }
}
