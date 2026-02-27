package com.lms.dto;

public class BookDTO {
private String bookName;
private String authorName;
private double finePerDay;
private String isbn;

// Constructor
	public BookDTO() {
	}		

	public BookDTO(String bookName, String authorName, double finePerDay) {
	    this.bookName = bookName;
	 	this.authorName = authorName;
	    this.finePerDay = finePerDay;
	}

	// Getters and Setters
	public String getBookName() {
	    return bookName;
	}
	
	public String getBookIsbn() {
	    return isbn;
	}
	
	public void setBookIsbn(String isbn) {
		this.isbn=isbn;
	}
	public void setBookName(String bookName) {
	    this.bookName = bookName;
	}

	public String getAuthorName() {
	    return authorName;
	}

	public void setAuthorName(String authorName) {
	    this.authorName = authorName;
	}

	public double getFinePerDay() {
	    return finePerDay;
	}

	public void setFinePerDay(double finePerDay) {
	    this.finePerDay = finePerDay;
	}
}
