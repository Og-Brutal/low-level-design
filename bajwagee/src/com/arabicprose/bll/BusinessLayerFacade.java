package com.arabicprose.bll;

import java.sql.SQLException;
import java.util.List;

import com.arabicprose.dto.AnalysisResultDTO;
import com.arabicprose.dto.AuthorDTO;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.FrequencyDTO;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.SearchDTO;
import com.arabicprose.dto.SegmentationDTO;
import com.arabicprose.dto.SentenceDTO;
import com.arabicprose.dto.TokenizationDTO;

public class BusinessLayerFacade implements IBusinessLayerFacade {
    private final IAuthorBO authorBO;
    private final IBookBO bookBO;
    private final ISentenceBO sentenceBO;
    private final IChapterBO chapterBO;
    private final IMorphologicalAnalysisBO morphologicalAnalysisBO;
    private final ISearchBO searchBO;
    
    public BusinessLayerFacade(IAuthorBO authorBO, IBookBO bookBO, ISentenceBO sentenceBO,  IChapterBO chapterBO, IMorphologicalAnalysisBO morphologicalAnalysisBO) {
        this.authorBO = authorBO;
        this.bookBO = bookBO;
        this.sentenceBO = sentenceBO;
        this.chapterBO = chapterBO;
        this.morphologicalAnalysisBO = morphologicalAnalysisBO;
        this.searchBO = new SearchBO(this);
    }
    
    // AuthorBO methods
    @Override
    public void addAuthor(AuthorDTO authorDTO) throws SQLException {
        authorBO.addAuthor(authorDTO);
    }
    
    @Override
    public List<AuthorDTO> getAllAuthors() throws SQLException {
        return authorBO.getAllAuthors();
    }
    
    @Override
    public void updateAuthor(AuthorDTO authorDTO) throws SQLException {
        authorBO.updateAuthor(authorDTO);
    }
    
    @Override
    public void deleteAuthor(int authorId) throws SQLException {
        authorBO.deleteAuthor(authorId);
    }
    
    @Override
    public AuthorDTO getAuthorById(int authorId) throws SQLException {
        return authorBO.getAuthorById(authorId);
    }
    
    @Override
    public String searchAuthor(int id) throws SQLException {
        return authorBO.searchAuthor(id);
    }
    
    // BookBO methods
    @Override
    public void addBook(BookDTO bookDTO) throws SQLException {
        bookBO.addBook(bookDTO);
    }
    
    @Override
    public List<BookDTO> getAllBooks() throws SQLException {
        return bookBO.getAllBooks();
    }
    
    @Override
    public void updateBook(BookDTO bookDTO) throws SQLException {
        bookBO.updateBook(bookDTO);
    }
    
    @Override
    public void deleteBook(int bookId) throws SQLException {
        bookBO.deleteBook(bookId);
    }
    
    @Override
    public BookDTO getBookById(int bookId) throws SQLException {
        return bookBO.getBookById(bookId);
    }
    
    @Override
    public List<BookDTO> searchBooks(String keyword) throws SQLException {
        return bookBO.searchBooks(keyword);
    }
    
    @Override
    public List<BookDTO> searchBooksByTitle(String title) throws SQLException {
        return bookBO.searchBooksByTitle(title);
    }
    
    @Override
    public List<BookDTO> searchBooksByAuthor(String authorName) throws SQLException {
        return bookBO.searchBooksByAuthor(authorName);
    }
    
    // SentenceBO methods
    @Override
    public void addSentence(SentenceDTO sentenceDTO) throws SQLException {
        sentenceBO.addSentence(sentenceDTO);
    }
    
    @Override
    public List<SentenceDTO> getAllSentences() throws SQLException {
        return sentenceBO.getAllSentences();
    }
    
    @Override
    public void updateSentence(SentenceDTO sentenceDTO) throws SQLException {
        sentenceBO.updateSentence(sentenceDTO);
    }
    
    @Override
    public void deleteSentence(int sentenceId) throws SQLException {
        sentenceBO.deleteSentence(sentenceId);
    }
    
    @Override
    public SentenceDTO getSentenceById(int sentenceId) throws SQLException {
        return sentenceBO.getSentenceById(sentenceId);
    }
    
    @Override
    public List<SentenceDTO> getSentencesByBookId(int bookId) throws SQLException {
        return sentenceBO.getSentencesByBookId(bookId);
    }
    
    @Override
    public List<SentenceDTO> getSentencesByChapterId(int chapterId) throws SQLException {
        return sentenceBO.getSentencesByChapterId(chapterId);
    }
    
