package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dto.BookDTO;

public class GetAllBooksTest {

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
    @DisplayName("TP1: No books in database → returns empty list")
    void getAllBooks_NoBooks() {
        ArrayList<BookDTO> result = bookDAO.getAllBooks();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP2: Two books → returns list with 2 DTOs")
    void getAllBooks_TwoBooks() {
        bookDAO.createBook("Genesis", -1, "OT");
        bookDAO.createBook("Exodus", -1, "OT");

        ArrayList<BookDTO> result = bookDAO.getAllBooks();
        System.out.println(result.size());
        assertEquals(2, result.size());

        assertEquals("Genesis", result.get(0).getTitle());
        assertEquals("Exodus", result.get(1).getTitle());
    }

    @Test
    @DisplayName("TP3: Simulate SQLException by closing connection")
    void getAllBooks_ConnectionClosed_ReturnsEmpty() throws Exception {
        conn.close(); // Close connection to simulate failure

        ArrayList<BookDTO> result = bookDAO.getAllBooks();
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }

    @Test
    @DisplayName("TP4: Loop exception simulation")
    void getAllBooks_LoopException() throws Exception {
        // We can simulate by inserting valid data, then manually closing conn inside getAllBooks
        bookDAO.createBook("Genesis", -1, "OT");
        conn.close(); // Simulate failure when trying to read

        ArrayList<BookDTO> result = bookDAO.getAllBooks();
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }

    @Test
    @DisplayName("TP5: Another SQLException simulation")
    void getAllBooks_ExecuteQueryFails() throws Exception {
        conn.close(); // Close connection to simulate failure

        ArrayList<BookDTO> result = bookDAO.getAllBooks();
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }
}
