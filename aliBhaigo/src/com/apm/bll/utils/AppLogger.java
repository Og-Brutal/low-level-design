package com.apm.bll.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.apm.dal.utils.LogFileDAO;

public class AppLogger {

    private static boolean isInitialized = false;
    private static FileHandler fileHandler;

    private AppLogger() {}

    public static Logger getLogger(Class<?> clazz) {
        ensureInitialized();
        return Logger.getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        ensureInitialized();
        return Logger.getLogger(name);
    }

    private static synchronized void ensureInitialized() {
        if (isInitialized) {
            return;
        }

        try {
            Logger rootLogger = Logger.getLogger("");
            
            // Clean up default console handlers
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // 🟢 CALL TO DAL: Get the FileHandler from the Data Access Layer
            LogFileDAO logDao = new LogFileDAO();
            fileHandler = logDao.createLogFileHandler();

            // 🟢 LOGIC LAYER: Apply Formatting and Rules
            fileHandler.setFormatter(new CustomLogFormatter());
            fileHandler.setLevel(Level.INFO); // Strict filtering

            // Configure Root Logger
            rootLogger.setLevel(Level.INFO);
            rootLogger.addHandler(fileHandler);

            // Explicitly set package level
            Logger.getLogger("com.apm").setLevel(Level.INFO);

            logRunSeparator(rootLogger);

            isInitialized = true;

        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to initialize application logger: " + e.getMessage());
        }
    }
    
    private static void logRunSeparator(Logger logger) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String separator = "\n" + "=".repeat(60) + "\n" +
                           "NEW RUN: " + sdf.format(new Date()) + "\n" +
                           "=".repeat(60);
        
        LogRecord record = new LogRecord(Level.INFO, separator);
        record.setParameters(new Object[]{"RAW_MODE"}); 
        logger.log(record);
    }

    // Formatter remains in Utils because it deals with "Presentation/Logic", not "Storage"
    private static class CustomLogFormatter extends Formatter {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            if (record.getParameters() != null && 
                record.getParameters().length > 0 && 
                "RAW_MODE".equals(record.getParameters()[0])) {
                return record.getMessage() + "\n";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(new Date(record.getMillis())));
            sb.append(" | ").append(String.format("%-7s", record.getLevel().getName()));

            String loggerName = record.getLoggerName();
            String simpleName = loggerName.substring(loggerName.lastIndexOf('.') + 1);
            sb.append(" | ").append(String.format("%-20s", simpleName));

            sb.append(" | ").append(formatMessage(record));

            if (record.getThrown() != null) {
                sb.append("\n");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                sb.append(sw.toString());
            }

            sb.append("\n");
            return sb.toString();
        }
    }
}