package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class UpdateAuthorTest{

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
    @DisplayName("TP1: Author exists → updated → return true")
    void updateAuthor_Success_ReturnsTrue() {
        authorDAO.createAuthor("Ibn Taymiyyah", "Original bio");

        boolean result = authorDAO.updateAuthor("Ibn Taymiyyah", "Ibn Taymiyah", "Updated bio");
        assertTrue(result);

        // Optionally, verify the updated name and bio
        assertTrue(authorDAO.searchAuthor("Ibn Taymiyah") > 0);
    }

    @Test
    @DisplayName("TP2: Author not found → return false")
    void updateAuthor_NotFound_ReturnsFalse() {
        boolean result = authorDAO.updateAuthor("NotExists", "New Name", "Any");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: Update author with empty old name → return false")
    void updateAuthor_EmptyOldName_ReturnsFalse() {
        boolean result = authorDAO.updateAuthor("", "New Name", "Any");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP4: Update author with empty new name → return false")
    void updateAuthor_EmptyNewName_ReturnsFalse() {
        authorDAO.createAuthor("Author1", "Bio1");

        boolean result = authorDAO.updateAuthor("Author1", "", "Updated bio");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP5: Simulate SQLException by closing connection → return false")
    void updateAuthor_ConnectionClosed_ReturnsFalse() throws Exception {
        authorDAO.createAuthor("Author1", "Bio1");

        conn.close(); // Close connection to simulate failure
        boolean result = authorDAO.updateAuthor("Author1", "AuthorUpdated", "Updated bio");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
    }
}
