package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;

public class DeleteBookTest {

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
    @DisplayName("TP1: Book exists → deleted → return true")
    void deleteBook_Success_ReturnsTrue() {
        bookDAO.createBook("Sahih al-Bukhari", 1, "Hadith Era");

        boolean result = bookDAO.deleteBook("Sahih al-Bukhari");
        assertTrue(result);
    }

    @Test
    @DisplayName("TP2: Book not found → return false")
    void deleteBook_NotFound_ReturnsFalse() {
        boolean result = bookDAO.deleteBook("NotExistsBook123");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: Delete book with empty name → return false")
    void deleteBook_EmptyName_ReturnsFalse() {
        boolean result = bookDAO.deleteBook("");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP4: Multiple books → delete correct book")
    void deleteBook_MultipleBooks() {
        bookDAO.createBook("Book1", 1, "Era1");
        bookDAO.createBook("Book2", 1, "Era2");

        boolean result1 = bookDAO.deleteBook("Book1");
        boolean result2 = bookDAO.deleteBook("Book2");

        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    @DisplayName("TP5: Simulate SQLException by closing connection → return false")
    void deleteBook_ConnectionClosed_ReturnsFalse() throws Exception {
        bookDAO.createBook("Book3", 1, "Era3");

        conn.close(); // Close connection to simulate failure
        boolean result = bookDAO.deleteBook("Book3");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }
}
