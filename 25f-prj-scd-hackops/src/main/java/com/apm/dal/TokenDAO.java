package main.java.com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public class TokenDAO implements ITokenDAO {

    private Connection conn;
    

    public TokenDAO(Connection conn) {
        this.conn = conn;
       
    }

    // ------------------------------ ADD TOKEN ------------------------------
    @Override
    public int addToken(int sentenceID, int lemmaID, String text) {
        int generatedId = -1; // Default if insert fails
        try {
            String sql = "INSERT INTO Token (sentence_id, lemma_id, token) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, sentenceID);
            ps.setInt(2, lemmaID);
            ps.setString(3, text);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1); // token_id
                }
                rs.close();
            }

            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }


    // ------------------------------ SEARCH TOKEN ------------------------------
    @Override
    public ArrayList<TokenDTO> getTokensByLemma(int lemmaID) {
        ArrayList<TokenDTO> tokens = new ArrayList<>();
        try {
            String sql = "SELECT token_id, sentence_id, lemma_id, token FROM Token WHERE lemma_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, lemmaID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TokenDTO dto = new TokenDTO();
                dto.setTokenId(rs.getInt("token_id"));
                dto.setSentenceId(rs.getInt("sentence_id"));
                dto.setLemmaId(rs.getInt("lemma_id"));
                dto.setToken(rs.getString("token"));

                tokens.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tokens; // empty if no token found
    }

    // ------------------------------ GET ALL TOKENS ------------------------------
    @Override
    public ArrayList<TokenDTO> getAllTokens() {
        ArrayList<TokenDTO> tokens = new ArrayList<>();
        try {
            String sql = "SELECT token_id, sentence_id, lemma_id, token FROM Token";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TokenDTO dto = new TokenDTO();
                dto.setTokenId(rs.getInt("token_id"));
                dto.setSentenceId(rs.getInt("sentence_id"));
                dto.setLemmaId(rs.getInt("lemma_id"));
                dto.setToken(rs.getString("token"));

                tokens.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tokens;
    }
    @Override
    public ArrayList<Integer> deleteTokensBySentence(int sentenceID) {
        ArrayList<Integer> deletedTokenIds = new ArrayList<>();

        String selectSql = "SELECT token_id FROM Token WHERE sentence_id = ?";
        String deleteSql = "DELETE FROM Token WHERE sentence_id = ?";

        try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
            selectPs.setInt(1, sentenceID);
            ResultSet rs = selectPs.executeQuery();

            while (rs.next()) {
                deletedTokenIds.add(rs.getInt("token_id"));
            }

            if (!deletedTokenIds.isEmpty()) {
                try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                    deletePs.setInt(1, sentenceID);
                    deletePs.executeUpdate(); // delete the tokens
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deletedTokenIds; // returns empty list if no tokens were found/deleted
    }

    @Override
    public String getTokenText(int tokenID) {
        String tokenText = null;
        try {
            String sql = "SELECT token FROM Token WHERE token_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, tokenID);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tokenText = rs.getString("token");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tokenText;
    }
    @Override
    public ArrayList<String> getAllTokensByBook(int bookId) {
        ArrayList<String> tokens = new ArrayList<>();

        String sql =
            "SELECT DISTINCT t.token " +
            "FROM Token t " +
            "INNER JOIN Sentence s ON t.sentence_id = s.sentence_id " +
            "INNER JOIN Chapter c ON s.chapter_id = c.chapter_id " +
            "WHERE c.book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tokens.add(rs.getString("token"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return tokens;
    }


}
