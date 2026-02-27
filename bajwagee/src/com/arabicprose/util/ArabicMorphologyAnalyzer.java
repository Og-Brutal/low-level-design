package com.arabicprose.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;
import net.oujda_nlp_team.entity.ResultList;
import net.oujda_nlp_team.entity.Segment;
import net.oujda_nlp_team.util.ArabicStringUtil;
import net.oujda_nlp_team.util.Stemming;

/**
 * Arabic Morphology Analyzer
 * Segments Arabic words into prefix, stem, and postfix components.
 */
public class ArabicMorphologyAnalyzer {
    
    /**
     * Analyzes an Arabic word and returns its segmentation in the specified format.
     * 
     * @param word The Arabic word to analyze
     * @return Formatted segmentation string with Prefix, Stem, and Postfix
     */
    public static String analyzeWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return formatOutput("-", "-", "-");
        }
        
        String cleanWord = word.trim();
        
        try {
            // Remove diacritics for analysis
            String unvoweled = ArabicStringUtil.getInstance().removeAllDiacriticsOfWord(cleanWord);
            
            // Try AlKhalil2Analyzer first
            ResultList resultList = AlKhalil2Analyzer.getInstance().processToken(unvoweled);
            
            if (resultList != null && resultList.isAnalyzed() && resultList.size() > 0) {
                Result primaryResult = resultList.getAllResults().get(0);
                String result = extractFromResult(primaryResult);
                // Improve the result by checking for multiple prefixes
                return improveSegmentation(result, cleanWord);
            }
            
            // Fallback to Stemming utility
            @SuppressWarnings("unchecked")
            List<Segment> segments = (List<Segment>) Stemming.getInstance().getListsSegment(unvoweled);
            if (segments != null && !segments.isEmpty()) {
                Segment segment = segments.get(0);
                String result = extractFromSegment(segment);
                // Improve the result by checking for multiple prefixes
                return improveSegmentation(result, cleanWord);
            }
            
        } catch (Exception e) {
            System.err.println("Error analyzing word '" + word + "': " + e.getMessage());
        }
        
        // If all else fails, try manual segmentation
        return manualSegmentation(cleanWord);
    }
    
    /**
     * Extracts segmentation from AlKhalil2 Result object.
     */
    private static String extractFromResult(Result result) {
        String prefix = cleanClitic(result.getProcliticNoDec());
        String stem = cleanValue(result.getStem());
        String postfix = cleanClitic(result.getEncliticNoDec());
        
        return formatOutput(prefix, stem, postfix);
    }
    
    /**
     * Extracts segmentation from Stemming Segment object.
     */
    private static String extractFromSegment(Segment segment) {
        String prefix = "";
        String stem = "";
        String postfix = "";
        
        // Extract prefix (proclitic)
        if (segment.getProclitic() != null) {
            prefix = cleanClitic(segment.getProclitic().toString());
        }
        
        // Extract stem
        if (segment.getStem() != null) {
            stem = cleanValue(segment.getStem().toString());
        }
        
        // Extract postfix (enclitic)
        if (segment.getEnclitic() != null) {
            postfix = cleanClitic(segment.getEnclitic().toString());
        }
        
        return formatOutput(prefix, stem, postfix);
    }
    
    /**
     * Cleans clitic values by removing object names and formatting multiple clitics.
     */
    private static String cleanClitic(String clitic) {
        if (clitic == null || clitic.trim().isEmpty()) {
            return null;
        }
        
        // Remove object names like "net.oujda_nlp_team.entity.Clitic" or "Clitic[value]"
        String cleaned = clitic.replaceAll("^.*\\.Clitic\\[?", "")
                              .replaceAll("^Clitic\\[?", "")
                              .replaceAll("\\]$", "")
                              .trim();
        
        // If it still contains object-like patterns, try to extract just the Arabic text
        if (cleaned.contains(".") || cleaned.contains("net.oujda")) {
            // Try to extract Arabic characters only
            Pattern arabicPattern = Pattern.compile("[\\u0600-\\u06FF]+");
            java.util.regex.Matcher matcher = arabicPattern.matcher(cleaned);
            if (matcher.find()) {
                cleaned = matcher.group();
            } else {
                return null;
            }
        }
        
        // Handle multiple clitics - split and combine with "+"
        if (cleaned.length() > 1 && containsMultipleClitics(cleaned)) {
            return formatMultipleClitics(cleaned);
        }
        
        return cleaned.isEmpty() ? null : cleaned;
    }
    
    /**
     * Checks if a string contains multiple clitics that should be separated.
     */
    private static boolean containsMultipleClitics(String text) {
        // Common Arabic clitics: و، ف، ب، ك، ل، س، الـ
        String[] clitics = {"و", "ف", "ب", "ك", "ل", "س", "ال"};
        
        int cliticCount = 0;
        for (String clitic : clitics) {
            if (text.startsWith(clitic)) {
                cliticCount++;
                text = text.substring(clitic.length());
            }
        }
        
        return cliticCount > 1 || (cliticCount == 1 && text.length() > 0);
    }
    
    /**
     * Formats multiple clitics with "+" separator.
     * Example: "وب" -> "و + ب"
     * Handles sequential clitics like "وب" (و followed by ب)
     */
    private static String formatMultipleClitics(String text) {
        List<String> clitics = new ArrayList<>();
        String remaining = text;
        
        // Extract clitics one by one (order matters - check longer ones first)
        String[] commonClitics = {"ال", "و", "ف", "ب", "ك", "ل", "س"};
        
        boolean found = true;
        while (found && remaining.length() > 0) {
            found = false;
            for (String clitic : commonClitics) {
                if (remaining.startsWith(clitic)) {
                    clitics.add(clitic);
                    remaining = remaining.substring(clitic.length());
                    found = true;
                    break; // Start over to check for more clitics
                }
            }
        }
        
        // Join clitics with "+"
        if (!clitics.isEmpty()) {
            return String.join(" + ", clitics);
        }
        
        return text;
    }
    
    /**
     * Cleans a value by removing object names and extracting Arabic text.
     */
    private static String cleanValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // Remove object names
        String cleaned = value.replaceAll("^.*\\.Clitic\\[?", "")
                             .replaceAll("^Clitic\\[?", "")
                             .replaceAll("\\]$", "")
                             .trim();
        
        // If it contains object-like patterns, extract Arabic characters
        if (cleaned.contains(".") || cleaned.contains("net.oujda")) {
            Pattern arabicPattern = Pattern.compile("[\\u0600-\\u06FF]+");
            java.util.regex.Matcher matcher = arabicPattern.matcher(cleaned);
            if (matcher.find()) {
                cleaned = matcher.group();
            } else {
                return null;
            }
        }
        
        return cleaned.isEmpty() ? null : cleaned;
    }
    
    /**
     * Formats the output in the exact required format.
     */
    private static String formatOutput(String prefix, String stem, String postfix) {
        StringBuilder output = new StringBuilder();
        
        output.append("Prefix: ");
        output.append(prefix != null && !prefix.isEmpty() ? prefix : "-");
        output.append("\n");
        
        output.append("Stem: ");
        output.append(stem != null && !stem.isEmpty() ? stem : "-");
        output.append("\n");
        
        output.append("Postfix: ");
        output.append(postfix != null && !postfix.isEmpty() ? postfix : "-");
        
        return output.toString();
    }
    
    /**
     * Advanced segmentation that handles complex cases like "وبكتابهم"
     * by manually parsing common patterns.
     */
    public static String analyzeWordAdvanced(String word) {
        if (word == null || word.trim().isEmpty()) {
            return formatOutput("-", "-", "-");
        }
        
        String cleanWord = word.trim();
        
        // Try the standard analyzer first
        String result = analyzeWord(cleanWord);
        
        // If result has all "-", try manual parsing
        if (result.contains("Prefix: -\nStem: -\nPostfix: -")) {
            return manualSegmentation(cleanWord);
        }
        
        // Check if we need to improve prefix/postfix detection
        return improveSegmentation(result, cleanWord);
    }
    
    /**
     * Manual segmentation for cases where automatic analysis fails.
     * Handles complex cases like "وبكتابهم" -> Prefix: و + ب, Stem: كتاب, Postfix: هم
     */
    private static String manualSegmentation(String word) {
        List<String> prefixes = new ArrayList<>();
        String stem = word;
        String postfix = null;
        
        // Extract prefixes sequentially (order matters - check longer ones first)
        String[] prefixPatterns = {"ال", "و", "ف", "ب", "ك", "ل", "س"};
        
        boolean foundPrefix = true;
        while (foundPrefix && stem.length() > 0) {
            foundPrefix = false;
            for (String prefix : prefixPatterns) {
                if (stem.startsWith(prefix)) {
                    prefixes.add(prefix);
                    stem = stem.substring(prefix.length());
                    foundPrefix = true;
                    break; // Start over to check for more prefixes
                }
            }
        }
        
        // Extract postfixes (check longer ones first)
        String[] postfixPatterns = {"هم", "ها", "نا", "ون", "ين", "ات", "ان", "ي", "ك", "ة", "ت", "ن"};
        
        for (String suffix : postfixPatterns) {
            if (stem.endsWith(suffix) && stem.length() > suffix.length()) {
                postfix = suffix;
                stem = stem.substring(0, stem.length() - suffix.length());
                break;
            }
        }
        
        String prefixStr = prefixes.isEmpty() ? null : String.join(" + ", prefixes);
        
        return formatOutput(prefixStr, stem.isEmpty() ? null : stem, postfix);
    }
    
    /**
     * Improves segmentation by checking for missed clitics.
     */
    private static String improveSegmentation(String currentResult, String originalWord) {
        // Parse current result
        String[] lines = currentResult.split("\n");
        String prefix = lines[0].replace("Prefix: ", "").trim();
        String stem = lines[1].replace("Stem: ", "").trim();
        String postfix = lines[2].replace("Postfix: ", "").trim();
        
        // If prefix is "-" but word starts with common clitics, try to extract them
        if ("-".equals(prefix) && originalWord.length() > 0) {
            List<String> foundPrefixes = new ArrayList<>();
            String remaining = originalWord;
            
            String[] clitics = {"ال", "و", "ف", "ب", "ك", "ل", "س"};
            for (String clitic : clitics) {
                if (remaining.startsWith(clitic)) {
                    foundPrefixes.add(clitic);
                    remaining = remaining.substring(clitic.length());
                }
            }
            
            if (!foundPrefixes.isEmpty()) {
                prefix = String.join(" + ", foundPrefixes);
                // Update stem if it was extracted incorrectly
                if (stem.equals(originalWord) || stem.equals("-")) {
                    stem = remaining;
                }
            }
        }
        
        return formatOutput(prefix.equals("-") ? null : prefix, 
                           stem.equals("-") ? null : stem, 
                           postfix.equals("-") ? null : postfix);
    }
}

