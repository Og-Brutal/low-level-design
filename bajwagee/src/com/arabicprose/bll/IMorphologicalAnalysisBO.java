package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.AnalysisResultDTO;
import com.arabicprose.dto.TokenizationDTO;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.SegmentationDTO;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.FrequencyDTO;

public interface IMorphologicalAnalysisBO {
    
    // Basic analysis methods
    AnalysisResultDTO analyzeSentence(int sentenceId, String text) throws SQLException;
    AnalysisResultDTO analyzeTextAutomatically(int sentenceId, String text) throws SQLException;
    AnalysisResultDTO analyzeSingleSentence(int sentenceId, String text) throws SQLException;
    
    // Retrieval methods for browsing
    List<TokenizationDTO> getAllTokens() throws SQLException;
    List<LemmatizationDTO> getAllLemmas() throws SQLException;
    List<RootDTO> getAllRoots() throws SQLException;
    List<SegmentationDTO> getAllSegmentations() throws SQLException;
    
    // History and management
    List<AnalysisResultDTO> getAnalysisHistory(int sentenceId) throws SQLException;
    void deleteAnalysis(int analysisId) throws SQLException;
    
    // Additional retrieval methods for detailed browsing
    List<LemmatizationDTO> getLemmasByValue(String lemma) throws SQLException;
    List<RootDTO> getRootsByValue(String root) throws SQLException;
    List<TokenizationDTO> getTokensByValue(String token) throws SQLException;
    boolean hasAnalysis(int sentenceId) throws SQLException;
    List<TokenizationDTO> getAllTokensByAnalysis(int analysisId) throws SQLException;
    List<String> getRootsFromLemma(String lemma);
    
    /**
     * Analyzes an Arabic word and returns its segmentation in the specified format.
     * Returns: Prefix: <prefix>\nStem: <stem>\nPostfix: <postfix>
     * 
     * @param word The Arabic word to analyze
     * @return Formatted segmentation string
     */
    String analyzeWordSegmentation(String word);
    
    // ==================== NEW INDEXING METHODS ====================
    
    /**
     * Get all lemmas associated with a specific root ID
     * @param rootId The root ID
     * @return List of lemmas for this root
     * @throws SQLException
     */
    List<LemmatizationDTO> getLemmasByRootId(int rootId) throws SQLException;
    
    /**
     * Get all lemmas associated with a root value (string)
     * @param rootValue The root value (e.g., "drs")
     * @return List of lemmas for this root
     * @throws SQLException
     */
    List<LemmatizationDTO> getLemmasByRootValue(String rootValue) throws SQLException;
    RootDTO getRootByValue(String rootValue) throws SQLException;
    
    /**
     * Get lemmas for a specific analysis
     * @param analysisId The analysis ID
     * @return List of lemmas for this analysis
     * @throws SQLException
     */
    List<LemmatizationDTO> getLemmatizationsByAnalysis(int analysisId) throws SQLException;
    
    /**
     * Get roots for a specific analysis
     * @param analysisId The analysis ID
     * @return List of roots for this analysis
     * @throws SQLException
     */
    List<RootDTO> getRootsByAnalysis(int analysisId) throws SQLException;
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    /**
     * Get token frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with token frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException;
    
    /**
     * Get lemma frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with lemma frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException;
    
    /**
     * Get root frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with root frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException;
    
    /**
     * Get token frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with token frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException;
    
    /**
     * Get lemma frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with lemma frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException;
    
    /**
     * Get root frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with root frequencies
     * @throws SQLException
     */
    List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException;
    
    /**
     * Get top N most frequent tokens for a book
     * @param bookId The book ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top tokens
     * @throws SQLException
     */
    List<FrequencyDTO> getTopTokens(int bookId, int limit) throws SQLException;
    
    /**
     * Get top N most frequent lemmas for a book
     * @param bookId The book ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top lemmas
     * @throws SQLException
     */
    List<FrequencyDTO> getTopLemmas(int bookId, int limit) throws SQLException;
    
    /**
     * Get top N most frequent roots for a book
     * @param bookId The book ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top roots
     * @throws SQLException
     */
    List<FrequencyDTO> getTopRoots(int bookId, int limit) throws SQLException;
    
    /**
     * Get top N most frequent tokens for a chapter
     * @param chapterId The chapter ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top tokens
     * @throws SQLException
     */
    List<FrequencyDTO> getTopTokensByChapter(int chapterId, int limit) throws SQLException;
    
    /**
     * Get top N most frequent lemmas for a chapter
     * @param chapterId The chapter ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top lemmas
     * @throws SQLException
     */
    List<FrequencyDTO> getTopLemmasByChapter(int chapterId, int limit) throws SQLException;
    
    /**
     * Get top N most frequent roots for a chapter
     * @param chapterId The chapter ID
     * @param limit Number of top items to return
     * @return List of FrequencyDTO objects with top roots
     * @throws SQLException
     */
    List<FrequencyDTO> getTopRootsByChapter(int chapterId, int limit) throws SQLException;
    
    /**
     * Get overall statistics for a book
     * @param bookId The book ID
     * @return Object array with [totalTokens, totalLemmas, totalRoots, uniqueTokens, uniqueLemmas, uniqueRoots]
     * @throws SQLException
     */
    Object[] getBookStatistics(int bookId) throws SQLException;
    
    /**
     * Get overall statistics for a chapter
     * @param chapterId The chapter ID
     * @return Object array with [totalTokens, totalLemmas, totalRoots, uniqueTokens, uniqueLemmas, uniqueRoots]
     * @throws SQLException
     */
    Object[] getChapterStatistics(int chapterId) throws SQLException;
}