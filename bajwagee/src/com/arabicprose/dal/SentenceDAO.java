package com.arabicprose.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.arabicprose.dto.SentenceDTO;

public class SentenceDAO implements ISentenceDAO {
    

    // Other existing methods (showing key ones for context)
    
    @Override
    public void addSentence(SentenceDTO sentenceDTO) throws SQLException {
        String sql = "INSERT INTO Sentence (book_id, chapter_id, text, text_diacritized, translation, notes, sentence_number) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceDTO.getBookId());
            pstmt.setInt(2, sentenceDTO.getChapterId());
            pstmt.setString(3, sentenceDTO.getText());
            pstmt.setString(4, sentenceDTO.getTextDiacritized());
            pstmt.setString(5, sentenceDTO.getTranslation());
            pstmt.setString(6, sentenceDTO.getNotes());
            pstmt.setInt(7, sentenceDTO.getSentenceNumber());
            
            pstmt.executeUpdate();
        }
    }
    
    
    @Override
    public void addBatchSentences(List<SentenceDTO> sentences) throws SQLException {
        String sql = "INSERT INTO Sentence (book_id, chapter_id, text, text_diacritized, translation, notes, sentence_number) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (SentenceDTO s : sentences) {
                pstmt.setInt(1, s.getBookId());
                pstmt.setInt(2, s.getChapterId());
                pstmt.setString(3, s.getText());
                pstmt.setString(4, s.getTextDiacritized());
                pstmt.setString(5, s.getTranslation());
                pstmt.setString(6, s.getNotes());
                pstmt.setInt(7, s.getSentenceNumber());

                pstmt.addBatch();  // Add to batch
            }

            pstmt.executeBatch();  // Execute all inserts at once
        }
    }

    @Override
    public List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();

        String sql = "SELECT sentence_id, book_id, chapter_id, text, text_diacritized, translation, notes, sentence_number " +
                     "FROM Sentence WHERE chapter_id = ? ORDER BY sentence_number";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chapterId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    SentenceDTO s = new SentenceDTO();
                    s.setSentenceId(rs.getInt("sentence_id"));
                    s.setBookId(rs.getInt("book_id"));
                    s.setChapterId(rs.getInt("chapter_id"));
                    s.setText(rs.getString("text"));
                    s.setTextDiacritized(rs.getString("text_diacritized"));
                    s.setTranslation(rs.getString("translation"));
                    s.setNotes(rs.getString("notes"));
                    s.setSentenceNumber(rs.getInt("sentence_number"));

                    sentences.add(s);
                }
            }
        }

        return sentences;
    }

    
    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();
        String sql = "SELECT * FROM Sentence ORDER BY book_id, chapter_id, sentence_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                
                sentences.add(sentence);
            }
        }
        return sentences;
    }
    
    
    @Override
    public List<SentenceDTO> getSentencesByBookId(int bookId) throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();
        String sql = "SELECT * FROM Sentence WHERE book_id = ? ORDER BY chapter_id, sentence_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                
                sentences.add(sentence);
            }
        }
        return sentences;
    }
    
    @Override
    public void updateSentence(SentenceDTO sentenceDTO) throws SQLException {
        String sql = "UPDATE Sentence SET book_id = ?, chapter_id = ?, text = ?, text_diacritized = ?, " +
                    "translation = ?, notes = ?, sentence_number = ? WHERE sentence_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceDTO.getBookId());
            pstmt.setInt(2, sentenceDTO.getChapterId());
            pstmt.setString(3, sentenceDTO.getText());
            pstmt.setString(4, sentenceDTO.getTextDiacritized());
            pstmt.setString(5, sentenceDTO.getTranslation());
            pstmt.setString(6, sentenceDTO.getNotes());
            pstmt.setInt(7, sentenceDTO.getSentenceNumber());
            pstmt.setInt(8, sentenceDTO.getSentenceId());
            
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public void deleteSentence(int sentenceId) throws SQLException {
        String sql = "DELETE FROM Sentence WHERE sentence_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceId);
            pstmt.executeUpdate();
        }
    }
    
    @Override
    public SentenceDTO getSentenceById(int sentenceId) throws SQLException {
        String sql = "SELECT * FROM Sentence WHERE sentence_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sentenceId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                return sentence;
            }
        }
        return null;
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByText(String text) throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();
        String sql = "SELECT * FROM Sentence WHERE text LIKE ? ORDER BY book_id, chapter_id, sentence_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + text + "%");
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                
                sentences.add(sentence);
            }
        }
        return sentences;
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByTranslation(String translation) throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();
        String sql = "SELECT * FROM Sentence WHERE translation LIKE ? ORDER BY book_id, chapter_id, sentence_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + translation + "%");
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                
                sentences.add(sentence);
            }
        }
        return sentences;
    }
    
    @Override
    public List<SentenceDTO> searchSentencesInBook(int bookId, String keyword) throws SQLException {
        List<SentenceDTO> sentences = new ArrayList<>();
        String sql = "SELECT * FROM Sentence WHERE book_id = ? AND (text LIKE ? OR translation LIKE ?) " +
                    "ORDER BY chapter_id, sentence_number";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                
                sentences.add(sentence);
            }
        }
        return sentences;
    }
    
    @Override
    public int getSentenceCountByBookId(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Sentence WHERE book_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    @Override
    public int getNextSentenceNumber(int bookId) throws SQLException {
        String sql = "SELECT MAX(sentence_number) FROM Sentence WHERE book_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int maxNumber = rs.getInt(1);
                return maxNumber + 1;
            }
        }
        return 1;
    }
    
    @Override
    public boolean isSentenceNumberExists(int bookId, int sentenceNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Sentence WHERE book_id = ? AND sentence_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, sentenceNumber);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    @Override
    public SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException {
        String sql = "SELECT * FROM Sentence WHERE book_id = ? AND text = ? ORDER BY sentence_id DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookId);
            pstmt.setString(2, text);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                return sentence;
            }
        }
        return null;
    }
    
}