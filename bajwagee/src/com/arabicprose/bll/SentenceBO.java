package com.arabicprose.bll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arabicprose.dal.DBConnection;
import com.arabicprose.dal.IDataAccessLayerFacade;
import com.arabicprose.dto.SentenceDTO;

public class SentenceBO implements ISentenceBO {

    private IDataAccessLayerFacade sentenceDAO;
    
    public SentenceBO(IDataAccessLayerFacade sentenceDAO)
    {
    	this.sentenceDAO = sentenceDAO;
    }

    // Existing methods remain the same...
    @Override
    public void addSentence(SentenceDTO sentenceDTO) throws SQLException {
        if (sentenceDTO.getBookId() <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        if (sentenceDTO.getText() == null || sentenceDTO.getText().isEmpty()) {
            throw new IllegalArgumentException("Sentence text is required.");
        }
        
        // Auto-generate sentence number if not provided
        if (sentenceDTO.getSentenceNumber() <= 0) {
            sentenceDTO.setSentenceNumber(sentenceDAO.getNextSentenceNumber(sentenceDTO.getBookId()));
        }
        
        // Check if sentence number already exists
        if (sentenceDAO.isSentenceNumberExists(sentenceDTO.getBookId(), sentenceDTO.getSentenceNumber())) {
            throw new IllegalArgumentException("Sentence number " + sentenceDTO.getSentenceNumber() + 
                    " already exists for this book. Please choose a different number.");
        }
        
        sentenceDAO.addSentence(sentenceDTO);
        sentenceDTO.setSentenceId(sentenceDTO.getSentenceId()); // Update DTO with generated ID
    }

    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        return sentenceDAO.getAllSentences();
    }

