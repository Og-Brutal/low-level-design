package com.lms.dto;
import java.util.List;

public class StudentRecord {
    private int learnerId;
    private List<Book> issuedBooks;
    private double overallPenalty;

    public StudentRecord(int learnerId, List<Book> issuedBooks, double overallPenalty) {
        this.learnerId = learnerId;
        this.issuedBooks = issuedBooks;
        this.overallPenalty = overallPenalty;
    }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public List<Book> getIssuedBooks() { return issuedBooks; }
    public void setIssuedBooks(List<Book> issuedBooks) { this.issuedBooks = issuedBooks; }

    public double getOverallPenalty() { return overallPenalty; }
    public void setOverallPenalty(double overallPenalty) { this.overallPenalty = overallPenalty; }
}
