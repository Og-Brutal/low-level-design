package main.java.com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;

public interface IChapterBO {
	public boolean createChapter(String bookName,String chapterName);
	public boolean updateChapter(String bookName,String oldChapterName,String newName);
	public ArrayList<ChapterDTO> retrieveChapters(String bookName);
	public boolean deleteChapter(String bookName,String chapterName);
	public int searchChapter(String chapterName);
	public void processChapterSentences(String chapterName, String chapterContent);
	public boolean sentenceExtracter(String bookName,String path);
	public ArrayList<IndexRow> getIndexRowsByRootId(String root);
	public ArrayList<IndexRow> getIndexRowsByLemmaId(String lemma);
	public ArrayList<IndexRow> getIndexRowsByTokenText(String tokenValue);
}
