package com.arabicprose.bll;

import com.arabicprose.dto.BookDTO;
import java.sql.SQLException;
import java.util.List;

public interface IBookBO {
    void addBook(BookDTO bookDTO) throws SQLException;
    List<BookDTO> getAllBooks() throws SQLException;
    void updateBook(BookDTO bookDTO) throws SQLException;
    void deleteBook(int bookId) throws SQLException;
    BookDTO getBookById(int bookId) throws SQLException;
    List<BookDTO> searchBooks(String keyword) throws SQLException;
    List<BookDTO> searchBooksByTitle(String title) throws SQLException;
    List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException;
    
}