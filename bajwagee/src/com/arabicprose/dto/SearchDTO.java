package com.arabicprose.dto;

/**
 * Data Transfer Object for search operations
 */
public class SearchDTO {
    private String searchText;
    private boolean isRegex;
    private int nGramSize;
    private double similarityThreshold;
    
    // Constructors
    public SearchDTO() {}
    
    public SearchDTO(String searchText, boolean isRegex) {
        this.searchText = searchText;
        this.isRegex = isRegex;
    }
    
    public SearchDTO(String searchText, int nGramSize, double similarityThreshold) {
        this.searchText = searchText;
        this.nGramSize = nGramSize;
        this.similarityThreshold = similarityThreshold;
    }
    
    // Getters and Setters
    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) { this.searchText = searchText; }
    
    public boolean isRegex() { return isRegex; }
    public void setRegex(boolean regex) { isRegex = regex; }
    
    public int getNGramSize() { return nGramSize; }
    public void setNGramSize(int nGramSize) { this.nGramSize = nGramSize; }
    
    public double getSimilarityThreshold() { return similarityThreshold; }
    public void setSimilarityThreshold(double similarityThreshold) { 
        this.similarityThreshold = similarityThreshold; 
    }
}