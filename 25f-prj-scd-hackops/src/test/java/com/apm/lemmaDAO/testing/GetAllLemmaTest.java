package test.java.com.apm.lemmaDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;
import com.apm.dto.LemmaDTO;

public class GetAllLemmaTest {

    private static Connection conn;

    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;

    private int rootId;

    @BeforeAll
    static void startDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {

        try (Statement st = conn.createStatement()) {

            // Clean tables before each test
            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);

        // Insert a root for lemmas
        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");
    }

    // -------------------------------------------------------
    // TP1: No lemmas → return empty list
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: No lemmas → return empty list")
    void getAllLemmas_NoRows_ReturnsEmpty() {

        ArrayList<LemmaDTO> result = lemmaDAO.getAllLemmas();

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------
    // TP2: Two lemmas → return list with 2 DTOs
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Two lemmas → return 2 DTOs")
    void getAllLemmas_TwoRows_ReturnsTwoDTOs() {

        // Insert two lemmas
        lemmaDAO.addLemmas(rootId, "كتاب");
        lemmaDAO.addLemmas(rootId, "كتب");

        ArrayList<LemmaDTO> result = lemmaDAO.getAllLemmas();

        assertEquals(2, result.size());
        assertEquals("كتاب", result.get(0).getLemma());
        assertEquals("كتب", result.get(1).getLemma());
    }

    // -------------------------------------------------------
    // TP5: SQLException inside loop → return empty list
    // Simulated by closing DB connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return empty list")
    void getAllLemmas_SQLException_ReturnsEmpty() throws Exception {

        conn.close(); // force failure

        ArrayList<LemmaDTO> result = lemmaDAO.getAllLemmas();

        assertTrue(result.isEmpty());

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }
}
