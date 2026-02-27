package com.arabicprose.dto;

public class LemmatizationDTO {
    private int lemmaId;
    private int tokenId;
    private String lemma;
    private double confidence;
    
    // ==================== NEW FIELDS ADDED ====================
    private String posTag;
    private String morphologicalPattern;
    private String voweledLemma;
    private String lemmaType;
    private int frequency;
    private boolean isPrimary;
    private String sourceAnalysis;
    private String alternativeLemmas;

    public LemmatizationDTO() {}

    public LemmatizationDTO(int lemmaId, int tokenId, String lemma, double confidence) {
        this.lemmaId = lemmaId;
        this.tokenId = tokenId;
        this.lemma = lemma;
        this.confidence = confidence;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public LemmatizationDTO(int lemmaId, int tokenId, String lemma, double confidence,
                          String posTag, String morphologicalPattern, String voweledLemma,
                          String lemmaType, int frequency, boolean isPrimary, 
                          String sourceAnalysis, String alternativeLemmas) {
        this.lemmaId = lemmaId;
        this.tokenId = tokenId;
        this.lemma = lemma;
        this.confidence = confidence;
        this.posTag = posTag;
        this.morphologicalPattern = morphologicalPattern;
        this.voweledLemma = voweledLemma;
        this.lemmaType = lemmaType;
        this.frequency = frequency;
        this.isPrimary = isPrimary;
        this.sourceAnalysis = sourceAnalysis;
        this.alternativeLemmas = alternativeLemmas;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getLemmaId() { return lemmaId; }
    public void setLemmaId(int lemmaId) { this.lemmaId = lemmaId; }
    
    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }
    
    public String getLemma() { return lemma; }
    public void setLemma(String lemma) { this.lemma = lemma; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public String getPosTag() { return posTag; }
    public void setPosTag(String posTag) { this.posTag = posTag; }
    
    public String getMorphologicalPattern() { return morphologicalPattern; }
    public void setMorphologicalPattern(String morphologicalPattern) { this.morphologicalPattern = morphologicalPattern; }
    
    public String getVoweledLemma() { return voweledLemma; }
    public void setVoweledLemma(String voweledLemma) { this.voweledLemma = voweledLemma; }
    
    public String getLemmaType() { return lemmaType; }
    public void setLemmaType(String lemmaType) { this.lemmaType = lemmaType; }
    
    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
    
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public String getSourceAnalysis() { return sourceAnalysis; }
    public void setSourceAnalysis(String sourceAnalysis) { this.sourceAnalysis = sourceAnalysis; }
    
    public String getAlternativeLemmas() { return alternativeLemmas; }
    public void setAlternativeLemmas(String alternativeLemmas) { this.alternativeLemmas = alternativeLemmas; }

    // ==================== NEW HELPER METHODS ====================
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
    
    public String getConfidenceLevel() {
        if (confidence >= 0.9) return "HIGH";
        if (confidence >= 0.7) return "MEDIUM";
        if (confidence >= 0.5) return "LOW";
        return "VERY_LOW";
    }
    
    public boolean hasDiacritics() {
        return voweledLemma != null && !voweledLemma.equals(lemma);
    }
    
    public String getDisplayLemma() {
        return voweledLemma != null ? voweledLemma : lemma;
    }
    
    public String getLemmaInfo() {
        return String.format("Lemma: %s (POS: %s, Conf: %.2f)", 
                           getDisplayLemma(), 
                           posTag != null ? posTag : "Unknown", 
                           confidence);
    }

    @Override
    public String toString() {
        return "LemmatizationDTO{" +
                "lemmaId=" + lemmaId +
                ", tokenId=" + tokenId +
                ", lemma='" + lemma + '\'' +
                ", confidence=" + confidence +
                ", posTag='" + posTag + '\'' +
                ", morphologicalPattern='" + morphologicalPattern + '\'' +
                ", voweledLemma='" + voweledLemma + '\'' +
                ", lemmaType='" + lemmaType + '\'' +
                ", frequency=" + frequency +
                ", isPrimary=" + isPrimary +
                ", sourceAnalysis='" + sourceAnalysis + '\'' +
                ", alternativeLemmas='" + alternativeLemmas + '\'' +
                '}';
    }
}