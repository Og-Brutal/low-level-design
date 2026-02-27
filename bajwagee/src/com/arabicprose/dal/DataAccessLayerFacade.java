package com.arabicprose.dal;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dto.AuthorDTO;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;
import com.arabicprose.dto.AnalysisResultDTO;
import com.arabicprose.dto.TokenizationDTO;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.SegmentationDTO;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.FrequencyDTO;

public class DataAccessLayerFacade implements IDataAccessLayerFacade {
    private final IAuthorDAO authorDAO;
    private final IBookDAO bookDAO;
    private final ISentenceDAO sentenceDAO;
    private final IChapterDAO chapterDAO;
    private final IAnalysisResultDAO analysisResultDAO;
    private final ITokenizationDAO tokenizationDAO;
    private final ILemmatizationDAO lemmatizationDAO;
    private final ISegmentationDAO segmentationDAO;
    private final IRootDAO rootDAO;
    
    public DataAccessLayerFacade(IAuthorDAO authorDAO, IBookDAO bookDAO, ISentenceDAO sentenceDAO,   IChapterDAO chapterDAO, IAnalysisResultDAO analysisResultDAO, ITokenizationDAO tokenizationDAO, ILemmatizationDAO lemmatizationDAO, ISegmentationDAO segmentationDAO, IRootDAO rootDAO) {
        this.authorDAO = authorDAO;
        this.bookDAO = bookDAO;
        this.sentenceDAO = sentenceDAO;
        this.chapterDAO = chapterDAO;
        this.analysisResultDAO = analysisResultDAO;
        this.tokenizationDAO = tokenizationDAO;
        this.lemmatizationDAO = lemmatizationDAO;
        this.segmentationDAO = segmentationDAO;
        this.rootDAO = rootDAO;
    }
    
    // Existing AuthorDAO methods
    @Override
    public void addAuthor(AuthorDTO author) throws SQLException {
        authorDAO.addAuthor(author);
    }
    
    @Override
    public List<AuthorDTO> getAllAuthors() throws SQLException {
        return authorDAO.getAllAuthors();
    }
    
    @Override
    public void updateAuthor(AuthorDTO author) throws SQLException {
        authorDAO.updateAuthor(author);
    }
    
    @Override
    public void deleteAuthor(int authorId) throws SQLException {
        authorDAO.deleteAuthor(authorId);
    }
    
    @Override
    public AuthorDTO getAuthorById(int authorId) throws SQLException {
        return authorDAO.getAuthorById(authorId);
    }
    
    @Override
    public String searchAuthor(int id) throws SQLException {
        return authorDAO.searchAuthor(id);
    }
    
    // Existing BookDAO methods
    @Override
    public void addBook(BookDTO book) throws SQLException {
        bookDAO.addBook(book);
    }
    
