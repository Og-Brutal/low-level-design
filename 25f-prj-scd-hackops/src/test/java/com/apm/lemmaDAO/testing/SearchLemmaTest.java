package test.java.com.apm.lemmaDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;

public class SearchLemmaTest {

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

        // Create the required root
        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");
    }

    // -------------------------------------------------------
    // TP1: Lemma exists → return lemma_id
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Lemma exists → return lemma_id")
    void searchLemma_Found_ReturnsId() {

        // Insert lemma using real DAO
        lemmaDAO.addLemmas(rootId, "كتاب");

        int result = lemmaDAO.searchLemma("كتاب");

        assertEquals(1, result);
    }

    // -------------------------------------------------------
    // TP2: Lemma does not exist → return -1
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Lemma not found → return -1")
    void searchLemma_NotFound_ReturnsMinusOne() {

        int result = lemmaDAO.searchLemma("NotExist");

        assertEquals(-1, result);
    }

    // -------------------------------------------------------
    // TP5: SQLException in setString / prepareStatement
    // Simulated by closing the connection
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return -1")
    void searchLemma_SQLException_ReturnsMinusOne() throws Exception {

        conn.close(); // Force SQLException

        int result = lemmaDAO.searchLemma("test");

        assertEquals(-1, result);

        // Reopen DB after forced failure
        conn = DBConnectionDBC.getConnection();
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }

}
