package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import com.arabicprose.dto.SentenceDTO;
import com.arabicprose.dto.SearchDTO;

/**
 * Business Object implementation for search operations
 * Uses existing SentenceBO functionality
 */
public class SearchBO implements ISearchBO {

    private final IBusinessLayerFacade facade;

    public SearchBO(IBusinessLayerFacade facade) {
        this.facade = facade;
    }

    @Override
    public List<SentenceDTO> performTextSearch(SearchDTO searchDTO) throws SQLException {
        String searchText = searchDTO.getSearchText();
        boolean isRegex = searchDTO.isRegex();

        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedInput = normalize(searchText);  // Used for exact match

        List<SentenceDTO> allSentences = facade.getAllSentences();
        List<SentenceDTO> searchResults = new ArrayList<>();

        for (SentenceDTO sentence : allSentences) {
            if (isRegex) {
                // Regex mode: search in text, translation, and notes
                if (matchesRegex(sentence, searchText)) {
                    searchResults.add(sentence);
                }
            } else {
                // Exact full sentence match on Arabic text only
                String sentenceText = sentence.getText();
                if (sentenceText != null && normalize(sentenceText).equals(normalizedInput)) {
                    searchResults.add(sentence);
                }
            }
        }

        return searchResults;
    }

    @Override
    public List<SentenceBO.SimilarSentenceResult> performSimilaritySearch(SearchDTO searchDTO) throws SQLException {
        String inputSentence = searchDTO.getSearchText();
        int nGramSize = searchDTO.getNGramSize();
        double similarityThreshold = searchDTO.getSimilarityThreshold();

        if (inputSentence == null || inputSentence.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return facade.findSimilarSentences(inputSentence, nGramSize, similarityThreshold);
    }

    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        return facade.getAllSentences();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helper: Normalize whitespace and case (for exact full-sentence match)
    // ─────────────────────────────────────────────────────────────────────
    private String normalize(String text) {
        if (text == null) return "";
        return text.trim()
                   .replaceAll("\\s+", " ")   // collapse multiple spaces, tabs, newlines
                   .toLowerCase();
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helper: Regex matching on text, translation, and notes
    // ─────────────────────────────────────────────────────────────────────
    private boolean matchesRegex(SentenceDTO sentence, String patternText) {
        try {
            Pattern pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE);
            return pattern.matcher(sentence.getText() != null ? sentence.getText() : "").find() ||
                   pattern.matcher(sentence.getTranslation() != null ? sentence.getTranslation() : "").find() ||
                   pattern.matcher(sentence.getNotes() != null ? sentence.getNotes() : "").find();
        } catch (PatternSyntaxException e) {
            throw e; // Will be caught in UI
        }
    }
}