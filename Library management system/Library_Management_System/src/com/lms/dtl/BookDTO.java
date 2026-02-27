package com.lms.dtl;

public class BookDTO {
    private int bookId;
    private String title;
    private String author;
    private boolean isAvailable;
    
   
    public BookDTO() {
       
    }

    public BookDTO(int bookId, String title, String author, String publisher, int year, String category, boolean isAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

   
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

   
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

  
    
}
