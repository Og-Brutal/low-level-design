package com.apm.bll.utils;

import java.util.List;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;

public class SegmentationUtil {
	
	
	
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
