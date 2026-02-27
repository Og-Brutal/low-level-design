package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.DBConnectionDBC;
import com.apm.dal.SentenceDAO;

public class SearchSentenceTest {

    private Connection conn;
    private SentenceDAO sentenceDAO;

    @BeforeEach
    void setup() throws Exception {
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);

        try (Statement st = conn.createStatement()) {
            // Clear the Sentence table and reset AUTO_INCREMENT
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");

            // Insert test data
            st.executeUpdate("INSERT INTO Sentence (chapter_id, text, text_diacritized, translation, notes) " +
                    "VALUES (1, 'Hello world', 'H', 'Hi', 'Note')");
        }
    }

    // ---------------------------------------------------------
    // TP1: Sentence found → return sentence_id
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Sentence found → return sentence_id")
    void searchSentence_Found() {
        int id = sentenceDAO.searchSentence("Hello world");
        assertEquals(1, id);
    }

    // ---------------------------------------------------------
    // TP2: Sentence not found → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Sentence not found → return -1")
    void searchSentence_NotFound() {
        int id = sentenceDAO.searchSentence("Nonexistent text");
        assertEquals(-1, id);
    }

    // ---------------------------------------------------------
    // TP3: DB failure → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return -1")
    void searchSentence_DBFailure() throws SQLException {
        conn.close(); // simulate DB connection failure
        int id = sentenceDAO.searchSentence("Hello world");
        assertEquals(-1, id);
    }

    // ---------------------------------------------------------
    // TP4: Exception during executeQuery → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: executeQuery failure → return -1")
    void searchSentence_ExecuteQueryFails() throws SQLException {
        // Close the connection to force executeQuery to fail
        conn.close();
        int id = sentenceDAO.searchSentence("Hello world");
        assertEquals(-1, id);
    }

    // ---------------------------------------------------------
    // TP5: Exception in setString → return -1
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP5: setString failure → return -1")
    void searchSentence_SetStringFails() {
        // Simulate invalid input that might throw an exception (e.g., too long)
        String invalidText = new String(new char[100000]).replace('\0', 'A');
        int id = sentenceDAO.searchSentence(invalidText);
        assertEquals(-1, id);
    }
}
