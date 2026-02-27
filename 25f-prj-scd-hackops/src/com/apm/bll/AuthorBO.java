package com.apm.bll;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.apm.dal.IDataAccessLayerFasade;
import com.apm.dto.AuthorDTO;
import com.apm.bll.utils.AppLogger;

public class AuthorBO implements IAuthorBO {

    private IDataAccessLayerFasade daf;

    // ✅ Logger for this class
    private static final Logger logger = AppLogger.getLogger(AuthorBO.class);

    public AuthorBO(IDataAccessLayerFasade daf) {
        this.daf = daf;
        logger.info("AuthorBO initialized.");
    }

    @Override
    public boolean createAuthor(String name, String biography) {
        logger.info("Creating author: " + name);
        if (daf.searchAuthor(name) != -1) {
            logger.warning("Author already exists: " + name);
            return false;
        }
        boolean created = daf.createAuthor(name, biography);
        logger.info("Author creation status for '" + name + "': " + created);
        return created;
    }

    @Override
    public AuthorDTO retrieveAuthor(String name) {
        logger.info("Retrieving author: " + name);
        return daf.retrieveAuthor(name);
    }

    @Override
    public boolean updateAuthor(String oldAuthorName, String name, String biography) {
        logger.info("Updating author: " + oldAuthorName + " -> " + name);
        boolean updated = daf.updateAuthor(oldAuthorName, name, biography);
        logger.info("Author update status for '" + oldAuthorName + "': " + updated);
        return updated;
    }

    @Override
    public boolean deleteAuthor(String name) {
        logger.info("Deleting author: " + name);
        boolean deleted = daf.deleteAuthor(name);
        logger.info("Author deletion status for '" + name + "': " + deleted);
        return deleted;
    }

    @Override
    public int searchAuthor(String name) {
        logger.info("Searching for author: " + name);
        return daf.searchAuthor(name);
    }

    @Override
    public ArrayList<AuthorDTO> getAllAuthors() {
        logger.info("Fetching all authors.");
        return daf.getAllAuthors();
    }

    @Override
    public String getAuthorById(int id) {
        logger.info("Fetching author by ID: " + id);
        return daf.getAuthorById(id);
    }
}
