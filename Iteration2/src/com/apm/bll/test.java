package com.apm.bll;

import net.oujda_nlp_team.ADATAnalyzer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.*;

public class test {

    private static final Pattern LEMMA_PATTERN =
            Pattern.compile("<lemma>\\s*(.+?)\\s*</lemma>", Pattern.DOTALL);

    public static String getLemma(String arabicWord) throws IOException {
        File in  = File.createTempFile("adat_in_", ".txt");
        File out = File.createTempFile("adat_out_", ".xml");
        in.deleteOnExit();
        out.deleteOnExit();

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(in), StandardCharsets.UTF_8))) {
            pw.print(arabicWord);
        }

        ADATAnalyzer.getInstance()
                .processLemmatization(
                        in.getAbsolutePath(), "UTF-8",
                        out.getAbsolutePath(), "UTF-8");

        String xml = new String(
                Files.readAllBytes(out.toPath()), StandardCharsets.UTF_8);

        Matcher m = LEMMA_PATTERN.matcher(xml);
        return m.find() ? m.group(1).trim() : arabicWord;
    }

    public static void main(String[] args) throws IOException {
        String word = "استكتبوا";
        String lemma = getLemma(word);
        System.out.println("Word  : " + word);
        System.out.println("Lemma : " + lemma);
    }
}
