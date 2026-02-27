package com.arabicprose.bll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.arabicprose.dal.IAnalysisResultDAO;
import com.arabicprose.dal.ILemmatizationDAO;
import com.arabicprose.dal.IRootDAO;
import com.arabicprose.dal.ISegmentationDAO;
import com.arabicprose.dal.ITokenizationDAO;
import com.arabicprose.dto.AnalysisResultDTO;
import com.arabicprose.dto.FrequencyDTO;
import com.arabicprose.dto.LemmatizationDTO;
import com.arabicprose.dto.RootDTO;
import com.arabicprose.dto.SegmentationDTO;
import com.arabicprose.dto.TokenizationDTO;

import net.oujda_nlp_team.AlKhalil2Analyzer;
import net.oujda_nlp_team.entity.Result;
import net.oujda_nlp_team.entity.ResultList;
import net.oujda_nlp_team.entity.Segment;
import net.oujda_nlp_team.util.ArabicStringUtil;
import net.oujda_nlp_team.util.Stemming;
import net.oujda_nlp_team.util.Transliteration;

public class MorphologicalAnalysisBO implements IMorphologicalAnalysisBO {

    /* ---------- DAOs ---------- */
    private final IAnalysisResultDAO analysisResultDAO;
    private final ITokenizationDAO tokenizationDAO;
    private final ILemmatizationDAO lemmatizationDAO;
    private final ISegmentationDAO segmentationDAO;
    private final IRootDAO rootDAO;

    public MorphologicalAnalysisBO(IAnalysisResultDAO analysisResultDAO,
                                   ITokenizationDAO tokenizationDAO,
                                   ILemmatizationDAO lemmatizationDAO,
                                   ISegmentationDAO segmentationDAO,
                                   IRootDAO rootDAO) {
        this.analysisResultDAO = analysisResultDAO;
        this.tokenizationDAO = tokenizationDAO;
        this.lemmatizationDAO = lemmatizationDAO;
        this.segmentationDAO = segmentationDAO;
        this.rootDAO = rootDAO;
    }

    // ==================== NEW INDEXING METHODS ====================

    @Override
    public List<LemmatizationDTO> getLemmasByRootId(int rootId) throws SQLException {
        return lemmatizationDAO.getLemmasByRootId(rootId);
    }

    @Override
    public List<LemmatizationDTO> getLemmasByRootValue(String rootValue) throws SQLException {
    	System.out.println("lemma for this root is : "+rootValue);
    	RootDTO root=rootDAO.getRootByValue(rootValue);
    	List<LemmatizationDTO> lemmalist =lemmatizationDAO.getLemmasByRootId(root.getRootId());
    	System.out.println("lemma for this root is : ");
    	System.out.println(lemmalist);
        return lemmalist;
    }
    public RootDTO getRootByValue(String rootValue) throws SQLException {
        return rootDAO.getRootByValue(rootValue);
    }


    @Override
    public List<LemmatizationDTO> getLemmatizationsByAnalysis(int analysisId) throws SQLException {
        return lemmatizationDAO.getLemmasByAnalysisId(analysisId);
    }

    @Override
    public List<RootDTO> getRootsByAnalysis(int analysisId) throws SQLException {
        return rootDAO.getRootsByAnalysisId(analysisId);
    }

    // ==================== FREQUENCY ANALYSIS METHODS ====================

    @Override
    public List<FrequencyDTO> getTokenFrequencies(int bookId) throws SQLException {
        return tokenizationDAO.getTokenFrequencies(bookId);
    }

    @Override
    public List<FrequencyDTO> getLemmaFrequencies(int bookId) throws SQLException {
        return lemmatizationDAO.getLemmaFrequencies(bookId);
    }

    @Override
    public List<FrequencyDTO> getRootFrequencies(int bookId) throws SQLException {
        return rootDAO.getRootFrequencies(bookId);
    }

    @Override
    public List<FrequencyDTO> getTokenFrequenciesByChapter(int chapterId) throws SQLException {
        return tokenizationDAO.getTokenFrequenciesByChapter(chapterId);
    }

    @Override
    public List<FrequencyDTO> getLemmaFrequenciesByChapter(int chapterId) throws SQLException {
        return lemmatizationDAO.getLemmaFrequenciesByChapter(chapterId);
    }

