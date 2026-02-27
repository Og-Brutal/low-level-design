package com.apm.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;

public class ChapterDAO implements IChapterDAO {
	 private Connection conn;
	 private Statement stmt;
	 public ChapterDAO( Connection conn,Statement stmt)
	 {	
		 this.conn = conn;
		 try {
			this.stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }

	@Override
	public boolean createChapter(int bookID, String chapterName) {
	    String query = "INSERT INTO Chapter (book_id, chapter_name) VALUES (?, ?)";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        // Set values
	        stmt.setInt(1, bookID);
	        stmt.setString(2, chapterName);
	        
	        // Execute the query
	        int rowsInserted = stmt.executeUpdate();
	        
	        // Return true if at least one row was inserted
	        return rowsInserted > 0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	@Override
	public boolean updateChaper(int bookID, String oldChapterName, String newName) {
	    String query = "UPDATE Chapter SET chapter_name = ? WHERE book_id = ? AND chapter_name = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        // Set parameters
	        stmt.setString(1, newName);
	        stmt.setInt(2, bookID);
	        stmt.setString(3, oldChapterName);
	        
	        // Execute the update
	        int rowsUpdated = stmt.executeUpdate();
	        
	        // Return true if at least one record was updated
	        return rowsUpdated > 0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	@Override
	public ArrayList<ChapterDTO> retrieveChapters(int bookID) {
	    ArrayList<ChapterDTO> chapters = new ArrayList<>();
	    String query = "SELECT chapter_id, book_id, chapter_name FROM Chapter WHERE book_id = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        
	        // Set parameter
	        stmt.setInt(1, bookID);
	        
	        // Execute query
	        ResultSet rs = stmt.executeQuery();
	        
	        // Map each row to a ChapterDTO object
	        while (rs.next()) {
	            ChapterDTO chapter = new ChapterDTO();
	            chapter.setChapterId(rs.getInt("chapter_id"));
	            chapter.setBookId(rs.getInt("book_id"));
	            chapter.setChapterName(rs.getString("chapter_name"));
	            
	            chapters.add(chapter);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return chapters;
	}


	@Override
	public boolean deleteChapter(int bookID, String chapterName) {
	    String query = "DELETE FROM Chapter WHERE book_id = ? AND chapter_name = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, bookID);
	        stmt.setString(2, chapterName);
	        
	        int rowsDeleted = stmt.executeUpdate();
	        return rowsDeleted > 0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	@Override
	public int searchChapter(String chapterName) {
	    int chapterId = 0; // default: not found
	    String sql = "SELECT chapter_id FROM Chapter WHERE chapter_name = ?";

	    // adjust table and column names according to your schema
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {

	        // Set query parameter
	        stmt.setString(1, chapterName);

	        // Execute query
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                chapterId = rs.getInt("chapter_id");
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return chapterId;
	}
	@Override
	public ArrayList<IndexRow> getIndexRowsByRootId(int rootId) {

	    ArrayList<IndexRow> list = new ArrayList<>();

	    try {
	        String sql =
	            "SELECT DISTINCT c.name AS chapterName, s.sentence_number AS sentenceNumber " +
	            "FROM root r " +
	            "INNER JOIN lemma l ON r.root_id = l.root_id " +
	            "INNER JOIN token t ON l.lemma_id = t.lemma_id " +
	            "INNER JOIN sentence s ON t.sentence_id = s.sentence_id " +
	            "INNER JOIN chapter c ON s.chapter_id = c.chapter_id " +
	            "WHERE r.root_id = ? " +
	            "ORDER BY c.name, s.sentence_number";

	        PreparedStatement pst = conn.prepareStatement(sql);
	        pst.setInt(1, rootId);

	        ResultSet rs = pst.executeQuery();

	        while (rs.next()) {
	            IndexRow row = new IndexRow(
	                rs.getString("chapterName"),
	                rs.getInt("sentenceNumber")
	            );
	            list.add(row);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return list;
	}

	@Override
	public ArrayList<IndexRow> getIndexRowsByLemmaId(int lemmaId) {

	    ArrayList<IndexRow> list = new ArrayList<>();

	    try {
	        String sql =
	            "SELECT DISTINCT c.name AS chapterName, s.sentence_number AS sentenceNumber " +
	            "FROM lemma l " +
	            "INNER JOIN token t ON l.lemma_id = t.lemma_id " +
	            "INNER JOIN sentence s ON t.sentence_id = s.sentence_id " +
	            "INNER JOIN chapter c ON s.chapter_id = c.chapter_id " +
	            "WHERE l.lemma_id = ? " +
	            "ORDER BY c.name, s.sentence_number";

	        PreparedStatement pst = conn.prepareStatement(sql);
	        pst.setInt(1, lemmaId);

	        ResultSet rs = pst.executeQuery();

	        while (rs.next()) {
	            IndexRow row = new IndexRow(
	                rs.getString("chapterName"),
	                rs.getInt("sentenceNumber")
	            );
	            list.add(row);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return list;
	}
	@Override
	public ArrayList<IndexRow> getIndexRowsByTokenText(String tokenValue) {

	    ArrayList<IndexRow> list = new ArrayList<>();

	    try {
	        String sql =
	            "SELECT DISTINCT c.chapter_name AS chapterName, s.sentence_number AS sentenceNumber " +
	            "FROM token t " +
	            "INNER JOIN sentence s ON t.sentence_id = s.sentence_id " +
	            "INNER JOIN chapter c ON s.chapter_id = c.chapter_id " +
	            "WHERE t.token = ? " +
	            "ORDER BY c.chapter_name, s.sentence_number";

	        PreparedStatement pst = conn.prepareStatement(sql);
	        pst.setString(1, tokenValue);

	        ResultSet rs = pst.executeQuery();

	        while (rs.next()) {
	            IndexRow row = new IndexRow(
	                rs.getString("chapterName"),
	                rs.getInt("sentenceNumber")
	            );
	            list.add(row);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return list;
	}


}
