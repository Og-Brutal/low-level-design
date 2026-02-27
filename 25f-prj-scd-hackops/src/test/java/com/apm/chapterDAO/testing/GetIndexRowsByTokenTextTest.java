package test.java.com.apm.chapterDAO.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.*;
import com.apm.dto.IndexRow;

public class GetIndexRowsByTokenTextTest {

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
    @DisplayName("TP1: No matching token → empty list")
    void getIndexRowsByTokenText_NoRows() {
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByTokenText("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("TP2: Token appears in two sentences → returns correct IndexRow list")
    void getIndexRowsByTokenText_TwoRows() throws Exception {
        // 1. Create Author and Book
        authorDAO.createAuthor("Author1", null);
        bookDAO.createBook("Book1", 1, null);

        // 2. Create Chapters
        chapterDAO.createChapter(1, "Matthew");
        chapterDAO.createChapter(1, "Luke");

        // 3. Create Root and Lemma
        rootDAO.addRoots("R1");
        lemmaDAO.addLemmas(1, "L1");

        // 4. Create Sentences
        sentenceDAO.createSentence(1, "Love thy neighbor", null, null, null);
        sentenceDAO.createSentence(2, "Spread love and peace", null, null, null);

        // 5. Add Tokens
        tokenDAO.addToken(1, 1, "love");
        tokenDAO.addToken(2, 1, "love");

        // Call DAO function
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByTokenText("love");

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Matthew", result.get(1).getChapterName());
        assertEquals(1, result.get(1).getSentenceNumber());
        assertEquals("Luke", result.get(0).getChapterName());
        assertEquals(1, result.get(0).getSentenceNumber());
    }

    @Test
    @DisplayName("TP3: Simulate DB failure → empty list")
    void getIndexRowsByTokenText_DBFailure() throws Exception {
        conn.close(); // Simulate DB failure
        ArrayList<IndexRow> result = chapterDAO.getIndexRowsByTokenText("error");
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