    @Override
    public List<FrequencyDTO> getRootFrequenciesByChapter(int chapterId) throws SQLException {
        return rootDAO.getRootFrequenciesByChapter(chapterId);
    }

    @Override
    public List<FrequencyDTO> getTopTokens(int bookId, int limit) throws SQLException {
        List<FrequencyDTO> allTokens = getTokenFrequencies(bookId);
        return allTokens.subList(0, Math.min(limit, allTokens.size()));
    }

    @Override
    public List<FrequencyDTO> getTopLemmas(int bookId, int limit) throws SQLException {
        List<FrequencyDTO> allLemmas = getLemmaFrequencies(bookId);
        return allLemmas.subList(0, Math.min(limit, allLemmas.size()));
    }

    @Override
    public List<FrequencyDTO> getTopRoots(int bookId, int limit) throws SQLException {
        List<FrequencyDTO> allRoots = getRootFrequencies(bookId);
        return allRoots.subList(0, Math.min(limit, allRoots.size()));
    }

    @Override
    public List<FrequencyDTO> getTopTokensByChapter(int chapterId, int limit) throws SQLException {
        List<FrequencyDTO> allTokens = getTokenFrequenciesByChapter(chapterId);
        return allTokens.subList(0, Math.min(limit, allTokens.size()));
    }

    @Override
    public List<FrequencyDTO> getTopLemmasByChapter(int chapterId, int limit) throws SQLException {
        List<FrequencyDTO> allLemmas = getLemmaFrequenciesByChapter(chapterId);
        return allLemmas.subList(0, Math.min(limit, allLemmas.size()));
    }

    @Override
    public List<FrequencyDTO> getTopRootsByChapter(int chapterId, int limit) throws SQLException {
        List<FrequencyDTO> allRoots = getRootFrequenciesByChapter(chapterId);
        return allRoots.subList(0, Math.min(limit, allRoots.size()));
    }

    @Override
    public Object[] getBookStatistics(int bookId) throws SQLException {
        List<FrequencyDTO> tokens = getTokenFrequencies(bookId);
        List<FrequencyDTO> lemmas = getLemmaFrequencies(bookId);
        List<FrequencyDTO> roots = getRootFrequencies(bookId);
        
        int totalTokens = tokens.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        int totalLemmas = lemmas.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        int totalRoots = roots.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        
        int uniqueTokens = tokens.size();
        int uniqueLemmas = lemmas.size();
        int uniqueRoots = roots.size();
        
        return new Object[]{totalTokens, totalLemmas, totalRoots, uniqueTokens, uniqueLemmas, uniqueRoots};
    }

    @Override
    public Object[] getChapterStatistics(int chapterId) throws SQLException {
        List<FrequencyDTO> tokens = getTokenFrequenciesByChapter(chapterId);
        List<FrequencyDTO> lemmas = getLemmaFrequenciesByChapter(chapterId);
        List<FrequencyDTO> roots = getRootFrequenciesByChapter(chapterId);
        
        int totalTokens = tokens.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        int totalLemmas = lemmas.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        int totalRoots = roots.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        
        int uniqueTokens = tokens.size();
        int uniqueLemmas = lemmas.size();
        int uniqueRoots = roots.size();
        
        return new Object[]{totalTokens, totalLemmas, totalRoots, uniqueTokens, uniqueLemmas, uniqueRoots};
    }

    // ==================== EXISTING METHODS (unchanged) ====================

