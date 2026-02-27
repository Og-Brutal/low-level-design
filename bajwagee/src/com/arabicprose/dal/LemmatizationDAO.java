package com.arabicprose.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.FrequencyDTO;

public class LemmatizationDAO implements ILemmatizationDAO {
    
    @Override
    public void addLemma(LemmatizationDTO lemma) throws SQLException {
        String sql = "INSERT INTO Lemmatization (token_id, lemma, confidence) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, lemma.getTokenId());
            pstmt.setString(2, lemma.getLemma());
            pstmt.setDouble(3, lemma.getConfidence());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                lemma.setLemmaId(rs.getInt(1));
            }
        }
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByTokenId(int tokenId) throws SQLException {
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        String sql = "SELECT * FROM Lemmatization WHERE token_id = ? ORDER BY confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                lemmas.add(new LemmatizationDTO(
                    rs.getInt("lemma_id"),
                    rs.getInt("token_id"),
                    rs.getString("lemma"),
                    rs.getDouble("confidence")
                ));
            }
        }
        return lemmas;
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByAnalysisId(int analysisId) throws SQLException {
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        String sql = "SELECT l.* FROM Lemmatization l " +
                    "JOIN Tokenization t ON l.token_id = t.token_id " +
                    "WHERE t.analysis_id = ? ORDER BY t.position, l.confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                lemmas.add(new LemmatizationDTO(
                    rs.getInt("lemma_id"),
                    rs.getInt("token_id"),
                    rs.getString("lemma"),
                    rs.getDouble("confidence")
                ));
            }
        }
        return lemmas;
    }
    
    @Override
    public void deleteLemmasByTokenId(int tokenId) throws SQLException {
        String sql = "DELETE FROM Lemmatization WHERE token_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<LemmatizationDTO> getAllLemmas() throws SQLException {
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        String sql = "SELECT DISTINCT lemma FROM Lemmatization ORDER BY lemma";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LemmatizationDTO lemma = new LemmatizationDTO();
                lemma.setLemma(rs.getString("lemma"));
                lemmas.add(lemma);
            }
        }
        return lemmas;
    }

    @Override
    public List<LemmatizationDTO> getLemmasByValue(String lemmaValue) throws SQLException {
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        String sql = "SELECT l.*, t.token, t.position " +
                    "FROM Lemmatization l " +
                    "JOIN Tokenization t ON l.token_id = t.token_id " +
                    "WHERE l.lemma = ? ORDER BY l.confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lemmaValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LemmatizationDTO lemma = new LemmatizationDTO(
                    rs.getInt("lemma_id"),
                    rs.getInt("token_id"),
                    rs.getString("lemma"),
                    rs.getDouble("confidence")
                );
                lemmas.add(lemma);
            }
        }
        return lemmas;
    }
    
    // ==================== NEW METHODS FOR INDEXING ====================
    
    @Override
    public List<LemmatizationDTO> getLemmasByRootId(int rootId) throws SQLException {
    	 System.out.println("lemma for this root is : ");
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        
        // SQL query: Join Lemmatization with Tokenization and Roots tables
        // Using DISTINCT to avoid duplicate lemmas
        String sql = "SELECT DISTINCT l.lemma_id, l.token_id, l.lemma, l.confidence " +
                    "FROM Lemmatization l " +
                    "JOIN Tokenization t ON l.token_id = t.token_id " +
                    "JOIN Roots r ON t.token_id = r.token_id " +
                    "WHERE r.root_id = ? " +
                    "ORDER BY l.confidence DESC, l.lemma";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rootId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LemmatizationDTO lemma = new LemmatizationDTO(
                    rs.getInt("lemma_id"),
                    rs.getInt("token_id"),
                    rs.getString("lemma"),
                    rs.getDouble("confidence")
                );
                lemmas.add(lemma);
            }
        }
        System.out.println("lemma for this root is : "+lemmas);
        return lemmas;
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByRootValue(String rootValue) throws SQLException {
        List<LemmatizationDTO> lemmas = new ArrayList<>();
        
        // SQL query: Join Lemmatization with Tokenization and Roots tables
        // Using DISTINCT to avoid duplicate lemmas
        String sql = "SELECT DISTINCT l.lemma_id, l.token_id, l.lemma, l.confidence " +
                    "FROM Lemmatization l " +
                    "JOIN Tokenization t ON l.token_id = t.token_id " +
                    "JOIN Roots r ON t.token_id = r.token_id " +
                    "WHERE r.root = ? " +
                    "ORDER BY l.confidence DESC, l.lemma";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LemmatizationDTO lemma = new LemmatizationDTO(
                    rs.getInt("lemma_id"),
                    rs.getInt("token_id"),
                    rs.getString("lemma"),
                    rs.getDouble("confidence")
                );
                lemmas.add(lemma);
            }
        }
        System.out.println(lemmas);
        return lemmas;
    }
    
    // ==================== WORKING FREQUENCY ANALYSIS METHODS ====================
    
    @Override
    public List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException {
        return getLemmaFrequenciesSimple();
    }
    
    @Override
    public List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException {
        return getLemmaFrequenciesSimple();
    }
    
    private List<FrequencyDTO> getLemmaFrequenciesSimple() throws SQLException {
        List<FrequencyDTO> frequencies = new ArrayList<>();
        
        String sql = "SELECT lemma, COUNT(*) as frequency FROM Lemmatization GROUP BY lemma ORDER BY frequency DESC, lemma";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int total = 0;
            List<FrequencyDTO> tempList = new ArrayList<>();
            
            while (rs.next()) {
                String lemma = rs.getString("lemma");
                int frequency = rs.getInt("frequency");
                total += frequency;
                tempList.add(new FrequencyDTO(lemma, frequency, 0.0));
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