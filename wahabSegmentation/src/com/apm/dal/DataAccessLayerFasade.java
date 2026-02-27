package com.apm.dal;

import java.util.ArrayList;

import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.SentenceDTO;
import com.apm.dto.TokenDTO;

public class DataAccessLayerFasade implements IDataAccessLayerFasade {


    private IBookDAO book;
    private IAuthorDAO author;
    private ISentenceDAO sentence;
    private IChapterDAO chapter;
    private ITextExtracter textExtracter;
    private ITokenDAO token;
    private ILemmaDAO lemma;   
    private IRootDAO root;     
    private ISegmentedTokenDAO segmentedTokenDAO; 
    // UPDATED constructor to include Lemma and Root
    public DataAccessLayerFasade(IBookDAO book, IAuthorDAO author, ISentenceDAO sentence,
                                 IChapterDAO chapter, ITextExtracter textExtracter,
                                 ITokenDAO token, ILemmaDAO lemma, IRootDAO root,ISegmentedTokenDAO segmentedTokenDAO) {
        this.book = book;
        this.author = author;
        this.sentence = sentence;
        this.chapter = chapter;
        this.textExtracter = textExtracter;
        this.token = token;
        this.lemma = lemma;
        this.root = root;
        this.segmentedTokenDAO=segmentedTokenDAO;
    }

    // -------------------- BOOK CRUD --------------------
    @Override
    public boolean createBook(String title, int authorId, String era) {
        return book.createBook(title, authorId, era);
    }

    @Override
    public BookDTO retrieveBook(String title) {
        return book.retrieveBook(title);
    }

    @Override
    public boolean updateBook(String oldBookName, String title, int authorId, String era) {
        return book.updateBook(oldBookName, title, authorId, era);
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


    // -------------------- AUTHOR CRUD --------------------
    @Override
    public boolean createAuthor(String name, String biography) {
        return author.createAuthor(name, biography);
    }

    @Override
    public AuthorDTO retrieveAuthor(String name) {
        return author.retrieveAuthor(name);
    }

    @Override
    public boolean updateAuthor(String oldAuthorName, String name, String biography) {
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


    // -------------------- SENTENCE CRUD --------------------
    @Override
    public boolean createSentence(int bookId, String text, String textDiacritized,
                                  String translation, String notes) {
        return sentence.createSentence(bookId, text, textDiacritized, translation, notes);
    }

    @Override
    public ArrayList<SentenceDTO> retrieveSentence(int bookID) {
        return sentence.retrieveSentence(bookID);
    }

    @Override
    public boolean updateSenetence(int bookId, int sentenceNumber, String text,
                                   String textDiacritized, String translation, String notes) {
        return sentence.updateSenetence(bookId, sentenceNumber, text, textDiacritized, translation, notes);
    }

    @Override
    public boolean deleteSentence(int bookId, int sentenceNumber) {
        return sentence.deleteSentence(bookId, sentenceNumber);
    }

    @Override
    public int searchSentence(String text) {
        return sentence.searchSentence(text);
    }


    // -------------------- CHAPTER CRUD --------------------
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


    // -------------------- DATA LOADING --------------------
    @Override
    public String getData(String path) {
        return textExtracter.getData(path);
    }

	@Override
	public int addToken(int sentenceID, int lemmaID, String text) {
		return token.addToken(sentenceID, lemmaID, text);
	}

	@Override
	public ArrayList<TokenDTO> getTokensByLemma(int lemmaID) {
		return token.getTokensByLemma(lemmaID);
	}

	@Override
	public ArrayList<TokenDTO> getAllTokens() {
		return token.getAllTokens();
	}

	@Override
	public boolean addLemmas(int rootID, String text) {
		return lemma.addLemmas(rootID, text);
	}

	@Override
	public int searchLemma(String text) {
		return lemma.searchLemma(text);
	}

	@Override
	public ArrayList<LemmaDTO> getLemmaByRoot(int rootID) {
		return lemma.getLemmaByRoot(rootID);
	}

	@Override
	public ArrayList<LemmaDTO> getAllLemmas() {
		return lemma.getAllLemmas();
	}

	@Override
	public boolean addRoots(String text) {
		return root.addRoots(text);
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

	@Override
	public ArrayList<Integer> deleteTokensBySentence(int sentenceID) {
		return token.deleteTokensBySentence(sentenceID);
	}

	@Override
	public SentenceDTO sentenceByNumbers(int chapterNumber, int sentenceNumber) {
		return sentence.sentenceByNumbers(chapterNumber, sentenceNumber);
	}

	@Override
	public boolean addSegments(int tokenID, String prefix, String stem, String lemma, String root) {
		return segmentedTokenDAO.addSegments(tokenID, prefix, stem, lemma, root);
	}

	@Override
	public ArrayList<SegmentedTokenDTO> getAllSegments() {
		return segmentedTokenDAO.getAllSegments();
	}

	@Override
	public boolean deleteSegments(int tokenID) {
		return segmentedTokenDAO.deleteSegments(tokenID);
	}

	@Override
	public String getTokenText(int tokenID) {
				return token.getTokenText(tokenID);
	}

	@Override
	public ArrayList<String> getSentencesByToken(String token) {
		return sentence.getSentencesByToken(token);
	}

	@Override
	public ArrayList<String> getSentencesByTokenPattern(String tokenPattern) {
		return sentence.getSentencesByTokenPattern(tokenPattern);
	}





    
}
