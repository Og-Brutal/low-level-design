package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.BookDTO;

public class BookBO implements IBookBO {
	
	private IDataAccessLayerFasade daf;
	
	
	public BookBO(IDataAccessLayerFasade daf)
	{
		this.daf = daf;
	}

	@Override
	public boolean createBook(String title, String authorName, String era) {
		
		int authorId=daf.searchAuthor(authorName);
		 if(authorId==-1) {
			 return false;
		 }
		 
		 if(daf.searchBook(title)!=-1) {
			 return false;
		 }
		 return daf.createBook(title, authorId, era);
	}

	@Override
	public BookDTO retrieveBook(String title) {
		
		return daf.retrieveBook(title);
	}

	@Override
	public boolean updateBook(String oldBookName,String title,String authorName, String era){
		System.out.println(authorName);
		int authorId=daf.searchAuthor(authorName);
		 if(authorId==-1) {
			 return false;
		 }
		 return daf.updateBook(oldBookName,title,authorId,era);
	}
		
	@Override
	public boolean deleteBook(String title) {
		return daf.deleteBook(title);
	}

	@Override
	public int searchBook(String title) {
		return daf.searchBook(title);
	}

	@Override
	public ArrayList<BookDTO> getAllBooks() {
		return daf.getAllBooks();
	}

	@Override
	public String getBookById(int id) {
		return daf.getBookById(id);
	}

	@Override
	public boolean chapterSeparater(String path) {
	    try {
	        // Step 1: Extract book name from file path
	        String[] pathParts = path.replace("\\", "/").split("/");
	        String fileName = pathParts[pathParts.length - 1];
	        String bookName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;

	        System.out.println(" Detected Book Name: " + bookName);

	        // Step 2: Read file content via DAL
	        String bookData = daf.getData(path);
	        if (bookData == null || bookData.isEmpty()) {
	            System.err.println(" No data found in file or unable to read: " + path);
	            return false;
	        }

	        // Step 3: Create book entry (default author & era can be improved later)
	        boolean bookCreated = daf.createBook(bookName, -1, null);
	        if (!bookCreated) {
	            System.out.println(" Book may already exist: " + bookName);
	        }

	        // Step 4: Get book ID
	        int bookID = daf.searchBook(bookName);
	        if (bookID == -1) {
	            System.err.println(" Could not find book ID for: " + bookName);
	            return false;
	        }

	        // Step 5: Split file content into chapters using ##CHAPTER##
	        String[] rawChapters = bookData.split("##CHAPTER##");
	        if (rawChapters.length == 0) {
	            System.err.println(" No chapters found using ##CHAPTER## separator.");
	            return false;
	        }

	        // Step 6: Process each chapter
	        for (String rawChapter : rawChapters) {
	            rawChapter = rawChapter.trim();
	            if (rawChapter.isEmpty()) continue;

	            // First line → Chapter Name
	            String[] lines = rawChapter.split("\\r?\\n", 2);
	            String chapterName = lines[0].trim();
	            if (chapterName.isEmpty()) {
	                System.err.println(" Chapter without a name found, skipping...");
	                continue;
	            }

	            // Create chapter
	            boolean chapterCreated = daf.createChapter(bookID, chapterName);
	            if (!chapterCreated) {
	                System.out.println(" Chapter may already exist: " + chapterName);
	            }

	            // Get Chapter ID
	            int chapterID = daf.searchChapter(chapterName);
	            if (chapterID == -1) {
	                System.err.println(" Chapter ID not found for: " + chapterName);
	                continue;
	            }

	            // Get chapter text (excluding chapter name)
	            String chapterContent = (lines.length > 1) ? lines[1].trim() : "";
	            if (chapterContent.isEmpty()) {
	                System.err.println(" Empty content for chapter: " + chapterName);
	                continue;
	            }

	            // Step 7: Break chapter into sentences
	            ArrayList<String> sentences = TextProcessingUtil.ChapterInToSentence(chapterContent);
	            if (sentences.isEmpty()) {
	                System.err.println(" No sentences extracted for chapter: " + chapterName);
	                continue;
	            }

	            // Step 8: Insert sentences into DB
	            for (String sentence : sentences) {
	                boolean inserted = daf.createSentence(chapterID, sentence,"" , "", "");
	                if (!inserted) {
	                    System.err.println(" Failed to insert sentence: " + sentence);
	                }
	            }

	            System.out.println(" Chapter processed successfully: " + chapterName);
	        }

	        System.out.println(" All chapters processed successfully for book: " + bookName);
	        return true;

	    } catch (Exception e) {
	        System.err.println(" Exception in chapterSeparater: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}


	
	

}
