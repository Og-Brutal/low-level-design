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

public class GetAllLemmaByRootTest {

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

            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);

        // Create required Root
        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");
    }

    // -------------------------------------------------------
    // TP1: No Lemmas → empty list
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: No lemmas for root → return empty list")
    void getLemmaByRoot_NoRows_ReturnsEmpty() {

        ArrayList<LemmaDTO> result = lemmaDAO.getLemmaByRoot(rootId);

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------
    // TP2: Root has 2 lemmas → return 2 items
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Two lemmas → return 2 DTOs")
    void getLemmaByRoot_TwoRows_ReturnsTwoDTOs() {

        // insert two lemmas
        lemmaDAO.addLemmas(rootId, "كتاب");
        lemmaDAO.addLemmas(rootId, "كتب");

        ArrayList<LemmaDTO> result = lemmaDAO.getLemmaByRoot(rootId);

        assertEquals(2, result.size());
        assertEquals("كتاب", result.get(0).getLemma());
        assertEquals("كتب", result.get(1).getLemma());
    }

    // -------------------------------------------------------
    // TP5: SQLException in prepareStatement/setInt
    // Simulated by closing DB connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return empty list")
    void getLemmaByRoot_SQLException_ReturnsEmpty() throws Exception {

        conn.close(); // force failure

        ArrayList<LemmaDTO> result = lemmaDAO.getLemmaByRoot(rootId);

        assertTrue(result.isEmpty());

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }
}
