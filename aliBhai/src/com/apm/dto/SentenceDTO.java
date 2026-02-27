package com.apm.dto;


public class SentenceDTO {
    private int sentenceId;
    private int chapterID;
    private int sentenceNumber;
    private String text;
    private String textDiacritized;
    private String translation;
    private String notes;

    // Constructors
    public SentenceDTO() {}

    public SentenceDTO(int sentenceId, int chapterID, int sentenceNumber,
                       String text, String textDiacritized, String translation, String notes) {
        this.sentenceId = sentenceId;
        this.chapterID = chapterID;
        this.sentenceNumber = sentenceNumber;
        this.text = text;
        this.textDiacritized = textDiacritized;
        this.translation = translation;
        this.notes = notes;
    }

    // Getters and Setters
    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getChapterId() {
        return chapterID;
    }

    public void setChapterId(int chapterID) {
        this.chapterID = chapterID;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public void setSentenceNumber(int sentenceNumber) {
        this.sentenceNumber = sentenceNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextDiacritized() {
        return textDiacritized;
    }

    public void setTextDiacritized(String textDiacritized) {
        this.textDiacritized = textDiacritized;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "SentenceDTO{" +
                "sentenceId=" + sentenceId +
                ", bookId=" + chapterID +
                ", sentenceNumber=" + sentenceNumber +
                ", text='" + text + '\'' +
                ", textDiacritized='" + textDiacritized + '\'' +
                ", translation='" + translation + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
