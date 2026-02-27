package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dto.AuthorDTO;

public class RetrieveAuthorTest {

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
    @DisplayName("TP1: Author exists → return filled DTO")
    void retrieveAuthor_Found_ReturnsDto() {
        authorDAO.createAuthor("Ibn Kathir", "Great scholar");

        AuthorDTO result = authorDAO.retrieveAuthor("Ibn Kathir");

        assertNotNull(result);
        assertEquals("Ibn Kathir", result.getName());
        assertEquals("Great scholar", result.getBiography());
        assertEquals(1, result.getAuthorId()); // ID is 1 because table was cleared
    }

    @Test
    @DisplayName("TP2: Author not found → return null")
    void retrieveAuthor_NotFound_ReturnsNull() {
        AuthorDTO result = authorDAO.retrieveAuthor("NotExists123");
        assertNull(result);
    }

    @Test
    @DisplayName("TP3: Author with empty name → returns null")
    void retrieveAuthor_EmptyName_ReturnsNull() {
        // DAO converts empty name to null → cannot retrieve
        authorDAO.createAuthor("", "No name");

        AuthorDTO result = authorDAO.retrieveAuthor("");
        assertNull(result);
    }

    @Test
    @DisplayName("TP4: Multiple authors → retrieve correct author")
    void retrieveAuthor_MultipleAuthors() {
        authorDAO.createAuthor("Author1", "Bio1");
        authorDAO.createAuthor("Author2", "Bio2");

        AuthorDTO result1 = authorDAO.retrieveAuthor("Author1");
        AuthorDTO result2 = authorDAO.retrieveAuthor("Author2");

        assertNotNull(result1);
        assertEquals("Author1", result1.getName());

        assertNotNull(result2);
        assertEquals("Author2", result2.getName());
    }

    @Test
    @DisplayName("TP5: Simulate SQLException by closing connection → return null")
    void retrieveAuthor_ConnectionClosed_ReturnsNull() throws Exception {
        conn.close(); // Close connection to simulate failure

        AuthorDTO result = authorDAO.retrieveAuthor("AnyAuthor");
        assertNull(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
    }
}
