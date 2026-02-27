package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.arabicprose.dto.SentenceDTO;

/**
 * Data Access Object implementation for search operations
 */
public class SearchDAO implements ISearchDAO {
    
    @Override
    public List<SentenceDTO> searchSentences(String searchText, boolean isRegex) throws SQLException {
        // This method would typically interact with database
        // For now, we'll use the existing facade method through the business layer
        throw new UnsupportedOperationException("Use business layer facade for search operations");
    }
    
    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        // This method would typically interact with database
        // For now, we'll use the existing facade method through the business layer
        throw new UnsupportedOperationException("Use business layer facade for getting all sentences");
    }
    
    /**
     * Helper method to check if a sentence matches the search criteria
     * This replicates the matching logic from SearchPL
     */
    public boolean matches(SentenceDTO sentence, String searchText, boolean regexMode) {
        if (regexMode) {
            try {
                Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                return pattern.matcher(sentence.getText() != null ? sentence.getText() : "").find() ||
                       pattern.matcher(sentence.getTranslation() != null ? sentence.getTranslation() : "").find() ||
                       pattern.matcher(sentence.getNotes() != null ? sentence.getNotes() : "").find();
            } catch (PatternSyntaxException e) {
                throw e;
            }
        } else {
            // Exact match (case-insensitive)
            String searchLower = searchText.toLowerCase();
            return (sentence.getText() != null && sentence.getText().toLowerCase().contains(searchLower)) ||
                   (sentence.getTranslation() != null && sentence.getTranslation().toLowerCase().contains(searchLower)) ||
                   (sentence.getNotes() != null && sentence.getNotes().toLowerCase().contains(searchLower));
        }
    }
}