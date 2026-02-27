package com.arabicprose.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.arabicprose.dto.ChapterDTO;

public class ChapterDAO implements IChapterDAO {
    
    @Override
    public void addChapter(ChapterDTO chapter) throws SQLException {
        String sql = "INSERT INTO Chapter (book_id, chapter_name, chapter_order, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, chapter.getBookId());
            pstmt.setString(2, chapter.getChapterName());
            pstmt.setInt(3, chapter.getChapterOrder());
            pstmt.setString(4, chapter.getDescription());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                chapter.setChapterId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException {
        List<ChapterDTO> chapters = new ArrayList<>();
        String sql = "SELECT * FROM Chapter WHERE book_id = ? ORDER BY chapter_order, chapter_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                chapters.add(new ChapterDTO(
                    rs.getInt("chapter_id"),
                    rs.getInt("book_id"),
                    rs.getString("chapter_name"),
                    rs.getInt("chapter_order"),
                    rs.getString("description")
                ));
            }
        }
        return chapters;
    }

    @Override
    public void updateChapter(ChapterDTO chapter) throws SQLException {
        String sql = "UPDATE Chapter SET chapter_name = ?, chapter_order = ?, description = ? WHERE chapter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chapter.getChapterName());
            pstmt.setInt(2, chapter.getChapterOrder());
            pstmt.setString(3, chapter.getDescription());
            pstmt.setInt(4, chapter.getChapterId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteChapter(int chapterId) throws SQLException {
        String sql = "DELETE FROM Chapter WHERE chapter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chapterId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public ChapterDTO getChapterById(int chapterId) throws SQLException {
        String sql = "SELECT * FROM Chapter WHERE chapter_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chapterId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ChapterDTO(
                    rs.getInt("chapter_id"),
                    rs.getInt("book_id"),
                    rs.getString("chapter_name"),
                    rs.getInt("chapter_order"),
                    rs.getString("description")
                );
            }
        }
        return null;
    }

    @Override
    public List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException {
        List<ChapterDTO> chapters = new ArrayList<>();
        String sql = "SELECT c.*, b.title as book_title FROM Chapter c " +
                     "JOIN Book b ON c.book_id = b.book_id " +
                     "WHERE c.chapter_name LIKE ? ORDER BY c.chapter_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + chapterName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ChapterDTO chapter = new ChapterDTO(
                    rs.getInt("chapter_id"),
                    rs.getInt("book_id"),
                    rs.getString("chapter_name"),
                    rs.getInt("chapter_order"),
                    rs.getString("description")
                );
                chapters.add(chapter);
            }
        }
        return chapters;
    }

    @Override
    public int getNextChapterOrder(int bookId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(chapter_order), 0) + 1 FROM Chapter WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    @Override
    public boolean isChapterNameExists(int bookId, String chapterName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Chapter WHERE book_id = ? AND chapter_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setString(2, chapterName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}