package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class SearchAuthorTest {

    private static Connection conn;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real TestDB connection
    }

    @BeforeEach
    void cleanTable() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }
        authorDAO = new AuthorDAO(conn);
    }

    @Test
    @DisplayName("TP1: Author exists → return author_id")
    void searchAuthor_Found_ReturnsId() {
        authorDAO.createAuthor("Imam Nawawi", "Hadith master");

        int result = authorDAO.searchAuthor("Imam Nawawi");

        assertEquals(1, result); // ID should be 1 after clearing table
    }

    @Test
    @DisplayName("TP2: Author not found → return -1")
    void searchAuthor_NotFound_ReturnsMinusOne() {
        int result = authorDAO.searchAuthor("NotExistsXYZ");
        assertEquals(-1, result);
    }

    @Test
    @DisplayName("TP3: Author with empty name → returns -1")
    void searchAuthor_EmptyName_ReturnsMinusOne() {
        // DAO converts empty name to NULL → cannot find
        authorDAO.createAuthor("", "No name");

        int result = authorDAO.searchAuthor("");
        assertEquals(-1, result);
    }

    @Test
    @DisplayName("TP4: Multiple authors → returns correct IDs")
    void searchAuthor_MultipleAuthors() {
        authorDAO.createAuthor("Author1", "Bio1");
        authorDAO.createAuthor("Author2", "Bio2");

        int id1 = authorDAO.searchAuthor("Author1");
        int id2 = authorDAO.searchAuthor("Author2");

        assertEquals(1, id1);
        assertEquals(2, id2);
    }

    @Test
    @DisplayName("TP5: Simulate SQLException by closing connection → return -1")
    void searchAuthor_ConnectionClosed_ReturnsMinusOne() throws Exception {
        conn.close(); // Close connection to simulate failure

        int result = authorDAO.searchAuthor("AnyAuthor");
        assertEquals(-1, result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
    }
}
