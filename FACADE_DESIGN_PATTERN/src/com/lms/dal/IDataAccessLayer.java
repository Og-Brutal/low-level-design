package com.lms.dal;
import java.util.List;
import com.lms.dto.Book;

public interface IDataAccessLayer {
    Book getBookDetails(String bookCode);
    List<Book> getIssuedBooks(String learnerId);
}
