package test.java.com.apm.segmentedTokenDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.*;

import com.apm.dal.*;
import com.apm.dto.SegmentedTokenDTO;

public class GetAllSegmentsTest {

    private static Connection conn;

    private AuthorDAO authorDAO;
    private BookDAO bookDAO;
    private ChapterDAO chapterDAO;
    private SentenceDAO sentenceDAO;
    private TokenDAO tokenDAO;
    private LemmaDAO lemmaDAO;
    private RootDAO rootDAO;
    private SegmentedTokenDAO segmentedDAO;

    private int authorId, bookId, chapterId, sentenceId, tokenId, lemmaId, rootId;

    // --------------------------------------------------------
    // Initialize Real DB Connection
    // --------------------------------------------------------
    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();
    }

    // --------------------------------------------------------
    // Prepare DB before every test
    // --------------------------------------------------------
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

        // Initialize DAOs
        authorDAO = new AuthorDAO(conn);
        bookDAO = new BookDAO(conn);
        chapterDAO = new ChapterDAO(conn);
        sentenceDAO = new SentenceDAO(conn);
        tokenDAO = new TokenDAO(conn);
        lemmaDAO = new LemmaDAO(conn);
        rootDAO = new RootDAO(conn);
        segmentedDAO = new SegmentedTokenDAO(conn);

        // Full dependency creation chain
        authorDAO.createAuthor("Test Author","hey man testing !");
        authorId = authorDAO.searchAuthor("Test Author");

        bookDAO.createBook("Book1", authorId, "TestEra");
        bookId = bookDAO.searchBook("Book1");

        chapterDAO.createChapter(bookId, "Chap1");
        chapterId = chapterDAO.searchChapter("Chap1");

        sentenceDAO.createSentence(chapterId, "This is a test sentence.","","","");
        sentenceId = sentenceDAO.searchSentence("This is a test sentence.");

        rootDAO.addRoots("كتب");
        rootId = rootDAO.searchRoot("كتب");

        lemmaDAO.addLemmas( rootId,"كتاب");
        lemmaId = lemmaDAO.searchLemma("كتاب");

        tokenId=tokenDAO.addToken(sentenceId, lemmaId, "كتب");

        
        
    }

    // ---------------------------------------------------------------------
    // TP1: No rows → empty list
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("TP1: No segments → return empty list")
    void testGetAll_NoRows() {
        ArrayList<SegmentedTokenDTO> list = segmentedDAO.getAllSegments();

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // ---------------------------------------------------------------------
    // TP2: Insert two rows → return list with size 2
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("TP2: Two segments → return list with 2 DTOs")
    void testGetAll_TwoRows() {

        segmentedDAO.addSegments(tokenId, "ال", "كتب", "كتاب", "كتب");
        segmentedDAO.addSegments(tokenId, "في",  "علم", "علم", "علم");

        ArrayList<SegmentedTokenDTO> list = segmentedDAO.getAllSegments();

        assertEquals(2, list.size());
        assertEquals("ال", list.get(0).getPrefix());
        assertEquals("في", list.get(1).getPrefix());
    }

    // ---------------------------------------------------------------------
    // TP3: SQLException in prepareStatement → close connection
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("TP3: SQLException in prepareStatement → return empty list")
    void testGetAll_PrepareException() throws Exception {
        conn.close();   // force error

        ArrayList<SegmentedTokenDTO> list = segmentedDAO.getAllSegments();
        assertTrue(list.isEmpty());

        // reopen for next tests
        conn = DBConnectionDBC.getConnection();
        segmentedDAO = new SegmentedTokenDAO(conn);
    }

    // ---------------------------------------------------------------------
    // TP4: SQLException in executeQuery → simulate by corrupting table
    // ---------------------------------------------------------------------
    @Test
    @DisplayName("TP4: SQLException in executeQuery → return empty list")
    void testGetAll_ExecuteException() throws Exception {

        // Rename table temporarily to break SELECT
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("RENAME TABLE SegmentedToken TO SegmentedToken_BAD");
        }

        ArrayList<SegmentedTokenDTO> list = segmentedDAO.getAllSegments();
        assertTrue(list.isEmpty());

        // Restore table
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("RENAME TABLE SegmentedToken_BAD TO SegmentedToken");
        }
    }

    
}
