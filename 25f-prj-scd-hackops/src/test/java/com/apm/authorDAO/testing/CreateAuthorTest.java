package test.java.com.apm.authorDAO.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.jupiter.api.*;

import com.apm.dal.AuthorDAO;
import com.apm.dal.DBConnectionDBC;

public class CreateAuthorTest {

    private static Connection conn;
    private AuthorDAO authorDAO;

    @BeforeAll
    static void initDB() {
        conn = DBConnectionDBC.getConnection();  // Real TestDB Connection
    }

    @BeforeEach
    void cleanTable() throws Exception {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Author");
            st.executeUpdate("ALTER TABLE Author AUTO_INCREMENT = 1");
        }
        authorDAO = new AuthorDAO(conn);
    }

    @Test
    @DisplayName("TP1: Insert new author → returns true")
    void createAuthor_Success() {
        boolean result = authorDAO.createAuthor("Ibn Sina", "Persian polymath");
        assertTrue(result);
    }

    @Test
    @DisplayName("TP2: Insert duplicate author name → returns false (UNIQUE constraint)")
    void createAuthor_DuplicateName_Fails() {
        // First insert should succeed
        boolean first = authorDAO.createAuthor("Al-Tabari", "Historian");
        assertTrue(first);

        // Second insert with same name → should fail
        boolean duplicate = authorDAO.createAuthor("Al-Tabari", "Historian again");
        assertFalse(duplicate);
    }

    @Test
    @DisplayName("TP3: Insert author with empty name → returns false")
    void createAuthor_EmptyName_Fails() {
        boolean result = authorDAO.createAuthor("", "No name");
        assertFalse(result);
    }

    @Test
    @DisplayName("TP4: Insert author with null biography → returns true")
    void createAuthor_NullBio_Success() {
        boolean result = authorDAO.createAuthor("Ibn Kathir", null);
        assertTrue(result);
    }

    @Test
    @DisplayName("TP5: Insert author with long biography → returns true")
    void createAuthor_LongBio_Success() {
        String longBio = "A".repeat(1000);
        boolean result = authorDAO.createAuthor("Ibn Khaldun", longBio);
        assertTrue(result);
    }
}
