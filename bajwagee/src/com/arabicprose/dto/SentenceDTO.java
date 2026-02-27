package com.arabicprose.dto;

import java.util.Date;

public class SentenceDTO {
    private int sentenceId;
    private int bookId;
    private int sentenceNumber;
    private String text;
    private String textDiacritized;
    private String translation;
    private String notes;
    private int chapterId;
    
    // ==================== NEW FIELDS ADDED ====================
    private int wordCount;
    private int tokenCount;
    private boolean isAnalyzed;
    private Date analyzedAt;
    private String analysisStatus;
    private double complexityScore;
    private String sentenceType;
    private String languageStyle;
    private int analysisId;

    public SentenceDTO() {}

    public SentenceDTO(int sentenceId, int bookId, int sentenceNumber, String text, 
            String textDiacritized, String translation, String notes) {
        this(sentenceId, bookId, 0, sentenceNumber, text, textDiacritized, translation, notes); // 0 for chapterId
    }
    
    public SentenceDTO(int sentenceId, int bookId, int chapterId, int sentenceNumber, String text, 
            String textDiacritized, String translation, String notes) {
        this.sentenceId = sentenceId;
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.sentenceNumber = sentenceNumber;
        this.text = text;
        this.textDiacritized = textDiacritized;
        this.translation = translation;
        this.notes = notes;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public SentenceDTO(int sentenceId, int bookId, int chapterId, int sentenceNumber, String text, 
                      String textDiacritized, String translation, String notes, int wordCount, 
                      int tokenCount, boolean isAnalyzed, Date analyzedAt, String analysisStatus,
                      double complexityScore, String sentenceType, String languageStyle, int analysisId) {
        this.sentenceId = sentenceId;
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.sentenceNumber = sentenceNumber;
        this.text = text;
        this.textDiacritized = textDiacritized;
        this.translation = translation;
        this.notes = notes;
        this.wordCount = wordCount;
        this.tokenCount = tokenCount;
        this.isAnalyzed = isAnalyzed;
        this.analyzedAt = analyzedAt;
        this.analysisStatus = analysisStatus;
        this.complexityScore = complexityScore;
        this.sentenceType = sentenceType;
        this.languageStyle = languageStyle;
        this.analysisId = analysisId;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getSentenceId() { return sentenceId; }
    public void setSentenceId(int sentenceId) { this.sentenceId = sentenceId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getSentenceNumber() { return sentenceNumber; }
    public void setSentenceNumber(int sentenceNumber) { this.sentenceNumber = sentenceNumber; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTextDiacritized() { return textDiacritized; }
    public void setTextDiacritized(String textDiacritized) { this.textDiacritized = textDiacritized; }

    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getChapterId() { return chapterId; }
    public void setChapterId(int chapterId) { this.chapterId = chapterId; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    
    public int getTokenCount() { return tokenCount; }
    public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
    
    public boolean isAnalyzed() { return isAnalyzed; }
    public void setAnalyzed(boolean isAnalyzed) { this.isAnalyzed = isAnalyzed; }
    
    public Date getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(Date analyzedAt) { this.analyzedAt = analyzedAt; }
    
    public String getAnalysisStatus() { return analysisStatus; }
    public void setAnalysisStatus(String analysisStatus) { this.analysisStatus = analysisStatus; }
    
    public double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
    
    public String getSentenceType() { return sentenceType; }
    public void setSentenceType(String sentenceType) { this.sentenceType = sentenceType; }
    
    public String getLanguageStyle() { return languageStyle; }
    public void setLanguageStyle(String languageStyle) { this.languageStyle = languageStyle; }
    
    public int getAnalysisId() { return analysisId; }
    public void setAnalysisId(int analysisId) { this.analysisId = analysisId; }

    // ==================== NEW HELPER METHODS ====================
    public boolean hasDiacritics() {
        return textDiacritized != null && !textDiacritized.equals(text);
    }
    
    public String getDisplayText() {
        return textDiacritized != null ? textDiacritized : text;
    }
    
    public boolean isComplex() {
        return complexityScore >= 0.7;
    }
    
    public boolean isSimple() {
        return complexityScore <= 0.3;
    }
    
    public boolean isAnalysisComplete() {
        return "COMPLETED".equals(analysisStatus) || "SUCCESS".equals(analysisStatus);
    }
    
    public boolean isAnalysisInProgress() {
        return "PROCESSING".equals(analysisStatus) || "IN_PROGRESS".equals(analysisStatus);
    }
    
    public boolean hasTranslation() {
        return translation != null && !translation.trim().isEmpty();
    }
    
    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
    
    public String getComplexityLevel() {
        if (complexityScore >= 0.8) return "VERY_HIGH";
        if (complexityScore >= 0.6) return "HIGH";
        if (complexityScore >= 0.4) return "MEDIUM";
        if (complexityScore >= 0.2) return "LOW";
        return "VERY_LOW";
    }
    
    public String getSentenceSummary() {
        return String.format("Sentence #%d: %d words, %s complexity", 
                           sentenceNumber, wordCount, getComplexityLevel());
    }
    
    public boolean isLiteraryStyle() {
        return "LITERARY".equals(languageStyle) || "CLASSICAL".equals(languageStyle);
    }
    
    public boolean isModernStyle() {
        return "MODERN".equals(languageStyle) || "CONTEMPORARY".equals(languageStyle);
    }
    
    public String getShortText(int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    @Override
    public String toString() {
        return "SentenceDTO{" +
                "sentenceId=" + sentenceId +
                ", bookId=" + bookId +
                ", chapterId=" + chapterId +
                ", sentenceNumber=" + sentenceNumber +
                ", text='" + getShortText(50) + '\'' +
                ", textDiacritized='" + (textDiacritized != null ? getShortText(30) : "null") + '\'' +
                ", translation='" + (translation != null ? getShortText(30) : "null") + '\'' +
                ", notes='" + (notes != null ? getShortText(30) : "null") + '\'' +
                ", wordCount=" + wordCount +
                ", tokenCount=" + tokenCount +
                ", isAnalyzed=" + isAnalyzed +
                ", analyzedAt=" + analyzedAt +
                ", analysisStatus='" + analysisStatus + '\'' +
                ", complexityScore=" + complexityScore +
                ", sentenceType='" + sentenceType + '\'' +
                ", languageStyle='" + languageStyle + '\'' +
                ", analysisId=" + analysisId +
                '}';
    }
}