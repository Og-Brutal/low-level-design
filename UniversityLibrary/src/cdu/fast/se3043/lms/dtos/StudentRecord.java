package cdu.fast.se3043.lms.dtos;

import java.util.List;

public class StudentRecord {
    private String studentId;
    private List<Book> books;
    private double totalFine;

    public StudentRecord(String studentId, List<Book> books, double totalFine) {
        this.studentId = studentId;
        this.books = books;
        this.totalFine = totalFine;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public List<Book> getBooks() {
        return books;
    }

    public double getTotalFine() {
        return totalFine;
    }

    // Setters
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void setTotalFine(double totalFine) {
        this.totalFine = totalFine;
    }
}

