package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.SentenceDTO;
import com.arabicprose.dto.SearchDTO;

/**
 * Business Object interface for search operations
 */
public interface ISearchBO {
    
    /**
     * Perform text search (exact match or regex)
     */
    List<SentenceDTO> performTextSearch(SearchDTO searchDTO) throws SQLException;
    
    /**
     * Perform similarity search using N-Grams
     */
    List<SentenceBO.SimilarSentenceResult> performSimilaritySearch(SearchDTO searchDTO) throws SQLException;
    
    /**
     * Get all sentences from database
     */
    List<SentenceDTO> getAllSentences() throws SQLException;
}