package main.java.com.apm.bll;

import java.util.ArrayList;

import com.apm.bll.utils.TextProcessingUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;

public class ChapterBO implements IChapterBO,IObserveable {
	
	private IDataAccessLayerFasade daf;
	private ISentenceBO sentenceBO;
	private ArrayList<IObserver> observers;
	
	public ChapterBO(IDataAccessLayerFasade daf,ISentenceBO sentenceBO,ArrayList<IObserver> observers)
	{
		this.daf = daf;
		this.sentenceBO=sentenceBO;
		this.observers=observers;
	}


	@Override
	public boolean createChapter(String bookName,String chapterName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return false;
		}
		boolean check=daf.createChapter(bookID, chapterName);
		update();
		return check;
	}


	@Override
	public boolean updateChapter(String bookName, String oldChapterName,String newName) {
		int bookID=daf.searchBook(bookName);
		if(bookID==-1) {
			return false;
		}
		boolean check=daf.updateChaper(bookID, oldChapterName, newName);
		update();
		return check;
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
		boolean check=daf.deleteChapter(bookID, chapterName);
		update();
		return check;
	}


	@Override
	public int searchChapter(String chapterName) {
		return daf.searchChapter(chapterName);
	}


	@Override
	public boolean sentenceExtracter(String bookName, String path) {
	    // Step 1: Get the book ID
	    int bookID = daf.searchBook(bookName);
	    if (bookID == -1) {
	        System.err.println(" Book not found: " + bookName);
	        return false;
	    }

	    // Step 2: Extract file data from DAL
	    String fileData = daf.getData(path);
	    if (fileData == null || fileData.isEmpty()) {
	        System.err.println("No data found or error reading file at: " + path);
	        return false;
	    }

	    // Step 3: Split file into lines
	    String[] lines = fileData.split("\\r?\\n");
	    if (lines.length == 0) {
	        System.err.println(" Empty file: " + path);
	        return false;
	    }

	    // Step 4: First line is the chapter name
	    String chapterName = lines[0].trim();
	    if (chapterName.isEmpty()) {
	        System.err.println(" Chapter name missing in first line of file.");
	        return false;
	    }

	    // Step 5: Create the chapter for this book
	    boolean chapterCreated = daf.createChapter(bookID, chapterName);
	    update();
	    if (!chapterCreated) {
	        System.out.println("Chapter may already exist: " + chapterName);
	    }

	    // Step 6: Get chapter ID
	    int chapterID = daf.searchChapter(chapterName);
	    
	    if (chapterID == -1) {
	        System.err.println(" Chapter ID not found for: " + chapterName);
	        return false;
	    }

	    // Step 7: Combine remaining lines (skip separators, bullets)
	    StringBuilder chapterTextBuilder = new StringBuilder();
	    for (int i = 1; i < lines.length; i++) {
	        String line = lines[i].trim();

	        // Skip separators and bullet lines
	        if (line.isEmpty() || line.matches("-+")) continue;
	        if (line.startsWith("•") || line.startsWith("*") || line.startsWith("—")) continue;

	        chapterTextBuilder.append(line).append(" ");
	    }

	    String chapterText = chapterTextBuilder.toString().trim();
	    if (chapterText.isEmpty()) {
	        System.err.println(" No valid content found for chapter: " + chapterName);
	        return false;
	    }

	    // Step 8: Extract sentences using helper function
	    ArrayList<String> sentences = TextProcessingUtil.ChapterInToSentence(chapterText);

	    if (sentences.isEmpty()) {
	        System.err.println(" No sentences extracted from chapter: " + chapterName);
	        return false;
	    }

	    // Step 9: Insert each sentence into the database
	    int count=0;
	    boolean success = true;
	    for (String sentence : sentences) {
	        boolean inserted = sentenceBO.createSentence(chapterName,sentence,"" , "", "");
	        System.out.println(count++);
	        if (!inserted) {
	            System.err.println(" Failed to insert sentence: " + sentence);
	            success = false;
	        }
	    }

	    System.out.println(" Sentences extracted and inserted successfully for chapter: " + chapterName);
	    return success;
	}


	@Override
	public boolean addObserver(IObserver observer) {
		this.observers.add(observer);
		return true;
	}

	@Override
	public boolean removeObserver() {
		return true;
	}

	@Override
	public void update() {
		for(IObserver observer :observers) {
			observer.autoRefresh("Chapter");
		}
		
	}


	@Override
		public void processChapterSentences(String chapterName, String chapterContent) {
	
		    ArrayList<String> sentences =TextProcessingUtil.ChapterInToSentence(chapterContent);
		    System.out.println(sentences+"\n\n");
		    if (sentences == null || sentences.isEmpty()) {
		        System.err.println("No sentences found for chapter ID: " + chapterName);
		        return;
		    }
	
		    for (String sentence : sentences) {
		        boolean inserted = sentenceBO.createSentence(chapterName, sentence, "", "", "");
		        if (!inserted) {
		            System.err.println("Failed to insert sentence: " + sentence);
		        }
		    }
		}


	@Override
	public ArrayList<IndexRow> getIndexRowsByRootId(String root) {
		int rootId=daf.searchRoot(root);
		if(rootId==-1) {
			return null;
		}
		return daf.getIndexRowsByRootId(rootId);
	}


	@Override
	public ArrayList<IndexRow> getIndexRowsByLemmaId(String lemma) {
		int lemmaId=daf.searchLemma(lemma);
		if(lemmaId==-1) {
			return null;
		}
		return daf.getIndexRowsByLemmaId(lemmaId);
	}


	@Override
	public ArrayList<IndexRow> getIndexRowsByTokenText(String tokenValue) {
		return daf.getIndexRowsByTokenText(tokenValue);
	}


	



	
}
