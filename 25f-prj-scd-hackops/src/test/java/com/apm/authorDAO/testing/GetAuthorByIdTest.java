package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class GetAuthorByIdTest {

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
    @DisplayName("TP1: Author exists → return name")
    void getAuthorById_Found_ReturnsName() {
        // Insert author
        authorDAO.createAuthor("Ibn Hajar", "Scholar");

        // Assuming ID 1 because table was just cleared and auto_increment reset
        String result = authorDAO.getAuthorById(1);

        assertEquals("Ibn Hajar", result);
    }

    @Test
    @DisplayName("TP2: Author not found → return null")
    void getAuthorById_NotFound_ReturnsNull() {
        String result = authorDAO.getAuthorById(999); // Non-existent ID
        assertNull(result);
    }

    @Test
    @DisplayName("TP3: Author with null name → insert and fetch")
    void getAuthorById_NullName() {
        // Insert author with empty name → DAO converts to NULL
        authorDAO.createAuthor("", "Biography with no name");

        // Fetch by ID 1
        String result = authorDAO.getAuthorById(1);
        assertNull(result); // Name should be null
    }

    @Test
    @DisplayName("TP4: Multiple authors → fetch correct author by ID")
    void getAuthorById_MultipleAuthors() {
        authorDAO.createAuthor("Author1", "Bio1");
        authorDAO.createAuthor("Author2", "Bio2");

        String result1 = authorDAO.getAuthorById(1);
        String result2 = authorDAO.getAuthorById(2);

        assertEquals("Author1", result1);
        assertEquals("Author2", result2);
    }

    @Test
    @DisplayName("TP5: Simulate SQLException by closing connection → return null")
    void getAuthorById_ConnectionClosed_ReturnsNull() throws Exception {
        conn.close(); // Close connection to simulate failure

        String result = authorDAO.getAuthorById(1);
        assertNull(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
    }
}
