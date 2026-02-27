package test.java.com.apm.lemmaDAO.testing;

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

public class GetAllLemmasByBookTest {

    private static Connection conn;

    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;

    private int rootId;
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

        // 1: Create Root
        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");

        // 2: Create Book
        bookDAO.createBook("MyBook",-1,"");
        bookId = bookDAO.searchBook("MyBook");

        // 3: Create Chapter
        chapterDAO.createChapter(bookId, "Chapter 1");
        chapterId = chapterDAO.searchChapter("Chapter 1");

        // 4: Create Sentence
        sentenceDAO.createSentence(chapterId, "هذا كتاب جميل علم","","","");
        sentenceId = sentenceDAO.searchSentence("هذا كتاب جميل علم");
    }

    // -------------------------------------------------------
    // TP1: Normal case → tokens exist → return lemma strings
    // -------------------------------------------------------
    @Test
    @DisplayName("TP1: Retrieve lemmas by book when tokens exist")
    void testTP1() {
        // Create Lemmas and Tokens
        lemmaDAO.addLemmas(rootId, "كتاب");
        lemmaDAO.addLemmas(rootId, "علم");

        int lemmaId1 = lemmaDAO.searchLemma("كتاب");
        int lemmaId2 = lemmaDAO.searchLemma("علم");

        tokenDAO.addToken(sentenceId, lemmaId1, "كتاب");
        tokenDAO.addToken(sentenceId, lemmaId2, "علم");

        ArrayList<String> lemmas = lemmaDAO.getAllLemmasByBook(bookId);
        assertTrue(lemmas.contains("كتاب"));
        assertTrue(lemmas.contains("علم"));
        assertEquals(2, lemmas.size());
    }

    // -------------------------------------------------------
    // TP2: No lemma → return empty list
    // -------------------------------------------------------
    @Test
    @DisplayName("TP2: Book has no lemmas → return empty list")
    void testTP2_NoLemma() {
        ArrayList<String> lemmas = lemmaDAO.getAllLemmasByBook(bookId);
        assertNotNull(lemmas);
        assertTrue(lemmas.isEmpty());
    }

    // -------------------------------------------------------
    // TP5: SQLException → return null
    // -------------------------------------------------------
    @Test
    @DisplayName("TP5: SQLException → return null")
    void testTP5_SQLExceptionReturnsNull() throws Exception {
        conn.close();
        ArrayList<String> lemmas = lemmaDAO.getAllLemmasByBook(bookId);
        assertNull(lemmas);

        // Reopen DB for next tests
        conn = DBConnectionDBC.getConnection();
        lemmaDAO = new LemmaDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);
        rootDAO = new RootDAO(conn);
    }
}
