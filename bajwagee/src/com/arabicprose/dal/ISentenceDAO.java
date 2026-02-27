package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.SentenceDTO;

public interface ISentenceDAO {
    
    // --- Basic CRUD Operations ---
    void addSentence(SentenceDTO sentence) throws SQLException;
    List<SentenceDTO> getAllSentences() throws SQLException;
    void updateSentence(SentenceDTO sentence) throws SQLException;
    void deleteSentence(int sentenceId) throws SQLException;
    SentenceDTO getSentenceById(int sentenceId) throws SQLException;
    void addBatchSentences(List<SentenceDTO> sentences) throws SQLException;
    List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException;
    
    // --- Book-specific Operations ---
    List<SentenceDTO> getSentencesByBookId(int bookId) throws SQLException;
    int getSentenceCountByBookId(int bookId) throws SQLException;
    
    // --- Search Operations ---
    List<SentenceDTO> searchSentencesByText(String text) throws SQLException;
    List<SentenceDTO> searchSentencesByTranslation(String translation) throws SQLException;
    List<SentenceDTO> searchSentencesInBook(int bookId, String keyword) throws SQLException;
    
    // --- Utility Operations ---
    int getNextSentenceNumber(int bookId) throws SQLException;
    boolean isSentenceNumberExists(int bookId, int sentenceNumber) throws SQLException;
    SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException;
    
}