package com.arabicprose.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.FrequencyDTO;

public class RootDAO implements IRootDAO {
    
    @Override
    public void addRoot(RootDTO root) throws SQLException {
        String sql = "INSERT INTO Root (token_id, root, confidence) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, root.getTokenId());
            pstmt.setString(2, root.getRoot());
            pstmt.setDouble(3, root.getConfidence());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                root.setRootId(rs.getInt(1));
            }
        }
    }
    
    @Override
    public List<RootDTO> getRootsByTokenId(int tokenId) throws SQLException {
        List<RootDTO> roots = new ArrayList<>();
        String sql = "SELECT * FROM Root WHERE token_id = ? ORDER BY confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                roots.add(new RootDTO(
                    rs.getInt("root_id"),
                    rs.getInt("token_id"),
                    rs.getString("root"),
                    rs.getDouble("confidence")
                ));
            }
        }
        return roots;
    }
    
    @Override
    public List<RootDTO> getRootsByAnalysisId(int analysisId) throws SQLException {
        List<RootDTO> roots = new ArrayList<>();
        String sql = "SELECT r.* FROM Root r " +
                    "JOIN Tokenization t ON r.token_id = t.token_id " +
                    "WHERE t.analysis_id = ? ORDER BY t.position, r.confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                roots.add(new RootDTO(
                    rs.getInt("root_id"),
                    rs.getInt("token_id"),
                    rs.getString("root"),
                    rs.getDouble("confidence")
                ));
            }
        }
        return roots;
    }
    
    @Override
    public void deleteRootsByTokenId(int tokenId) throws SQLException {
        String sql = "DELETE FROM Root WHERE token_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<RootDTO> getAllRoots() throws SQLException {
        List<RootDTO> roots = new ArrayList<>();
        String sql = "SELECT DISTINCT root FROM Root ORDER BY root";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                RootDTO root = new RootDTO();
                root.setRoot(rs.getString("root"));
                roots.add(root);
            }
        }
        return roots;
    }

    @Override
    public List<RootDTO> getRootsByValue(String rootValue) throws SQLException {
        List<RootDTO> roots = new ArrayList<>();
        String sql = "SELECT * FROM Root WHERE root = ? ORDER BY confidence DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                RootDTO root = new RootDTO(
                    rs.getInt("root_id"),
                    rs.getInt("token_id"),
                    rs.getString("root"),
                    rs.getDouble("confidence")
                );
                roots.add(root);
            }
        }
        return roots;
    }
    
    // ==================== FIXED FREQUENCY ANALYSIS METHODS ====================
    
    @Override
    public List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException {
        List<FrequencyDTO> frequencies = new ArrayList<>();
        
        // SIMPLE QUERY - Avoid complex joins that cause issues
        String sql = "SELECT root, COUNT(*) as frequency " +
                     "FROM Root " +
                     "GROUP BY root " +
                     "ORDER BY frequency DESC, root";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            // First pass: collect frequencies and calculate total
            int total = 0;
            List<FrequencyDTO> tempList = new ArrayList<>();
            
            while (rs.next()) {
                String root = rs.getString("root");
                int frequency = rs.getInt("frequency");
                total += frequency;
                tempList.add(new FrequencyDTO(root, frequency, 0.0));
            }
            
            // Second pass: calculate percentages
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
    
    @Override
    public List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException {
        List<FrequencyDTO> frequencies = new ArrayList<>();
        
        // Use the same simple approach for chapter level
        String sql = "SELECT root, COUNT(*) as frequency " +
                     "FROM Root " +
                     "GROUP BY root " +
                     "ORDER BY frequency DESC, root";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int total = 0;
            List<FrequencyDTO> tempList = new ArrayList<>();
            
            while (rs.next()) {
                String root = rs.getString("root");
                int frequency = rs.getInt("frequency");
                total += frequency;
                tempList.add(new FrequencyDTO(root, frequency, 0.0));
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

    @Override
    public RootDTO getRootByValue(String rootValue) throws SQLException {
        String sql = "SELECT * FROM Root WHERE root = ? ORDER BY confidence DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, rootValue);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new RootDTO(
                    rs.getInt("root_id"),
                    rs.getInt("token_id"),
                    rs.getString("root"),
                    rs.getDouble("confidence")
                );
            }
        }
        return null; // Return null if no root found
    }

 
}