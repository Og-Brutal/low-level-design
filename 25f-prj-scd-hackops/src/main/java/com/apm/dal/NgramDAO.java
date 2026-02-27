package main.java.com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class NgramDAO implements INgramDAO {
    private Connection conn;

    public NgramDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean saveNgrams(int sentenceId, Set<String> ngrams) {
        if (ngrams.isEmpty()) return true;
        // First clear existing ngrams for this sentence (for updates)
        String deleteSQL = "DELETE FROM NgramIndex WHERE sentence_id = ?";
        String insertSQL = "INSERT INTO NgramIndex (ngram_text, sentence_id) VALUES (?, ?)";

        try {
            // Delete old
            PreparedStatement delPs = conn.prepareStatement(deleteSQL);
            delPs.setInt(1, sentenceId);
            delPs.executeUpdate();

            // Insert new
            PreparedStatement insPs = conn.prepareStatement(insertSQL);
            for (String gram : ngrams) {
                insPs.setString(1, gram);
                insPs.setInt(2, sentenceId);
                insPs.addBatch(); // Batch insert for performance
            }
            insPs.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Integer> getCandidateSentenceIds(Set<String> ngrams) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (ngrams.isEmpty()) return ids;

        // Dynamic SQL: SELECT DISTINCT sentence_id FROM NgramIndex WHERE ngram_text IN (?,?,?)
        StringBuilder queryObj = new StringBuilder("SELECT DISTINCT sentence_id FROM NgramIndex WHERE ngram_text IN (");
        for (int i = 0; i < ngrams.size(); i++) {
            queryObj.append("?");
            if (i < ngrams.size() - 1) queryObj.append(",");
        }
        queryObj.append(")");

        try {
            PreparedStatement ps = conn.prepareStatement(queryObj.toString());
            int index = 1;
            for (String gram : ngrams) {
                ps.setString(index++, gram);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("sentence_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}