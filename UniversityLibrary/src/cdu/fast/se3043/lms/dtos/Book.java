package cdu.fast.se3043.lms.dtos;

public class Book {
    private String title;
    private String author;
    private String dueDate;   
    private double finePerDay;


    public Book(String title, String author, String dueDate, double finePerDay) {
        this.title = title;
        this.author = author;
        this.dueDate = dueDate;
        this.finePerDay = finePerDay;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDueDate() {
        return dueDate;
    }

    public double getFinePerDay() {
        return finePerDay;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setFinePerDay(double finePerDay) {
        this.finePerDay = finePerDay;
    }
}

