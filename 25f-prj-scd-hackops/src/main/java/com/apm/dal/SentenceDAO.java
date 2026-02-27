package main.java.com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.SentenceDTO;

public class SentenceDAO implements ISentenceDAO {
    private Connection conn;
    
    public SentenceDAO() {
       
    }

    public SentenceDAO(Connection conn) {
        this.conn = conn;
       
    }

    // 🟢 IMPLEMENTED: Get Sentence by ID
    @Override
    public SentenceDTO getSentenceById(int id) {
        SentenceDTO dto = null;
        try {
            String sql = "SELECT sentence_id, chapter_id, sentence_number, text, text_diacritized, translation, notes " +
                         "FROM Sentence WHERE sentence_id = ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                dto = new SentenceDTO();
                dto.setSentenceId(rs.getInt("sentence_id"));
                dto.setChapterId(rs.getInt("chapter_id"));
                dto.setSentenceNumber(rs.getInt("sentence_number"));
                dto.setText(rs.getString("text"));
                dto.setTextDiacritized(rs.getString("text_diacritized"));
                dto.setTranslation(rs.getString("translation"));
                dto.setNotes(rs.getString("notes"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dto;
    }

    // 🟢 IMPLEMENTED: Get Source Path (Book/Chapter/Sentence)
    @Override
    public String getSourcePath(int id) {
        String path = "Unknown Source";
        try {
            // Join 3 tables to get the full hierarchy
            String sql = "SELECT b.title, c.chapter_name, s.sentence_number " +
                         "FROM Sentence s " +
                         "JOIN Chapter c ON s.chapter_id = c.chapter_id " +
                         "JOIN Book b ON c.book_id = b.book_id " +
                         "WHERE s.sentence_id = ?";
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String bookTitle = rs.getString("title");
                String chapterName = rs.getString("chapter_name");
                int sentenceNum = rs.getInt("sentence_number");
                
                // Construct URL-like path
                path = "/" + bookTitle + "/" + chapterName + "/Sentence #" + sentenceNum;
            }
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    public boolean createSentence(int chapterID, String text, String textDiacritized, String translation, String notes) {
        try {
            String sql = "INSERT INTO Sentence (chapter_id, text, text_diacritized, translation, notes) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, chapterID);
            ps.setString(2, text);

            if (textDiacritized == null || textDiacritized.trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, textDiacritized);
            }

            if (translation == null || translation.trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, translation);
            }

            if (notes == null || notes.trim().isEmpty()) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, notes);
            }

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSenetence(int chapterID, int sentenceNumber, String text, String textDiacritized, String translation, String notes) {
        try {
            String sql = "UPDATE Sentence SET text = ?, text_diacritized = ?, translation = ?, notes = ? " +
                         "WHERE chapter_id = ? AND sentence_number = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, text);
            ps.setString(2, textDiacritized);
            ps.setString(3, translation);
            ps.setString(4, notes);
            ps.setInt(5, chapterID);
            ps.setInt(6, sentenceNumber);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteSentence(int chapterID, int sentenceNumber) {
        try {
            String sql = "DELETE FROM Sentence WHERE chapter_id = ? AND sentence_number = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, chapterID);
            ps.setInt(2, sentenceNumber);

            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                renumberSentences(chapterID);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void renumberSentences(int chapterID) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("SET @row = 0;");

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE Sentence SET sentence_number = (@row := @row + 1) WHERE chapter_id = ? ORDER BY sentence_id;"
            );
            ps.setInt(1, chapterID);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<SentenceDTO> retrieveSentence(int chapterID) {
        ArrayList<SentenceDTO> sentences = new ArrayList<>();
        try {
            String sql = "SELECT sentence_id, sentence_number, text, text_diacritized, translation, notes " +
                         "FROM Sentence WHERE chapter_id = ? ORDER BY sentence_number ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, chapterID);

            ResultSet rs = ps.executeQuery();
            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setChapterId(chapterID);
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));

                sentences.add(sentence);
            }
            if (!hasData) return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return sentences;
    }

    @Override
    public int searchSentence(String text) {
        try {
            String sql = "SELECT sentence_id FROM Sentence WHERE text = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, text);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("sentence_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public SentenceDTO sentenceByNumbers(int chapterNumber, int sentenceNumber) {
        try {
            String sql = "SELECT sentence_id, sentence_number, text, text_diacritized, translation, notes " +
                         "FROM Sentence WHERE chapter_id = ? AND sentence_number = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, chapterNumber);
            ps.setInt(2, sentenceNumber);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SentenceDTO dto = new SentenceDTO();
                dto.setSentenceId(rs.getInt("sentence_id"));
                dto.setChapterId(chapterNumber);
                dto.setSentenceNumber(rs.getInt("sentence_number"));
                dto.setText(rs.getString("text"));
                dto.setTextDiacritized(rs.getString("text_diacritized"));
                dto.setTranslation(rs.getString("translation"));
                dto.setNotes(rs.getString("notes"));
                return dto;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<String> getSentencesByToken(String token) {
        ArrayList<String> sentences = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT s.text " +
                         "FROM Sentence s " +
                         "INNER JOIN Token t ON s.sentence_id = t.sentence_id " +
                         "WHERE t.token = ? " +
                         "ORDER BY s.chapter_id, s.sentence_number";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, token);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sentences.add(rs.getString("text"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    @Override
    public ArrayList<String> getSentencesByTokenPattern(String tokenPattern) {
        ArrayList<String> result = new ArrayList<>();
        try {
            String sqlPattern = tokenPattern.replace("?", "_");
            String sql = "SELECT DISTINCT s.text " +
                         "FROM Sentence s " +
                         "INNER JOIN Token t ON s.sentence_id = t.sentence_id " +
                         "WHERE t.token LIKE ? " +
                         "ORDER BY s.chapter_id, s.sentence_number";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sqlPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("text"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public ArrayList<String> getAllSentences() {
        return null;
    }

    @Override
    public ArrayList<SentenceDTO> getSentencesByRoot(String rootText) {
        ArrayList<SentenceDTO> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT s.sentence_id, s.chapter_id, s.sentence_number, " +
                         "       s.text, s.text_diacritized, s.translation, s.notes " +
                         "FROM Sentence s " +
                         "INNER JOIN Token t ON s.sentence_id = t.sentence_id " +
                         "INNER JOIN Lemma l ON t.lemma_id = l.lemma_id " +
                         "INNER JOIN Root r ON l.root_id = r.root_id " +
                         "WHERE r.root = ? " +
                         "ORDER BY s.sentence_id ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, rootText);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SentenceDTO dto = new SentenceDTO();
                dto.setSentenceId(rs.getInt("sentence_id"));
                dto.setChapterId(rs.getInt("chapter_id"));
                dto.setSentenceNumber(rs.getInt("sentence_number"));
                dto.setText(rs.getString("text"));
                dto.setTextDiacritized(rs.getString("text_diacritized"));
                dto.setTranslation(rs.getString("translation"));
                dto.setNotes(rs.getString("notes"));
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}