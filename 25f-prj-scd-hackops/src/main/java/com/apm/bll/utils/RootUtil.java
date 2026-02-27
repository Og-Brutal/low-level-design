package main.java.com.apm.bll.utils;

import java.util.ArrayList;
import java.util.List;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;
import net.oujda_nlp_team.entity.ResultList;

public class RootUtil {
	
	
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
}
