package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.apm.bll.utils.AppLogger;
import com.apm.bll.utils.TextProcessingUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;

public class ChapterBO implements IChapterBO, IObserveable {

    private IDataAccessLayerFasade daf;
    private ISentenceBO sentenceBO;
    private ArrayList<IObserver> observers;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(ChapterBO.class);

    public ChapterBO(IDataAccessLayerFasade daf, ISentenceBO sentenceBO, ArrayList<IObserver> observers) {
        this.daf = daf;
        this.sentenceBO = sentenceBO;
        this.observers = observers;
        logger.info("ChapterBO initialized.");
    }

    @Override
    public boolean createChapter(String bookName, String chapterName) {
        logger.info("Creating chapter: " + chapterName + " in book: " + bookName);
        int bookID = daf.searchBook(bookName);
        if (bookID == -1) {
            logger.warning("Book not found: " + bookName);
            return false;
        }
        boolean check = daf.createChapter(bookID, chapterName);
        logger.info("Chapter creation status for '" + chapterName + "': " + check);
        update();
        return check;
    }

    @Override
    public boolean updateChapter(String bookName, String oldChapterName, String newName) {
        logger.info("Updating chapter: " + oldChapterName + " -> " + newName + " in book: " + bookName);
        int bookID = daf.searchBook(bookName);
        if (bookID == -1) {
            logger.warning("Book not found: " + bookName);
            return false;
        }
        boolean check = daf.updateChaper(bookID, oldChapterName, newName);
        logger.info("Chapter update status for '" + oldChapterName + "': " + check);
        update();
        return check;
    }

    @Override
    public ArrayList<ChapterDTO> retrieveChapters(String bookName) {
        logger.info("Retrieving chapters for book: " + bookName);
        int bookID = daf.searchBook(bookName);
        if (bookID == -1) {
            logger.warning("Book not found: " + bookName);
            return null;
        }
        return daf.retrieveChapters(bookID);
    }

    @Override
    public boolean deleteChapter(String bookName, String chapterName) {
        logger.info("Deleting chapter: " + chapterName + " from book: " + bookName);
        int bookID = daf.searchBook(bookName);
        if (bookID == -1) {
            logger.warning("Book not found: " + bookName);
            return false;
        }
        boolean check = daf.deleteChapter(bookID, chapterName);
        logger.info("Chapter deletion status for '" + chapterName + "': " + check);
        update();
        return check;
    }

    @Override
    public int searchChapter(String chapterName) {
        logger.info("Searching for chapter: " + chapterName);
        return daf.searchChapter(chapterName);
    }

    @Override
    public boolean sentenceExtracter(String bookName, String path) {
        logger.info("Extracting sentences for book: " + bookName + " from file: " + path);

        int bookID = daf.searchBook(bookName);
        if (bookID == -1) {
            logger.severe("Book not found: " + bookName);
            return false;
        }

        String fileData = daf.getData(path);
        if (fileData == null || fileData.isEmpty()) {
            logger.warning("No data found or error reading file at: " + path);
            return false;
        }

        String[] lines = fileData.split("\\r?\\n");
        if (lines.length == 0) {
            logger.warning("Empty file: " + path);
            return false;
        }

        String chapterName = lines[0].trim();
        if (chapterName.isEmpty()) {
            logger.warning("Chapter name missing in first line of file.");
            return false;
        }

        boolean chapterCreated = daf.createChapter(bookID, chapterName);
        update();
        if (!chapterCreated) {
            logger.warning("Chapter may already exist: " + chapterName);
        }

        int chapterID = daf.searchChapter(chapterName);
        if (chapterID == -1) {
            logger.severe("Chapter ID not found for: " + chapterName);
            return false;
        }

        StringBuilder chapterTextBuilder = new StringBuilder();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || line.matches("-+")) continue;
            if (line.startsWith("•") || line.startsWith("*") || line.startsWith("—")) continue;
            chapterTextBuilder.append(line).append(" ");
        }

        String chapterText = chapterTextBuilder.toString().trim();
        if (chapterText.isEmpty()) {
            logger.warning("No valid content found for chapter: " + chapterName);
            return false;
        }

        ArrayList<String> sentences = TextProcessingUtil.ChapterInToSentence(chapterText);
        if (sentences.isEmpty()) {
            logger.warning("No sentences extracted from chapter: " + chapterName);
            return false;
        }

        boolean success = true;
        int count = 0;
        for (String sentence : sentences) {
            boolean inserted = sentenceBO.createSentence(chapterName, sentence, "", "", "");
            logger.info("Inserted sentence #" + count++ + ": " + inserted);
            if (!inserted) {
                logger.warning("Failed to insert sentence: " + sentence);
                success = false;
            }
        }

        logger.info("Sentences extracted and inserted successfully for chapter: " + chapterName);
        return success;
    }

    @Override
    public boolean addObserver(IObserver observer) {
        logger.info("Adding observer: " + observer);
        this.observers.add(observer);
        return true;
    }

    @Override
    public boolean removeObserver() {
        logger.info("Removing all observers for ChapterBO");
        return true;
    }

    @Override
    public void update() {
        logger.info("Updating observers for ChapterBO");
        for (IObserver observer : observers) {
            observer.autoRefresh("Chapter");
        }
    }

    @Override
    public void processChapterSentences(String chapterName, String chapterContent) {
        logger.info("Processing sentences for chapter: " + chapterName);
        ArrayList<String> sentences = TextProcessingUtil.ChapterInToSentence(chapterContent);
        logger.info("Sentences extracted: " + sentences.size());
        if (sentences == null || sentences.isEmpty()) {
            logger.warning("No sentences found for chapter: " + chapterName);
            return;
        }

        for (String sentence : sentences) {
            boolean inserted = sentenceBO.createSentence(chapterName, sentence, "", "", "");
            if (!inserted) {
                logger.warning("Failed to insert sentence: " + sentence);
            }
        }
    }

    @Override
    public ArrayList<IndexRow> getIndexRowsByRootId(String root) {
        logger.info("Fetching index rows by root: " + root);
        int rootId = daf.searchRoot(root);
        if (rootId == -1) {
            logger.warning("Root not found: " + root);
            return null;
        }
        return daf.getIndexRowsByRootId(rootId);
    }

    @Override
    public ArrayList<IndexRow> getIndexRowsByLemmaId(String lemma) {
        logger.info("Fetching index rows by lemma: " + lemma);
        int lemmaId = daf.searchLemma(lemma);
        if (lemmaId == -1) {
            logger.warning("Lemma not found: " + lemma);
            return null;
        }
        return daf.getIndexRowsByLemmaId(lemmaId);
    }

    @Override
    public ArrayList<IndexRow> getIndexRowsByTokenText(String tokenValue) {
        logger.info("Fetching index rows by token: " + tokenValue);
        return daf.getIndexRowsByTokenText(tokenValue);
    }
}
