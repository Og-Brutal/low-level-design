package com.apm.dal.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

public class LogFileDAO {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PATH = "logs/application.log";

    /**
     * Prepares the file system and returns a configured FileHandler.
     * This moves the "Physical File Access" logic to the DAL.
     * * @return FileHandler connected to the physical file.
     * @throws IOException If file creation fails.
     */
    public FileHandler createLogFileHandler() throws IOException {
        // 1. Physical Directory Management
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (!created) {
                System.err.println("DAL ERROR: Failed to create logs directory at " + logDir.getAbsolutePath());
            }
        }

        // 2. Open File Stream (Append mode = true)
        // The FileHandler represents the connection to the data source (the file).
        return new FileHandler(LOG_FILE_PATH, true);
    }
}