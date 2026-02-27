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

public class GetSentencesByRootTest {

    private static Connection conn;
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
        rootDAO.addRoots("كتب");           // rootId = 1
        lemmaDAO.addLemmas(1, "كتب");     // lemmaId = 1

        // Sentences
        sentenceDAO.createSentence(1, "كَتَبَ الرَّجُلُ كِتَابًا", "kataba", "The man wrote a book", "past"); // sentenceId = 1
        sentenceDAO.createSentence(1, "يَكْتُبُونَ الكُتُبَ", "yaktubūna", "They write the books", "present"); // sentenceId = 2

        // Tokens
        tokenDAO.addToken(1, 1, "كتب");
        tokenDAO.addToken(2, 1, "كتب");
    }

    // ---------------------------------------------------------
    // TP1: Root not found → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP1: Root not found → return empty list")
    void getSentencesByRoot_NoMatch() {
        ArrayList<SentenceDTO> result = sentenceDAO.getSentencesByRoot("غيرموجود");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------
    // TP2: Root appears in 2 sentences → return 2 DTOs
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP2: Root appears in 2 sentences → return list with 2 DTOs")
    void getSentencesByRoot_TwoMatches() {
        ArrayList<SentenceDTO> result = sentenceDAO.getSentencesByRoot("كتب");
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("كَتَبَ الرَّجُلُ كِتَابًا", result.get(0).getText());
        assertEquals("يَكْتُبُونَ الكُتُبَ", result.get(1).getText());
    }

    // ---------------------------------------------------------
    // TP3: Simulate DB failure → return empty list
    // ---------------------------------------------------------
    @Test
    @DisplayName("TP3: DB failure → empty list")
    void getSentencesByRoot_DBFailure() throws Exception {
        conn.close(); // simulate DB failure
        ArrayList<SentenceDTO> result = sentenceDAO.getSentencesByRoot("كتب");
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // reopen connection
        conn = DBConnectionDBC.getConnection();
        sentenceDAO = new SentenceDAO(conn);
    }
}
