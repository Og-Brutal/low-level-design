package com.arabicprose.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.arabicprose.dto.TokenizationDTO;
import com.arabicprose.dto.FrequencyDTO;

public class TokenizationDAO implements ITokenizationDAO {
    
    @Override
    public void addToken(TokenizationDTO token) throws SQLException {
        String sql = "INSERT INTO Tokenization (analysis_id, token, position) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, token.getAnalysisId());
            pstmt.setString(2, token.getToken());
            pstmt.setInt(3, token.getPosition());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                token.setTokenId(rs.getInt(1));
            }
        }
    }
    
    @Override
    public List<TokenizationDTO> getTokensByAnalysisId(int analysisId) throws SQLException {
        List<TokenizationDTO> tokens = new ArrayList<>();
        String sql = "SELECT * FROM Tokenization WHERE analysis_id = ? ORDER BY position";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                tokens.add(new TokenizationDTO(
                    rs.getInt("token_id"),
                    rs.getInt("analysis_id"),
                    rs.getString("token"),
                    rs.getInt("position")
                ));
            }
        }
        return tokens;
    }
    
    @Override
    public void deleteTokensByAnalysisId(int analysisId) throws SQLException {
        String sql = "DELETE FROM Tokenization WHERE analysis_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<TokenizationDTO> getAllTokens() throws SQLException {
        List<TokenizationDTO> tokens = new ArrayList<>();
        String sql = "SELECT DISTINCT token FROM Tokenization ORDER BY token";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TokenizationDTO token = new TokenizationDTO();
                token.setToken(rs.getString("token"));
                tokens.add(token);
            }
        }
        return tokens;
    }

    @Override
    public List<TokenizationDTO> getTokensByValue(String tokenValue) throws SQLException {
        List<TokenizationDTO> tokens = new ArrayList<>();
        String sql = "SELECT * FROM Tokenization WHERE token = ? ORDER BY position";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, tokenValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TokenizationDTO token = new TokenizationDTO(
                    rs.getInt("token_id"),
                    rs.getInt("analysis_id"),
                    rs.getString("token"),
                    rs.getInt("position")
                );
                tokens.add(token);
            }
        }
        return tokens;
    }
    
    @Override
    public TokenizationDTO getTokenById(int tokenId) throws SQLException {
        String sql = "SELECT * FROM Tokenization WHERE token_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new TokenizationDTO(
                    rs.getInt("token_id"),
                    rs.getInt("analysis_id"),
                    rs.getString("token"),
                    rs.getInt("position")
                );
            }
        }
        return null;
    }
    
    // ==================== WORKING FREQUENCY ANALYSIS METHODS ====================
    
    @Override
    public List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException {
        return getTokenFrequenciesSimple();
    }
    
    @Override
    public List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException {
        return getTokenFrequenciesSimple();
    }
    
    private List<FrequencyDTO> getTokenFrequenciesSimple() throws SQLException {
        List<FrequencyDTO> frequencies = new ArrayList<>();
        
        String sql = "SELECT token, COUNT(*) as frequency FROM Tokenization GROUP BY token ORDER BY frequency DESC, token";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int total = 0;
            List<FrequencyDTO> tempList = new ArrayList<>();
            
            while (rs.next()) {
                String token = rs.getString("token");
                int frequency = rs.getInt("frequency");
                total += frequency;
                tempList.add(new FrequencyDTO(token, frequency, 0.0));
            }
            
            for (FrequencyDTO freq : tempList) {
                double percentage = total > 0 ? (freq.getFrequency() * 100.0 / total) : 0;
                frequencies.add(new FrequencyDTO(
                    freq.getItem(), 
                    freq.getFrequency(), 
                    Math.round(percentage * 100.0) / 100.0
                ));
            }
        }
        return frequencies;
    }
}