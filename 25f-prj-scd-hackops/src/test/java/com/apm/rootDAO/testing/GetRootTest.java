package test.java.com.apm.rootDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.RootDAO;
import com.apm.dto.RootDTO;

public class GetRootTest {

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

        // Insert a sample root
        rootDAO.addRoots("كتب");
    }

    // -------------------------------------------------------
    // TP1: Root exists → return filled RootDTO
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Root exists → return filled RootDTO")
    void getRoot_Found_ReturnsDto() {
        RootDTO root = rootDAO.getRoot("كتب");

        assertNotNull(root);
        assertEquals(1, root.getRootId()); // first root inserted, ID should be 1
        assertEquals("كتب", root.getRoot());
    }

    // -------------------------------------------------------
    // TP2: Root not found → return null
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Root not found → return null")
    void getRoot_NotFound_ReturnsNull() {
        RootDTO root = rootDAO.getRoot("nonexistent");
        assertNull(root);
    }

    // -------------------------------------------------------
    // TP5: SQLException → return null
    // Simulated by closing the connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return null")
    void getRoot_SQLException_ReturnsNull() throws Exception {
        conn.close(); // force failure

        RootDTO root = rootDAO.getRoot("error");
        assertNull(root);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        rootDAO = new RootDAO(conn);
    }
}
