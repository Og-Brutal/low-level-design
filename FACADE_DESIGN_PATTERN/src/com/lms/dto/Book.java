package com.lms.dto;
import java.util.Date;

public class Book {
    private String bookTitle;
    private String bookAuthor;
    private double penaltyRate;
    private Date returnDate;

    public Book(String bookTitle, String bookAuthor, double penaltyRate, Date returnDate) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.penaltyRate = penaltyRate;
        this.returnDate = returnDate;
    }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public double getPenaltyRate() { return penaltyRate; }
    public void setPenaltyRate(double penaltyRate) { this.penaltyRate = penaltyRate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }
}
