package com.arabicprose.dto;

public class TokenizationDTO {
    private int tokenId;
    private int analysisId;
    private String token;
    private int position;
    
    // ==================== NEW FIELDS ADDED ====================
    private int sentenceId;
    private String tokenDiacritized;
    private String posTag;
    private String morphologicalFeatures;
    private double confidence;
    private boolean isStopword;
    private String tokenType;
    private String lemma;
    private String root;
    private int length;

    public TokenizationDTO() {}

    public TokenizationDTO(int tokenId, int analysisId, String token, int position) {
        this.tokenId = tokenId;
        this.analysisId = analysisId;
        this.token = token;
        this.position = position;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public TokenizationDTO(int tokenId, int analysisId, String token, int position,
                          int sentenceId, String tokenDiacritized, String posTag,
                          String morphologicalFeatures, double confidence, boolean isStopword,
                          String tokenType, String lemma, String root, int length) {
        this.tokenId = tokenId;
        this.analysisId = analysisId;
        this.token = token;
        this.position = position;
        this.sentenceId = sentenceId;
        this.tokenDiacritized = tokenDiacritized;
        this.posTag = posTag;
        this.morphologicalFeatures = morphologicalFeatures;
        this.confidence = confidence;
        this.isStopword = isStopword;
        this.tokenType = tokenType;
        this.lemma = lemma;
        this.root = root;
        this.length = length;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }
    
    public int getAnalysisId() { return analysisId; }
    public void setAnalysisId(int analysisId) { this.analysisId = analysisId; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public int getSentenceId() { return sentenceId; }
    public void setSentenceId(int sentenceId) { this.sentenceId = sentenceId; }
    
    public String getTokenDiacritized() { return tokenDiacritized; }
    public void setTokenDiacritized(String tokenDiacritized) { this.tokenDiacritized = tokenDiacritized; }
    
    public String getPosTag() { return posTag; }
    public void setPosTag(String posTag) { this.posTag = posTag; }
    
    public String getMorphologicalFeatures() { return morphologicalFeatures; }
    public void setMorphologicalFeatures(String morphologicalFeatures) { this.morphologicalFeatures = morphologicalFeatures; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public boolean isStopword() { return isStopword; }
    public void setStopword(boolean isStopword) { this.isStopword = isStopword; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public String getLemma() { return lemma; }
    public void setLemma(String lemma) { this.lemma = lemma; }
    
    public String getRoot() { return root; }
    public void setRoot(String root) { this.root = root; }
    
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    // ==================== NEW HELPER METHODS ====================
    public boolean hasDiacritics() {
        return tokenDiacritized != null && !tokenDiacritized.equals(token);
    }
    
    public String getDisplayToken() {
        return tokenDiacritized != null ? tokenDiacritized : token;
    }
    
    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }
    
    public boolean isNoun() {
        return posTag != null && (posTag.startsWith("N") || posTag.contains("NOUN"));
    }
    
    public boolean isVerb() {
        return posTag != null && (posTag.startsWith("V") || posTag.contains("VERB"));
    }
    
    public boolean isParticle() {
        return posTag != null && (posTag.startsWith("P") || posTag.contains("PART"));
    }
    
    public boolean isPunctuation() {
        return tokenType != null && "PUNCTUATION".equals(tokenType);
    }
    
    public boolean isWord() {
        return tokenType != null && "WORD".equals(tokenType);
    }
    
    public boolean hasLemma() {
        return lemma != null && !lemma.trim().isEmpty();
    }
    
    public boolean hasRoot() {
        return root != null && !root.trim().isEmpty();
    }
    
    public String getConfidenceLevel() {
        if (confidence >= 0.9) return "HIGH";
        if (confidence >= 0.7) return "MEDIUM";
        if (confidence >= 0.5) return "LOW";
        return "VERY_LOW";
    }
    
    public boolean isShortWord() {
        return length <= 3;
    }
    
    public boolean isLongWord() {
        return length >= 7;
    }
    
    public String getTokenInfo() {
        return String.format("Token[%d]: '%s' (POS: %s, Lemma: %s)", 
                           position, getDisplayToken(), 
                           posTag != null ? posTag : "Unknown",
                           lemma != null ? lemma : "Unknown");
    }
    
    public String getMorphologicalSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(getDisplayToken());
        
        if (hasLemma()) {
            summary.append(" → Lemma: ").append(lemma);
        }
        if (hasRoot()) {
            summary.append(" → Root: ").append(root);
        }
        if (posTag != null) {
            summary.append(" (").append(posTag).append(")");
        }
        
        return summary.toString();
    }

    @Override
    public String toString() {
        return "TokenizationDTO{" +
                "tokenId=" + tokenId +
                ", analysisId=" + analysisId +
                ", sentenceId=" + sentenceId +
                ", token='" + token + '\'' +
                ", position=" + position +
                ", tokenDiacritized='" + tokenDiacritized + '\'' +
                ", posTag='" + posTag + '\'' +
                ", morphologicalFeatures='" + morphologicalFeatures + '\'' +
                ", confidence=" + confidence +
                ", isStopword=" + isStopword +
                ", tokenType='" + tokenType + '\'' +
                ", lemma='" + lemma + '\'' +
                ", root='" + root + '\'' +
                ", length=" + length +
                '}';
    }
}