    @Override
    public int getSentenceCountByBookId(int bookId) throws SQLException {
        return sentenceBO.getSentenceCountByBookId(bookId);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByText(String text) throws SQLException {
        return sentenceBO.searchSentencesByText(text);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesByTranslation(String translation) throws SQLException {
        return sentenceBO.searchSentencesByTranslation(translation);
    }
    
    @Override
    public List<SentenceDTO> searchSentencesInBook(int bookId, String keyword) throws SQLException {
        return sentenceBO.searchSentencesInBook(bookId, keyword);
    }
    
    @Override
    public int getNextSentenceNumber(int bookId) throws SQLException {
        return sentenceBO.getNextSentenceNumber(bookId);
    }
    
    @Override
    public boolean isSentenceNumberExists(int bookId, int sentenceNumber) throws SQLException {
        return sentenceBO.isSentenceNumberExists(bookId, sentenceNumber);
    }
    
    // ==================== N-GRAM SIMILARITY SEARCH METHODS ====================
    
    @Override
    public List<SentenceBO.SimilarSentenceResult> findSimilarSentences(String inputSentence, int nGramSize, double similarityThreshold) throws SQLException {
        return sentenceBO.findSimilarSentences(inputSentence, nGramSize, similarityThreshold);
    }
    
    @Override
    public List<SentenceBO.SimilarSentenceResult> findSimilarSentences(String inputSentence) throws SQLException {
        return sentenceBO.findSimilarSentences(inputSentence);
    }
    
    // ChapterBO methods
    @Override
    public void addChapter(ChapterDTO chapterDTO) throws SQLException {
        chapterBO.addChapter(chapterDTO);
    }
    
    @Override
    public List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException {
        return chapterBO.getChaptersByBookId(bookId);
    }
    
    @Override
    public void updateChapter(ChapterDTO chapterDTO) throws SQLException {
        chapterBO.updateChapter(chapterDTO);
    }
    
    @Override
    public void deleteChapter(int chapterId) throws SQLException {
        chapterBO.deleteChapter(chapterId);
    }
    
    @Override
    public ChapterDTO getChapterById(int chapterId) throws SQLException {
        return chapterBO.getChapterById(chapterId);
    }
    
    @Override
    public List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException {
        return chapterBO.searchChaptersByName(chapterName);
    }
    
    @Override
    public int getNextChapterOrder(int bookId) throws SQLException {
        return chapterBO.getNextChapterOrder(bookId);
    }
    
    @Override
    public boolean sentenceSplitter(String path, int bookID) {
        return chapterBO.sentenceSplitter(path, bookID);
    }
    
    // MorphologicalAnalysisBO methods
    @Override
    public AnalysisResultDTO analyzeSentence(int sentenceId, String text) throws SQLException {
        return morphologicalAnalysisBO.analyzeSentence(sentenceId, text);
    }
    
    @Override
    public AnalysisResultDTO analyzeTextAutomatically(int sentenceId, String text) throws SQLException {
        return morphologicalAnalysisBO.analyzeTextAutomatically(sentenceId, text);
    }
    
    @Override
    public List<TokenizationDTO> getAllTokens() throws SQLException {
        return morphologicalAnalysisBO.getAllTokens();
    }
    
    @Override
    public List<LemmatizationDTO> getAllLemmas() throws SQLException {
        return morphologicalAnalysisBO.getAllLemmas();
    }
    
    @Override
    public List<RootDTO> getAllRoots() throws SQLException {
        return morphologicalAnalysisBO.getAllRoots();
    }
    
    @Override
    public List<SegmentationDTO> getAllSegmentations() throws SQLException {
        return morphologicalAnalysisBO.getAllSegmentations();
    }
    
    @Override
    public List<AnalysisResultDTO> getAnalysisHistory(int sentenceId) throws SQLException {
        return morphologicalAnalysisBO.getAnalysisHistory(sentenceId);
    }
    
    @Override
    public void deleteAnalysis(int analysisId) throws SQLException {
        morphologicalAnalysisBO.deleteAnalysis(analysisId);
    }
    
    @Override
    public List<LemmatizationDTO> getLemmasByValue(String lemma) throws SQLException {
        return morphologicalAnalysisBO.getLemmasByValue(lemma);
    }
    
    @Override
    public List<RootDTO> getRootsByValue(String root) throws SQLException {
        return morphologicalAnalysisBO.getRootsByValue(root);
    }
    
    @Override
    public List<TokenizationDTO> getTokensByValue(String token) throws SQLException {
        return morphologicalAnalysisBO.getTokensByValue(token);
    }
    
    @Override
    public SentenceDTO getSentenceByTextAndBook(String text, int bookId) throws SQLException {
        return sentenceBO.getSentenceByTextAndBook(text, bookId);
    }
    
    @Override
    public List<TokenizationDTO> getAllTokensByAnalysis(int analysisId) throws SQLException {
        return morphologicalAnalysisBO.getAllTokensByAnalysis(analysisId);
    }
    
    @Override
    public AnalysisResultDTO analyzeSingleSentence(int sentenceId, String text) throws SQLException {
        return morphologicalAnalysisBO.analyzeSingleSentence(sentenceId, text);
    }
    
    @Override
    public boolean hasAnalysis(int sentenceId) throws SQLException {
        return morphologicalAnalysisBO.hasAnalysis(sentenceId);
    }
    
    @Override
    public List<String> getRootsFromLemma(String lemma) {
        return morphologicalAnalysisBO.getRootsFromLemma(lemma);
    }
    
    @Override
    public String analyzeWordSegmentation(String word) {
        return morphologicalAnalysisBO.analyzeWordSegmentation(word);
    }
    
    // ==================== FREQUENCY ANALYSIS METHODS ====================
    
    @Override
    public List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException {
        return morphologicalAnalysisBO.getTokenFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException {
        return morphologicalAnalysisBO.getLemmaFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException {
        return morphologicalAnalysisBO.getRootFrequencies(bookId);
    }
    
    @Override
    public List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException {
        return morphologicalAnalysisBO.getTokenFrequenciesByChapter(chapterId);
    }
    
    @Override
    public List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException {
        return morphologicalAnalysisBO.getLemmaFrequenciesByChapter(chapterId);
    }
    
    @Override
    public List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException {
        return morphologicalAnalysisBO.getRootFrequenciesByChapter(chapterId);
    }
    
    @Override
    public List<FrequencyDTO> getTopTokens(int bookId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopTokens(bookId, limit);
    }
    
    @Override
    public List<FrequencyDTO> getTopLemmas(int bookId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopLemmas(bookId, limit);
    }
    
    @Override
    public List<FrequencyDTO> getTopRoots(int bookId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopRoots(bookId, limit);
    }
    
    @Override
    public List<FrequencyDTO> getTopTokensByChapter(int chapterId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopTokensByChapter(chapterId, limit);
    }
    
    @Override
    public List<FrequencyDTO> getTopLemmasByChapter(int chapterId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopLemmasByChapter(chapterId, limit);
    }
    
    @Override
    public List<FrequencyDTO> getTopRootsByChapter(int chapterId, int limit) throws SQLException {
        return morphologicalAnalysisBO.getTopRootsByChapter(chapterId, limit);
    }
    
    @Override
    public Object[] getBookStatistics(int bookId) throws SQLException {
        return morphologicalAnalysisBO.getBookStatistics(bookId);
    }
    
    @Override
    public Object[] getChapterStatistics(int chapterId) throws SQLException {
        return morphologicalAnalysisBO.getChapterStatistics(chapterId);
    }
    @Override
    public List<SentenceDTO> performTextSearch(SearchDTO searchDTO) throws SQLException {
        return searchBO.performTextSearch(searchDTO);
    }

    @Override
    public List<SentenceBO.SimilarSentenceResult> performSimilaritySearch(SearchDTO searchDTO) throws SQLException {
        return searchBO.performSimilaritySearch(searchDTO);
    }

	@Override
	public List<LemmatizationDTO> getLemmasByRootId(int rootId) throws SQLException {
		
		return morphologicalAnalysisBO.getLemmasByRootId(rootId);
	}

	@Override
	public List<LemmatizationDTO> getLemmasByRootValue(String rootValue) throws SQLException {
		return morphologicalAnalysisBO.getLemmasByRootValue(rootValue) ;
	}

	@Override
	public List<LemmatizationDTO> getLemmatizationsByAnalysis(int analysisId) throws SQLException {
		return morphologicalAnalysisBO.getLemmatizationsByAnalysis(analysisId);
	}

	@Override
	public List<RootDTO> getRootsByAnalysis(int analysisId) throws SQLException {
		return morphologicalAnalysisBO.getRootsByAnalysis(analysisId);
	}

	@Override
	public RootDTO getRootByValue(String rootValue) throws SQLException {
		return morphologicalAnalysisBO.getRootByValue(rootValue);
	}
    
}