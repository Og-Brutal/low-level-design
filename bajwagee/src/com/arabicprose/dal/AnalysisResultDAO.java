package com.arabicprose.dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.arabicprose.dto.AnalysisResultDTO;

public class AnalysisResultDAO implements IAnalysisResultDAO {
    
    @Override
    public void addAnalysisResult(AnalysisResultDTO analysis) throws SQLException {
        String sql = "INSERT INTO AnalysisResult (sentence_id, original_text, analyzed_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, analysis.getSentenceId());
            pstmt.setString(2, analysis.getOriginalText());
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                analysis.setAnalysisId(rs.getInt(1));
            }
        }
    }
    
    @Override
    public AnalysisResultDTO getAnalysisResultById(int analysisId) throws SQLException {
        String sql = "SELECT * FROM AnalysisResult WHERE analysis_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new AnalysisResultDTO(
                    rs.getInt("analysis_id"),
                    rs.getInt("sentence_id"),
                    rs.getString("original_text"),
                    rs.getTimestamp("analyzed_at")
                );
            }
        }
        return null;
    }
    
    @Override
    public List<AnalysisResultDTO> getAnalysisResultsBySentenceId(int sentenceId) throws SQLException {
        List<AnalysisResultDTO> analyses = new ArrayList<>();
        String sql = "SELECT * FROM AnalysisResult WHERE sentence_id = ? ORDER BY analyzed_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                analyses.add(new AnalysisResultDTO(
                    rs.getInt("analysis_id"),
                    rs.getInt("sentence_id"),
                    rs.getString("original_text"),
                    rs.getTimestamp("analyzed_at")
                ));
            }
        }
        return analyses;
    }
    
    @Override
    public void deleteAnalysisResult(int analysisId) throws SQLException {
        String sql = "DELETE FROM AnalysisResult WHERE analysis_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, analysisId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public boolean analysisExistsForSentence(int sentenceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM AnalysisResult WHERE sentence_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}