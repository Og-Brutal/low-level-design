package com.apm.dto;


public class SentenceDTO {
    private int sentenceId;
    private int bookId;
    private int sentenceNumber;
    private String text;
    private String textDiacritized;
    private String translation;
    private String notes;

    // Constructors
    public SentenceDTO() {}

    public SentenceDTO(int sentenceId, int bookId, int sentenceNumber,
                       String text, String textDiacritized, String translation, String notes) {
        this.sentenceId = sentenceId;
        this.bookId = bookId;
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

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
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
                ", bookId=" + bookId +
                ", sentenceNumber=" + sentenceNumber +
                ", text='" + text + '\'' +
                ", textDiacritized='" + textDiacritized + '\'' +
                ", translation='" + translation + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