    @Override
    public void updateSentence(SentenceDTO sentenceDTO) throws SQLException {
        if (sentenceDTO.getBookId() <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        if (sentenceDTO.getText() == null || sentenceDTO.getText().isEmpty()) {
            throw new IllegalArgumentException("Sentence text is required.");
        }
        
        // Check if sentence number already exists (excluding current sentence)
        SentenceDTO existingSentence = sentenceDAO.getSentenceById(sentenceDTO.getSentenceId());
        if (existingSentence != null && 
            existingSentence.getSentenceNumber() != sentenceDTO.getSentenceNumber() &&
            sentenceDAO.isSentenceNumberExists(sentenceDTO.getBookId(), sentenceDTO.getSentenceNumber())) {
            throw new IllegalArgumentException("Sentence number " + sentenceDTO.getSentenceNumber() + 
                    " already exists for this book. Please choose a different number.");
        }
        
        sentenceDAO.updateSentence(sentenceDTO);
    }

    @Override
    public void deleteSentence(int sentenceId) throws SQLException {
        sentenceDAO.deleteSentence(sentenceId);
    }

    @Override
    public SentenceDTO getSentenceById(int sentenceId) throws SQLException {
        SentenceDTO sentence = sentenceDAO.getSentenceById(sentenceId);
        return sentence;
    }

    @Override
    public List<SentenceDTO> getSentencesByBookId(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        return sentenceDAO.getSentencesByBookId(bookId);
    }

    @Override
    public int getSentenceCountByBookId(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        return sentenceDAO.getSentenceCountByBookId(bookId);
    }

    @Override
    public List<SentenceDTO> searchSentencesByText(String text) throws SQLException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text is required.");
        }
        return sentenceDAO.searchSentencesByText(text.trim());
    }

    @Override
    public List<SentenceDTO> searchSentencesByTranslation(String translation) throws SQLException {
        if (translation == null || translation.trim().isEmpty()) {
            throw new IllegalArgumentException("Search translation is required.");
        }
        return sentenceDAO.searchSentencesByTranslation(translation.trim());
    }

    @Override
    public List<SentenceDTO> searchSentencesInBook(int bookId, String keyword) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword is required.");
        }
        return sentenceDAO.searchSentencesInBook(bookId, keyword.trim());
    }
    
    @Override
    public List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException {
        // Your implementation to get sentences by chapter ID
        String sql = "SELECT * FROM Sentence WHERE chapter_id = ? ORDER BY sentence_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chapterId);
            ResultSet rs = pstmt.executeQuery();
            
            List<SentenceDTO> sentences = new ArrayList<>();
            while (rs.next()) {
                SentenceDTO sentence = new SentenceDTO();
                sentence.setSentenceId(rs.getInt("sentence_id"));
                sentence.setBookId(rs.getInt("book_id"));
                sentence.setChapterId(rs.getInt("chapter_id"));
                sentence.setSentenceNumber(rs.getInt("sentence_number"));
                sentence.setText(rs.getString("text"));
                sentence.setTextDiacritized(rs.getString("text_diacritized"));
                sentence.setTranslation(rs.getString("translation"));
                sentence.setNotes(rs.getString("notes"));
                sentences.add(sentence);
            }
            return sentences;
        }
    }

    @Override
    public int getNextSentenceNumber(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        return sentenceDAO.getNextSentenceNumber(bookId);
    }

    @Override
    public boolean isSentenceNumberExists(int bookId, int sentenceNumber) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        if (sentenceNumber <= 0) {
            throw new IllegalArgumentException("Valid sentence number is required.");
        }
        return sentenceDAO.isSentenceNumberExists(bookId, sentenceNumber);
    }
    
    @Override
    public SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty.");
        }
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        
        return sentenceDAO.getSentenceByTextAndBook(text, bookId);
    }

    // ==================== N-GRAM SIMILARITY SEARCH METHODS ====================

    /**
     * Generate n-grams from a given text
     * @param text The input text
     * @param n The size of n-grams (1 for unigrams, 2 for bigrams, etc.)
     * @return Set of n-grams
     */
    private Set<String> generateNGrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return ngrams;
        }
        
        String cleanedText = text.trim().replaceAll("\\s+", " ");
        String[] words = cleanedText.split("\\s+");
        
        if (words.length < n) {
            // If text has fewer words than n, return the entire text as single n-gram
            ngrams.add(cleanedText);
            return ngrams;
        }
        
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder ngram = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) ngram.append(" ");
                ngram.append(words[i + j]);
            }
            ngrams.add(ngram.toString());
        }
        
        return ngrams;
    }

    /**
     * Calculate Jaccard similarity between two sets of n-grams
     * @param ngrams1 First set of n-grams
     * @param ngrams2 Second set of n-grams
     * @return Similarity score between 0.0 and 1.0
     */
    private double calculateSimilarity(Set<String> ngrams1, Set<String> ngrams2) {
        if (ngrams1.isEmpty() && ngrams2.isEmpty()) {
            return 1.0;
        }
        if (ngrams1.isEmpty() || ngrams2.isEmpty()) {
            return 0.0;
        }
        
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);
        
        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);
        
        return (double) intersection.size() / union.size();
    }

    /**
     * Find similar sentences using n-gram similarity
     * @param inputSentence The input sentence to compare against
     * @param nGramSize The size of n-grams to use (default: 2 for bigrams)
     * @param similarityThreshold Minimum similarity threshold (0.0 to 1.0)
     * @return List of similar sentences with their similarity scores and source information
     * @throws SQLException
     */
    @Override
    public List<SimilarSentenceResult> findSimilarSentences(String inputSentence, int nGramSize, double similarityThreshold) throws SQLException {
        if (inputSentence == null || inputSentence.trim().isEmpty()) {
            throw new IllegalArgumentException("Input sentence cannot be null or empty.");
        }
        if (nGramSize < 1) {
            throw new IllegalArgumentException("N-gram size must be at least 1.");
        }
        if (similarityThreshold < 0.0 || similarityThreshold > 1.0) {
            throw new IllegalArgumentException("Similarity threshold must be between 0.0 and 1.0.");
        }

        // Generate n-grams for input sentence
        Set<String> inputNGrams = generateNGrams(inputSentence, nGramSize);
        
        // Get all sentences from database
        List<SentenceDTO> allSentences = getAllSentences();
        List<SimilarSentenceResult> similarSentences = new ArrayList<>();

        for (SentenceDTO sentence : allSentences) {
            if (sentence.getText() == null || sentence.getText().trim().isEmpty()) {
                continue;
            }
            
            // Generate n-grams for current sentence
            Set<String> sentenceNGrams = generateNGrams(sentence.getText(), nGramSize);
            
            // Calculate similarity
            double similarity = calculateSimilarity(inputNGrams, sentenceNGrams);
            
            // Add to results if above threshold
            if (similarity >= similarityThreshold) {
                SimilarSentenceResult result = new SimilarSentenceResult(
                    sentence,
                    similarity,
                    generateSourceURL(sentence)
                );
                similarSentences.add(result);
            }
        }

        // Sort by similarity score (descending)
        similarSentences.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        
        return similarSentences;
    }

    /**
     * Find similar sentences with default parameters (bigrams, 0.5 threshold)
     * @param inputSentence The input sentence to compare against
     * @return List of similar sentences
     * @throws SQLException
     */
    @Override
    public List<SimilarSentenceResult> findSimilarSentences(String inputSentence) throws SQLException {
        return findSimilarSentences(inputSentence, 2, 0.5);
    }

    /**
     * Generate a URL-like source identifier for a sentence
     * @param sentence The sentence DTO
     * @return Source identifier string
     */
    private String generateSourceURL(SentenceDTO sentence) {
        // Format: book/{bookId}/chapter/{chapterId}/sentence/{sentenceId}
        // If chapterId is 0 or negative, you might want to handle it differently
        int chapterId = sentence.getChapterId();
        
        // If chapterId is not valid, you can use a placeholder
        if (chapterId <= 0) {
            return String.format("book/%d/sentence/%d", 
                sentence.getBookId(), 
                sentence.getSentenceId());
        } else {
            return String.format("book/%d/chapter/%d/sentence/%d", 
                sentence.getBookId(), 
                chapterId, 
                sentence.getSentenceId());
        }
    }

    /**
     * Inner class to hold similar sentence results with metadata
     */
    public static class SimilarSentenceResult {
        private final SentenceDTO sentence;
        private final double similarityScore;
        private final String sourceURL;

        public SimilarSentenceResult(SentenceDTO sentence, double similarityScore, String sourceURL) {
            this.sentence = sentence;
            this.similarityScore = similarityScore;
            this.sourceURL = sourceURL;
        }

        // Getters
        public SentenceDTO getSentence() { return sentence; }
        public double getSimilarityScore() { return similarityScore; }
        public String getSourceURL() { return sourceURL; }
        
        @Override
        public String toString() {
            return String.format("Similarity: %.2f | Source: %s | Text: %s", 
                similarityScore, sourceURL, 
                sentence.getText().length() > 50 ? 
                    sentence.getText().substring(0, 50) + "..." : 
                    sentence.getText());
        }
    }
}