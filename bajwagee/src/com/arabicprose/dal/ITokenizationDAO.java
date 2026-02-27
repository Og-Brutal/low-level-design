package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.TokenizationDTO;
import com.arabicprose.dto.FrequencyDTO;

public interface ITokenizationDAO {
    void addToken(TokenizationDTO token) throws SQLException;
    List<TokenizationDTO> getTokensByAnalysisId(int analysisId) throws SQLException;
    void deleteTokensByAnalysisId(int analysisId) throws SQLException;
    
    // New method for browsing
    List<TokenizationDTO> getAllTokens() throws SQLException;
    List<TokenizationDTO> getTokensByValue(String token) throws SQLException;
    TokenizationDTO getTokenById(int tokenId) throws SQLException;
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    /**
     * Get token frequencies for a specific book
     * @param bookId The book ID
     * @return List of FrequencyDTO objects with token frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException;
    
    /**
     * Get token frequencies for a specific chapter
     * @param chapterId The chapter ID
     * @return List of FrequencyDTO objects with token frequencies and percentages
     * @throws SQLException
     */
    List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException;
}