package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dto.AuthorDTO;

public class GetAllAuthorsTest {

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
    @DisplayName("TP1: No authors → return empty list")
    void getAllAuthors_NoRows_ReturnsEmpty() {
        ArrayList<AuthorDTO> result = authorDAO.getAllAuthors();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP2: Two authors → return list with 2 DTOs")
    void getAllAuthors_TwoRows_ReturnsTwoDTOs() {
        // Insert two authors into the test DB
        authorDAO.createAuthor("Ibn Taymiyyah", "Great scholar");
        authorDAO.createAuthor("Imam Nawawi", "Hadith master");

        ArrayList<AuthorDTO> result = authorDAO.getAllAuthors();

        assertEquals(2, result.size());
        assertEquals("Ibn Taymiyyah", result.get(0).getName());
        assertEquals("Imam Nawawi", result.get(1).getName());
    }

    @Test
    @DisplayName("TP3: Insert author with null values → getAllAuthors returns DTO")
    void getAllAuthors_WithNullValues() {
        // Insert author with null biography
        authorDAO.createAuthor("Al-Tabari", null);

        ArrayList<AuthorDTO> result = authorDAO.getAllAuthors();

        assertEquals(1, result.size());
        assertEquals("Al-Tabari", result.get(0).getName());
        assertNull(result.get(0).getBiography());
    }

    @Test
    @DisplayName("TP4: Delete all authors → getAllAuthors returns empty list")
    void getAllAuthors_AfterDeleteAll_ReturnsEmpty() {
        authorDAO.createAuthor("Author1", "Bio1");
        authorDAO.createAuthor("Author2", "Bio2");

        // Delete all authors
        authorDAO.deleteAuthor("Author1");
        authorDAO.deleteAuthor("Author2");

        ArrayList<AuthorDTO> result = authorDAO.getAllAuthors();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP5: Simulate failure by closing connection → getAllAuthors returns empty list")
    void getAllAuthors_ConnectionClosed_ReturnsEmpty() throws Exception {
        conn.close(); // Close connection to simulate failure

        ArrayList<AuthorDTO> result = authorDAO.getAllAuthors();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        authorDAO = new AuthorDAO(conn);
    }
}
