package com.apm.bll.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;
import net.oujda_nlp_team.util.Tokenization;

public class TokanizationUtil {
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
}
