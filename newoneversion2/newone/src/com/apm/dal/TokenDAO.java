package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.TokenDTO;

public class TokenDAO implements ITokenDAO {

    private Connection conn;
    private Statement stmt;

    public TokenDAO(Connection conn, Statement stmt) {
        this.conn = conn;
        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------ ADD TOKEN ------------------------------
    @Override
    public boolean addToken(int sentenceID, int lemmaID, String text) {
        try {
            String sql = "INSERT INTO Token (sentence_id, lemma_id, token) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, sentenceID);
            ps.setInt(2, lemmaID);
            ps.setString(3, text);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
    public boolean deleteTokensBySentence(int sentenceID) {
        String sql = "DELETE FROM Token WHERE sentence_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sentenceID);

            int rows = ps.executeUpdate();   // number of deleted rows

            return rows > 0;  // true if at least one token was deleted
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // delete failed
    }

}
