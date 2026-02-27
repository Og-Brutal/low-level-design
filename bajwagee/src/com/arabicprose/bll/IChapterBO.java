package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;
import com.arabicprose.dto.ChapterDTO;

public interface IChapterBO {
    void addChapter(ChapterDTO chapterDTO) throws SQLException;
    List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException;
    void updateChapter(ChapterDTO chapterDTO) throws SQLException;
    void deleteChapter(int chapterId) throws SQLException;
    ChapterDTO getChapterById(int chapterId) throws SQLException;
    List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException;
    int getNextChapterOrder(int bookId) throws SQLException;
    public boolean sentenceSplitter(String path, int bookID);
    
}