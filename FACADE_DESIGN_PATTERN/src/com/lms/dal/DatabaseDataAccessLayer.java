package com.lms.dal;
import java.util.List;
import com.lms.dto.Book;

public class DatabaseDataAccessLayer implements IDataAccessLayer {
    private BookDAO bookRepo;
    private StudentDAO studentRepo;

    public DatabaseDataAccessLayer() {
        bookRepo = new BookDAO();
        studentRepo = new StudentDAO();
    }

    public Book getBookDetails(String bookCode) {
        return bookRepo.getBookDetails(bookCode);
    }

    public List<Book> getIssuedBooks(String learnerId) {
        return studentRepo.getIssuedBooks(learnerId);
    }
} 
  