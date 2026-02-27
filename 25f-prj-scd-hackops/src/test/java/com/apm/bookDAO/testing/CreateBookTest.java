package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;

public class CreateBookTest {

    private static Connection conn;
    private BookDAO bookDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();  // Real TestDB connection
    }

    @BeforeEach
    void cleanTable() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
        }
        bookDAO = new BookDAO(conn);
    }

    // --- TP1: Valid author, valid era → row inserted ---
    @Test
    void testCreateBook_ValidAuthorValidEra() {
        boolean result = bookDAO.createBook("Book1", 1, "Modern");
        assertTrue(result);
    }

    // --- TP2: Valid author, null era → row inserted ---
    @Test
    void testCreateBook_ValidAuthorNullEra() {
        boolean result = bookDAO.createBook("Book2", 1, null);
        assertTrue(result);
    }

    // --- TP3: Null/invalid author, valid era → row inserted ---
    @Test
    void testCreateBook_NullAuthorValidEra() {
        boolean result = bookDAO.createBook("Book3", 0, "Modern"); // assuming 0 or negative means null
        assertTrue(result);
    }

    // --- TP4: Null/invalid author, null era → row inserted ---
    @Test
    void testCreateBook_NullAuthorNullEra() {
        boolean result = bookDAO.createBook("Book4", 0, null);
        assertTrue(result);
    }

    // --- TP5: Row not inserted (simulate by duplicate or invalid data) ---
    @Test
    void testCreateBook_RowNotInserted() {
        // Insert first time → success
        bookDAO.createBook("Book5", 1, "Era");
        // Insert again → assuming duplicate or invalid triggers failure
        boolean result = bookDAO.createBook("Book5", 1, "Era"); 
        // Depending on your DB constraints, adjust assertion
        assertFalse(result);
    }

    // --- TP6: Simulate SQLException by closing connection ---
    @Test
    void testCreateBook_SQLException() throws Exception {
        conn.close(); // Close connection to simulate failure

        boolean result = bookDAO.createBook("Book6", 1, "Era");
        assertFalse(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }
}
