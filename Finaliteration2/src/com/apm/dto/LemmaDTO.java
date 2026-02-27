package com.apm.dto;

public class LemmaDTO {

    private int lemmaId;    // lemma_id (PK)
    private int rootId;     // root_id (FK)
    private String lemma;   // lemma text (unique)

    // --- Constructors ---
    public LemmaDTO() {}

    public LemmaDTO(int lemmaId, int rootId, String lemma) {
        this.lemmaId = lemmaId;
        this.rootId = rootId;
        this.lemma = lemma;
    }

    // --- Getters & Setters ---
    public int getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }

    public int getRootId() {
        return rootId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    // --- toString() ---
    @Override
    public String toString() {
        return "LemmaDTO{" +
                "lemmaId=" + lemmaId +
                ", rootId=" + rootId +
                ", lemma='" + lemma + '\'' +
                '}';
    }
}
