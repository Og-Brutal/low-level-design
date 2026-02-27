package test.java.com.apm.rootDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.RootDAO;

public class AddRootsTest {

    private static Connection conn;
    private RootDAO rootDAO;

    @BeforeAll
    static void startDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {
        try (Statement st = conn.createStatement()) {
            // Clean Root table before each test
            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        rootDAO = new RootDAO(conn);
    }

    // -------------------------------------------------------
    // TP1: Root inserted successfully → return true
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Root inserted successfully → return true")
    void addRoots_Success_ReturnsTrue() {
        boolean result = rootDAO.addRoots("كتب");
        assertTrue(result);
    }

    // -------------------------------------------------------
    // TP2: Duplicate root → return false
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Duplicate root → return false")
    void addRoots_Duplicate_ReturnsFalse() {
        rootDAO.addRoots("كتب"); // Insert first time
        boolean result = rootDAO.addRoots("كتب"); // Attempt duplicate insert
        assertFalse(result);
    }

    // -------------------------------------------------------
    // TP3: Root table closed / SQL error → return false
    // Simulated by closing connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException → return false")
    void addRoots_SQLException_ReturnsFalse() throws Exception {
        conn.close(); // Force SQLException

        boolean result = rootDAO.addRoots("error");
        assertFalse(result);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        rootDAO = new RootDAO(conn);
    }
}
