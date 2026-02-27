package com.apm.bll;

import java.util.ArrayList;
import java.util.LinkedHashSet;

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

        for (String s : sentencesList) {
            System.out.println(s);
        }

        return sentencesList;
    }

    // --- New Function to split sentence into tokens ---
    public static ArrayList<String> sentenceToTokens(String sentence) {
        ArrayList<String> tokens = new ArrayList<>();

        if (sentence == null || sentence.trim().isEmpty()) {
            System.err.println("⚠️ Empty or null sentence provided.");
            return tokens;
        }

        // Split by spaces
        String[] words = sentence.trim().split("\\s+");

        // Use LinkedHashSet to keep unique tokens while preserving order
        LinkedHashSet<String> uniqueTokens = new LinkedHashSet<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                uniqueTokens.add(word);
            }
        }

        // Print the HashSet (unique tokens)

        // Convert back to ArrayList
        tokens.addAll(uniqueTokens);

        // Print the ArrayList

        return tokens;
    }

}
