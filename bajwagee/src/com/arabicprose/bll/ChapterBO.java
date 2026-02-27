package com.arabicprose.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.arabicprose.dal.IDataAccessLayerFacade;
import com.arabicprose.dal.ITextFileDAO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

public class ChapterBO implements IChapterBO {
    private IDataAccessLayerFacade chapterDAO;
    private ITextFileDAO iTextFileDAO;
    private IMorphologicalAnalysisBO morphologicalAnalysisBO; // Add this field
    
    // Sentence splitting pattern for Arabic and English punctuation
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("(?<=[.!?؟])\\s+");
    
    // Update constructor to include morphologicalAnalysisBO
    public ChapterBO(IDataAccessLayerFacade chapterDAO, ITextFileDAO iTextFileDAO, 
                    IMorphologicalAnalysisBO morphologicalAnalysisBO) {
        this.chapterDAO = chapterDAO;
        this.iTextFileDAO = iTextFileDAO;
        this.morphologicalAnalysisBO = morphologicalAnalysisBO; // Initialize this
    }

    @Override
    public void addChapter(ChapterDTO chapterDTO) throws SQLException {
        if (chapterDTO.getBookId() <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        if (chapterDTO.getChapterName() == null || chapterDTO.getChapterName().trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter name is required.");
        }
        
        // Auto-generate chapter order if not provided
        if (chapterDTO.getChapterOrder() <= 0) {
            chapterDTO.setChapterOrder(chapterDAO.getNextChapterOrder(chapterDTO.getBookId()));
        }
        
        // Check if chapter name already exists for this book
        if (chapterDAO.isChapterNameExists(chapterDTO.getBookId(), chapterDTO.getChapterName())) {
            throw new IllegalArgumentException("Chapter name '" + chapterDTO.getChapterName() + 
                    "' already exists for this book. Please choose a different name.");
        }
        
        chapterDAO.addChapter(chapterDTO);
    }

    @Override
    public List<ChapterDTO> getChaptersByBookId(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        return chapterDAO.getChaptersByBookId(bookId);
    }

    @Override
    public void updateChapter(ChapterDTO chapterDTO) throws SQLException {
        if (chapterDTO.getChapterName() == null || chapterDTO.getChapterName().trim().isEmpty()) {
            throw new IllegalArgumentException("Chapter name is required.");
        }
        
        ChapterDTO existingChapter = chapterDAO.getChapterById(chapterDTO.getChapterId());
        if (existingChapter != null && 
            !existingChapter.getChapterName().equals(chapterDTO.getChapterName()) &&
            chapterDAO.isChapterNameExists(chapterDTO.getBookId(), chapterDTO.getChapterName())) {
            throw new IllegalArgumentException("Chapter name '" + chapterDTO.getChapterName() + 
                    "' already exists for this book. Please choose a different name.");
        }
        
        chapterDAO.updateChapter(chapterDTO);
    }

    @Override
    public void deleteChapter(int chapterId) throws SQLException {
        chapterDAO.deleteChapter(chapterId);
    }

    @Override
    public ChapterDTO getChapterById(int chapterId) throws SQLException {
        ChapterDTO chapter = chapterDAO.getChapterById(chapterId);
        return chapter;
    }
    
    public boolean sentenceSplitter(String path, int bookID) {
        try {
            // Validate file
            if (!iTextFileDAO.isValidFile(path)) {
                System.err.println("Invalid file: " + path);
                return false;
            }
            
            // Read file content
            String fileContent = iTextFileDAO.readFileContent(path);
            if (fileContent == null || fileContent.trim().isEmpty()) {
                System.err.println("File is empty: " + path);
                return false;
            }
            
            // Split content into lines
            String[] lines = fileContent.split("\\r?\\n");
            if (lines.length == 0) {
                System.err.println("No content found in file: " + path);
                return false;
            }
            
            // First line is chapter name
            String chapterName = lines[0].trim();
            if (chapterName.isEmpty()) {
                System.err.println("First line (chapter name) is empty");
                return false;
            }
            
            // Check if chapter name already exists for this book
            if (chapterDAO.isChapterNameExists(bookID, chapterName)) {
                System.err.println("Chapter name already exists: " + chapterName);
                return false;
            }
            
            // Build content from remaining lines, ignoring only numbered lines
            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                
                // Skip lines that are numbered items
                if (isNumberedLine(line)) {
                    System.out.println("Skipping numbered line: " + line);
                    continue;
                }
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                contentBuilder.append(line).append(" ");
            }
            
            String content = contentBuilder.toString().trim();
            
            if (content.isEmpty()) {
                System.err.println("No content found after chapter name");
                return false;
            }
            
            // Split content into sentences
            List<String> sentenceTexts = splitIntoSentences(content);
            if (sentenceTexts.isEmpty()) {
                System.err.println("No sentences found after splitting");
                return false;
            }
            
            // Create and add chapter
            ChapterDTO chapter = createChapter(bookID, chapterName);
            chapterDAO.addChapter(chapter);
            
            // Get the actual chapter ID (we need to retrieve the chapter we just created)
            ChapterDTO createdChapter = getRecentlyCreatedChapter(bookID, chapterName);
            if (createdChapter == null) {
                System.err.println("Failed to retrieve the created chapter");
                return false;
            }
            
            // Add sentences to database WITH AUTOMATIC MORPHOLOGICAL ANALYSIS
            int sentencesAdded = addSentencesToDatabase(bookID, createdChapter.getChapterId(), sentenceTexts);
            
            System.out.println("Successfully processed text file:");
            System.out.println("- Chapter: " + chapterName);
            System.out.println("- Sentences added: " + sentencesAdded + " out of " + sentenceTexts.size());
            System.out.println("- Book ID: " + bookID);
            System.out.println("- Chapter ID: " + createdChapter.getChapterId());
            
            return sentencesAdded > 0;
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Checks if a line is a numbered item
     * @param line the line to check
     * @return true if it's a numbered line, false otherwise
     */
    private boolean isNumberedLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        
        String trimmedLine = line.trim();
        
        // Check for lines starting with numbers followed by dot, colon, parenthesis, or space
        // Examples: "1.", "2)", "3-", "1:", "5 Text"
        boolean isNumbered = trimmedLine.matches("^\\d+[\\.\\-\\)\\:]\\s.*") || 
                             trimmedLine.matches("^\\d+\\s.*");
        
        // Check for lines that are just numbers (e.g., "1", "2", "3")
        boolean isJustNumber = trimmedLine.matches("^\\d+$");
        
        // Check for Arabic numbered patterns (e.g., "١.", "٢)", etc.)
        boolean isArabicNumbered = trimmedLine.matches("^[٠١٢٣٤٥٦٧٨٩]+[\\.\\-\\)\\:]\\s.*") ||
                                   trimmedLine.matches("^[٠١٢٣٤٥٦٧٨٩]+\\s.*");
        
        // Check for Roman numerals (basic pattern)
        boolean isRomanNumeral = trimmedLine.matches("^[IVXLCDM]+\\.\\s.*") ||
                                 trimmedLine.matches("^[IVXLCDM]+\\).*");
        
        return isNumbered || isJustNumber || isArabicNumbered || isRomanNumeral;
    }
    
    private int addSentencesToDatabase(int bookId, int chapterId, List<String> sentenceTexts) throws SQLException {
    int sentenceNumber = chapterDAO.getNextSentenceNumber(bookId);
    List<SentenceDTO> sentencesBatch = new ArrayList<>();
    int sentencesAdded = 0;

    // -------------------------
    // 1) PREPARE SENTENCES LIST
    // -------------------------
    for (String sentenceText : sentenceTexts) {
        String trimmed = sentenceText.trim();

        if (!trimmed.isEmpty() && trimmed.length() > 1) {

            // Create DTO
            SentenceDTO sentence = createSentence(bookId, chapterId, sentenceNumber++, trimmed);

            // Add to batch list
            sentencesBatch.add(sentence);
        }
    }

    // ------------------------------------
    // 2) INSERT ALL SENTENCES IN ONE BATCH
    // ------------------------------------
    if (!sentencesBatch.isEmpty()) {
        chapterDAO.addBatchSentences(sentencesBatch);
        sentencesAdded = sentencesBatch.size();
    }

    // ------------------------------------------------------------
    // 3) RE-FETCH ALL SENTENCES OF THE CHAPTER (NOW WITH IDs)
    // ------------------------------------------------------------
    List<SentenceDTO> dbSentences = chapterDAO.getSentencesByChapterId(chapterId);

    // Map to find newly added sentences by sentence text
    // (in case old sentences already exist in chapter)
    for (SentenceDTO original : sentencesBatch) {

        // find the matching sentence in DB
        SentenceDTO addedSentence = dbSentences.stream()
                .filter(s -> s.getText().equals(original.getText())
                        && s.getSentenceNumber() == original.getSentenceNumber())
                .findFirst()
                .orElse(null);

        if (addedSentence == null) {
            System.err.println("✗ Could not find just-inserted sentence: " + original.getText());
            continue;
        }

        System.out.println("=== STARTING MORPHOLOGICAL ANALYSIS FOR SENTENCE "
                + addedSentence.getSentenceId() + " ===");

        try {
            morphologicalAnalysisBO.analyzeTextAutomatically(
                    addedSentence.getSentenceId(),
                    addedSentence.getText()
            );

            System.out.println("✓ Successfully analyzed sentence ID: " + addedSentence.getSentenceId());

        } catch (Exception e) {
            System.err.println("✗ Analysis failed for sentence: " + e.getMessage());
        }
    }

    return sentencesAdded;
}

    /**
     * Validates if text is suitable for morphological analysis
     */
    private boolean isValidForAnalysis(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = text.trim();
        
        // Check for problematic patterns that might cause NPE in AlKhalil2
        if (trimmed.matches(".*[0-9].*")) {
            return false; // Skip texts with numbers
        }
        
        if (trimmed.length() > 100) {
            return false; // Skip very long texts
        }
        
        // Check if text contains only Arabic characters and common punctuation
        if (!trimmed.matches("[\\p{InArabic}\\s.,!?؛،:()\\-\"]+")) {
            return false;
        }
        
        return true;
    }
    
    private SentenceDTO createSentence(int bookId, int chapterId, int sentenceNumber, String text) {
        SentenceDTO sentence = new SentenceDTO();
        sentence.setBookId(bookId);
        sentence.setChapterId(chapterId);
        sentence.setSentenceNumber(sentenceNumber);
        sentence.setText(text);
        sentence.setTextDiacritized(""); // Can be filled later
        sentence.setTranslation(""); // Can be filled later
        sentence.setNotes("Imported from text file");
        return sentence;
    }
    
    private ChapterDTO createChapter(int bookId, String chapterName) throws SQLException {
        ChapterDTO chapter = new ChapterDTO();
        chapter.setBookId(bookId);
        chapter.setChapterName(chapterName);
        chapter.setChapterOrder(chapterDAO.getNextChapterOrder(bookId));
        chapter.setDescription("Imported from text file");
        return chapter;
    }
    
    private ChapterDTO getRecentlyCreatedChapter(int bookId, String chapterName) throws SQLException {
        List<ChapterDTO> chapters = chapterDAO.getChaptersByBookId(bookId);
        return chapters.stream()
            .filter(c -> c.getChapterName().equals(chapterName))
            .findFirst()
            .orElse(null);
    }
    
    private List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return sentences;
        }
        
