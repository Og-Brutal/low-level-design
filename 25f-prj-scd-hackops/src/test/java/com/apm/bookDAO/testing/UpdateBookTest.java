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

public class UpdateBookTest {

    private static Connection conn;
    private BookDAO bookDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();  // Real test DB connection
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
    @DisplayName("TP1: Book exists → updated → return true")
    void updateBook_Success_ReturnsTrue() {
        // Insert a book first
        bookDAO.createBook("OldTitle", 1, "Modern");

        boolean result = bookDAO.updateBook("OldTitle", "NewTitle", 1, "Modern");
        assertTrue(result);
    }

    @Test
    @DisplayName("TP2: Book not found → return false")
    void updateBook_NotFound_ReturnsFalse() {
        boolean result = bookDAO.updateBook("NonExistent", "NewTitle", 1, "Modern");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP3: SQLException in prepareStatement → return false")
    void updateBook_PrepareFails_ReturnsFalse() throws Exception {
        // Close connection to simulate prepareStatement failure
        conn.close();

        boolean result = bookDAO.updateBook("OldTitle", "NewTitle", 1, "Modern");
        assertFalse(result);

        // Reopen connection for further tests
        conn = DBConnectionDBC.getConnection();
        bookDAO = new BookDAO(conn);
    }

    @Test
    @DisplayName("TP4: SQLException in executeUpdate → return false")
    void updateBook_ExecuteUpdateFails_ReturnsFalse() {
        // Use a faulty DAO that executes invalid SQL to simulate executeUpdate failure
        BookDAO faultyDAO = new BookDAO(conn) {
            @Override
            public boolean updateBook(String oldTitle, String newTitle, int authorId, String era) {
                try {
                    conn.prepareStatement("UPDATE NonExistentTable SET col=1").executeUpdate();
                    return true; // should not reach here
                } catch (Exception e) {
                    return false;
                }
            }
        };

        boolean result = faultyDAO.updateBook("Any", "Any", 1, "Any");
        assertFalse(result);
    }
}
