package test.java.com.apm.rootDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.apm.dal.BookDAO;
import com.apm.dal.ChapterDAO;
import com.apm.dal.DBConnectionDBC;
import com.apm.dal.LemmaDAO;
import com.apm.dal.RootDAO;
import com.apm.dal.SentenceDAO;
import com.apm.dal.TokenDAO;

public class GetAllRootsByBookTest {

    private static Connection conn;

    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;

    private int bookId;
    private int chapterId;
    private int sentenceId;

    @BeforeAll
    static void startDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {
        try (Statement st = conn.createStatement()) {
            // Clear tables
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("DELETE FROM Book");

            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
        }

        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);

        // Create book and chapter
        bookDAO.createBook("MyBook", -1, "");
        bookId = bookDAO.searchBook("MyBook");

        chapterDAO.createChapter(bookId, "Chapter 1");
        chapterId = chapterDAO.searchChapter("Chapter 1");

        // Create sentence
        sentenceDAO.createSentence(chapterId, "هذا كتاب جميل علم", "", "", "");
        sentenceId = sentenceDAO.searchSentence("هذا كتاب جميل علم");
    }

    // -------------------------------------------------------
    // TP1: No roots in book → return empty list
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: No roots in book → return empty list")
    void testTP1_NoRoots() {
        ArrayList<String> roots = rootDAO.getAllRootsByBook(999); // non-existent book
        assertNotNull(roots);
        assertTrue(roots.isEmpty());
    }

    // -------------------------------------------------------
    // TP2: Book has roots → return list of root strings
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Book has roots → return list of strings")
    void testTP2_RootsWithLemmaAndTokens() {
        // Step 1: Add roots
        rootDAO.addRoots("كتب");
        rootDAO.addRoots("علم");
        int rootId1 = rootDAO.searchRoot("كتب");
        int rootId2 = rootDAO.searchRoot("علم");

        // Step 2: Add lemmas for each root
        lemmaDAO.addLemmas(rootId1, "كتاب");
        lemmaDAO.addLemmas(rootId2, "علم");
        int lemmaId1 = lemmaDAO.searchLemma("كتاب");
        int lemmaId2 = lemmaDAO.searchLemma("علم");

        // Step 3: Add tokens linking sentence → lemma
        tokenDAO.addToken(sentenceId, lemmaId1, "كتاب");
        tokenDAO.addToken(sentenceId, lemmaId2, "علم");

        // Retrieve roots linked to book
        ArrayList<String> roots = rootDAO.getAllRootsByBook(bookId);
        assertEquals(2, roots.size());
        assertTrue(roots.contains("كتب"));
        assertTrue(roots.contains("علم"));
    }

    // -------------------------------------------------------
    // TP3: SQLException → return null
    // -------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException → return null")
    void testTP3_SQLExceptionReturnsNull() throws Exception {
        conn.close(); // force SQL exception

        ArrayList<String> roots = rootDAO.getAllRootsByBook(bookId);
        assertNull(roots);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);
    }
}
