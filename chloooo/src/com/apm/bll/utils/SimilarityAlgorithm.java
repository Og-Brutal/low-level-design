package com.apm.bll.utils;

import java.util.HashSet;
import java.util.Set;

public class SimilarityAlgorithm {

    public static double jaccardCharNGram(String s1, String s2, int n) {
        if (s1 == null || s2 == null || n <= 0) return 0.0;

        // Remove extra spaces
        s1 = s1.replaceAll("\\s+", "");
        s2 = s2.replaceAll("\\s+", "");

        Set<String> ngrams1 = getCharNGrams(s1, n);
        Set<String> ngrams2 = getCharNGrams(s2, n);

        // Intersection
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        // Union
        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        if (union.size() == 0) return 0.0;

        return ((double) intersection.size() / union.size()) * 100;
    }

    private static Set<String> getCharNGrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        if (text.length() < n) return ngrams;

        for (int i = 0; i <= text.length() - n; i++) {
            ngrams.add(text.substring(i, i + n));
        }
        return ngrams;
    }

    public static void main(String[] args) {
        String sentence1 = "اللغة العربية جميلة";
        String sentence2 = "العربية لغة جميلة";

        int n = 2; // character bigrams
        double similarity = jaccardCharNGram(sentence1, sentence2, n);

        System.out.println("Jaccard similarity between sentences: " + similarity + "%");
    }
}
