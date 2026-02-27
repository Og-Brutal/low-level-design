package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.ChapterDTO;

public class ChapterBO implements IChapterBO {
	
	private IDataAccessLayerFasade daf;
	
	
	public ChapterBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}


	@Override
	public boolean createChapter(String bookName,String chapterName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return false;
		}
		return daf.createChapter(bookID, chapterName);
	}


	@Override
	public boolean updateChapter(String bookName, String oldChapterName,String newName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return false;
		}
		return daf.updateChaper(bookID, oldChapterName, newName);
	}


	@Override
	public ArrayList<ChapterDTO> retrieveChapters(String bookName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return null;
		}
		return daf.retrieveChapters(bookID);
	}


	@Override
	public boolean deleteChapter(String bookName, String chapterName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return false;
		}
		return daf.deleteChapter(bookID, chapterName);
	}


	@Override
	public int searchChapter(String chapterName) {
		return daf.searchChapter(chapterName);
	}
}
