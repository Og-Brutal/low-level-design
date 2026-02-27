//package com.apm.bll;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import net.oujda_nlp_team.ADATAnalyzer;
//import net.oujda_nlp_team.AlKhalil2Analyzer;
//import net.oujda_nlp_team.entity.Result;
//import net.oujda_nlp_team.entity.ResultList;
//import net.oujda_nlp_team.util.Tokenization;
//
//public class test {
//
//    private static final Pattern LEMMA_PATTERN =
//            Pattern.compile("<lemma>\\s*(.+?)\\s*</lemma>", Pattern.DOTALL);
//    private static AlKhalil2Analyzer analyzer = null;
//
//    static {
//        try {
//            // Get a singleton instance of the analyzer. This might take a moment 
//            // the first time resources are loaded.
//            analyzer = AlKhalil2Analyzer.getInstance();
//        } catch (Exception e) {
//            System.err.println("FATAL: Failed to initialize AlKhalil2Analyzer. " +
//                               "Check if 'AlkhalilMorphSys2.jar' and its dependencies are correct.");
//            e.printStackTrace();
//            analyzer = null;
//        }
//    }
//    
//    public static String getLemma(String arabicWord) throws IOException {
//        File in  = File.createTempFile("adat_in_", ".txt");
//        File out = File.createTempFile("adat_out_", ".xml");
//        in.deleteOnExit();
//        out.deleteOnExit();
//
//        try (PrintWriter pw = new PrintWriter(
//                new OutputStreamWriter(new FileOutputStream(in), StandardCharsets.UTF_8))) {
//            pw.print(arabicWord);
//        }
//
//        ADATAnalyzer.getInstance()
//                .processLemmatization(
//                        in.getAbsolutePath(), "UTF-8",
//                        out.getAbsolutePath(), "UTF-8");
//
//        String xml = new String(
//                Files.readAllBytes(out.toPath()), StandardCharsets.UTF_8);
//
//        Matcher m = LEMMA_PATTERN.matcher(xml);
//        return m.find() ? m.group(1).trim() : arabicWord;
//    }
//
//
//    
//    
//    
//    
//    public static List<String> analyzeAndSegmentSentence(String sentence) {
//        List<String> rawTokens = tokenizeSentence(sentence);
//            
//        if (rawTokens.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<String> segmentedTokens = segmentTokens(rawTokens);
//
//        // NEW LINE → remove empty tokens
//        segmentedTokens.removeIf(token -> token == null || token.trim().isEmpty());
//            
//        return segmentedTokens;
//    }
//    public static List<String> tokenizeSentence(String sentence) {
//        if (sentence == null || sentence.trim().isEmpty()) {
//            return Collections.emptyList();
//        }
//        
//        try {
//            Tokenization.getInstance().setTokenizationString(sentence);
//            
//            // getAllText() returns the full list of words, preserving order and repeats.
//            @SuppressWarnings("unchecked") 
//            List<String> tokens = Tokenization.getInstance().getAllText();
//            
//            return tokens;
//
//        } catch (Exception e) {
//            System.err.println("An unexpected error occurred during tokenization: " + e.getMessage());
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
//    public static List<String> segmentTokens(List<String> tokens) {
//        // Initialize the analyzer properly using the AlKhalil2Analyzer from your code
//        if (AlKhalil2Analyzer.getInstance() == null) {
//            System.err.println("Error: Analyzer not initialized. Cannot segment.");
//            return tokens; // Return original tokens if segmentation fails
//        }
//        
//        if (tokens.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<String> segmentedTokens = new ArrayList<>();
//        
//        try {
//            for (String word : tokens) {
//                // 1. Get morphological analysis results using the correct analyzer instance
//                List<Result> analysisResults = AlKhalil2Analyzer.getInstance().analyzerToken(word);
//                
//                if (analysisResults != null && !analysisResults.isEmpty()) {
//                    // 2. Use the first result to get the stem
//                    String stem = analysisResults.get(0).getStem();
//                    
//                    // Check for invalid analysis marker ("#")
//                    if (stem != null && !stem.equals("#")) {
//                        segmentedTokens.add(stem);
//                    } else {
//                        // If no valid stem is found, keep the original word as the segment
//                        segmentedTokens.add(word); 
//                    }
//                } else {
//                    // If no analysis is returned, keep the original word
//                    segmentedTokens.add(word);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("An error occurred during segmentation: " + e.getMessage());
//            e.printStackTrace();
//            // In case of a failure, return the original tokens
//            return tokens; 
//        }
//
//        return segmentedTokens;
//    }
//       public static String getThreeLetterRoot(String word) {
//
//        if (word == null || word.trim().isEmpty()) {
//            return word;
//        }
//
//        String clean = word.trim();
//
//        try {
//            // Main analysis
//            ResultList resultList = AlKhalil2Analyzer.getInstance().processToken(clean);
//
//            if (resultList.isAnalyzed()) {
//
//                // STEP 1 — Try all roots (List<String>)
//                List<String> allRoots = resultList.getAllRoots();
//                if (allRoots != null && !allRoots.isEmpty()) {
//
//                    for (String root : allRoots) {
//                        if (root != null && !root.equals("#") && root.length() == 3) {
//                            return root;   // First valid 3-letter root
//                        }
//                    }
//                }
//
//                // STEP 2 — Check individual results
//                List<Result> results = resultList.getAllResults();
//                if (results != null) {
//                    for (Result r : results) {
//                        String root = r.getRoot();
//                        if (root != null && !root.equals("#") && root.length() == 3) {
//                            return root;
//                        }
//                    }
//                }
//            }
//
//            // STEP 3 — Fallback: analyzerToken()
//            List<Result> altResults = AlKhalil2Analyzer.getInstance().analyzerToken(clean);
//            if (altResults != null) {
//                for (Result r : altResults) {
//                    String root = r.getRoot();
//                    if (root != null && !root.equals("#") && root.length() == 3) {
//                        return root;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error extracting root for '" + clean + "': " + e.getMessage());
//        }
//
//        // No 3-letter root found → return original word
//        return clean;
//    }
//
//
//       public static String getLemma2(String arabicWord) {
//           if (arabicWord == null || arabicWord.trim().isEmpty()) {
//               return arabicWord;
//           }
//
//           try {
//               ResultList result = AlKhalil2Analyzer.getInstance().processToken(arabicWord);
//
//               if (result != null && result.isAnalyzed()) {
//                   List<String> lemmas = result.getAllLemmas();
//
//                   if (lemmas != null && !lemmas.isEmpty()) {
//                       return lemmas.get(0).trim();   // Best lemma
//                   }
//               }
//
//               return arabicWord.trim();
//
//           } catch (Exception e) {
//               System.err.println("Lemma error for: " + arabicWord);
//               return arabicWord.trim();
//           }
//       }
//
//
//
//// Utility to remove Arabic diacritics
//public static String removeDiacritics(String s) {
//    return s.replaceAll("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]", "");
//}
//
//
//
//
//public static String getPrefix(String token) {
//    if (token == null || token.trim().isEmpty()) return "";
//
//    token = token.trim();
//    String cleanToken = removeDiacritics(token);
//
//    // Step 1: Get stem from analyzer
//    String stem = token;
//    try {
//        List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(token);
//        if (results != null && !results.isEmpty()) {
//            Result r = results.get(0);
//            if (r.getStem() != null && !r.getStem().equals("#") && !r.getStem().isEmpty()) {
//                stem = r.getStem().trim();
//            }
//        }
//    } catch (Exception ignored) {}
//
//    String cleanStem = removeDiacritics(stem);
//
//    // Step 2: Find stem position in token
//    int pos = cleanToken.indexOf(cleanStem);
//
//    if (pos > 0) {
//        // Everything before stem is prefix
//    	String pre=token.substring(0, pos);
//    	System.out.println(pre);
//        return pre;
//    }
//
//    // Step 3: Fallback: try common prefixes if stem matching fails
//    String[] commonPrefixes = {"ال", "و", "ف", "ب", "ك", "ل", "س"};
//    for (String p : commonPrefixes) {
//        if (cleanToken.startsWith(removeDiacritics(p)) && token.length() > p.length()) {
//        	String prefix=token.substring(0, p.length());
//        	System.out.println(prefix);
//            return prefix;
//        }
//    }
//
//    // No prefix found
//    return "";
//}
//public static String getStem(String token) {
//    if (token == null || token.trim().isEmpty()) return "";
//
//    token = token.trim();
//    String stem = token; // fallback if analyzer fails
//
//    try {
//        List<Result> results = AlKhalil2Analyzer.getInstance().analyzerToken(token);
//        if (results != null && !results.isEmpty()) {
//            Result r = results.get(0);
//            if (r.getStem() != null && !r.getStem().equals("#") && !r.getStem().isEmpty()) {
//                stem = r.getStem().trim();
//            }
//        }
//    } catch (Exception e) {
//        System.err.println("Analyzer error: " + e.getMessage());
//    }
//
//    // Remove prefix before returning stem
//    String prefix = getPrefix(token);
//    if (!prefix.isEmpty() && token.startsWith(prefix)) {
//        String remaining = token.substring(prefix.length());
//        if (remaining.endsWith(stem)) {
//            return stem;
//        } else {
//            return remaining; // fallback
//        }
//    }
//
//    return stem;
//}
//
//
//
//    public static void main(String[] args)  {
////        String word = "استكتبوا";
////        String lemma = getLemma2(word);
////        System.out.println("Word  : " + word);
////        System.out.println("Lemma : " + lemma);
////        String arabicSentence = "ذهب الطالب إلى المدرسة ثم الطالب رجع استكتبوا"; 
////        
////        List<String> tokens = analyzeAndSegmentSentence(arabicSentence);
////        
////        if (!tokens.isEmpty()) {
////            System.out.println("\nOriginal Sentence: " + arabicSentence);
////            System.out.println("--- Generated Tokens (Including Duplicates) ---");
////            System.out.println("size : "+ tokens.size());
////            for (String token : tokens) {
////                System.out.println("- " + token);
////            }
////            System.out.println("----------------------------------------------");
////        } else {
////             System.out.println("Tokenization failed or resulted in an empty list.");
////        }
////        System.out.println("root المدرسة:"+getThreeLetterRoot("المدرسة"));
//    	System.out.println(getPrefix("الطالب"));
//    	System.out.println(getStem("الطالب"));
//    }
//}
//
