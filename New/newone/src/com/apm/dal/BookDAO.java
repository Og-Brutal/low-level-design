package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // ✅ use JDBC Statement, not XDevAPI
import java.util.ArrayList;

import com.apm.dto.BookDTO;

public class BookDAO implements IBookDAO {
	 private Connection conn;
	 private Statement stmt;
	 public BookDAO( Connection conn,Statement stmt)
	 {	
		 this.conn = conn;
		 try {
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
    @Override
    public boolean createBook(String title, int authorId, String era) {
    	
        
        try {
            // SQL query to insert a new book into the table
            String sql = "INSERT INTO Book ( title, author_id, era) VALUES ( ?, ?, ?)";

            // Use PreparedStatement to prevent SQL injection and handle parameters safely
            PreparedStatement ps = conn.prepareStatement(sql);
           
            ps.setString(1, title);
            ps.setInt(2, authorId);
            ps.setString(3,era);
           

            // Execute the insert command
            int rowsInserted = ps.executeUpdate(); // ✅ executes INSERT, UPDATE, DELETE

            // If at least one row was inserted, return true
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }		
      
    }

    @Override
    public BookDTO retrieveBook(String title) {
        try {
            // SQL query to get a book by its title
            String sql = "SELECT * FROM Book WHERE title = ?";

            // Prepare the statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // If a book is found, create a DTO and return it
            if (rs.next()) {
                int bookId = rs.getInt("book_id");
                int authorId = rs.getInt("author_id");
                String era = rs.getString("era");

                // Create and return the BookDTO
                return new BookDTO(bookId, title, authorId, era);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If not found or exception occurs
        return null;
    }


    @Override
    public boolean updateBook(String oldBookName,String title,int authorId, String era) {
    	System.out.println(oldBookName+ title+authorId+ era);
        try {
            // SQL query to update book details using book_id as the key
            String sql = "UPDATE Book SET title = ?, author_id = ?,  era = ? WHERE title = ?";

            // Use PreparedStatement to safely inject parameters
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setInt(2, authorId);
            ps.setString(3, era);
            ps.setString(4, oldBookName);

            // Execute the update command
            int rowsUpdated = ps.executeUpdate(); // returns number of rows affected

            // Return true if at least one row was updated
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteBook(String title) {
        try {
            // SQL query to delete a book by title
            String sql = "DELETE FROM Book WHERE title = ?";

            // Prepare the SQL command safely
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);

            // Execute the delete command
            int rowsDeleted = ps.executeUpdate();  // returns how many rows were deleted

            // Return true if at least one row was deleted
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public int searchBook(String title) {
        try {
            // Prepare SQL query to check if the book exists
            String sql = "SELECT book_id FROM Book WHERE title = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);

            // Execute query
            ResultSet rs = ps.executeQuery();
            

            // rs.next() returns true if a record exists
             if(rs.next())
             {
            	 return  rs.getInt("book_id");
             }
             else
            	 return -1;
             

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return false if any error occurs
        }
    }
	@Override
	public ArrayList<BookDTO> getAllBooks() {
	    ArrayList<BookDTO> books = new ArrayList<>();
	    
	    try {
	        String sql = "SELECT book_id, title, author_id, era FROM Book";
	        ResultSet rs = stmt.executeQuery(sql);
	        
	        while (rs.next()) {
	            int id = rs.getInt("book_id");
	            String title = rs.getString("title");
	            int authorId = rs.getInt("author_id");
	            String era = rs.getString("era");
	            
	            // Assuming BookDTO has a constructor with authorId instead of authorName
	            BookDTO book = new BookDTO(id, title, authorId, era);
	            books.add(book);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return books;
	}
	@Override
    public String getBookById(int id) {
        String title = null;
        String sql = "SELECT title FROM Book WHERE book_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    title = rs.getString("title");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return title; // returns null if not found
    }

}