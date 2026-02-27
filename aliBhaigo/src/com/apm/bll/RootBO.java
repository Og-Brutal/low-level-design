package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.apm.bll.utils.RootUtil;
import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.RootDTO;
import com.apm.bll.utils.AppLogger;

public class RootBO implements IRootBO {

    private IDataAccessLayerFasade daf;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(RootBO.class);

    public RootBO(IDataAccessLayerFasade daf) {
        this.daf = daf;
        logger.info("RootBO initialized.");
    }

    @Override
    public boolean addRoots(ArrayList<String> lemmas) {
        logger.info("Adding roots for lemmas: " + lemmas);
        boolean inserted = true;

        ArrayList<String> roots = RootUtil.getRootList(lemmas);
        logger.info("Extracted roots: " + roots);

        for (String root : roots) {
            daf.addRoots(root);
            logger.info("Added root: " + root);
        }

        logger.info("All roots added successfully.");
        return inserted;
    }

    @Override
    public int searchRoot(String text) {
        logger.info("Searching for root: " + text);
        int id = daf.searchRoot(text);
        if (id == -1) {
            logger.warning("Root not found: " + text);
        } else {
            logger.info("Found root '" + text + "' with ID: " + id);
        }
        return id;
    }

    @Override
    public RootDTO getRoot(String text) {
        logger.info("Fetching root details for: " + text);
        return daf.getRoot(text);
    }

    @Override
    public ArrayList<RootDTO> getAllRoots() {
        logger.info("Fetching all roots.");
        ArrayList<RootDTO> roots = daf.getAllRoots();
        logger.info("Total roots found: " + (roots != null ? roots.size() : 0));
        return roots;
    }

    @Override
    public ArrayList<String> getAllRootsByBook(String bookName) {
        logger.info("Fetching all roots for book: " + bookName);
        int bookId = daf.searchBook(bookName);
        if (bookId == -1) {
            logger.warning("Book not found: " + bookName);
            return null;
        }
        ArrayList<String> rootsByBook = daf.getAllRootsByBook(bookId);
        logger.info("Found " + (rootsByBook != null ? rootsByBook.size() : 0) + " roots for book: " + bookName);
        return rootsByBook;
    }
}
