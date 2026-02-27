package main.java.com.apm.bll.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.ResultList;

public class LemmaUtil {
    private static final Pattern LEMMA_PATTERN =
            Pattern.compile("<lemma>\\s*(.+?)\\s*</lemma>", Pattern.DOTALL);
    
    
    
    
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
    
    
}
