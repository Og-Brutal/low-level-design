package test.java.com.apm.segmentedTokenDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.*;
import com.apm.dal.DBConnectionDBC;

public class DeleteSegmentsTest {

    private static Connection conn;

    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;
    private RootDAO rootDAO;
    private LemmaDAO lemmaDAO;
    private SegmentedTokenDAO segmentedDAO;

    private int authorId, bookId, chapterId, sentenceId, lemmaId, rootId, tokenId, segId;

    @BeforeAll
    static void startDB() {
        conn = DBConnectionDBC.getConnection();
    }

    @BeforeEach
    void setup() throws Exception {

        try (Statement st = conn.createStatement()) {

            st.executeUpdate("DELETE FROM SegmentedToken");
            st.executeUpdate("ALTER TABLE SegmentedToken AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Token");
            st.executeUpdate("ALTER TABLE Token AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Lemma");
            st.executeUpdate("ALTER TABLE Lemma AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Root");
            st.executeUpdate("ALTER TABLE Root AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Sentence");
            st.executeUpdate("ALTER TABLE Sentence AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Chapter");
            st.executeUpdate("ALTER TABLE Chapter AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Book");
            st.executeUpdate("ALTER TABLE Book AUTO_INCREMENT = 1");

            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }

        // Initialize all DAOs
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);
        rootDAO = new RootDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        segmentedDAO = new SegmentedTokenDAO(conn);

        // Full chain creation
        authorDAO.createAuthor("DeleteTestAuthor","deleted author !");
        authorId = authorDAO.searchAuthor("DeleteTestAuthor");

        bookDAO.createBook("DelBook", authorId, "Era");
        bookId = bookDAO.searchBook("DelBook");

        chapterDAO.createChapter(bookId, "DelChap");
        chapterId = chapterDAO.searchChapter("DelChap");

        sentenceDAO.createSentence(chapterId, "delete test sentence","","","");
        sentenceId = sentenceDAO.searchSentence("delete test sentence");

        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");

        lemmaDAO.addLemmas(rootId,"كتاب" );
        lemmaId = lemmaDAO.searchLemma("كتاب");

        tokenId = tokenDAO.addToken(sentenceId, lemmaId, "كتب");
        
    }

    // ----------------------------------------------------------
    // TP1: Segments exist → delete → return true
    // ----------------------------------------------------------
    @Test
    @DisplayName("TP1: Existing Segments → delete → return true")
    void testDeleteSegments_Success() {

        segmentedDAO.addSegments(tokenId, "ال", "كتب", "كتاب", "كتب");

        boolean result = segmentedDAO.deleteSegments(tokenId);

        assertTrue(result);
    }

    // ----------------------------------------------------------
    // TP2: No Segments → return false
    // ----------------------------------------------------------
    @Test
    @DisplayName("TP2: No segments for token → return false")
    void testDeleteSegments_NotFound() {

        boolean result = segmentedDAO.deleteSegments(9999);

        assertFalse(result);
    }

    // ----------------------------------------------------------
    // TP3: SQLException (connection closed) → false
    // ----------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException in prepare → return false")
    void testDeleteSegments_SQLException() throws Exception {

        conn.close();  // force SQLException

        boolean result = segmentedDAO.deleteSegments(tokenId);
        assertFalse(result);

        // Reopen connection for next tests
        conn = DBConnectionDBC.getConnection();
        segmentedDAO = new SegmentedTokenDAO(conn);
    }
}