    /* ====================== NEW METHOD: LEMMA → ROOTS ====================== */
    /**
     * Given an Arabic lemma (e.g., "كتاب"), returns all possible roots in format:
     * ك - ت - ب (K–T–B)
     *
     * @param lemma Arabic word (can be diacritized or not)
     * @return List of formatted root strings
     */
    @Override
    public List<String> getRootsFromLemma(String lemma) {
        List<String> formattedRoots = new ArrayList<>();

        if (lemma == null || lemma.trim().isEmpty()) {
            return formattedRoots;
        }

        String input = lemma.trim();
        System.out.println("=== EXTRACTING ROOTS FOR LEMMA: " + input + " ===");

        try {
            // Step 1: Run AlKhalil-2 morphological analysis
            ResultList resultList = AlKhalil2Analyzer.getInstance().processToken(input);

            if (!resultList.isAnalyzed() || resultList.size() == 0) {
                System.out.println("No morphological analysis found for: " + input);
                return formattedRoots;
            }

            // Step 2: Collect all unique roots (in Buckwalter)
            Set<String> rootSet = new HashSet<>();
            for (Result result : resultList.getAllResults()) {
                String root = result.getRoot();
                if (root != null && !root.trim().isEmpty()) {
                    rootSet.add(root.trim());
                }
            }

            // Step 3: Convert each root to formatted Arabic + Buckwalter
            for (String rootBW : rootSet) {
                String rootArabic = Transliteration.getInstance().getBuckWalterToArabic(rootBW);

                StringBuilder arabicPart = new StringBuilder();
                StringBuilder bwPart = new StringBuilder();

                for (int i = 0; i < rootArabic.length(); i++) {
                    char ar = rootArabic.charAt(i);
                    char bw = rootBW.charAt(i);

                    if (i > 0) {
                        arabicPart.append(" - ");
                        bwPart.append("–");
                    }
                    arabicPart.append(ar);
                    bwPart.append(bw);
                }

                String formatted = arabicPart + " (" + bwPart + ")";
                formattedRoots.add(formatted);
                System.out.println("Root found: " + formatted);
            }

        } catch (Exception e) {
            System.err.println("Error extracting roots for lemma '" + input + "': " + e.getMessage());
            e.printStackTrace();
        }

        return formattedRoots;
    }

    /* ====================== ORIGINAL METHODS (with database connection fixes) ====================== */
    @Override
    public AnalysisResultDTO analyzeSentence(int sentenceId, String text) throws SQLException {
        return performFullAnalysis(sentenceId, text);
    }

    @Override
    public AnalysisResultDTO analyzeTextAutomatically(int sentenceId, String text) throws SQLException {
        return performFullAnalysis(sentenceId, text);
    }

    @Override
    public AnalysisResultDTO analyzeSingleSentence(int sentenceId, String text) throws SQLException {
        System.out.println("=== ANALYZING SINGLE SENTENCE ===");
        System.out.println("Sentence ID: " + sentenceId);
        System.out.println("Text: " + text);

        if (hasAnalysis(sentenceId)) {
            System.out.println("Analysis already exists for sentence " + sentenceId);
            return getAnalysisHistory(sentenceId).get(0);
        }
        return performFullAnalysis(sentenceId, text);
    }

    @Override
    public boolean hasAnalysis(int sentenceId) throws SQLException {
        return !getAnalysisHistory(sentenceId).isEmpty();
    }

    @Override
    public List<TokenizationDTO> getAllTokensByAnalysis(int analysisId) throws SQLException {
        return tokenizationDAO.getTokensByAnalysisId(analysisId);
    }

    /* ====================== FIXED: SINGLE CONNECTION FOR ENTIRE ANALYSIS ====================== */
    private AnalysisResultDTO performFullAnalysis(int sentenceId, String text) throws SQLException {
        System.out.println("=== STARTING MORPHOLOGICAL ANALYSIS ===");
        System.out.println("Sentence ID: " + sentenceId);
        System.out.println("Text: " + text);

        Connection conn = null;

        try {
            // Direct connection — no external DatabaseConnection class needed
            String URL = "jdbc:mysql://localhost:3306/arabic_prose_db";  // Change to your DB name
            String USER = "root";                                           // Change if needed
            String PASS = "";                                  // Change to your password

            conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);  // Start transaction

            System.out.println("Database connected successfully");

            // 1. Create analysis record
            AnalysisResultDTO analysis = new AnalysisResultDTO();
            analysis.setSentenceId(sentenceId);
            analysis.setOriginalText(text);
            analysisResultDAO.addAnalysisResult(analysis);  // This should use the same conn if DAO supports it
            System.out.println("Analysis created with ID: " + analysis.getAnalysisId());

            // 2. Tokenize the sentence
            List<String> tokens = tokenizeSentence(text);
            System.out.println("Tokenized into " + tokens.size() + " tokens: " + tokens);

            // 3. Process each token
            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                System.out.println("\n--- Processing Token " + (i + 1) + ": '" + token + "' ---");

                TokenizationDTO tokenDTO = new TokenizationDTO();
                tokenDTO.setAnalysisId(analysis.getAnalysisId());
                tokenDTO.setToken(token);
                tokenDTO.setPosition(i);

                tokenizationDAO.addToken(tokenDTO);  // Make sure your DAO uses the same connection!
                System.out.println("Token saved with ID: " + tokenDTO.getTokenId());

                // Perform full morphological analysis (root, lemma, POS, etc.)
                performCompleteAnalysis(conn, tokenDTO);
            }

