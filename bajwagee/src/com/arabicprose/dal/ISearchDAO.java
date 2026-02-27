package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.SentenceDTO;

/**
 * Data Access Object interface for search operations
 */
public interface ISearchDAO {
    
    /**
     * Search sentences using exact match or regex pattern
     */
    List<SentenceDTO> searchSentences(String searchText, boolean isRegex) throws SQLException;
    
    /**
     * Get all sentences for similarity comparison
     */
    List<SentenceDTO> getAllSentences() throws SQLException;
}