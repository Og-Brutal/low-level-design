package com.abft.dal;

import java.sql.*;

public class BookDAO implements IBookDAO {
    private Connection connection;

    public BookDAO() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library",
                "root",
                "root"
            );
            initDB(); // Create table if not exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Ensures the books table exists before any operations */
    private void initDB() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBook(Book book) {
        String query = "INSERT INTO books (title, author) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book getBookById(int id) {
        String query = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
