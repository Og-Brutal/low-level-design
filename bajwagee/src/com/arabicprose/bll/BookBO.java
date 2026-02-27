package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dal.IDataAccessLayerFacade;
import com.arabicprose.dto.BookDTO;

public class BookBO implements IBookBO {
    private IDataAccessLayerFacade bookDAO ;

    public BookBO(IDataAccessLayerFacade bookDAO)
    {
    	this.bookDAO = bookDAO;
    }

    @Override
    public void addBook(BookDTO bookDTO) throws SQLException {
        if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Book title is required.");
        }
        if (bookDTO.getAuthorId() <= 0) {
            throw new IllegalArgumentException("Valid author ID is required.");
        }
        bookDAO.addBook(bookDTO);
        bookDTO.setBookId(bookDTO.getBookId()); // Update DTO with generated ID
    }

    @Override
    public List<BookDTO> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    @Override
    public void updateBook(BookDTO bookDTO) throws SQLException {
        if (bookDTO.getTitle() == null || bookDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Book title is required.");
        }
        bookDAO.updateBook(bookDTO);
    }

    @Override
    public void deleteBook(int bookId) throws SQLException {
        bookDAO.deleteBook(bookId);
    }

    @Override
    public BookDTO getBookById(int bookId) throws SQLException {
        BookDTO book = bookDAO.getBookById(bookId);
        return book;
    }

    @Override
    public List<BookDTO> searchBooks(String keyword) throws SQLException {
        return bookDAO.searchBooks(keyword);
    }

    @Override
    public List<BookDTO> searchBooksByTitle(String title) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Search title is required.");
        }
        return bookDAO.searchBooksByTitle(title.trim());
    }

    @Override
    public List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException {
        if (authorName == null || authorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Search author name is required.");
        }
        return bookDAO.searchBooksByAuthor(authorName.trim());
    }
}