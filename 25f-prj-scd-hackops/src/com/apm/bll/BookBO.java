package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.BookDTO;
import com.apm.observers.IObserveable;
import com.apm.observers.IObserver;
import com.apm.bll.utils.AppLogger;

public class BookBO implements IBookBO, IObserveable {

    private IDataAccessLayerFasade daf;
    private IChapterBO chapterBO;
    private ArrayList<IObserver> observers;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(BookBO.class);

    public BookBO(IDataAccessLayerFasade daf, IChapterBO chapterBO, ArrayList<IObserver> observers) {
        this.daf = daf;
        this.chapterBO = chapterBO;
        this.observers = observers;
        logger.info("BookBO initialized.");
    }

    @Override
    public boolean createBook(String title, String authorName, String era) {
        logger.info("Creating book: " + title + " by " + authorName);
        int authorId = daf.searchAuthor(authorName);
        if (authorId == -1) {
            logger.warning("Author not found: " + authorName);
            return false;
        }

        if (daf.searchBook(title) != -1) {
            logger.warning("Book already exists: " + title);
            return false;
        }

        boolean created = daf.createBook(title, authorId, era);
        logger.info("Book creation status for '" + title + "': " + created);
        return created;
    }

    @Override
    public BookDTO retrieveBook(String title) {
        logger.info("Retrieving book: " + title);
        return daf.retrieveBook(title);
    }

    @Override
    public boolean updateBook(String oldBookName, String title, String authorName, String era) {
        logger.info("Updating book: " + oldBookName + " -> " + title + " by " + authorName);
        int authorId = daf.searchAuthor(authorName);
        if (authorId == -1) {
            logger.warning("Author not found: " + authorName);
            return false;
        }
        boolean updated = daf.updateBook(oldBookName, title, authorId, era);
        logger.info("Book update status for '" + oldBookName + "': " + updated);
        return updated;
    }

    @Override
    public boolean deleteBook(String title) {
        logger.info("Deleting book: " + title);
        boolean deleted = daf.deleteBook(title);
        logger.info("Book deletion status for '" + title + "': " + deleted);
        return deleted;
    }

    @Override
    public int searchBook(String title) {
        logger.info("Searching for book: " + title);
        return daf.searchBook(title);
    }

    @Override
    public ArrayList<BookDTO> getAllBooks() {
        logger.info("Fetching all books.");
        return daf.getAllBooks();
    }

    @Override
    public String getBookById(int id) {
        logger.info("Fetching book by ID: " + id);
        return daf.getBookById(id);
    }

    @Override
    public boolean chapterSeparater(String path) {
        logger.info("Starting chapter separation for file: " + path);
        try {
            // Extract book name from file path
            String[] pathParts = path.replace("\\", "/").split("/");
            String fileName = pathParts[pathParts.length - 1];
            String bookName = fileName.contains(".")
                    ? fileName.substring(0, fileName.lastIndexOf('.'))
                    : fileName;

            logger.info("Detected Book Name: " + bookName);

            // Read file content via DAL
            String bookData = daf.getData(path);
            if (bookData == null || bookData.isEmpty()) {
                logger.warning("No data found in file or unable to read: " + path);
                return false;
            }

            // Create book entry
            boolean bookCreated = daf.createBook(bookName, -1, null);
            if (!bookCreated) {
                logger.warning("Book may already exist: " + bookName);
            }

            // Split file content into chapters
            String[] rawChapters = bookData.split("##CHAPTER##");
            if (rawChapters.length == 0) {
                logger.warning("No chapters found using ##CHAPTER## separator.");
                return false;
            }

            // Process each chapter
            for (String rawChapter : rawChapters) {
                rawChapter = rawChapter.trim();
                if (rawChapter.isEmpty()) continue;

                String[] lines = rawChapter.split("\\r?\\n", 2);
                String chapterName = lines[0].trim();

                if (chapterName.isEmpty()) {
                    logger.warning("Chapter without a name found, skipping...");
                    continue;
                }

                // Create chapter
                boolean chapterCreated = chapterBO.createChapter(bookName, chapterName);
                if (!chapterCreated) {
                    logger.warning("Chapter may already exist: " + chapterName);
                }

                String chapterContent = (lines.length > 1) ? lines[1].trim() : "";
                if (chapterContent.isEmpty()) {
                    logger.warning("Empty content for chapter: " + chapterName);
                    continue;
                }

                // Process sentences
                chapterBO.processChapterSentences(chapterName, chapterContent);
                logger.info("Chapter processed successfully: " + chapterName);
            }

            logger.info("All chapters processed successfully for book: " + bookName);
            return true;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in chapterSeparater", e);
            return false;
        }
    }

    @Override
    public boolean addObserver(IObserver observer) {
        logger.info("Adding observer: " + observer);
        this.observers.add(observer);
        return true;
    }

    @Override
    public boolean removeObserver() {
        logger.info("Removing all observers");
        this.observers.clear();
        return true;
    }

    @Override
    public void update() {
        logger.info("Updating observers for BookBO");
        for (IObserver observer : observers) {
            observer.autoRefresh("Book");
        }
    }
}
