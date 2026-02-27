package com.apm.bll;

import java.util.ArrayList;

import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.SentenceDTO;

public class BussinessLayerFasade implements IBussinessLayerFasade{
	private IBookBO book;
	private IAuthorBO author;
	private ISentenceBO sentence;
	private IChapterBO chapter;
	public BussinessLayerFasade(IBookBO book,IAuthorBO author,ISentenceBO sentence,IChapterBO chapter)
	{
		this.book = book;
		this.author = author;
		this.sentence = sentence;
		this.chapter=chapter;
	}

	@Override
	public boolean createBook(String title, String authorName, String era) {
		return book.createBook(title, authorName, era);
	}

	@Override
	public BookDTO retrieveBook(String title) {
		return book.retrieveBook(title);
	}

	@Override
	public boolean updateBook(String oldBookName,String title,String authorName, String era){
		return book.updateBook(oldBookName,title,authorName,era);
	}

	@Override
	public boolean deleteBook(String title) {
		return book.deleteBook(title);
	}
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
	public boolean createAuthor(String name, String biography){
		return author.createAuthor(name, biography);
	}

	@Override
	public AuthorDTO retrieveAuthor(String name) {
		return author.retrieveAuthor(name);
	}

	@Override
	public boolean updateAuthor(String oldAuthorName,String name, String biography){
		return author.updateAuthor(oldAuthorName, name, biography);
	}

	@Override
	public boolean deleteAuthor(String name) {
		return author.deleteAuthor(name);
	}

	@Override
	public int searchAuthor(String name) {
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
	public boolean createSentence(String bookName, String text, String textDiacritized, String translation,String notes) {
		System.out.println("in business fasade");
		return sentence.createSentence(bookName, text, textDiacritized, translation, notes);
	}

	@Override
	public ArrayList<SentenceDTO> retrieveSentence(String bookName) {
		return sentence.retrieveSentence(bookName);
	}

	@Override
	public boolean updateSenetence(String bookName, int sentenceNumber, String text, String textDiacritized,String translation, String notes) {
		return sentence.updateSenetence(bookName, sentenceNumber, text, textDiacritized, translation, notes);
	}

	@Override
	public boolean deleteSentence(String bookName, int sentenceNumber) {
		System.out.println(bookName+sentenceNumber);
		return sentence.deleteSentence(bookName, sentenceNumber);
	}

	@Override
	public boolean createChapter(String bookName, String chapterName) {
		return chapter.createChapter(bookName, chapterName);
	}

	@Override
	public boolean updateChapter(String bookName, String oldChapterName, String newName) {
		return chapter.updateChapter(bookName, oldChapterName, newName);
	}

	@Override
	public ArrayList<ChapterDTO> retrieveChapters(String bookName) {
		return chapter.retrieveChapters(bookName);
	}

	@Override
	public boolean deleteChapter(String bookName, String chapterName) {
		return chapter.deleteChapter(bookName, chapterName);
	}

	@Override
	public int searchChapter(String chapterName) {
		return chapter.searchChapter(chapterName);
	}	

	@Override
	public boolean sentenceExtracter(String bookName, String path) {
		return chapter.sentenceExtracter(bookName, path);
	}

	@Override
	public boolean chapterSeparater(String path) {
		return book.chapterSeparater(path);
	}
	
	
}
