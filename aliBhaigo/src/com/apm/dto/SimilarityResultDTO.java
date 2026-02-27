package com.apm.dto;

public class SimilarityResultDTO {
    private String text;
    private double score;
    private String source;

    public SimilarityResultDTO(String text, double score, String source) {
        this.text = text;
        this.score = score;
        this.source = source;
    }
    // Getters...
    public String getText() { return text; }
    public double getScore() { return score; }
    public String getSource() { return source; }
}