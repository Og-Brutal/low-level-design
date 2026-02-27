package com.arabicprose.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.arabicprose.dto.BookDTO;

public class BookDAO implements IBookDAO {
    @Override
    public void addBook(BookDTO book) throws SQLException {
        String sql = "INSERT INTO Book (title, author_id, era) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getAuthorId());
            pstmt.setString(3, book.getEra());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                book.setBookId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<BookDTO> getAllBooks() throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM Book";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new BookDTO(rs.getInt("book_id"), rs.getString("title"), rs.getInt("author_id"), rs.getString("era")));
            }
        }
        return books;
    }

    @Override
    public void updateBook(BookDTO book) throws SQLException {
        String sql = "UPDATE Book SET title = ?, author_id = ?, era = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getAuthorId());
            pstmt.setString(3, book.getEra());
            pstmt.setInt(4, book.getBookId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM Book WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public BookDTO getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM Book WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new BookDTO(rs.getInt("book_id"), rs.getString("title"), rs.getInt("author_id"), rs.getString("era"));
            }
        }
        return null;
    }
 // In BookDAO.java - ensure these methods exist and work correctly
    @Override
    public List<BookDTO> searchBooksByTitle(String title) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE title LIKE ? ORDER BY title";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + title + "%";
            pstmt.setString(1, searchTerm);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new BookDTO(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("author_id"),
                    rs.getString("era")
                ));
            }
        }
        return books;
    }

    @Override
    public List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT b.* FROM Book b JOIN Author a ON b.author_id = a.author_id " +
                     "WHERE a.name LIKE ? ORDER BY b.title";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + authorName + "%";
            pstmt.setString(1, searchTerm);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(new BookDTO(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("author_id"),
                    rs.getString("era")
                ));
            }
        }
        return books;
    }
    @Override
    public List<BookDTO> searchBooks(String keyword) throws SQLException {
        List<BookDTO> books = new ArrayList<>();
        String sql = "SELECT b.*, a.name as author_name FROM Book b " +
                     "JOIN Author a ON b.author_id = a.author_id " +
                     "WHERE b.title LIKE ? OR a.name LIKE ? " +
                     "ORDER BY b.title";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                BookDTO book = new BookDTO(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("author_id"),
                    rs.getString("era")
                );
                books.add(book);
            }
        }
        return books;
    }
}