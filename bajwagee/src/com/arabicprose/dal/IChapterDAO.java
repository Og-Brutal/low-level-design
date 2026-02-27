package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.ChapterDTO;

public interface IChapterDAO {
    void addChapter(ChapterDTO chapter) throws SQLException;
    List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException;
    void updateChapter(ChapterDTO chapter) throws SQLException;
    void deleteChapter(int chapterId) throws SQLException;
    ChapterDTO getChapterById(int chapterId) throws SQLException;
    List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException;
    int getNextChapterOrder(int bookId) throws SQLException;
    boolean isChapterNameExists(int bookId, String chapterName) throws SQLException;
}