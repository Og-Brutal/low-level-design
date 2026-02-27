package test.java.com.apm.sentenceDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.*;
import com.apm.dto.SentenceDTO;

public class GetSentencesByTokenPatternTest {

    private Connection conn;
    private SentenceDAO sentenceDAO;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private TokenDAO tokenDAO;

    @BeforeEach
    void setup() throws Exception {
        conn = DBConnectionDBC.getConnection();

        try (Statement st = conn.createStatement()) {
            // Clear tables
            st.executeUpdate("DELETE FROM SegmentedToken");
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("DELETE FROM Root");

            st.executeUpdate("ALTER TABLE SegmentedToken AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
        }

        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        tokenDAO = new TokenDAO(conn);

        // Insert base data
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);
        chapterDAO.createChapter(1, "Chapter One"); // chapterId = 1

        // Roots and Lemmas
        rootDAO.addRoots("حب");           // rootId = 1
        lemmaDAO.addLemmas(1, "حب");     // lemmaId = 1

        // Sentences
        sentenceDAO.createSentence(1, "We love Allah.", null, null, null);  // sentenceId = 1
        sentenceDAO.createSentence(1, "They love the Book.", null, null, null); // sentenceId = 2

        // Tokens
        tokenDAO.addToken(1, 1, "love");
        tokenDAO.addToken(2, 1, "love");
    }

    // ---------------------------------------------------------
    // TP1: No match → empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: No match → return empty list")
    void getSentencesByTokenPattern_NoMatch() {
        ArrayList<String> result = sentenceDAO.getSentencesByTokenPattern("xyz");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Pattern matches 2 sentences → return list with 2 items
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Pattern matches 2 sentences → return list with 2 items")
    void getSentencesByTokenPattern_TwoMatches() {
        ArrayList<String> result = sentenceDAO.getSentencesByTokenPattern("lo?e");
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("We love Allah.", result.get(0));
        assertEquals("They love the Book.", result.get(1));
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → return empty list")
    void getSentencesByTokenPattern_DBFailure() throws Exception {
        conn.close();
        ArrayList<String> result = sentenceDAO.getSentencesByTokenPattern("lo?e");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }
}