    @Override
    public List<BookDTO> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }
    
    @Override
    public void updateBook(BookDTO book) throws SQLException {
        bookDAO.updateBook(book);
    }
    
    @Override
    public void deleteBook(int bookId) throws SQLException {
        bookDAO.deleteBook(bookId);
    }
    
    @Override
    public BookDTO getBookById(int bookId) throws SQLException {
        return bookDAO.getBookById(bookId);
    }
    
    @Override
    public List<BookDTO> searchBooks(String keyword) throws SQLException {
        return bookDAO.searchBooks(keyword);
    }
    
    @Override
    public List<BookDTO> searchBooksByTitle(String title) throws SQLException {
        return bookDAO.searchBooksByTitle(title);
    }
    
    @Override
    public List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException {
        return bookDAO.searchBooksByAuthor(authorName);
    }
    
    // Existing SentenceDAO methods
    @Override
    public void addSentence(SentenceDTO sentence) throws SQLException {
        sentenceDAO.addSentence(sentence);
    }
    
    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        return sentenceDAO.getAllSentences();
    }
    
    @Override
    public void updateSentence(SentenceDTO sentence) throws SQLException {
        sentenceDAO.updateSentence(sentence);
    }
    
    @Override
    public void deleteSentence(int sentenceId) throws SQLException {
        sentenceDAO.deleteSentence(sentenceId);
    }
    
    @Override
    public SentenceDTO getSentenceById(int sentenceId) throws SQLException {
        return sentenceDAO.getSentenceById(sentenceId);
    }
    
    @Override
    public List<SentenceDTO> getSentencesByBookId(int bookId) throws SQLException {
        return sentenceDAO.getSentencesByBookId(bookId);
    }
    
    @Override
    public int getSentenceCountByBookId(int bookId) throws SQLException {
        return sentenceDAO.getSentenceCountByBookId(bookId);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByText(String text) throws SQLException {
        return sentenceDAO.searchSentencesByText(text);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByTranslation(String translation) throws SQLException {
        return sentenceDAO.searchSentencesByTranslation(translation);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesInBook(int bookId, String keyword) throws SQLException {
        return sentenceDAO.searchSentencesInBook(bookId, keyword);
    }
    
    @Override
    public int getNextSentenceNumber(int bookId) throws SQLException {
        return sentenceDAO.getNextSentenceNumber(bookId);
    }
    
    @Override
    public boolean isSentenceNumberExists(int bookId, int sentenceNumber) throws SQLException {
        return sentenceDAO.isSentenceNumberExists(bookId, sentenceNumber);
    }
    
    // Existing ChapterDAO methods
    @Override
    public void addChapter(ChapterDTO chapter) throws SQLException {
        chapterDAO.addChapter(chapter);
    }
    
    @Override
    public List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException {
        return chapterDAO.getChaptersByBookId(bookId);
    }
    
    @Override
    public void updateChapter(ChapterDTO chapter) throws SQLException {
        chapterDAO.updateChapter(chapter);
    }
    
    @Override
    public void deleteChapter(int chapterId) throws SQLException {
        chapterDAO.deleteChapter(chapterId);
    }
    
    @Override
    public ChapterDTO getChapterById(int chapterId) throws SQLException {
        return chapterDAO.getChapterById(chapterId);
    }
    
    @Override
    public List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException {
        return chapterDAO.searchChaptersByName(chapterName);
    }
    
    @Override
    public int getNextChapterOrder(int bookId) throws SQLException {
        return chapterDAO.getNextChapterOrder(bookId);
    }
    
    @Override
    public boolean isChapterNameExists(int bookId, String chapterName) throws SQLException {
        return chapterDAO.isChapterNameExists(bookId, chapterName);
    }
    
    // Morphological Analysis Methods - AnalysisResultDAO
    @Override
    public void addAnalysisResult(AnalysisResultDTO analysis) throws SQLException {
        analysisResultDAO.addAnalysisResult(analysis);
    }
    
    @Override
    public AnalysisResultDTO getAnalysisResultById(int analysisId) throws SQLException {
        return analysisResultDAO.getAnalysisResultById(analysisId);
    }
    
    @Override
    public List<AnalysisResultDTO> getAnalysisResultsBySentenceId(int sentenceId) throws SQLException {
        return analysisResultDAO.getAnalysisResultsBySentenceId(sentenceId);
    }
    
    @Override
    public void deleteAnalysisResult(int analysisId) throws SQLException {
        analysisResultDAO.deleteAnalysisResult(analysisId);
    }
    
    @Override
    public boolean analysisExistsForSentence(int sentenceId) throws SQLException {
        return analysisResultDAO.analysisExistsForSentence(sentenceId);
    }
    
    // Morphological Analysis Methods - TokenizationDAO
    @Override
    public void addToken(TokenizationDTO token) throws SQLException {
        tokenizationDAO.addToken(token);
    }
    
    @Override
    public List<TokenizationDTO> getTokensByAnalysisId(int analysisId) throws SQLException {
        return tokenizationDAO.getTokensByAnalysisId(analysisId);
    }
    
    @Override
    public void deleteTokensByAnalysisId(int analysisId) throws SQLException {
        tokenizationDAO.deleteTokensByAnalysisId(analysisId);
    }
    
    // ADD THESE BROWSING METHODS FOR TokenizationDAO:
    @Override
    public List<TokenizationDTO> getAllTokens() throws SQLException {
        return tokenizationDAO.getAllTokens();
    }
    
    @Override
    public List<TokenizationDTO> getTokensByValue(String token) throws SQLException {
        return tokenizationDAO.getTokensByValue(token);
    }
    
    // Morphological Analysis Methods - LemmatizationDAO
    @Override
    public void addLemma(LemmatizationDTO lemma) throws SQLException {
        lemmatizationDAO.addLemma(lemma);
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByTokenId(int tokenId) throws SQLException {
        return lemmatizationDAO.getLemmasByTokenId(tokenId);
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByAnalysisId(int analysisId) throws SQLException {
        return lemmatizationDAO.getLemmasByAnalysisId(analysisId);
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByValue(String lemma) throws SQLException {
        return lemmatizationDAO.getLemmasByValue(lemma);
    }
    
    @Override
    public void deleteLemmasByTokenId(int tokenId) throws SQLException {
        lemmatizationDAO.deleteLemmasByTokenId(tokenId);
    }
    
    // ADD THESE BROWSING METHODS FOR LemmatizationDAO:
    @Override
    public List<LemmatizationDTO> getAllLemmas() throws SQLException {
        return lemmatizationDAO.getAllLemmas();
    }
    
    // Morphological Analysis Methods - SegmentationDAO
    @Override
    public void addSegmentation(SegmentationDTO segmentation) throws SQLException {
        segmentationDAO.addSegmentation(segmentation);
    }
    
    @Override
    public SegmentationDTO getSegmentationByTokenId(int tokenId) throws SQLException {
        return segmentationDAO.getSegmentationByTokenId(tokenId);
    }
    
    @Override
    public void deleteSegmentationByTokenId(int tokenId) throws SQLException {
        segmentationDAO.deleteSegmentationByTokenId(tokenId);
    }
    
    // ADD THESE BROWSING METHODS FOR SegmentationDAO:
    @Override
    public List<SegmentationDTO> getAllSegmentations() throws SQLException {
        return segmentationDAO.getAllSegmentations();
    }
    
    // Morphological Analysis Methods - RootDAO
    @Override
    public void addRoot(RootDTO root) throws SQLException {
        rootDAO.addRoot(root);
    }
    
    @Override
    public List<RootDTO> getRootsByTokenId(int tokenId) throws SQLException {
        return rootDAO.getRootsByTokenId(tokenId);
    }
    
    @Override
    public List<RootDTO> getRootsByAnalysisId(int analysisId) throws SQLException {
        return rootDAO.getRootsByAnalysisId(analysisId);
    }
    
    @Override
    public void deleteRootsByTokenId(int tokenId) throws SQLException {
        rootDAO.deleteRootsByTokenId(tokenId);
    }
    
    // ADD THESE BROWSING METHODS FOR RootDAO:
    @Override
    public List<RootDTO> getAllRoots() throws SQLException {
        return rootDAO.getAllRoots();
    }
    
    @Override
    public List<RootDTO> getRootsByValue(String root) throws SQLException {
        return rootDAO.getRootsByValue(root);
    }
    
    // ADD THIS SENTENCE METHOD FOR AUTO-ANALYSIS:
    @Override
    public SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException {
        return sentenceDAO.getSentenceByTextAndBook(text, bookId);
    }

    @Override
    public void addBatchSentences(List<SentenceDTO> sentences) throws SQLException {
        sentenceDAO.addBatchSentences(sentences);
    }

    @Override
    public List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException {
        return sentenceDAO.getSentencesByChapterId(chapterId);
    }

    @Override
    public TokenizationDTO getTokenById(int tokenId) throws SQLException {
        return tokenizationDAO.getTokenById(tokenId);
    }
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    @Override
    public List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException {
        return tokenizationDAO.getTokenFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException {
        return lemmatizationDAO.getLemmaFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException {
        return rootDAO.getRootFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException {
        return tokenizationDAO.getTokenFrequenciesByChapter(chapterId);
    }
    
    @Override
    public List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException {
        return lemmatizationDAO.getLemmaFrequenciesByChapter(chapterId);
    }
    
    @Override
    public List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException {
        return rootDAO.getRootFrequenciesByChapter(chapterId);
    }

	@Override
	public List<LemmatizationDTO> getLemmasByRootId(int rootId) throws SQLException {
		return lemmatizationDAO.getLemmasByRootId(rootId);
	}

	@Override
	public List<LemmatizationDTO> getLemmasByRootValue(String rootValue) throws SQLException {
		return lemmatizationDAO.getLemmasByRootValue(rootValue);
	}

	@Override
	public RootDTO getRootByValue(String rootValue) throws SQLException {
		return rootDAO.getRootByValue(rootValue);
	}
}