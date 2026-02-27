package com.apm.dto;

public class SegmentedTokenDTO {
    private int segmentedTokenId;
    private int tokenId;
    private String prefix;
    private String stem;
    private String lemma;
    private String root;

    // --- Constructors ---
    public SegmentedTokenDTO() {}

    public SegmentedTokenDTO(int segmentedTokenId, int tokenId, String prefix, String stem, String lemma, String root) {
        this.segmentedTokenId = segmentedTokenId;
        this.tokenId = tokenId;
        this.prefix = prefix;
        this.stem = stem;
        this.lemma = lemma;
        this.root = root;
    }

    // --- Getters and Setters ---
    public int getSegmentedTokenId() {
        return segmentedTokenId;
    }

    public void setSegmentedTokenId(int segmentedTokenId) {
        this.segmentedTokenId = segmentedTokenId;
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "SegmentedTokenDTO [segmentedTokenId=" + segmentedTokenId + ", tokenId=" + tokenId +
               ", prefix=" + prefix + ", stem=" + stem + ", lemma=" + lemma + ", root=" + root + "]";
    }
}
