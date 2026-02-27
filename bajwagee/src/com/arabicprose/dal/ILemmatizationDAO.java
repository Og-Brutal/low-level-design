package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.FrequencyDTO;

public interface ILemmatizationDAO {
    void addLemma(LemmatizationDTO lemma) throws SQLException;
    List<LemmatizationDTO> getLemmasByTokenId(int tokenId) throws SQLException;
    List<LemmatizationDTO> getLemmasByAnalysisId(int analysisId) throws SQLException;
    void deleteLemmasByTokenId(int tokenId) throws SQLException;
    
    // New method for browsing
    List<LemmatizationDTO> getAllLemmas() throws SQLException;
    List<LemmatizationDTO> getLemmasByValue(String lemma) throws SQLException;
    
    // ==================== NEW METHOD FOR INDEXING ====================
    
    /**
     * Get all lemmas associated with a specific root
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
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    /**
     * Get lemma frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with lemma frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException;
    
    /**
     * Get lemma frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with lemma frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException;
}