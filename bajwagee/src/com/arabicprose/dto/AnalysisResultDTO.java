package com.arabicprose.dto;

import java.util.Date;

public class AnalysisResultDTO {
    private int analysisId;
    private int sentenceId;
    private String originalText;
    private Date analyzedAt;
    
    // ==================== NEW FIELDS ADDED ====================
    private int tokenCount;
    private int lemmaCount;
    private int rootCount;
    private int segmentationCount;
    private String analysisStatus;
    private double confidenceScore;
    private String processingTime;
    private String analysisType;

    public AnalysisResultDTO() {}

    public AnalysisResultDTO(int analysisId, int sentenceId, String originalText, Date analyzedAt) {
        this.analysisId = analysisId;
        this.sentenceId = sentenceId;
        this.originalText = originalText;
        this.analyzedAt = analyzedAt;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public AnalysisResultDTO(int analysisId, int sentenceId, String originalText, Date analyzedAt,
                           int tokenCount, int lemmaCount, int rootCount, int segmentationCount,
                           String analysisStatus, double confidenceScore, String processingTime, 
                           String analysisType) {
        this.analysisId = analysisId;
        this.sentenceId = sentenceId;
        this.originalText = originalText;
        this.analyzedAt = analyzedAt;
        this.tokenCount = tokenCount;
        this.lemmaCount = lemmaCount;
        this.rootCount = rootCount;
        this.segmentationCount = segmentationCount;
        this.analysisStatus = analysisStatus;
        this.confidenceScore = confidenceScore;
        this.processingTime = processingTime;
        this.analysisType = analysisType;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getAnalysisId() { return analysisId; }
    public void setAnalysisId(int analysisId) { this.analysisId = analysisId; }
    
    public int getSentenceId() { return sentenceId; }
    public void setSentenceId(int sentenceId) { this.sentenceId = sentenceId; }
    
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    
    public Date getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(Date analyzedAt) { this.analyzedAt = analyzedAt; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public int getTokenCount() { return tokenCount; }
    public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
    
    public int getLemmaCount() { return lemmaCount; }
    public void setLemmaCount(int lemmaCount) { this.lemmaCount = lemmaCount; }
    
    public int getRootCount() { return rootCount; }
    public void setRootCount(int rootCount) { this.rootCount = rootCount; }
    
    public int getSegmentationCount() { return segmentationCount; }
    public void setSegmentationCount(int segmentationCount) { this.segmentationCount = segmentationCount; }
    
    public String getAnalysisStatus() { return analysisStatus; }
    public void setAnalysisStatus(String analysisStatus) { this.analysisStatus = analysisStatus; }
    
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public String getProcessingTime() { return processingTime; }
    public void setProcessingTime(String processingTime) { this.processingTime = processingTime; }
    
    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    // ==================== NEW HELPER METHODS ====================
    public boolean isSuccessful() {
        return "COMPLETED".equals(analysisStatus) || "SUCCESS".equals(analysisStatus);
    }
    
    public boolean hasHighConfidence() {
        return confidenceScore >= 0.8;
    }
    
    public boolean hasMorphologicalData() {
        return tokenCount > 0 || lemmaCount > 0 || rootCount > 0;
    }
    
    public String getSummary() {
        return String.format("Analysis #%d: %d tokens, %d lemmas, %d roots", 
                           analysisId, tokenCount, lemmaCount, rootCount);
    }

    @Override
    public String toString() {
        return "AnalysisResultDTO{" +
                "analysisId=" + analysisId +
                ", sentenceId=" + sentenceId +
                ", originalText='" + (originalText != null ? originalText.substring(0, Math.min(50, originalText.length())) + "..." : "null") + '\'' +
                ", analyzedAt=" + analyzedAt +
                ", tokenCount=" + tokenCount +
                ", lemmaCount=" + lemmaCount +
                ", rootCount=" + rootCount +
                ", segmentationCount=" + segmentationCount +
                ", analysisStatus='" + analysisStatus + '\'' +
                ", confidenceScore=" + confidenceScore +
                ", processingTime='" + processingTime + '\'' +
                ", analysisType='" + analysisType + '\'' +
                '}';
    }
}