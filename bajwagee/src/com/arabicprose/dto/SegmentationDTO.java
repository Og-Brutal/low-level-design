package com.arabicprose.dto;

public class SegmentationDTO {
    private int segmentationId;
    private int tokenId;
    private String prefix;
    private String stem;
    private String suffix;
    
    // ==================== NEW FIELDS ADDED ====================
    private String prefixType;
    private String stemType;
    private String suffixType;
    private String morphologicalPattern;
    private double confidence;
    private boolean isPrimary;
    private String segmentationType;
    private String fullForm;
    private String analysisSource;
    private String alternativeSegmentations;

    public SegmentationDTO() {}

    public SegmentationDTO(int segmentationId, int tokenId, String prefix, String stem, String suffix) {
        this.segmentationId = segmentationId;
        this.tokenId = tokenId;
        this.prefix = prefix;
        this.stem = stem;
        this.suffix = suffix;
    }

    // ==================== NEW CONSTRUCTOR ====================
    public SegmentationDTO(int segmentationId, int tokenId, String prefix, String stem, String suffix,
                          String prefixType, String stemType, String suffixType, String morphologicalPattern,
                          double confidence, boolean isPrimary, String segmentationType, String fullForm,
                          String analysisSource, String alternativeSegmentations) {
        this.segmentationId = segmentationId;
        this.tokenId = tokenId;
        this.prefix = prefix;
        this.stem = stem;
        this.suffix = suffix;
        this.prefixType = prefixType;
        this.stemType = stemType;
        this.suffixType = suffixType;
        this.morphologicalPattern = morphologicalPattern;
        this.confidence = confidence;
        this.isPrimary = isPrimary;
        this.segmentationType = segmentationType;
        this.fullForm = fullForm;
        this.analysisSource = analysisSource;
        this.alternativeSegmentations = alternativeSegmentations;
    }

