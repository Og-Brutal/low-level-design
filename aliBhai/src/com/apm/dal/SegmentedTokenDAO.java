package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.SegmentedTokenDTO;

public class SegmentedTokenDAO implements ISegmentedTokenDAO {
    private Connection conn;
    private Statement stmt;

    public SegmentedTokenDAO(Connection conn, Statement stmt) {
        this.conn = conn;
        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addSegments(int tokenID, String prefix, String stem, String lemma, String root) {
        String sql = "INSERT INTO SegmentedToken (tokenId, prefix, stem, lemma, root) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tokenID);

            // Only handle prefix: set NULL if empty
            if (prefix == null || prefix.trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, prefix);
            }

            // Stem, lemma, and root are inserted as-is
            ps.setString(3, stem);
            ps.setString(4, lemma);
            ps.setString(5, root);

            int rows = ps.executeUpdate();
            return rows > 0; // true if insertion succeeded
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<SegmentedTokenDTO> getAllSegments() {
        ArrayList<SegmentedTokenDTO> segments = new ArrayList<>();
        String sql = "SELECT segmentedTokenId, tokenId, prefix, stem, lemma, root FROM SegmentedToken";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SegmentedTokenDTO dto = new SegmentedTokenDTO();
                dto.setSegmentedTokenId(rs.getInt("segmentedTokenId"));
                dto.setTokenId(rs.getInt("tokenId"));
                dto.setPrefix(rs.getString("prefix"));
                dto.setStem(rs.getString("stem"));
                dto.setLemma(rs.getString("lemma"));
                dto.setRoot(rs.getString("root"));

                segments.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return segments;
    }

    @Override
    public boolean deleteSegments(int tokenID) {
        String sql = "DELETE FROM SegmentedToken WHERE tokenId = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tokenID);
            int rowsDeleted = ps.executeUpdate(); // execute deletion
            return rowsDeleted > 0; // return true if at least one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // deletion failed
        }
    }

}
