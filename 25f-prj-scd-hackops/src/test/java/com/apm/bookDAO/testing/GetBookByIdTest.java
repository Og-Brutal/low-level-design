package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;

public class GetBookByIdTest {

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
    @DisplayName("TP1: No row found → returns null")
    void getBookById_NoRecord_ReturnsNull() {
        String result = bookDAO.getBookById(5);
        assertNull(result);
    }

    @Test
    @DisplayName("TP2: Row found → returns book title")
    void getBookById_Found_ReturnsTitle() {
        // Insert a book first
        bookDAO.createBook("Data Structures", 1, "Modern Era");

        // ID = 1 because table was cleaned before
        String result = bookDAO.getBookById(1);
        assertEquals("Data Structures", result);
    }

    @Test
    @DisplayName("TP3: Simulate SQLException → returns null")
    void getBookById_SQLException_ReturnsNull() throws Exception {
        // Close connection to simulate DB failure
        conn.close();

        String result = bookDAO.getBookById(1);
        assertNull(result);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }
}
