package test.java.com.apm.rootDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.RootDAO;
import com.apm.dto.RootDTO;

public class GetAllRootsTest {

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
    }

    // -------------------------------------------------------
    // TP1: No roots → return empty list
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: No roots → return empty list")
    void getAllRoots_NoRows_ReturnsEmpty() {
        ArrayList<RootDTO> roots = rootDAO.getAllRoots();
        assertNotNull(roots);
        assertTrue(roots.isEmpty());
    }

    // -------------------------------------------------------
    // TP2: Two roots → return list with 2 DTOs
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Two roots → return list with 2 DTOs")
    void getAllRoots_TwoRows_ReturnsTwoDTOs() {
        // Insert two roots
        rootDAO.addRoots("كتب");
        rootDAO.addRoots("علم");

        ArrayList<RootDTO> roots = rootDAO.getAllRoots();

        assertEquals(2, roots.size());
        assertEquals("كتب", roots.get(1).getRoot());
        assertEquals("علم", roots.get(0).getRoot());
        assertEquals(1, roots.get(1).getRootId());
        assertEquals(2, roots.get(0).getRootId());
    }

    // -------------------------------------------------------
    // TP5: SQLException → return empty list
    // Simulated by closing DB connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return empty list")
    void getAllRoots_SQLException_ReturnsEmpty() throws Exception {
        conn.close(); // Force failure

        ArrayList<RootDTO> roots = rootDAO.getAllRoots();
        assertNotNull(roots);
        assertTrue(roots.isEmpty());

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        rootDAO = new RootDAO(conn);
    }
}