            // Everything succeeded → commit
            conn.commit();
            System.out.println("=== MORPHOLOGICAL ANALYSIS COMPLETED SUCCESSFULLY ===");
            return analysis;

        } catch (Exception e) {
            // Any error → rollback everything
            System.err.println("=== ANALYSIS FAILED — ROLLING BACK ===");
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back successfully");
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Morphological analysis failed: " + e.getMessage(), e);

        } finally {
            // Always close connection safely
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /* ====================== UPDATED: All analysis methods now accept connection ====================== */
    private void performCompleteAnalysis(Connection conn, TokenizationDTO tokenDTO) throws SQLException {
        String token = tokenDTO.getToken();
        System.out.println("Analyzing token: '" + token + "'");

        try {
            String unvoweled = ArabicStringUtil.getInstance().removeAllDiacriticsOfWord(token);
            System.out.println("Unvoweled word: " + unvoweled);

            ResultList resultList = AlKhalil2Analyzer.getInstance().processToken(unvoweled);
            System.out.println("Found " + resultList.size() + " analysis results");

            if (resultList.size() > 0 && resultList.isAnalyzed()) {
                List<Result> results = resultList.getAllResults();
                Result primary = results.get(0);

                storeLemma(conn, tokenDTO, primary);
                storeRootsFromResultList(conn, tokenDTO, results);
                storeSegmentation(conn, tokenDTO, primary);
                storeAdditionalSegments(conn, tokenDTO, unvoweled);
            } else {
                fallbackAnalysis(conn, tokenDTO, token);
            }
        } catch (Exception e) {
            System.err.println("Error analyzing token '" + token + "': " + e.getMessage());
            e.printStackTrace();
            fallbackAnalysis(conn, tokenDTO, token);
        }
    }

    private void fallbackAnalysis(Connection conn, TokenizationDTO tokenDTO, String token) throws SQLException {
        storeBasicSegmentation(conn, tokenDTO, token);
        storeFallbackRoot(conn, tokenDTO, token);
        storeLemmaFromString(conn, tokenDTO, token);
    }

    private void storeLemma(Connection conn, TokenizationDTO tokenDTO, Result result) throws SQLException {
        String lemma = result.getLemma();
        LemmatizationDTO dto = new LemmatizationDTO();
        dto.setTokenId(tokenDTO.getTokenId());
        dto.setLemma(lemma != null && !lemma.trim().isEmpty() ? lemma : tokenDTO.getToken());
        dto.setConfidence(lemma != null && !lemma.trim().isEmpty() ? 0.95 : 0.1);
        lemmatizationDAO.addLemma(dto);
        System.out.println("Stored lemma: " + dto.getLemma() + " (conf " + dto.getConfidence() + ")");
    }

    private void storeLemmaFromString(Connection conn, TokenizationDTO tokenDTO, String lemma) throws SQLException {
        LemmatizationDTO dto = new LemmatizationDTO();
        dto.setTokenId(tokenDTO.getTokenId());
        dto.setLemma(lemma);
        dto.setConfidence(0.1);
        lemmatizationDAO.addLemma(dto);
        System.out.println("Stored fallback lemma: " + lemma);
    }

    private void storeRootsFromResultList(Connection conn, TokenizationDTO tokenDTO, List<Result> results) throws SQLException {
        int idx = 0;
        for (Result r : results) {
            String root = r.getRoot();
            if (root != null && !root.trim().isEmpty()) {
                RootDTO dto = new RootDTO();
                dto.setTokenId(tokenDTO.getTokenId());
                dto.setRoot(root);
                double conf = Math.max(0.1, 1.0 - idx * 0.1);
                dto.setConfidence(conf);
                rootDAO.addRoot(dto);
                System.out.println("Stored root: " + root + " (conf " + conf + ")");
            }
            idx++;
            if (idx >= 10) break;
        }
    }

    private void storeFallbackRoot(Connection conn, TokenizationDTO tokenDTO, String token) throws SQLException {
        RootDTO dto = new RootDTO();
        dto.setTokenId(tokenDTO.getTokenId());
        dto.setRoot(token);
        dto.setConfidence(0.1);
        rootDAO.addRoot(dto);
        System.out.println("Stored fallback root: " + token);
    }

    private void storeSegmentation(Connection conn, TokenizationDTO tokenDTO, Result result) throws SQLException {
        SegmentationDTO seg = new SegmentationDTO();
        seg.setTokenId(tokenDTO.getTokenId());

        String prefix = result.getProcliticNoDec();
        String stem = result.getStem();
        String suffix = result.getEncliticNoDec();

        // Clean clitic values to remove object references
        prefix = cleanCliticValue(prefix);
        stem = cleanValue(stem);
        suffix = cleanCliticValue(suffix);

        // Always set values (use empty string instead of null) to prevent ArrayIndexOutOfBoundsException
        seg.setPrefix((prefix != null && !prefix.trim().isEmpty()) ? prefix : "");
        seg.setStem((stem != null && !stem.trim().isEmpty()) ? stem : "");
        seg.setSuffix((suffix != null && !suffix.trim().isEmpty()) ? suffix : "");

        // Store segmentation if at least one part exists
        if (seg.hasPrefix() || seg.hasStem() || seg.hasSuffix()) {
            segmentationDAO.addSegmentation(seg);
            System.out.println("Stored segmentation – P:'" + (prefix != null ? prefix : "") + 
                             "' S:'" + (stem != null ? stem : "") + 
                             "' E:'" + (suffix != null ? suffix : "") + "'");
        } else {
            System.out.println("Warning: Skipping empty segmentation for token ID: " + tokenDTO.getTokenId());
        }
    }

    private void storeAdditionalSegments(Connection conn, TokenizationDTO tokenDTO, String unvoweled) throws SQLException {
        try {
            @SuppressWarnings("unchecked")
            List<Segment> segs = (List<Segment>) Stemming.getInstance().getListsSegment(unvoweled);
            if (!segs.isEmpty() && segs.get(0) != null) {
                Segment s = segs.get(0);
                SegmentationDTO alt = new SegmentationDTO();
                alt.setTokenId(tokenDTO.getTokenId());

                // Extract and clean clitic values properly
                String p = s.getProclitic() != null ? cleanCliticValue(s.getProclitic().toString()) : "";
                String m = s.getStem() != null ? cleanValue(s.getStem().toString()) : "";
                String e = s.getEnclitic() != null ? cleanCliticValue(s.getEnclitic().toString()) : "";

                // Always set values (use empty string instead of null) to prevent ArrayIndexOutOfBoundsException
                alt.setPrefix((p != null && !p.trim().isEmpty()) ? p : "");
                alt.setStem((m != null && !m.trim().isEmpty()) ? m : "");
                alt.setSuffix((e != null && !e.trim().isEmpty()) ? e : "");

                // Store segmentation if at least one part exists
                if (alt.hasPrefix() || alt.hasStem() || alt.hasSuffix()) {
                    segmentationDAO.addSegmentation(alt);
                    System.out.println("Stored alt seg – P:'" + p + "' S:'" + m + "' E:'" + e + "'");
                } else {
                    System.out.println("Warning: Skipping empty additional segmentation for token ID: " + tokenDTO.getTokenId());
                }
            }
        } catch (Exception ex) {
            System.err.println("Additional segments error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void storeBasicSegmentation(Connection conn, TokenizationDTO tokenDTO, String token) throws SQLException {
        SegmentationDTO seg = new SegmentationDTO();
        seg.setTokenId(tokenDTO.getTokenId());
        // Always set all three parts (use empty strings for missing parts)
        seg.setPrefix("");
        seg.setStem((token != null && !token.trim().isEmpty()) ? token : "");
        seg.setSuffix("");
        segmentationDAO.addSegmentation(seg);
        System.out.println("Stored basic segmentation (stem = token): " + token);
    }

    /* ====================== TOKENIZATION (unchanged) ====================== */
    private List<String> tokenizeSentence(String text) {
        List<String> tokens = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(ArabicStringUtil.getInstance().getPatternCompile());
            text = text.replaceAll("ـ", "");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                String word = matcher.group();
                if (!isSeparator(word.charAt(0))) {
                    word = ArabicStringUtil.getInstance().correctErreur(word);
                    tokens.add(word);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in tokenization: " + e.getMessage());
            String[] fb = text.split("\\s+");
            for (String t : fb) {
                if (!t.trim().isEmpty() && !isSeparator(t.charAt(0))) {
                    tokens.add(t.trim());
                }
            }
        }
        return tokens;
    }

    private boolean isSeparator(char c) {
        return !Character.isLetterOrDigit(c) && c != 'ـ' && !(c >= '\u0600' && c <= '\u06FF');
    }
    
    /**
     * Cleans clitic values by removing Java object references like "net.oujda_nlp_team.entity.Clitic@3fe46a03"
     * and extracting only the Arabic text.
     */
    private String cleanCliticValue(String clitic) {
        if (clitic == null || clitic.trim().isEmpty()) {
            return null;
        }
        
        String cleaned = clitic.trim();
        
        // Remove object references like "net.oujda_nlp_team.entity.Clitic@3fe46a03"
        if (cleaned.contains("net.oujda") || cleaned.contains("Clitic@") || cleaned.contains(".entity.")) {
            // Extract only Arabic characters
            Pattern arabicPattern = Pattern.compile("[\\u0600-\\u06FF]+");
            java.util.regex.Matcher matcher = arabicPattern.matcher(cleaned);
            if (matcher.find()) {
                cleaned = matcher.group();
            } else {
                return null;
            }
        }
        
        // Remove Clitic[ and ] patterns
        cleaned = cleaned.replaceAll("^Clitic\\[?", "")
                         .replaceAll("\\]$", "")
                         .trim();
        
        return cleaned.isEmpty() ? null : cleaned;
    }
    
    /**
     * Cleans general values by removing Java object references and extracting Arabic text.
     */
    private String cleanValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String cleaned = value.trim();
        
        // Remove object references
        if (cleaned.contains("net.oujda") || cleaned.contains("Clitic@") || cleaned.contains(".entity.")) {
            // Extract only Arabic characters
            Pattern arabicPattern = Pattern.compile("[\\u0600-\\u06FF]+");
            java.util.regex.Matcher matcher = arabicPattern.matcher(cleaned);
            if (matcher.find()) {
                cleaned = matcher.group();
            } else {
                return null;
            }
        }
        
        // Remove Clitic[ and ] patterns
        cleaned = cleaned.replaceAll("^Clitic\\[?", "")
                         .replaceAll("\\]$", "")
                         .trim();
        
        return cleaned.isEmpty() ? null : cleaned;
    }

    /* ---------- Retrieval Helpers ---------- */
    @Override public List<TokenizationDTO> getAllTokens() throws SQLException { return tokenizationDAO.getAllTokens(); }
    @Override public List<LemmatizationDTO> getAllLemmas() throws SQLException { return lemmatizationDAO.getAllLemmas(); }
    @Override public List<RootDTO> getAllRoots() throws SQLException { return rootDAO.getAllRoots(); }
    @Override public List<SegmentationDTO> getAllSegmentations() throws SQLException { return segmentationDAO.getAllSegmentations(); }
    @Override public List<LemmatizationDTO> getLemmasByValue(String lemma) throws SQLException { return lemmatizationDAO.getLemmasByValue(lemma); }
    @Override public List<RootDTO> getRootsByValue(String root) throws SQLException { return rootDAO.getRootsByValue(root); }
    @Override public List<TokenizationDTO> getTokensByValue(String token) throws SQLException { return tokenizationDAO.getTokensByValue(token); }
    @Override public List<AnalysisResultDTO> getAnalysisHistory(int sentenceId) throws SQLException { return analysisResultDAO.getAnalysisResultsBySentenceId(sentenceId); }
    @Override public void deleteAnalysis(int analysisId) throws SQLException { analysisResultDAO.deleteAnalysisResult(analysisId); }
    
    @Override
    public String analyzeWordSegmentation(String word) {
        return com.arabicprose.util.ArabicMorphologyAnalyzer.analyzeWord(word);
    }
}