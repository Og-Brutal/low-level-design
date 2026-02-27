package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class DeleteAuthorTest {

    private static Connection conn;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();  // Real TestDB connection
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
    @DisplayName("TP1: Author exists → deleted → return true")
    void deleteAuthor_Success_ReturnsTrue() {
        authorDAO.createAuthor("Al-Ghazali", "Philosopher");
        boolean result = authorDAO.deleteAuthor("Al-Ghazali");
        assertTrue(result);
    }

    @Test
    @DisplayName("TP2: Author not found → return false")
    void deleteAuthor_NotFound_ReturnsFalse() {
        boolean result = authorDAO.deleteAuthor("NonExistentAuthor");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: Author with empty name → insert and delete fails")
    void deleteAuthor_EmptyName_Fails() {
        boolean inserted = authorDAO.createAuthor("", "No name");
        assertFalse(inserted);

        boolean deleted = authorDAO.deleteAuthor("");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("TP4: Delete multiple authors → only existing deleted")
    void deleteAuthor_MultipleAuthors() {
        authorDAO.createAuthor("Author1", "Bio1");
        authorDAO.createAuthor("Author2", "Bio2");

        boolean deleted1 = authorDAO.deleteAuthor("Author1");
        boolean deleted2 = authorDAO.deleteAuthor("NonExistingAuthor");

        assertTrue(deleted1);
        assertFalse(deleted2);
    }

    @Test
    @DisplayName("TP5: SQLException during deletion → return false")
    void deleteAuthor_ExecuteUpdateFails_ReturnsFalse() throws SQLException {
        // Close connection to simulate failure
        conn.close();

        boolean result = authorDAO.deleteAuthor("AnyAuthor");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
    }
}
