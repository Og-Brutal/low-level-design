package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.*;
import com.apm.dto.IndexRow;

public class GetIndexRowsByLemmaIdTest {

    private static Connection conn;
    private ChapterDAO chapterDAO;
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private TokenDAO tokenDAO;
    private LemmaDAO lemmaDAO;
    private RootDAO rootDAO;
    private SentenceDAO sentenceDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection(); // Real test DB connection
    }

    @BeforeEach
    void setup() throws Exception {
        if (conn == null || conn.isClosed()) {
            conn = DBConnectionDBC.getConnection();
        }

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM SegmentedToken");
            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("DELETE FROM Author");

            st.executeUpdate("ALTER TABLE SegmentedToken AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);
        tokenDAO = new TokenDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }

    @Test
    @DisplayName("TP1: No rows → empty list")
    void getIndexRowsByLemmaId_NoRows() {
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByLemmaId(999); // non-existent lemmaId
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP2: Two rows → returns correct IndexRow list")
    void getIndexRowsByLemmaId_TwoRows() throws Exception {
        // 1. Create Author and Book
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);

        // 2. Create Chapters
        chapterDAO.createChapter(1, "Genesis");
        chapterDAO.createChapter(1, "Exodus");

        // 3. Create Root and Lemma
        rootDAO.addRoots("R1");
        lemmaDAO.addLemmas(1, "L1");

        // 4. Create Sentences
        sentenceDAO.createSentence(1, "Sentence1", null, null, null);
        sentenceDAO.createSentence(2, "Sentence2", null, null, null);

        // 5. Add Tokens
        tokenDAO.addToken(1, 1, "token1");
        tokenDAO.addToken(2, 1, "token2");

        // Call DAO function
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByLemmaId(1);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Genesis", result.get(1).getChapterName());
        assertEquals(1, result.get(1).getSentenceNumber());
        assertEquals("Exodus", result.get(0).getChapterName());
        assertEquals(1, result.get(0).getSentenceNumber());
    }

    @Test
    @DisplayName("TP3: Simulate DB failure → empty list")
    void getIndexRowsByLemmaId_DBFailure() throws Exception {
        conn.close(); // Simulate DB failure
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByLemmaId(1);
        assertTrue(result.isEmpty());

        // Reopen connection for other tests
        conn = DBConnectionDBC.getConnection();
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        bookDAO = new BookDAO(conn);
        authorDAO = new AuthorDAO(conn);
        tokenDAO = new TokenDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
    }
}
