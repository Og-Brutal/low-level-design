package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.ChapterDTO;

public interface IChapterDAO {
	public boolean createChapter(int bookID,String chapterName);
	public boolean updateChaper(int bookID,String oldChapterName,String newName);
	public ArrayList<ChapterDTO> retrieveChapters(int bookID);
	public boolean deleteChapter(int bookID,String chapterName);
	public int searchChapter(String chapterName);
}
