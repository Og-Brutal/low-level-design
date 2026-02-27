package test.java.com.apm.bookDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.BookDAO;
import com.apm.dal.DBConnectionDBC;

public class SearchBookTest {

    private static Connection conn;
    private BookDAO bookDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real test DB connection
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
    @DisplayName("TP1: Book exists → returns book_id")
    void searchBook_Found_ReturnsId() {
        bookDAO.createBook("ABC", 1, "Modern");

        int bookId = bookDAO.searchBook("ABC");

        assertEquals(1, bookId);
    }

    @Test
    @DisplayName("TP2: Book not found → returns -1")
    void searchBook_NotFound_ReturnsMinusOne() {
        int bookId = bookDAO.searchBook("NonExistentBook");
        assertEquals(-1, bookId);
    }

    @Test
    @DisplayName("TP3: Simulate SQLException in prepareStatement → returns -1")
    void searchBook_PrepareFails_ReturnsMinusOne() throws Exception {
        conn.close(); // Simulate DB failure

        int bookId = bookDAO.searchBook("AnyBook");
        assertEquals(-1, bookId);

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }


}
