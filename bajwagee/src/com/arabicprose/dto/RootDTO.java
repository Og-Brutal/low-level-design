package com.arabicprose.dto;

public class RootDTO {
    private int rootId;
    private int tokenId;
    private String root;
    private double confidence;
    
    // ==================== NEW FIELDS ADDED ====================
    private String arabicRoot;
    private String rootType;
    private String morphologicalFamily;
    private int frequency;
    private boolean isPrimary;
    private String sourceAnalysis;
    private String derivedForms;
    private String rootPattern;

    public RootDTO() {}

    public RootDTO(int rootId, int tokenId, String root, double confidence) {
        this.rootId = rootId;
        this.tokenId = tokenId;
        this.root = root;
        this.confidence = confidence;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public RootDTO(int rootId, int tokenId, String root, double confidence,
                  String arabicRoot, String rootType, String morphologicalFamily,
                  int frequency, boolean isPrimary, String sourceAnalysis,
                  String derivedForms, String rootPattern) {
        this.rootId = rootId;
        this.tokenId = tokenId;
        this.root = root;
        this.confidence = confidence;
        this.arabicRoot = arabicRoot;
        this.rootType = rootType;
        this.morphologicalFamily = morphologicalFamily;
        this.frequency = frequency;
        this.isPrimary = isPrimary;
        this.sourceAnalysis = sourceAnalysis;
        this.derivedForms = derivedForms;
        this.rootPattern = rootPattern;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getRootId() { return rootId; }
    public void setRootId(int rootId) { this.rootId = rootId; }
    
    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }
    
    public String getRoot() { return root; }
    public void setRoot(String root) { this.root = root; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public String getArabicRoot() { return arabicRoot; }
    public void setArabicRoot(String arabicRoot) { this.arabicRoot = arabicRoot; }
    
    public String getRootType() { return rootType; }
    public void setRootType(String rootType) { this.rootType = rootType; }
    
    public String getMorphologicalFamily() { return morphologicalFamily; }
    public void setMorphologicalFamily(String morphologicalFamily) { this.morphologicalFamily = morphologicalFamily; }
    
    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
    
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public String getSourceAnalysis() { return sourceAnalysis; }
    public void setSourceAnalysis(String sourceAnalysis) { this.sourceAnalysis = sourceAnalysis; }
    
    public String getDerivedForms() { return derivedForms; }
    public void setDerivedForms(String derivedForms) { this.derivedForms = derivedForms; }
    
    public String getRootPattern() { return rootPattern; }
    public void setRootPattern(String rootPattern) { this.rootPattern = rootPattern; }

    // ==================== NEW HELPER METHODS ====================
    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }
    
    public boolean isTrilateral() {
        return root != null && root.length() == 3;
    }
    
    public boolean isQuadrilateral() {
        return root != null && root.length() == 4;
    }
    
    public String getFormattedArabicRoot() {
        if (arabicRoot == null || arabicRoot.isEmpty()) {
            return root; // Fallback to Buckwalter if Arabic not available
        }
        
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < arabicRoot.length(); i++) {
            if (i > 0) formatted.append(" - ");
            formatted.append(arabicRoot.charAt(i));
        }
        return formatted.toString();
    }
    
    public String getFormattedRootWithBuckwalter() {
        String arabic = getFormattedArabicRoot();
        return arabic + " (" + root.toUpperCase() + ")";
    }
    
    public String getConfidenceLevel() {
        if (confidence >= 0.9) return "HIGH";
        if (confidence >= 0.7) return "MEDIUM";
        if (confidence >= 0.5) return "LOW";
        return "VERY_LOW";
    }
    
    public boolean isCommonRoot() {
        return frequency > 10; // Consider common if frequency > 10
    }
    
    public String getRootInfo() {
        return String.format("Root: %s (Type: %s, Conf: %.2f)", 
                           getFormattedArabicRoot(),
                           rootType != null ? rootType : "Unknown",
                           confidence);
    }
    
    public String[] getDerivedFormsList() {
        if (derivedForms == null || derivedForms.isEmpty()) {
            return new String[0];
        }
        return derivedForms.split(",");
    }
    
    public boolean hasDerivedForms() {
        return derivedForms != null && !derivedForms.isEmpty();
    }

    @Override
    public String toString() {
        return "RootDTO{" +
                "rootId=" + rootId +
                ", tokenId=" + tokenId +
                ", root='" + root + '\'' +
                ", confidence=" + confidence +
                ", arabicRoot='" + arabicRoot + '\'' +
                ", rootType='" + rootType + '\'' +
                ", morphologicalFamily='" + morphologicalFamily + '\'' +
                ", frequency=" + frequency +
                ", isPrimary=" + isPrimary +
                ", sourceAnalysis='" + sourceAnalysis + '\'' +
                ", derivedForms='" + derivedForms + '\'' +
                ", rootPattern='" + rootPattern + '\'' +
                '}';
    }
}