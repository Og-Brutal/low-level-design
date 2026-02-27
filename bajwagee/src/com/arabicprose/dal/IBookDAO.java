package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dto.BookDTO;

public interface IBookDAO {
    // --- Basic CRUD ---
    void addBook(BookDTO book) throws SQLException;
    List<BookDTO> getAllBooks() throws SQLException;
    void updateBook(BookDTO book) throws SQLException;
    void deleteBook(int bookId) throws SQLException;
    BookDTO getBookById(int bookId) throws SQLException;

    // --- Search Methods ---
    List<BookDTO> searchBooks(String keyword) throws SQLException;
    List<BookDTO> searchBooksByTitle(String title) throws SQLException;
    List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException;
}