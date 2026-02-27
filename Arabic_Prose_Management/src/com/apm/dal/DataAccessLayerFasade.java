package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.SentenceDTO;

public class DataAccessLayerFasade implements IDataAccessLayerFasade{
    
	
	private IBookDAO book;
	private IAuthorDAO author;
	private ISentenceDAO sentence;
	private IChapterDAO chapter;
	
	//title , author id ,era
	public DataAccessLayerFasade(IBookDAO book,IAuthorDAO author,ISentenceDAO sentence,IChapterDAO chapter)
	{
		this.book = book;
		this.author = author;
		this.sentence = sentence;
		this.chapter=chapter;
	}
	@Override
	public boolean createBook(String title , int authorId, String era) {
		return book.createBook( title, authorId,  era);
	}

	@Override
	public BookDTO retrieveBook(String title) {
		return book.retrieveBook(title);
	}

	@Override
	public boolean updateBook(String oldBookName,String title,int authorId, String era){
		return book.updateBook(oldBookName,title, authorId, era);
	}

	@Override
	public boolean deleteBook(String title) {
		return book.deleteBook(title);
	}

	@Override
	public int searchBook(String title) {
		return book.searchBook(title);
	}
	
	@Override
	public ArrayList<BookDTO> getAllBooks() {
		return book.getAllBooks();
	}
	@Override
	public String getBookById(int id) {
		return book.getBookById(id);
	}
	

	@Override
	public boolean createAuthor(String name, String biography) {
		return author.createAuthor(name, biography);
	}

	@Override
	public AuthorDTO retrieveAuthor(String name) {
		return author.retrieveAuthor(name);
	}

	@Override
	public boolean updateAuthor(String oldAuthorName,String name, String biography)  {
		return author.updateAuthor(oldAuthorName, name, biography);
	}

	@Override
	public boolean deleteAuthor(String name) {
		return author.deleteAuthor(name);
	}

	@Override
	public int searchAuthor(String name) {
		// TODO Auto-generated method stub
		return author.searchAuthor(name);
	}
	
	
	
	@Override
	public ArrayList<AuthorDTO> getAllAuthors() {
		return author.getAllAuthors();
	}
	
	
	@Override
	public String getAuthorById(int id) {
		return author.getAuthorById(id);
	}
	
	
	@Override
	public boolean createSentence(int bookId, String text, String textDiacritized,String translation, String notes) {
		return sentence.createSentence(bookId, text, textDiacritized, translation, notes);
	}

	@Override
	public ArrayList<SentenceDTO> retrieveSentence(int bookID) {
		return sentence.retrieveSentence(bookID);
	}

	@Override
	public boolean updateSenetence(int BookId, int sentenceNumber,String text, String textDiacritized, String translation, String notes) {
		return sentence.updateSenetence(BookId, sentenceNumber, text, textDiacritized, translation, notes);
	}

	@Override
	public boolean deleteSentence(int bookId, int sentenceNumber) {
		return sentence.deleteSentence(bookId, sentenceNumber);
	}
	@Override
	public boolean createChapter(int bookID, String chapterName) {
		return chapter.createChapter(bookID, chapterName);
	}
	@Override
	public boolean updateChaper(int bookID, String oldChapterName, String newName) {
		return chapter.updateChaper(bookID, oldChapterName, newName);
	}
	@Override
	public ArrayList<ChapterDTO> retrieveChapters(int bookID) {
		return chapter.retrieveChapters(bookID);
	}
	@Override
	public boolean deleteChapter(int bookID, String chapterName) {
		return chapter.deleteChapter(bookID, chapterName);
	}
	@Override
	public int searchChapter(String chapterName) {
		return chapter.searchChapter(chapterName);
	}







}
