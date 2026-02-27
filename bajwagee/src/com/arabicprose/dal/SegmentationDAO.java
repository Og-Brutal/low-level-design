package com.arabicprose.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.arabicprose.dto.SegmentationDTO;

public class SegmentationDAO implements ISegmentationDAO {
    
    @Override
    public void addSegmentation(SegmentationDTO segmentation) throws SQLException {
        String sql = "INSERT INTO Segmentation (token_id, prefix, stem, suffix) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, segmentation.getTokenId());
            pstmt.setString(2, segmentation.getPrefix());
            pstmt.setString(3, segmentation.getStem());
            pstmt.setString(4, segmentation.getSuffix());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                segmentation.setSegmentationId(rs.getInt(1));
            }
        }
    }
    
    @Override
    public SegmentationDTO getSegmentationByTokenId(int tokenId) throws SQLException {
        String sql = "SELECT * FROM Segmentation WHERE token_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Safely handle null values from database
                String prefix = rs.getString("prefix");
                String stem = rs.getString("stem");
                String suffix = rs.getString("suffix");
                
                // Log incomplete segmentations for debugging
                if ((prefix == null || prefix.trim().isEmpty()) && 
                    (stem == null || stem.trim().isEmpty()) && 
                    (suffix == null || suffix.trim().isEmpty())) {
                    System.out.println("Warning: Found empty segmentation for TokenID: " + tokenId);
                }
                
                return new SegmentationDTO(
                    rs.getInt("segmentation_id"),
                    rs.getInt("token_id"),
                    prefix != null ? prefix : "",
                    stem != null ? stem : "",
                    suffix != null ? suffix : ""
                );
            }
        }
        return null;
    }
    
    @Override
    public void deleteSegmentationByTokenId(int tokenId) throws SQLException {
        String sql = "DELETE FROM Segmentation WHERE token_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tokenId);
            pstmt.executeUpdate();
        }
    }
    @Override
    public List<SegmentationDTO> getAllSegmentations() throws SQLException {
        List<SegmentationDTO> segmentations = new ArrayList<>();
        String sql = "SELECT * FROM Segmentation ORDER BY segmentation_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Safely handle null values from database
                String prefix = rs.getString("prefix");
                String stem = rs.getString("stem");
                String suffix = rs.getString("suffix");
                
                // Log incomplete segmentations for debugging
                if ((prefix == null || prefix.trim().isEmpty()) && 
                    (stem == null || stem.trim().isEmpty()) && 
                    (suffix == null || suffix.trim().isEmpty())) {
                    System.out.println("Warning: Found empty segmentation (ID: " + 
                        rs.getInt("segmentation_id") + ", TokenID: " + 
                        rs.getInt("token_id") + ")");
                }
                
                SegmentationDTO segmentation = new SegmentationDTO(
                    rs.getInt("segmentation_id"),
                    rs.getInt("token_id"),
                    prefix != null ? prefix : "",
                    stem != null ? stem : "",
                    suffix != null ? suffix : ""
                );
                segmentations.add(segmentation);
            }
        }
        return segmentations;
    }
}