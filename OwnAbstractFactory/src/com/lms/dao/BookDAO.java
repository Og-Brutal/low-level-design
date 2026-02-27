package com.lms.dao;

import com.lms.dto.BookDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO implements IBookDAO {

    private static final String URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12802733?useSSL=false&serverTimezone=UTC";
    private static final String USER = "sql12802733";
    private static final String PASSWORD = "qtlZHRLtFJ";

    @Override
    public BookDTO getBook() {
        BookDTO book = null;

        String query = "SELECT title, era FROM Book LIMIT 1"; // Adjust if your table name or columns differ

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                // Use the parameterized constructor from BookDTO
                book = new BookDTO(rs.getString("title"), rs.getString("era"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return book;
    }
}
