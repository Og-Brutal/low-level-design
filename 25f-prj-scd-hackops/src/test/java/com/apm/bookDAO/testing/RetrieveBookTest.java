package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dto.BookDTO;

public class RetrieveBookTest {

    private static Connection conn;
    private BookDAO bookDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real TestDB connection
    }

    @BeforeEach
    void cleanTable() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
        }
        bookDAO = new BookDAO(conn);
    }

    @Test
    @DisplayName("TP1: Book exists → returns filled DTO")
    void retrieveBook_Found_ReturnsDTO() {
        // Insert a book first
        bookDAO.createBook("ABC", 1, "Modern");

        // Retrieve the inserted book
        BookDTO result = bookDAO.retrieveBook("ABC");

        assertNotNull(result);
        assertEquals(1, result.getBookId()); // ID = 1 because table was cleaned
        assertEquals(1, result.getAuthorId());
        assertEquals("Modern", result.getEra());
    }

    @Test
    @DisplayName("TP2: Book does not exist → returns null")
    void retrieveBook_NotFound_ReturnsNull() {
        BookDTO result = bookDAO.retrieveBook("XYZ");
        assertNull(result);
    }

    @Test
    @DisplayName("TP3: Simulate SQLException → returns null")
    void retrieveBook_SQLException_ReturnsNull() throws Exception {
        // Close connection to simulate DB failure
        conn.close();

        BookDTO result = bookDAO.retrieveBook("ERR");
        assertNull(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }
}
