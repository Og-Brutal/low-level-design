package test.java.com.apm.segmentedTokenDAO.testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Types;

import org.junit.jupiter.api.*;

import com.apm.dal.*;

public class AddSegmentsTest {

    private static Connection conn;

    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;
    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private SegmentedTokenDAO segmentDAO;

    private int sentenceId;
    private int tokenId;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM SegmentedToken");
            st.executeUpdate("ALTER TABLE SegmentedToken AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        // Initialize DAOs
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);
        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        segmentDAO = new SegmentedTokenDAO(conn);

        // -----------------------------
        // CHAIN CREATION (Required)
        // -----------------------------
        authorDAO.createAuthor("A1", null);
        bookDAO.createBook("B1", 1, null);
        chapterDAO.createChapter(1, "Ch1");

        // sentence
        sentenceDAO.createSentence(1, "نص", "nas", "text", "past");
        sentenceId = 1;

        // root → lemma → token
        rootDAO.addRoots("كتب");     // root 1
        lemmaDAO.addLemmas(1, "كتب"); // lemma 1

        tokenDAO.addToken(sentenceId, 1, "كتب"); // token_id = 1
        tokenId = 1;
    }

    // ---------------------------------------------------------
    // TP1: prefix = null → setNull → success
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: prefix = null → success")
    void addSegments_PrefixNull_Success() {
        boolean result = segmentDAO.addSegments(tokenId, null, "كتب", "كتب", "كتب");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP2: prefix non-empty → success
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: prefix non-empty → success")
    void addSegments_PrefixNonEmpty_Success() {
        boolean result = segmentDAO.addSegments(tokenId, "ال", "كتاب", "كتاب", "كتب");
        assertTrue(result);
    }

    // ---------------------------------------------------------
    // TP3: Insert fails → return false
    // (Simulate by passing invalid token_id)
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: invalid token_id → return false")
    void addSegments_InsertFails_ReturnsFalse() {
        boolean result = segmentDAO.addSegments(9999, "في", "كتب", "كتب", "كتب");
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP4: Simulate SQLException → return false
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP4: DB down → return false")
    void addSegments_PrepareFails_ReturnsFalse() throws Exception {
        conn.close();
        boolean result = segmentDAO.addSegments(tokenId, "بـ", "سم", "سم", "سم");
        assertFalse(result);

        // Reopen connection
        conn = DBConnectionDBC.getConnection();
        segmentDAO = new SegmentedTokenDAO(conn);
    }

    // ---------------------------------------------------------
    // TP5: setInt fails is DB-level impossible, but simulate using negative id
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP5: negative token_id → return false")
    void addSegments_SetIntFails_ReturnsFalse() {
        boolean result = segmentDAO.addSegments(-5, "و", "قرأ", "قرأ", "قرأ");
        assertFalse(result);
    }

    // ---------------------------------------------------------
    // TP6: executeUpdate fails → return false
    // (simulate by violating NOT NULL columns)
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP6: null root segmentation → fails")
    void addSegments_ExecuteUpdateFails_ReturnsFalse() {
        boolean result = segmentDAO.addSegments(tokenId, "لـ", null, null, null);
        assertFalse(result);
    }
}
