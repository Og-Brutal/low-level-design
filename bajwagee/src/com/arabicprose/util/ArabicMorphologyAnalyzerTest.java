package com.arabicprose.util;

/**
 * Test class for ArabicMorphologyAnalyzer
 * Demonstrates usage of the analyzer.
 */
public class ArabicMorphologyAnalyzerTest {
    
    public static void main(String[] args) {
        // Test with the example word
        String word = "وبكتابهم";
        System.out.println("Input word: " + word);
        System.out.println("\nOutput:");
        System.out.println(ArabicMorphologyAnalyzer.analyzeWord(word));
        
        // Test with other examples
        System.out.println("\n\n=== Additional Tests ===");
        
        String[] testWords = {
            "كتاب",
            "بالكتاب",
            "كتابه",
            "فالكتاب",
            "وكتابهم"
        };
        
        for (String testWord : testWords) {
            System.out.println("\nInput word: " + testWord);
            System.out.println(ArabicMorphologyAnalyzer.analyzeWord(testWord));
        }
    }
}

