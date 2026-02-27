package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.SentenceDTO;

public class SentenceDAO implements ISentenceDAO {
	 private Connection conn;
	 private Statement stmt;
	 public SentenceDAO( Connection conn,Statement stmt)
	 {	
		 this.conn = conn;
		 try {
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }

	@Override
	public boolean createSentence(int chapterID, String text, String textDiacritized, String translation, String notes) {
	    try {
	        // SQL query to insert a new sentence
	        String sql = "INSERT INTO Sentence (chapter_id, text, text_diacritized, translation, notes) VALUES (?, ?, ?, ?, ?)";
	        
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, chapterID);
	        ps.setString(2, text);

	        // Optional fields
	        if (textDiacritized == null || textDiacritized.trim().isEmpty()) {
	            ps.setNull(3, java.sql.Types.VARCHAR);
	        } else {
	            ps.setString(3, textDiacritized);
	        }

	        if (translation == null || translation.trim().isEmpty()) {
	            ps.setNull(4, java.sql.Types.VARCHAR);
	        } else {
	            ps.setString(4, translation);
	        }

	        if (notes == null || notes.trim().isEmpty()) {
	            ps.setNull(5, java.sql.Types.VARCHAR);
	        } else {
	            ps.setString(5, notes);
	        }

	        // Execute the insert
	        int rowsInserted = ps.executeUpdate();
	        return rowsInserted > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	


	@Override
	public boolean updateSenetence(int chapterID, int sentenceNumber,String text, String textDiacritized, String translation, String notes	) {
	    try {
	        String sql = "UPDATE Sentence SET text = ?, text_diacritized = ?, translation = ?, notes = ? " +
	                     "WHERE chapter_id = ? AND sentence_number = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);

	        ps.setString(1, text);
	        ps.setString(2, textDiacritized);
	        ps.setString(3, translation);
	        ps.setString(4, notes);
	        ps.setInt(5, chapterID);
	        ps.setInt(6, sentenceNumber);

	        int rowsUpdated = ps.executeUpdate();
	        return rowsUpdated > 0; // true if at least one row was updated
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
		
	}

	@Override
	public boolean deleteSentence(int chapterID, int sentenceNumber) {
		   try {
		        // Step 1: Delete the target sentence
		        String sql = "DELETE FROM Sentence WHERE chapter_id = ? AND sentence_number = ?";
		        PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setInt(1, chapterID);
		        ps.setInt(2, sentenceNumber);

		        int rowsDeleted = ps.executeUpdate();

		        // Step 2: If deleted, renumber remaining sentences
		        if (rowsDeleted > 0) {
		            renumberSentences(chapterID);
		            return true;
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
	}
	
	private void renumberSentences(int chapterID) {
	    try {
	        // Reset row number and update sequence per book
	        String sql = ""+
	            "SET @row = 0;"+
	            "UPDATE Sentence"+ 
	            "SET sentence_number = (@row := @row + 1)"+
	            "WHERE chapter_id = ?"+
	            "ORDER BY sentence_id;";

	        // MySQL does not allow multiple statements in one PreparedStatement by default,
	        // so we execute them separately:
	        Statement stmt = conn.createStatement();
	        stmt.execute("SET @row = 0;");

	        PreparedStatement ps = conn.prepareStatement(
	            "UPDATE Sentence SET sentence_number = (@row := @row + 1) WHERE chapter_id = ? ORDER BY sentence_id;"
	        );
	        ps.setInt(1, chapterID);
	        ps.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public ArrayList<SentenceDTO> retrieveSentence(int chapterID) {
		 ArrayList<SentenceDTO> sentences = new ArrayList<>();

		    try {
		        String sql = "SELECT sentence_id, sentence_number, text, text_diacritized, translation, notes " +
		                     "FROM Sentence WHERE chapter_id = ? ORDER BY sentence_number ASC";
		        PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setInt(1, chapterID);

		        ResultSet rs = ps.executeQuery();
		        boolean hasData = false; // Flag to track if at least one record is found

		        while (rs.next()) {
		            hasData = true;
		            SentenceDTO sentence = new SentenceDTO();
		            sentence.setSentenceId(rs.getInt("sentence_id"));
		            sentence.setBookId(chapterID);
		            sentence.setSentenceNumber(rs.getInt("sentence_number"));
		            sentence.setText(rs.getString("text"));
		            sentence.setTextDiacritized(rs.getString("text_diacritized"));
		            sentence.setTranslation(rs.getString("translation"));
		            sentence.setNotes(rs.getString("notes"));

		            sentences.add(sentence);
		        }

		        // If no data found, return null
		        if (!hasData) {
		            return null;
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		        return null; // Return null on error as well
		    }

		    return sentences;
	}


	

}
