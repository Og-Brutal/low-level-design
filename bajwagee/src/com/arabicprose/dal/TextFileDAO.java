package com.arabicprose.dal;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementation of TextFileDAO for reading text files with UTF-8 encoding
 */
public class TextFileDAO implements ITextFileDAO {
    
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    
    @Override
    public String readFileContent(String filePath) throws IOException {
        validateFilePath(filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }
        
        // Try to detect encoding and read the file
        byte[] fileBytes = Files.readAllBytes(path);
        
        // Try UTF-8 first (most common for Arabic text)
        String content = new String(fileBytes, DEFAULT_CHARSET);
        
        // If content seems corrupted, try other common encodings for Arabic
        if (isContentCorrupted(content)) {
            content = tryAlternativeEncodings(fileBytes);
        }
        
        return content;
    }
    
    @Override
    public List<String> readFileLines(String filePath) throws IOException {
        validateFilePath(filePath);
        
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }
        
        return Files.readAllLines(path, DEFAULT_CHARSET);
    }
    
    @Override
    public boolean isValidFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path path = Paths.get(filePath);
            return Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path);
        } catch (Exception e) {
            return false;
        }
    }
    
    private void validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
    }
    
    private boolean isContentCorrupted(String content) {
        // Check for common corruption signs in Arabic text
        if (content == null || content.isEmpty()) {
            return false;
        }
        
        // Look for replacement characters that indicate encoding issues
        if (content.contains("�") || content.contains("ï»¿")) {
            return true;
        }
        
        // Check if Arabic characters are properly represented
        // Arabic Unicode range: \u0600-\u06FF
        boolean hasArabicChars = content.matches(".*[\\u0600-\\u06FF].*");
        boolean hasGibberish = content.matches(".*[ÂÃÄÅÆÇÈÉÊË].*");
        
        return hasGibberish && !hasArabicChars;
    }
    
    private String tryAlternativeEncodings(byte[] fileBytes) {
        // Try common encodings for Arabic text
        Charset[] encodings = {
            StandardCharsets.UTF_16,
            StandardCharsets.ISO_8859_1,
            Charset.forName("Windows-1256"), // Arabic Windows encoding
            Charset.forName("ISO-8859-6"),   // Arabic ISO encoding
            StandardCharsets.US_ASCII
        };
        
        for (Charset encoding : encodings) {
            try {
                String content = new String(fileBytes, encoding);
                if (!isContentCorrupted(content)) {
                    System.out.println("Successfully read file with encoding: " + encoding);
                    return content;
                }
            } catch (Exception e) {
                // Try next encoding
            }
        }
        
        // If all else fails, return with default encoding
        return new String(fileBytes, DEFAULT_CHARSET);
    }
}