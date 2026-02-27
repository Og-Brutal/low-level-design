package main.java.com.apm.bll.utils;

import java.util.ArrayList;

public class TextProcessingUtil {
	
	
	public static ArrayList<String> ChapterInToSentence(String chapter) {
    ArrayList<String> sentencesList = new ArrayList<>();

    if (chapter == null || chapter.trim().isEmpty()) {
        System.err.println("⚠️ Empty or null chapter content provided.");
        return sentencesList;
    }

    // Step 1: Split file data into lines
    String[] lines = chapter.split("\\r?\\n");

    // Step 2: Clean lines and remove numbering
    StringBuilder textBuilder = new StringBuilder();
    for (String line : lines) {
        line = line.trim();

        // Skip empty or bullet lines
        if (line.isEmpty()) continue;
        if (line.matches("-+")) continue;
        if (line.startsWith("•") || line.startsWith("*") || line.startsWith("—")) continue;

        // 🔥 Remove numbering at start:
        // English numbers: 1. 1) 1-
        // Arabic numbers: ١. ١) ١-
        line = line.replaceFirst("^[0-9]+[\\).\\-\\s]*", "");      // English digits
        line = line.replaceFirst("^[\u0660-\u0669]+[\\).\\-\\s]*", ""); // Arabic digits (٠١٢٣٤٥٦٧٨٩)

        textBuilder.append(line).append(" ");
    }

    String cleanedText = textBuilder.toString().trim();
    if (cleanedText.isEmpty()) {
        System.err.println("⚠️ No valid text found after cleaning chapter data.");
        return sentencesList;
    }

    // Step 3: Split cleaned text into sentences
    String[] sentences = cleanedText.split("(?<=[.!؟?!])\\s+");

    // Step 4: Add cleaned sentences
    for (String s : sentences) {
        String sentence = s.trim();
        if (!sentence.isEmpty()) {
            sentencesList.add(sentence);
        }
    }

    return sentencesList;
	}

}
