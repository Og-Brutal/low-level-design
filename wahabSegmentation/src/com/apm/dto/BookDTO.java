package com.apm.dto;


public class BookDTO {
    private int bookId;
    private String title;
    private int authorId;
    private String era;

    // Constructors
    public BookDTO() {}

    public BookDTO(int bookId, String title, int authorId, String era) {
        this.bookId = bookId;
        this.title = title;
        this.authorId = authorId;
        this.era = era;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    @Override
    public String toString() {
        return 
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", era='" + era + '\'' 
                ;
    }
}

