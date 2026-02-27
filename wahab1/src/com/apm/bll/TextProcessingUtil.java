package com.apm.bll;

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

        // Step 2: Combine all valid lines into a single cleaned text
        StringBuilder textBuilder = new StringBuilder();
        for (String line : lines) {
            line = line.trim();

            // Skip empty lines or formatting lines (---, •, *, —)
            if (line.isEmpty() || line.matches("-+")) continue;
            if (line.startsWith("•") || line.startsWith("*") || line.startsWith("—")) continue;

            textBuilder.append(line).append(" ");
        }

        String cleanedText = textBuilder.toString().trim();
        if (cleanedText.isEmpty()) {
            System.err.println("⚠️ No valid text found after cleaning chapter data.");
            return sentencesList;
        }

        // Step 3: Split cleaned text into sentences (handles Arabic & English punctuation)
        String[] sentences = cleanedText.split("(?<=[.!؟?!])\\s+");

        // Step 4: Add each non-empty sentence to the list
        for (String s : sentences) {
            String sentence = s.trim();
            if (!sentence.isEmpty()) {
                sentencesList.add(sentence);
            }
        }
        for(String s:sentencesList ) {
        	System.out.println(s);
        }
        return sentencesList;
    }
}
