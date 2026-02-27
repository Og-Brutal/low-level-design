package com.apm.bll;

import java.util.ArrayList;

import com.apm.dal.ITokenDAO;
import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;
import com.apm.dto.SentenceDTO;
import com.apm.dto.TokenDTO;

public class BussinessLayerFasade implements IBussinessLayerFasade{
	private IBookBO book;
	private IAuthorBO author;
	private ISentenceBO sentence;
	private IChapterBO chapter;
	private ITokenBO token;
	private ILemmaBO lemma;
	private IRootBO root;
	public BussinessLayerFasade(IBookBO book,IAuthorBO author,ISentenceBO sentence,IChapterBO chapter,ITokenBO token,ILemmaBO lemma,IRootBO root)
	{
		this.book = book;
		this.author = author;
		this.sentence = sentence;
		this.chapter=chapter;
		this.token=token;
		this.lemma=lemma;
		this.root=root;
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

	@Override
	public int searchSentence(String text) {
		return sentence.searchSentence(text);
	}

	@Override
	public boolean addToken(String text, ArrayList<String> tokens) {
		return token.addToken(text, tokens);
	}

	@Override
	public ArrayList<TokenDTO> getTokensByLemma(String lemma) {
		return token.getTokensByLemma(lemma);
	}

	@Override
	public ArrayList<TokenDTO> getAllTokens() {
		return token.getAllTokens();
	}

	@Override
	public boolean addLemmas(ArrayList<String> Lemma) {
		return lemma.addLemmas(Lemma);
	}

	@Override
	public int searchLemma(String text) {
		return lemma.searchLemma(text);
	}

	@Override
	public ArrayList<LemmaDTO> getLemmaByRoot(String root) {
		return lemma.getLemmaByRoot(root);
	}

	@Override
	public ArrayList<LemmaDTO> getAllLemmas() {
		return lemma.getAllLemmas();
	}

	@Override
	public boolean addRoots(ArrayList<String> roots) {
		return root.addRoots(roots);
	}

	@Override
	public int searchRoot(String text) {
		return root.searchRoot(text);
	}

	@Override
	public RootDTO getRoot(String text) {
		return root.getRoot(text);
	}

	@Override
	public ArrayList<RootDTO> getAllRoots() {
		return root.getAllRoots();
	}

	

	
	
	
}
