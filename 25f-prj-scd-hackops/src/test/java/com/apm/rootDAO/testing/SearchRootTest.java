package test.java.com.apm.rootDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.RootDAO;

public class SearchRootTest {

    private static Connection conn;
    private RootDAO rootDAO;

    @BeforeAll
    static void startDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {
        try (Statement st = conn.createStatement()) {
            // Clean Root table
            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        rootDAO = new RootDAO(conn);

        // Add a sample root for testing
        rootDAO.addRoots("كتب");
    }

    // -------------------------------------------------------
    // TP1: Root exists → return root_id
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Root exists → return root_id")
    void searchRoot_Found_ReturnsId() {
        int rootId = rootDAO.searchRoot("كتب");
        // Since it's the first root inserted, the ID should be 1
        assertEquals(1, rootId);
    }

    // -------------------------------------------------------
    // TP2: Root not found → return -1
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Root not found → return -1")
    void searchRoot_NotFound_ReturnsMinusOne() {
        int rootId = rootDAO.searchRoot("nonexistent");
        assertEquals(-1, rootId);
    }

    // -------------------------------------------------------
    // TP3: SQLException → return -1
    // Simulated by closing the connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException → return -1")
    void searchRoot_SQLException_ReturnsMinusOne() throws Exception {
        conn.close(); // Force SQLException

        int rootId = rootDAO.searchRoot("error");
        assertEquals(-1, rootId);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        rootDAO = new RootDAO(conn);
    }
}
