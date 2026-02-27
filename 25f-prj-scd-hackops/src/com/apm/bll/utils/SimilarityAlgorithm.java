package com.apm.bll.utils;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class SimilarityAlgorithm {

//    public static void main(String[] args) {
//        String s1 = "فَسَلَّمَ الدَّنَانِيرَ إِلَيْهَا، وَضَرَبَ عُنُقَ الأَسْوَدِ، وَأَمَرَ أَنْ يُوضَعَ فِي الأَتُّونِ.\n"
//        		+ "";
//        String s2 = "ف فَنَادَى فِي المَدِينَةِ، فَحَضَرَتِ امْرَأَتُهُ، وَقَالَت: «هَذَا زَوْجِي، وَقَدْ تَرَكَ طِفْلاً صَغِيرًا، خَرَجَ فِي وَقْتِ كَذَا وَمَعَهُ كِيسٌ فِيهِ أَلْفُ دِينَارٍ، فَغَابَ إِلَى الآنَ.» فَسَلَّمَ الدَّنَانِيرَ إِلَيْهَا، وَضَرَبَ عُنُقَ الأَسْوَدِ، وَأَمَرَ أَنْ يُوضَعَ فِي الأَتُّونِ.\n"
//        		+ "";
//
//        double perc = jaccardCharNGram(s1, s2, 3);
//        System.out.printf("Jaccard (char 3-gram) = %.4f%%\n", perc);
//    }

   
    public static double jaccardCharNGram(String s1, String s2, int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be >= 1");

        String a = normalizeArabic(s1);
        String b = normalizeArabic(s2);

        Set<String> ngrams1 = getCharNGrams(a, n);
        Set<String> ngrams2 = getCharNGrams(b, n);

        // Intersection
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);

        // Union
        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);

        if (union.isEmpty()) return 0.0;

        // Important: compute using double to avoid integer division+
        return (double) (intersection.size()) / union.size();
    }

    // Helper function to generate character N-grams
    public static Set<String> getCharNGrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();

        if (text == null || text.isEmpty()) return ngrams;

        // If you prefer to ignore spaces inside n-grams, uncomment next line:
        // text = text.replaceAll("\\s+", "");

        if (text.length() < n) return ngrams;

        for (int i = 0; i <= text.length() - n; i++) {
            ngrams.add(text.substring(i, i + n));
        }
        return ngrams;
    }

    /**
     * Normalize Arabic text:
     * - Unicode NFKD then remove combining marks (diacritics/tashkeel)
     * - Normalize Alef variants to 'ا'
     * - Map ى to ي
     * - Remove tatweel (kashida)
     * - Remove non-Arabic characters except spaces (removes punctuation, Latin letters, numbers, etc.)
     * - Collapse multiple spaces and trim
     */
    private static String normalizeArabic(String text) {
        if (text == null) return "";

        // Unicode decomposition so we can remove combining marks
        String s = Normalizer.normalize(text, Normalizer.Form.NFKD);

        // remove combining diacritic marks (harakat / tashkeel)
        s = s.replaceAll("\\p{M}", "");

        // remove tatweel (kashida)
        s = s.replace("\u0640", "");

        // Normalize alef variants to bare alef (ا)
        s = s.replaceAll("[\\u0622\\u0623\\u0625\\u0671]", "\u0627");

        // Map alef maksura (ى) to ya (ي)
        s = s.replace('ى', 'ي');

      

        // Remove anything that's not Arabic script or whitespace
        s = s.replaceAll("[^\\p{IsArabic}\\s]", " ");

        // Collapse multiple spaces and trim
        s = s.replaceAll("\\s+", " ").trim();

        return s;
    }
}
