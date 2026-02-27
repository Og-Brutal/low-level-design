package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.apm.bll.utils.LemmaUtil;
import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.LemmaDTO;
import com.apm.bll.utils.AppLogger;

public class LemmaBO implements ILemmaBO {

    private IDataAccessLayerFasade daf;
    private RootBO rootBO;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(LemmaBO.class);

    public LemmaBO(IDataAccessLayerFasade daf, RootBO rootBO) {
        this.daf = daf;
        this.rootBO = rootBO;
        logger.info("LemmaBO initialized.");
    }

    @Override
    public boolean addLemmas(ArrayList<String> tokens) {
        logger.info("Adding lemmas for tokens: " + tokens);
        boolean inserted = false;

        ArrayList<String> lemmas = LemmaUtil.getLemmaList(tokens);
        logger.info("Extracted lemmas: " + lemmas);

        if (rootBO.addRoots(lemmas)) {
            inserted = true;
            for (String lemma : lemmas) {
                String root = RootUtil.getThreeLetterRoot(lemma);
                int rootID = daf.searchRoot(root);
                if (rootID != -1) {
                    daf.addLemmas(rootID, lemma);
                    logger.info("Added lemma '" + lemma + "' under root ID " + rootID);
                } else {
                    logger.warning("Root not found for lemma: " + lemma);
                }
            }
        } else {
            logger.warning("Failed to add roots for lemmas: " + lemmas);
        }

        logger.info("Lemma addition status: " + inserted);
        return inserted;
    }

    @Override
    public int searchLemma(String text) {
        logger.info("Searching for lemma: " + text);
        int id = daf.searchLemma(text);
        if (id == -1) {
            logger.warning("Lemma not found: " + text);
        } else {
            logger.info("Found lemma '" + text + "' with ID: " + id);
        }
        return id;
    }

    @Override
    public ArrayList<LemmaDTO> getLemmaByRoot(String root) {
        logger.info("Fetching lemmas by root: " + root);
        ArrayList<LemmaDTO> lemmaList = null;
        int rootID = daf.searchRoot(root);
        if (rootID != -1) {
            lemmaList = daf.getLemmaByRoot(rootID);
            logger.info("Found " + lemmaList.size() + " lemmas for root: " + root);
        } else {
            logger.warning("Root not found: " + root);
        }
        return lemmaList;
    }

    @Override
    public ArrayList<LemmaDTO> getAllLemmas() {
        logger.info("Fetching all lemmas.");
        ArrayList<LemmaDTO> allLemmas = daf.getAllLemmas();
        logger.info("Total lemmas found: " + (allLemmas != null ? allLemmas.size() : 0));
        return allLemmas;
    }

    @Override
    public ArrayList<String> getAllLemmasByBook(String bookName) {
        logger.info("Fetching all lemmas for book: " + bookName);
        int bookId = daf.searchBook(bookName);
        if (bookId == -1) {
            logger.warning("Book not found: " + bookName);
            return null;
        }
        ArrayList<String> lemmasByBook = daf.getAllLemmasByBook(bookId);
        logger.info("Found " + (lemmasByBook != null ? lemmasByBook.size() : 0) + " lemmas for book: " + bookName);
        return lemmasByBook;
    }
}
