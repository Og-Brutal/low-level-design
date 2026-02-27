package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.bll.SentenceBO.SimilarSentenceResult;
import com.arabicprose.dto.SentenceDTO;

public interface ISentenceBO {
    
    // --- Basic CRUD Operations ---
    void addSentence(SentenceDTO sentenceDTO) throws SQLException;
    List<SentenceDTO> getAllSentences() throws SQLException;
    void updateSentence(SentenceDTO sentenceDTO) throws SQLException;
    void deleteSentence(int sentenceId) throws SQLException;
    SentenceDTO getSentenceById(int sentenceId) throws SQLException;
    
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
    List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException;
    SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException;
    
 // Add to ISentenceBO interface
    List<SimilarSentenceResult> findSimilarSentences(String inputSentence, int nGramSize, double similarityThreshold) throws SQLException;
    List<SimilarSentenceResult> findSimilarSentences(String inputSentence) throws SQLException;
    
}