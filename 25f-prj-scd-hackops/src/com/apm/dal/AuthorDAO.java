package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.AuthorDTO;

public class AuthorDAO implements IAuthorDAO {
    private Connection conn;
    private Statement stmt;

    public AuthorDAO(Connection conn, Statement stmt) {
        this.conn = conn;
        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ CREATE Author (auto-generated author_id)
    @Override
    public boolean createAuthor(String name, String biography) {
        try {
            String sql = "INSERT INTO Author (name, biography) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, biography);

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ RETRIEVE Author by name
    @Override
    public AuthorDTO retrieveAuthor(String name) {
        try {
            String sql = "SELECT * FROM Author WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int authorId = rs.getInt("author_id");
                String biography = rs.getString("biography");
                return new AuthorDTO(authorId, name, biography);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    // ✅ UPDATE Author by name
    @Override
    public boolean updateAuthor(String oldAuthorName,String name, String biography){
    	System.out.println("in updating function dao");
        try {
            String sql = "UPDATE Author SET biography = ?,name=? WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, biography);
            ps.setString(2, name);
            ps.setString(3, oldAuthorName);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE Author by ID (Updated)
    @Override
    public boolean deleteAuthor(String name) {
        try {
            String sql = "DELETE FROM Author WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0; // ✅ true if deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ SEARCH Author by name → returns ID
    @Override
    public int searchAuthor(String name) {
        try {
            String sql = "SELECT author_id FROM Author WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("author_id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

	@Override
	public ArrayList<AuthorDTO> getAllAuthors() {
	    ArrayList<AuthorDTO> authors = new ArrayList<>();

	    try {
	        String sql = "SELECT author_id, name, biography FROM Author";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            AuthorDTO author = new AuthorDTO();
	            author.setAuthorId(rs.getInt("author_id"));
	            author.setName(rs.getString("name"));
	            author.setBiography(rs.getString("biography"));

	            authors.add(author);
	        }

	        rs.close();
	        ps.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return authors;
	}

	@Override
	 public String getAuthorById(int id) {
        String name = null;
        String sql = "SELECT name FROM Author WHERE author_id = ?"; 

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return name; 
    }

}
