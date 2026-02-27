package main.java.com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;

public interface IChapterDAO {
	public boolean createChapter(int bookID,String chapterName);
	public boolean updateChaper(int bookID,String oldChapterName,String newName);
	public ArrayList<ChapterDTO> retrieveChapters(int bookID);
	public boolean deleteChapter(int bookID,String chapterName);
	public int searchChapter(String chapterName);
	public ArrayList<IndexRow> getIndexRowsByRootId(int rootId);
	public ArrayList<IndexRow> getIndexRowsByLemmaId(int lemmaId);
	public ArrayList<IndexRow> getIndexRowsByTokenText(String tokenValue);
}
