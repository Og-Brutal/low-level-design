package com.arabicprose.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.arabicprose.dto.AuthorDTO;


public class AuthorDAO implements IAuthorDAO {
    @Override
    public void addAuthor(AuthorDTO author) throws SQLException {
        String sql = "INSERT INTO Author (name, biography) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, author.getName());
            pstmt.setString(2, author.getBiography());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                author.setAuthorId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<AuthorDTO> getAllAuthors() throws SQLException {
        List<AuthorDTO> authors = new ArrayList<>();
        String sql = "SELECT * FROM Author";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(new AuthorDTO(rs.getInt("author_id"), rs.getString("name"), rs.getString("biography")));
            }
        }
        return authors;
    }

    @Override
    public void updateAuthor(AuthorDTO author) throws SQLException {
        String sql = "UPDATE Author SET name = ?, biography = ? WHERE author_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author.getName());
            pstmt.setString(2, author.getBiography());
            pstmt.setInt(3, author.getAuthorId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteAuthor(int authorId) throws SQLException {
        String sql = "DELETE FROM Author WHERE author_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public AuthorDTO getAuthorById(int authorId) throws SQLException {
        String sql = "SELECT * FROM Author WHERE author_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new AuthorDTO(rs.getInt("author_id"), rs.getString("name"), rs.getString("biography"));
            }
        }
        return null;
    }

	@Override
	public String searchAuthor(int bookId) throws SQLException {
	    String sql = "SELECT title FROM Book WHERE book_id = ?";
	    Connection conn = DBConnection.getConnection();
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, bookId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("title"); // Return the book title if found
	            } else {
	                return null; // No book found for this ID
	            }
	        }
	    }
	}


    
    
}