package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.ChapterDTO;

public interface IChapterBO {
	public boolean createChapter(String bookName,String chapterName);
	public boolean updateChapter(String bookName,String oldChapterName,String newName);
	public ArrayList<ChapterDTO> retrieveChapters(String bookName);
	public boolean deleteChapter(String bookName,String chapterName);
	public int searchChapter(String chapterName);
	
	public boolean sentenceExtracter(String bookName,String path);
}