        try {
            // Split using Arabic and English punctuation
            String[] rawSentences = SENTENCE_PATTERN.split(text);
            
            for (String sentence : rawSentences) {
                String trimmed = sentence.trim();
                
                // Additional filtering: remove any sentences that look like numbered items
                if (!trimmed.isEmpty() && 
                    !isNumberedLine(trimmed) && 
                    isMeaningfulSentence(trimmed)) {
                    sentences.add(trimmed);
                }
            }
            
            // Fallback: if no sentences found with punctuation, split by newlines
            if (sentences.isEmpty()) {
                String[] lines = text.split("\\r?\\n");
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && 
                        !isNumberedLine(trimmed) && 
                        isMeaningfulSentence(trimmed)) {
                        sentences.add(trimmed);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in sentence splitting: " + e.getMessage());
            // Fallback to simple space splitting
            String[] fallback = text.split("\\s+");
            for (String part : fallback) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && 
                    !isNumberedLine(trimmed) && 
                    trimmed.length() > 1) {
                    sentences.add(trimmed);
                }
            }
        }
        
        return sentences;
    }
    
    /**
     * Checks if a sentence is meaningful (not just punctuation or very short)
     */
    private boolean isMeaningfulSentence(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = sentence.trim();
        
        // Check if it's just punctuation or very short meaningless text
        if (trimmed.length() <= 1) {
            return false;
        }
        
        // Check if it contains at least one letter or Arabic character
        boolean hasLetters = false;
        for (char c : trimmed.toCharArray()) {
            if (Character.isLetter(c) || 
                (c >= '\u0600' && c <= '\u06FF') || // Arabic characters
                (c >= '\uFE70' && c <= '\uFEFF')) { // Arabic presentation forms
                hasLetters = true;
                break;
            }
        }
        
        return hasLetters;
    }
    
    @Override
    public List<ChapterDTO> searchChaptersByName(String chapterName) throws SQLException {
        if (chapterName == null || chapterName.trim().isEmpty()) {
            throw new IllegalArgumentException("Search chapter name is required.");
        }
        return chapterDAO.searchChaptersByName(chapterName.trim());
    }

    @Override
    public int getNextChapterOrder(int bookId) throws SQLException {
        if (bookId <= 0) {
            throw new IllegalArgumentException("Valid book ID is required.");
        }
        return chapterDAO.getNextChapterOrder(bookId);
    }
}