    // ==================== EXISTING GETTERS AND SETTERS ====================
    public int getSegmentationId() { return segmentationId; }
    public void setSegmentationId(int segmentationId) { this.segmentationId = segmentationId; }
    
    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }
    
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }

    // ==================== NEW GETTERS AND SETTERS ====================
    public String getPrefixType() { return prefixType; }
    public void setPrefixType(String prefixType) { this.prefixType = prefixType; }
    
    public String getStemType() { return stemType; }
    public void setStemType(String stemType) { this.stemType = stemType; }
    
    public String getSuffixType() { return suffixType; }
    public void setSuffixType(String suffixType) { this.suffixType = suffixType; }
    
    public String getMorphologicalPattern() { return morphologicalPattern; }
    public void setMorphologicalPattern(String morphologicalPattern) { this.morphologicalPattern = morphologicalPattern; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public String getSegmentationType() { return segmentationType; }
    public void setSegmentationType(String segmentationType) { this.segmentationType = segmentationType; }
    
    public String getFullForm() { return fullForm; }
    public void setFullForm(String fullForm) { this.fullForm = fullForm; }
    
    public String getAnalysisSource() { return analysisSource; }
    public void setAnalysisSource(String analysisSource) { this.analysisSource = analysisSource; }
    
    public String getAlternativeSegmentations() { return alternativeSegmentations; }
    public void setAlternativeSegmentations(String alternativeSegmentations) { this.alternativeSegmentations = alternativeSegmentations; }

    // ==================== NEW HELPER METHODS ====================
    public boolean hasPrefix() {
        return prefix != null && !prefix.trim().isEmpty();
    }
    
    public boolean hasSuffix() {
        return suffix != null && !suffix.trim().isEmpty();
    }
    
    public boolean hasStem() {
        return stem != null && !stem.trim().isEmpty();
    }
    
    public boolean isCompleteSegmentation() {
        return hasPrefix() || hasStem() || hasSuffix();
    }
    
    public boolean isHighConfidence() {
        return confidence >= 0.8;
    }
    
    public String getSegmentedForm() {
        StringBuilder sb = new StringBuilder();
        if (hasPrefix()) {
            sb.append(prefix).append("-");
        }
        if (hasStem()) {
            sb.append(stem);
        } else {
            sb.append("[STEM]");
        }
        if (hasSuffix()) {
            sb.append("-").append(suffix);
        }
        return sb.toString();
    }
    
    public String getDisplaySegmentation() {
        return String.format("%s | %s | %s", 
                           prefix != null ? prefix : "—", 
                           stem != null ? stem : "—", 
                           suffix != null ? suffix : "—");
    }
    
    public String getConfidenceLevel() {
        if (confidence >= 0.9) return "HIGH";
        if (confidence >= 0.7) return "MEDIUM";
        if (confidence >= 0.5) return "LOW";
        return "VERY_LOW";
    }
    
    public boolean isVerbPattern() {
        return morphologicalPattern != null && 
               (morphologicalPattern.startsWith("فعل") || 
                morphologicalPattern.contains("VERB"));
    }
    
    public boolean isNounPattern() {
        return morphologicalPattern != null && 
               (morphologicalPattern.startsWith("اسم") || 
                morphologicalPattern.contains("NOUN"));
    }
    
    public String getPatternType() {
        if (morphologicalPattern == null) return "UNKNOWN";
        if (morphologicalPattern.contains("فعل")) return "VERB";
        if (morphologicalPattern.contains("اسم")) return "NOUN";
        if (morphologicalPattern.contains("حرف")) return "PARTICLE";
        return "OTHER";
    }
    
    public int getSegmentCount() {
        int count = 0;
        if (hasPrefix()) count++;
        if (hasStem()) count++;
        if (hasSuffix()) count++;
        return count;
    }
    
    public String getSegmentationSummary() {
        return String.format("Segmentation: %s (Pattern: %s, Conf: %.2f)", 
                           getSegmentedForm(),
                           morphologicalPattern != null ? morphologicalPattern : "Unknown",
                           confidence);
    }
    
    public boolean hasAlternativeSegmentations() {
        return alternativeSegmentations != null && !alternativeSegmentations.trim().isEmpty();
    }
    
    public String[] getAlternativeSegmentationList() {
        if (alternativeSegmentations == null || alternativeSegmentations.isEmpty()) {
            return new String[0];
        }
        return alternativeSegmentations.split(";");
    }
    
    public String getFullAnalysis() {
        StringBuilder analysis = new StringBuilder();
        analysis.append("Prefix: ").append(hasPrefix() ? prefix : "None");
        analysis.append(" | Stem: ").append(hasStem() ? stem : "None");
        analysis.append(" | Suffix: ").append(hasSuffix() ? suffix : "None");
        
        if (morphologicalPattern != null) {
            analysis.append(" | Pattern: ").append(morphologicalPattern);
        }
        
        if (confidence > 0) {
            analysis.append(" | Confidence: ").append(String.format("%.2f", confidence));
        }
        
        return analysis.toString();
    }
    
    /**
     * Returns a safe 3-element array [prefix, stem, suffix] for this segmentation.
     * Always returns exactly 3 elements, using empty strings for missing parts.
     * This prevents ArrayIndexOutOfBoundsException when accessing segmentation parts.
     * 
     * @return String array with exactly 3 elements: [prefix, stem, suffix]
     */
    public String[] getSafeSegmentation() {
        String[] result = new String[3];
        result[0] = (prefix != null && !prefix.trim().isEmpty()) ? prefix : "";
        result[1] = (stem != null && !stem.trim().isEmpty()) ? stem : "";
        result[2] = (suffix != null && !suffix.trim().isEmpty()) ? suffix : "";
        return result;
    }
    
    /**
     * Static helper method to get safe segmentation from a SegmentationDTO.
     * If the DTO is null or incomplete, returns ["", "", ""].
     * 
     * @param segmentation The SegmentationDTO (can be null)
     * @return String array with exactly 3 elements: [prefix, stem, suffix]
     */
    public static String[] getSafeSegmentation(SegmentationDTO segmentation) {
        if (segmentation == null) {
            return new String[]{"", "", ""};
        }
        return segmentation.getSafeSegmentation();
    }
    
    /**
     * Static helper method to get safe segmentation from a list of Segmentations.
     * Returns the first valid segmentation, or ["", "", ""] if list is empty/null.
     * 
     * @param segmentations List of SegmentationDTO (can be null or empty)
     * @return String array with exactly 3 elements: [prefix, stem, suffix]
     */
    public static String[] getSafeSegmentation(java.util.List<SegmentationDTO> segmentations) {
        if (segmentations == null || segmentations.isEmpty()) {
            return new String[]{"", "", ""};
        }
        return getSafeSegmentation(segmentations.get(0));
    }

    @Override
    public String toString() {
        return "SegmentationDTO{" +
                "segmentationId=" + segmentationId +
                ", tokenId=" + tokenId +
                ", prefix='" + prefix + '\'' +
                ", stem='" + stem + '\'' +
                ", suffix='" + suffix + '\'' +
                ", prefixType='" + prefixType + '\'' +
                ", stemType='" + stemType + '\'' +
                ", suffixType='" + suffixType + '\'' +
                ", morphologicalPattern='" + morphologicalPattern + '\'' +
                ", confidence=" + confidence +
                ", isPrimary=" + isPrimary +
                ", segmentationType='" + segmentationType + '\'' +
                ", fullForm='" + fullForm + '\'' +
                ", analysisSource='" + analysisSource + '\'' +
                ", alternativeSegmentations='" + alternativeSegmentations + '\'' +
                '}';
    }
}