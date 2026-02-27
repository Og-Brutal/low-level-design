package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.FrequencyDTO;

public interface IRootDAO {
    void addRoot(RootDTO root) throws SQLException;
    List<RootDTO> getRootsByTokenId(int tokenId) throws SQLException;
    List<RootDTO> getRootsByAnalysisId(int analysisId) throws SQLException;
    void deleteRootsByTokenId(int tokenId) throws SQLException;
    
    // New method for browsing
    List<RootDTO> getAllRoots() throws SQLException;
    List<RootDTO> getRootsByValue(String root) throws SQLException;
    
    // ==================== NEW METHOD FOR INDEXING ====================
    
    /**
     * Get a specific RootDTO by its root value (exact match)
     * @param rootValue The root value (e.g., "drs")
     * @return RootDTO object or null if not found
     * @throws SQLException
     */
    RootDTO getRootByValue(String rootValue) throws SQLException;
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    /**
     * Get root frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with root frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException;
    
    /**
     * Get root frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with root frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException;
}