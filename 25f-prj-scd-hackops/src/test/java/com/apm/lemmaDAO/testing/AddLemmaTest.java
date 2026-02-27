package test.java.com.apm.lemmaDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;


public class AddLemmaTest {

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

        // Create dependency → root
        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");
    }

    // -------------------------------------------------------
    // TP1: Lemma inserted successfully → return true
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Lemma inserted → return true")
    void testAddLemma_Success() {

        boolean result = lemmaDAO.addLemmas(rootId, "كتاب");

        assertTrue(result);
    }
    

    // -------------------------------------------------------
    // TP2: SQLException (connection closed) → return false
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: SQLException → return false")
    void testAddLemma_SQLException() throws Exception {

        conn.close();  // Force SQLException

        boolean result = lemmaDAO.addLemmas(rootId, "اختبار");

        assertFalse(result);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }
}
