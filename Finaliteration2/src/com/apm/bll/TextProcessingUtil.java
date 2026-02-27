package com.apm.bll;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.oujda_nlp_team.ADATAnalyzer;
import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;
import net.oujda_nlp_team.entity.ResultList;
import net.oujda_nlp_team.util.Tokenization;

public class TextProcessingUtil {
    private static final Pattern LEMMA_PATTERN =
            Pattern.compile("<lemma>\\s*(.+?)\\s*</lemma>", Pattern.DOTALL);

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
    
    
 
    // ---------------------------------------------------------------
    // 1. Generate Lemma List for All Tokens (Unique Lemmas Only)
    // 	---------------------------------------------------------------
    public static ArrayList<String> getLemmaList(ArrayList<String> tokens) {
        ArrayList<String> lemmaList = new ArrayList<>();

        if (tokens == null || tokens.isEmpty()) {
            return lemmaList;
        }

        java.util.Set<String> uniqueSet = new java.util.LinkedHashSet<>();

        for (String token : tokens) {
            try {
                if (token != null && !token.trim().isEmpty()) {
                    String lemma = getLemma(token.trim());
                    if (lemma != null && !lemma.trim().isEmpty()) {
                        uniqueSet.add(lemma.trim());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error getting lemma for token: " + token);
            }
        }

        lemmaList.addAll(uniqueSet);
        return lemmaList;
    }




    // ---------------------------------------------------------------
    // 2. Generate Root List from Lemma List (Unique Roots Only)
    // 	---------------------------------------------------------------
    public static ArrayList<String> getRootList(ArrayList<String> lemmas) {
    	ArrayList<String> rootList = new ArrayList<>();

    	if (lemmas == null || lemmas.isEmpty()) {
    		return rootList;
    	}

    	java.util.Set<String> uniqueRoots = new java.util.LinkedHashSet<>();

    	for (String lemma : lemmas) {
    		if (lemma != null && !lemma.trim().isEmpty()) 	{
    			String root = getThreeLetterRoot(lemma.trim());
            if (root != null && !root.trim().isEmpty()) {
                uniqueRoots.add(root.trim());
            	}
    		}
    	}

    	rootList.addAll(uniqueRoots);
    	return rootList;
    }

    // --- New Function to split sentence into tokens ---
    public static ArrayList<String> analyzeAndSegmentSentence(String sentence) {
        List<String> rawTokens = tokenizeSentence(sentence);

        if (rawTokens.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> segmentedTokens = segmentTokens(rawTokens);

        // Remove empty tokens
        segmentedTokens.removeIf(token -> token == null || token.trim().isEmpty());

        // Ensure return type is ArrayList
        return new ArrayList<>(segmentedTokens);
    }

    public static List<String> tokenizeSentence(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            Tokenization.getInstance().setTokenizationString(sentence);
            
            // getAllText() returns the full list of words, preserving order and repeats.
            @SuppressWarnings("unchecked") 
            List<String> tokens = Tokenization.getInstance().getAllText();
            
            return tokens;

        } catch (Exception e) {
            System.err.println("An unexpected error occurred during tokenization: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public static List<String> segmentTokens(List<String> tokens) {
        // Initialize the analyzer properly using the AlKhalil2Analyzer from your code
        if (AlKhalil2Analyzer.getInstance() == null) {
            System.err.println("Error: Analyzer not initialized. Cannot segment.");
            return tokens; // Return original tokens if segmentation fails
        }
        
        if (tokens.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> segmentedTokens = new ArrayList<>();
        
        try {
            for (String word : tokens) {
                // 1. Get morphological analysis results using the correct analyzer instance
                List<Result> analysisResults = AlKhalil2Analyzer.getInstance().analyzerToken(word);
                
                if (analysisResults != null && !analysisResults.isEmpty()) {
                    // 2. Use the first result to get the stem
                    String stem = analysisResults.get(0).getStem();
                    
                    // Check for invalid analysis marker ("#")
                    if (stem != null && !stem.equals("#")) {
                        segmentedTokens.add(stem);
                    } else {
                        // If no valid stem is found, keep the original word as the segment
                        segmentedTokens.add(word); 
                    }
                } else {
                    // If no analysis is returned, keep the original word
                    segmentedTokens.add(word);
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred during segmentation: " + e.getMessage());
            e.printStackTrace();
            // In case of a failure, return the original tokens
            return tokens; 
        }

        return segmentedTokens;
    }
    public static String getLemma(String arabicWord) {
        if (arabicWord == null || arabicWord.trim().isEmpty()) {
            return arabicWord;
        }

        try {
            ResultList result = AlKhalil2Analyzer.getInstance().processToken(arabicWord);

            if (result != null && result.isAnalyzed()) {
                List<String> lemmas = result.getAllLemmas();

                if (lemmas != null && !lemmas.isEmpty()) {
                    return lemmas.get(0).trim();   // Best lemma
                }
            }

            return arabicWord.trim();

        } catch (Exception e) {
            System.err.println("Lemma error for: " + arabicWord);
            return arabicWord.trim();
        }
    }

    public static String getThreeLetterRoot(String word) {

        if (word == null || word.trim().isEmpty()) {
            return word;
        }

        String clean = word.trim();

        try {
            // Main analysis
            ResultList resultList = AlKhalil2Analyzer.getInstance().processToken(clean);

            if (resultList.isAnalyzed()) {

                // STEP 1 — Try all roots (List<String>)
                List<String> allRoots = resultList.getAllRoots();
                if (allRoots != null && !allRoots.isEmpty()) {

                    for (String root : allRoots) {
                        if (root != null && !root.equals("#") && root.length() == 3) {
                            return root;   // First valid 3-letter root
                        }
                    }
                }

                // STEP 2 — Check individual results
                List<Result> results = resultList.getAllResults();
                if (results != null) {
                    for (Result r : results) {
                        String root = r.getRoot();
                        if (root != null && !root.equals("#") && root.length() == 3) {
                            return root;
                        }
                    }
                }
            }

            // STEP 3 — Fallback: analyzerToken()
            List<Result> altResults = AlKhalil2Analyzer.getInstance().analyzerToken(clean);
            if (altResults != null) {
                for (Result r : altResults) {
                    String root = r.getRoot();
                    if (root != null && !root.equals("#") && root.length() == 3) {
                        return root;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error extracting root for '" + clean + "': " + e.getMessage());
        }

        // No 3-letter root found → return original word
        return clean;
    }
public static String getPrefix(String token) {
    if (token == null || token.trim().isEmpty()) return "";

    token = token.trim();
    String cleanToken = removeDiacritics(token);

    // Step 1: Get stem from analyzer
    String stem = token;
    try {
        List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(token);
        if (results != null && !results.isEmpty()) {
            Result r = results.get(0);
            if (r.getStem() != null && !r.getStem().equals("#") && !r.getStem().isEmpty()) {
                stem = r.getStem().trim();
            }
        }
    } catch (Exception ignored) {}

    String cleanStem = removeDiacritics(stem);

    // Step 2: Find stem position in token
    int pos = cleanToken.indexOf(cleanStem);

    if (pos > 0) {
        // Everything before stem is prefix
    	String pre=token.substring(0, pos);
    	System.out.println(pre);
        return pre;
    }

    // Step 3: Fallback: try common prefixes if stem matching fails
    String[] commonPrefixes = {"ال", "و", "ف", "ب", "ك", "ل", "س"};
    for (String p : commonPrefixes) {
        if (cleanToken.startsWith(removeDiacritics(p)) && token.length() > p.length()) {
        	String prefix=token.substring(0, p.length());
        	System.out.println(prefix);
            return prefix;
        }
    }

    // No prefix found
    return "";
}

public static String getStem(String token) {
    if (token == null || token.trim().isEmpty()) return "";

    token = token.trim();
    String stem = token; // fallback if analyzer fails

    try {
        List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(token);
        if (results != null && !results.isEmpty()) {
            Result r = results.get(0);
            if (r.getStem() != null && !r.getStem().equals("#") && !r.getStem().isEmpty()) {
                stem = r.getStem().trim();
            }
        }
    } catch (Exception e) {
        System.err.println("Analyzer error: " + e.getMessage());
    }

    // Remove prefix before returning stem
    String prefix = getPrefix(token);
    if (!prefix.isEmpty() && token.startsWith(prefix)) {
        String remaining = token.substring(prefix.length());
        if (remaining.endsWith(stem)) {
            return stem;
        } else {
            return remaining; // fallback
        }
    }

    return stem;
}

public static String removeDiacritics(String s) {
    if (s == null) return "";
    return s.replaceAll("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]", "");
}

}
