package com.apm.pl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher; // NEW IMPORT
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
// import javax.swing.text.StyleSheet; // Kept commented out to avoid compilation issues

import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.SentenceDTO;
import com.apm.dto.TokenDTO;

/**
 * Professional Morphological Browser
 * Features: Data Aggregation (Frequency counts), Drill-down navigation, Material Design.
 */
public class MorphologicalBrowserPanel extends JPanel {

    // --- Professional Color Palette ---
    private static final Color BG_COLOR = new Color(245, 247, 250);      // Background
    private static final Color HEADER_BG = Color.WHITE;                  // Header
    private static final Color CARD_BG = Color.WHITE;                    // Card Body
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);  // Brand Blue
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);     // Dark Text
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);// Grey Text
    private static final Color BORDER_COLOR = new Color(224, 224, 224);  // Borders
    private static final Color BADGE_BG = new Color(231, 76, 60);        // Red for frequency
    private static final Color BADGE_TEXT = Color.WHITE;
    private static final Color SENTENCE_HL_COLOR = new Color(255, 230, 150); // Yellow for highlight
    private static final Color META_BG_COLOR = new Color(240, 240, 240); // Light grey for metadata

    private final IBussinessLayerFasade bl;
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    private final CardSelectionManager selectionManager = new CardSelectionManager();

    public MorphologicalBrowserPanel(IBussinessLayerFasade bl) {
        this.bl = bl;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(BG_COLOR);

        // Add Main Menu
        cardsPanel.add(createMainMenuPanel(), "MAIN");

        add(cardsPanel, BorderLayout.CENTER);
        cardLayout.show(cardsPanel, "MAIN");
    }

    // ================= MAIN MENU (DASHBOARD) ====================
    private JPanel createMainMenuPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(25, 40, 25, 40)
        ));

        JLabel title = new JLabel("Morphological Dictionary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Browse corpus data by morphology with frequency analysis");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(HEADER_BG);
        textPanel.add(title);
        textPanel.add(subtitle);
        header.add(textPanel, BorderLayout.CENTER);

        // Grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 30, 30));
        grid.setBackground(BG_COLOR);
        grid.setBorder(new EmptyBorder(40, 60, 40, 60));

        grid.add(createDashboardTile("Browse Roots", "View logical roots", "ROOT", this::showRoots));
        grid.add(createDashboardTile("Browse Lemmas", "Dictionary forms", "LEMMA", this::showLemmas));
        grid.add(createDashboardTile("Browse Tokens", "Raw occurrences", "TOKEN", this::showTokens));
        grid.add(createDashboardTile("Segments", "Morphological breakdown", "SEGMENT", this::showSegments));

        container.add(header, BorderLayout.NORTH);
        container.add(new JScrollPane(grid), BorderLayout.CENTER);

        return container;
    }

    private JButton createDashboardTile(String title, String subtitle, String typeCode, Runnable action) {
        JButton tile = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isHover = getModel().isRollover();

                if(isHover) {
                    g2d.setColor(new Color(225, 230, 235));
                    g2d.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 15, 15);
                }

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);

                g2d.setColor(isHover ? PRIMARY_COLOR : BORDER_COLOR);
                g2d.setStroke(new BasicStroke(isHover ? 2f : 1f));
                g2d.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);

                // Accent Bar
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, 6, getHeight()-5, 15, 15);
                g2d.fillRect(4, 0, 2, getHeight()-5);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        tile.setLayout(new GridLayout(3, 1));
        tile.setBorder(new EmptyBorder(20, 25, 20, 20));

        JLabel lblType = new JLabel(typeCode);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblType.setForeground(PRIMARY_COLOR);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_SECONDARY);

        tile.add(lblType);
        tile.add(lblTitle);
        tile.add(lblSub);

        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.setContentAreaFilled(false);
        tile.setFocusPainted(false);
        tile.setBorderPainted(false);
        tile.addActionListener(e -> action.run());

        return tile;
    }

    // ================= DATA PROCESSING (AGGREGATION) ====================
    
    /**
     * Helper to count occurrences of strings in a list.
     */
    private Map<String, Integer> aggregateFrequencies(ArrayList<String> rawList) {
        Map<String, Integer> frequencyMap = new TreeMap<>(); // TreeMap keeps it sorted alphabetically
        if (rawList != null) {
            for (String item : rawList) {
                if (item != null && !item.trim().isEmpty()) {
                    frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
                }
            }
        }
        return frequencyMap;
    }

    // ================= VIEW LOGIC ====================

    private void showRoots() {
        ArrayList<RootDTO> rawRoots = bl.getAllRoots();
        ArrayList<String> rootStrings = new ArrayList<>();
        if(rawRoots != null) for(RootDTO r : rawRoots) rootStrings.add(r.getRoot());
        
        Map<String, Integer> data = aggregateFrequencies(rootStrings);
        
        JPanel panel = createBrowserPage("Root Index", "Total Unique Roots: " + data.size(), "MAIN");
        JPanel grid = createCardGrid();

        data.forEach((rootText, count) -> {
            // Double-click now goes to showSentencesByRoot
            grid.add(createDataCard(rootText, count, "ROOT", () -> showSentencesByRoot(rootText)));
        });

        finalizeBrowserPage(panel, grid, "ROOTS");
    }
    
    /**
     * Shows a list of sentences where the given root is present in any token.
     */
    private void showSentencesByRoot(String rootText) {
        
        // 1. Determine all unique tokens derived from this root (for highlighting)
        Set<String> relatedTokens = new HashSet<>();
        RootDTO rootDTO = bl.getRoot(rootText); 
        
        if (rootDTO != null) {
            
            // NOTE: Assumes getLemmaByRoot takes String (adapting to BL FASADE provided earlier)
            ArrayList<LemmaDTO> lemmas = bl.getLemmaByRoot(rootText); 
            
            if (lemmas != null) {
                for (LemmaDTO lemma : lemmas) {
                    ArrayList<TokenDTO> tokens = bl.getTokensByLemma(lemma.getLemma());
                    
                    if (tokens != null) {
                        for (TokenDTO token : tokens) {
                            if (token.getToken() != null && !token.getToken().isEmpty()) {
                                relatedTokens.add(token.getToken());
                            }
                        }
                    }
                }
            }
        }
        
        // 2. Fetch the SentenceDTOs (Supports metadata display)
        ArrayList<SentenceDTO> sentences = bl.getSentencesByRoot(rootText); 
        
        JPanel panel = createBrowserPage("Sentences", "Instances for Root: " + rootText, "ROOTS");
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        if (sentences != null && !sentences.isEmpty()) {
            for (SentenceDTO sentence : sentences) {
                
                String rawText = sentence.getText();
                String highlightedText = rawText;
                
                // 3. Highlight the specific tokens found in step 1
                for(String token : relatedTokens) {
                    if (rawText.contains(token)) {
                        String pattern = Pattern.quote(token);
                        String highlight = "<span class='highlight'>$0</span>";
                        
                        // FIX: Use Pattern and Matcher for safe replacement to prevent HTML corruption
                        highlightedText = Pattern.compile(pattern, Pattern.LITERAL).matcher(highlightedText).replaceAll(Matcher.quoteReplacement(highlight));
                    }
                }
                
                // Use full card for Root results (has metadata)
                JPanel sentenceCard = createSentenceCard(sentence, highlightedText);
                listPanel.add(sentenceCard);
                listPanel.add(Box.createVerticalStrut(15)); // Increased spacer for professional look
            }
        } else {
            JLabel noData = new JLabel("No sentences found for root: " + rootText);
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noData.setForeground(TEXT_SECONDARY);
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.setBackground(BG_COLOR);
            wrapper.add(noData);
            listPanel.add(wrapper);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scroll, BorderLayout.CENTER);
        
        String cardId = "SENTENCES_BY_ROOT_" + rootText;
        cardsPanel.add(panel, cardId);
        cardLayout.show(cardsPanel, cardId);
    }
    
    /**
     * Shows a list of sentences where the given lemma is present.
     * Uses BL method returning ArrayList<String> (raw text) due to BL limitation.
     */
    private void showSentencesByLemma(String lemmaText) {
        
        // 1. Determine all unique tokens derived from this lemma (for highlighting)
        Set<String> relatedTokens = new HashSet<>();
        
        ArrayList<TokenDTO> tokens = bl.getTokensByLemma(lemmaText);
        if (tokens != null) {
            for (TokenDTO token : tokens) {
                if (token.getToken() != null && !token.getToken().isEmpty()) {
                    relatedTokens.add(token.getToken());
                }
            }
        }
        
        // 2. Fetch the sentences (BL returns raw strings, so metadata is lost)
        ArrayList<String> rawSentences = bl.getSentencesByToken(lemmaText);

        JPanel panel = createBrowserPage("Sentences", "Instances for Lemma: " + lemmaText + " (Metadata not available)", "LEMMAS");
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        if (rawSentences != null && !rawSentences.isEmpty()) {
            for (String rawText : rawSentences) {
                String highlightedText = rawText;
                
                // 3. Highlight the specific tokens found in step 1
                for(String token : relatedTokens) {
                    if (rawText.contains(token)) {
                        String pattern = Pattern.quote(token);
                        String highlight = "<span class='highlight'>$0</span>";
                        
                        // FIX: Use Pattern and Matcher for safe replacement
                        highlightedText = Pattern.compile(pattern, Pattern.LITERAL).matcher(highlightedText).replaceAll(Matcher.quoteReplacement(highlight));
                    }
                }
                
                // Use simple card for raw String results (now correctly uses highlightedText)
                JPanel sentenceCard = createSimpleSentenceCard(highlightedText);
                listPanel.add(sentenceCard);
                listPanel.add(Box.createVerticalStrut(15));
            }
        } else {
            JLabel noData = new JLabel("No sentences found for lemma: " + lemmaText);
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noData.setForeground(TEXT_SECONDARY);
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.setBackground(BG_COLOR);
            wrapper.add(noData);
            listPanel.add(wrapper);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scroll, BorderLayout.CENTER);
        
        String cardId = "SENTENCES_BY_LEMMA_" + lemmaText;
        cardsPanel.add(panel, cardId);
        cardLayout.show(cardsPanel, cardId);
    }
    
    /**
     * Shows a list of sentences where the given token is present.
     * Uses BL method returning ArrayList<String> (raw text) due to BL limitation.
     */
    private void showSentencesByToken(String tokenText) {
        
        // 1. The only token to highlight is the token itself
        
        // 2. Fetch the sentences (BL returns raw strings, so metadata is lost)
        ArrayList<String> rawSentences = bl.getSentencesByToken(tokenText);
        
        JPanel panel = createBrowserPage("Sentences", "Instances for Token: " + tokenText + " (Metadata not available)", "TOKENS");
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG_COLOR);
        listPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        if (rawSentences != null && !rawSentences.isEmpty()) {
            for (String rawText : rawSentences) {
                String highlightedText = rawText;
                
                // Highlight the single token
                String pattern = Pattern.quote(tokenText);
                String highlight = "<span class='highlight'>$0</span>";
                
                // FIX: Use Pattern and Matcher for safe replacement
                highlightedText = Pattern.compile(pattern, Pattern.LITERAL).matcher(highlightedText).replaceAll(Matcher.quoteReplacement(highlight));
                
                // Use simple card for raw String results (now correctly uses highlightedText)
                JPanel sentenceCard = createSimpleSentenceCard(highlightedText);
                listPanel.add(sentenceCard);
                listPanel.add(Box.createVerticalStrut(15));
            }
        } else {
            JLabel noData = new JLabel("No sentences found for token: " + tokenText);
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noData.setForeground(TEXT_SECONDARY);
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            wrapper.setBackground(BG_COLOR);
            wrapper.add(noData);
            listPanel.add(wrapper);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scroll, BorderLayout.CENTER);
        
        String cardId = "SENTENCES_BY_TOKEN_" + tokenText;
        cardsPanel.add(panel, cardId);
        cardLayout.show(cardsPanel, cardId);
    }
    
    // --- MODIFIED: showLemmas (Links to Sentence View) ---
    private void showLemmas() {
        ArrayList<LemmaDTO> rawLemmas = bl.getAllLemmas();
        ArrayList<String> lemmaStrings = new ArrayList<>();
        if(rawLemmas != null) for(LemmaDTO l : rawLemmas) lemmaStrings.add(l.getLemma());

        Map<String, Integer> data = aggregateFrequencies(lemmaStrings);

        JPanel panel = createBrowserPage("Lemma Index", "Total Dictionary Forms: " + data.size(), "MAIN");
        JPanel grid = createCardGrid();

        data.forEach((lemmaText, count) -> {
            // MODIFIED: Double-click now goes to showSentencesByLemma
            grid.add(createDataCard(lemmaText, count, "LEMMA", () -> showSentencesByLemma(lemmaText)));
        });

        finalizeBrowserPage(panel, grid, "LEMMAS");
    }

    // --- MODIFIED: showTokens (Links to Sentence View) ---
    private void showTokens() {
        ArrayList<TokenDTO> rawTokens = bl.getAllTokens();
        ArrayList<String> tokenStrings = new ArrayList<>();
        if(rawTokens != null) for(TokenDTO t : rawTokens) tokenStrings.add(t.getToken());

        Map<String, Integer> data = aggregateFrequencies(tokenStrings);

        JPanel panel = createBrowserPage("Token Index", "Total Unique Tokens: " + data.size(), "MAIN");
        JPanel grid = createCardGrid();

        data.forEach((tokenText, count) -> {
            // MODIFIED: Double-click now goes to showSentencesByToken
            grid.add(createDataCard(tokenText, count, "TOKEN", () -> showSentencesByToken(tokenText)));
        });

        finalizeBrowserPage(panel, grid, "TOKENS");
    }
    
    /**
     * Helper method to create a card for displaying a sentence with full metadata (Only usable for Root search).
     */
    private JPanel createSentenceCard(SentenceDTO sentence, String highlightedText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        
        // Advanced Theme: Clean border
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // --- Center: Sentence Text (JTextPane with HTML) ---
        JTextPane textPane = new JTextPane();
        HTMLEditorKit kit = new HTMLEditorKit();

        // Define CSS styles and inject them directly into the HTML <head>
        String highlightColorHex = Integer.toHexString(SENTENCE_HL_COLOR.getRGB()).substring(2);
        String textColorHex = Integer.toHexString(TEXT_PRIMARY.getRGB()).substring(2);

        String cssStyles = String.format(
            "body { font-family: 'Segoe UI', Tahoma, sans-serif; font-size: 16px; line-height: 1.8; color: #%s; margin: 15px; text-align: right; }" +
            ".highlight { background-color: #%s; padding: 2px 4px; border-radius: 4px; font-weight: bold; }",
            textColorHex, highlightColorHex
        );

        String htmlContent = String.format(
            "<html><head><style>%s</style></head><body dir='rtl'>%s</body></html>",
            cssStyles,
            highlightedText
        );
                        
        textPane.setEditorKit(kit);
        textPane.setContentType("text/html");
        textPane.setText(htmlContent);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setBorder(null);

        // --- Bottom: Metadata Panel (Professional look) ---
        JPanel metaPanel = new JPanel(new BorderLayout());
        metaPanel.setBackground(META_BG_COLOR);
        metaPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
        
        // Get data (Assumed bl.getBookById(chapterId) returns Book Name)
        String bookName = bl.getBookById(sentence.getChapterId()); 
        
        // Left side: Book Name & Chapter ID
        String leftMeta = String.format("Book Name: %s | Chapter ID: %d", 
                                        (bookName != null ? bookName : "N/A"), 
                                        sentence.getChapterId());
                                        
        JLabel bookChapterLabel = new JLabel(leftMeta);
        bookChapterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookChapterLabel.setForeground(TEXT_SECONDARY.darker());

        // Right side: Sentence Number
        String rightMeta = String.format("Sentence No: %d", 
                                        sentence.getSentenceNumber());
                                        
        JLabel sentNoLabel = new JLabel(rightMeta, SwingConstants.RIGHT);
        sentNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sentNoLabel.setForeground(PRIMARY_COLOR.darker());

        metaPanel.add(bookChapterLabel, BorderLayout.WEST);
        metaPanel.add(sentNoLabel, BorderLayout.EAST);
        
        card.add(textPane, BorderLayout.CENTER);
        card.add(metaPanel, BorderLayout.SOUTH);

        return card;
    }
    
    /**
     * Helper method to create a card for displaying a sentence when only raw text (String) is available.
     */
    private JPanel createSimpleSentenceCard(String highlightedText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        JTextPane textPane = new JTextPane();
        HTMLEditorKit kit = new HTMLEditorKit();

        String highlightColorHex = Integer.toHexString(SENTENCE_HL_COLOR.getRGB()).substring(2);
        String textColorHex = Integer.toHexString(TEXT_PRIMARY.getRGB()).substring(2);

        // Define CSS styles and inject them directly into the HTML <head>
        String cssStyles = String.format(
            "body { font-family: 'Segoe UI', Tahoma, sans-serif; font-size: 16px; line-height: 1.8; color: #%s; margin: 15px; text-align: right; }" +
            ".highlight { background-color: #%s; padding: 2px 4px; border-radius: 4px; font-weight: bold; }",
            textColorHex, highlightColorHex
        );

        String htmlContent = String.format(
            "<html><head><style>%s</style></head><body dir='rtl'>%s</body></html>",
            cssStyles,
            highlightedText
        );
                        
        textPane.setEditorKit(kit);
        textPane.setContentType("text/html");
        textPane.setText(htmlContent);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setBorder(null);

        // Add a simple note that metadata is unavailable
        JLabel noteLabel = new JLabel("Metadata (Book/Chapter/Sent. No.) unavailable for this search type.", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        noteLabel.setForeground(TEXT_SECONDARY);
        
        JPanel metaPanel = new JPanel(new BorderLayout());
        metaPanel.setBackground(META_BG_COLOR);
        metaPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        metaPanel.add(noteLabel, BorderLayout.CENTER);
        
        card.add(textPane, BorderLayout.CENTER);
        card.add(metaPanel, BorderLayout.SOUTH);

        return card;
    }


    private void showLemmasByRoot(String root) {
        ArrayList<LemmaDTO> rawLemmas = bl.getLemmaByRoot(root);
        ArrayList<String> lemmaStrings = new ArrayList<>();
        if(rawLemmas != null) for(LemmaDTO l : rawLemmas) lemmaStrings.add(l.getLemma());

        Map<String, Integer> data = aggregateFrequencies(lemmaStrings);

        JPanel panel = createBrowserPage("Lemmas", "Derived from Root: " + root, "ROOTS");
        JPanel grid = createCardGrid();

        data.forEach((lemmaText, count) -> {
            grid.add(createDataCard(lemmaText, count, "LEMMA", () -> showTokensByLemma(lemmaText, root)));
        });

        finalizeBrowserPage(panel, grid, "LEMMA_BY_ROOT_" + root);
    }

    private void showTokensByLemma(String lemma, String root) {
        ArrayList<TokenDTO> rawTokens = bl.getTokensByLemma(lemma);
        ArrayList<String> tokenStrings = new ArrayList<>();
        if(rawTokens != null) for(TokenDTO t : rawTokens) tokenStrings.add(t.getToken());

        Map<String, Integer> data = aggregateFrequencies(tokenStrings);

        String backTarget = (root == null) ? "LEMMAS" : "LEMMA_BY_ROOT_" + root;
        JPanel panel = createBrowserPage("Tokens", "Instances of Lemma: " + lemma, backTarget);
        JPanel grid = createCardGrid();

        data.forEach((tokenText, count) -> {
            grid.add(createDataCard(tokenText, count, "TOKEN", null));
        });

        finalizeBrowserPage(panel, grid, "TOKENS_BY_" + lemma);
    }

 // --- CORRECTED IMPLEMENTATION FOR SEGMENTS ---
    private void showSegments() {
        ArrayList<SegmentedTokenDTO> rawSegments = bl.getAllSegments();
        
        // Map to hold unique morphological features (Key) -> List of Tokens with those features (Value)
        Map<String, ArrayList<String>> aggregatedMap = new TreeMap<>();
        
        // Map to cache Token ID -> Token Text, minimizing database calls
        Map<Integer, String> tokenTextCache = new TreeMap<>();

        if (rawSegments != null) {
            for (SegmentedTokenDTO s : rawSegments) {
                
                // --- 1. Efficiently get Token Text (Cache Lookup) ---
                int tokenId = s.getTokenId();
                String token = tokenTextCache.get(tokenId);
                
                if (token == null) {
                    // Fetch from BLF if not in cache (expensive)
                    token = bl.getTokenText(tokenId);
                    if (token == null || token.trim().isEmpty()) token = "?"; 
                    tokenTextCache.put(tokenId, token);
                }

                // --- 2. Clean/Normalize Components ---
                // Use a clear delimiter (|) for internal key construction
                String prefix = (s.getPrefix() == null || s.getPrefix().isEmpty()) ? "-" : s.getPrefix();
                String stem   = (s.getStem() == null   || s.getStem().isEmpty())   ? "-" : s.getStem();
                String lemma  = (s.getLemma() == null  || s.getLemma().isEmpty())  ? "-" : s.getLemma();
                String root   = (s.getRoot() == null   || s.getRoot().isEmpty())   ? "-" : s.getRoot();
                
                // The key used for aggregation (Frequency counting)
                String aggregationKey = prefix + "|" + stem + "|" + lemma + "|" + root;

                // --- 3. Aggregate ---
                // Store the full display components (including the Token) under the aggregation key
                String displayKey = token + "|" + prefix + "|" + stem + "|" + lemma + "|" + root;
                
                aggregatedMap.computeIfAbsent(aggregationKey, k -> new ArrayList<>()).add(displayKey);
            }
        }

        JPanel panel = createBrowserPage("Segments", "Token & Morphological Breakdown", "MAIN");
        JPanel grid = createCardGrid();

        // 4. Generate Cards
        aggregatedMap.forEach((aggregationKey, displayKeyList) -> {
            
            // Get the count (frequency) and a sample display string
            int count = displayKeyList.size();
            String sampleDisplayKey = displayKeyList.get(0);
            
            // Split the sample key to extract parts (using "|" delimiter).
            String[] parts = sampleDisplayKey.split("\\|", 5); 
            
            // Check array length explicitly
            if (parts.length < 5) {
                return; 
            } 
            
            String tok = parts[0]; 
            String pfx = parts[1];
            String stm = parts[2];
            String lem = parts[3];
            String rt  = parts[4]; 

            // HTML Layout: Token as a header, others in a table
            String htmlDisplay = "<html>" +
                    // Token Header (Blue & Centered) 
                    "<div style='text-align:center; margin-bottom:4px; border-bottom:1px solid #eee; padding-bottom:3px;'>" +
                    "  <span style='font-size:11px; color:#2980b9;'>Token: <b>" + tok + "</b></span>" + 
                    "</div>" +
                    // Data Table
                    "<table width='100%' cellspacing='0' cellpadding='1' style='font-family:Tahoma;'>" +
                    "  <tr><td width='45' style='color:#95a5a6; font-size:8px'>Prefix:</td><td style='font-size:9px'>" + pfx + "</td></tr>" +
                    "  <tr><td width='45' style='color:#95a5a6; font-size:8px'>Stem:</td><td style='font-size:9px'><b>" + stm + "</b></td></tr>" +
                    "  <tr><td width='45' style='color:#95a5a6; font-size:8px'>Lemma:</td><td style='font-size:9px'>" + lem + "</td></tr>" +
                    "  <tr><td width='45' style='color:#95a5a6; font-size:8px'>Root:</td><td style='font-size:9px'>" + rt + "</td></tr>" +
                    "</table>" +
                    "</html>";

            // Add to grid
            grid.add(createDataCard(htmlDisplay, count, "SEGMENT", null));
        });

        finalizeBrowserPage(panel, grid, "SEGMENTS");
    }
    // ================= UI HELPERS ====================

    private JPanel createBrowserPage(String title, String subtitle, String backTarget) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JButton backBtn = createStyledBackButton(targetId -> {
            // Special handling for dynamic back targets
            if (targetId.startsWith("SENTENCES_BY_ROOT") || targetId.startsWith("SENTENCES_BY_LEMMA") || targetId.startsWith("SENTENCES_BY_TOKEN")) {
                // Determine previous step based on current ID
                if (targetId.startsWith("SENTENCES_BY_ROOT")) cardLayout.show(cardsPanel, "ROOTS"); 
                else if (targetId.startsWith("SENTENCES_BY_LEMMA")) cardLayout.show(cardsPanel, "LEMMAS");
                else if (targetId.startsWith("SENTENCES_BY_TOKEN")) cardLayout.show(cardsPanel, "TOKENS");
            } else {
                cardLayout.show(cardsPanel, targetId);
            }
        }, backTarget);

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setBackground(HEADER_BG);
        titles.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(TEXT_SECONDARY);

        titles.add(lblTitle);
        titles.add(lblSub);

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrapper.setBackground(HEADER_BG);
        leftWrapper.add(backBtn);
        leftWrapper.add(titles);

        headerPanel.add(leftWrapper, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        return panel;
    }

    private void finalizeBrowserPage(JPanel panel, JPanel grid, String id) {
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);
        cardsPanel.add(panel, id);
        cardLayout.show(cardsPanel, id);
    }

    private JPanel createCardGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 5, 15, 15)); // 5 Columns
        grid.setBackground(BG_COLOR);
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));
        grid.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectionManager.clearSelection(); }
        });
        return grid;
    }

    /**
     * Creates a Data Card with a visual Frequency Badge.
     */
    private JPanel createDataCard(String text, int count, String badgeText, Runnable onDoubleClick) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Boolean selected = (Boolean) getClientProperty("selected");
                boolean isSelected = (selected != null && selected);

                int w = getWidth(), h = getHeight();
                int arc = 12;

                g2d.setColor(isSelected ? new Color(235, 245, 251) : CARD_BG);
                g2d.fillRoundRect(0, 0, w-1, h-1, arc, arc);

                if (isSelected) {
                    g2d.setColor(PRIMARY_COLOR);
                    g2d.setStroke(new BasicStroke(2f));
                } else {
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1f));
                }
                g2d.drawRoundRect(0, 0, w-1, h-1, arc, arc);
                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 100)); 
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        // --- Top Row: Type Badge (Left) + Frequency Count (Right) ---
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel lblType = new JLabel(badgeText);
        lblType.setFont(new Font("SansSerif", Font.BOLD, 9));
        lblType.setForeground(Color.LIGHT_GRAY);
        
        // Frequency Badge (The "Column" requested)
        JLabel lblFreq = new JLabel(" x" + count + " ");
        lblFreq.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblFreq.setForeground(count > 1 ? BADGE_BG : Color.LIGHT_GRAY); 
        lblFreq.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topRow.add(lblType, BorderLayout.WEST);
        topRow.add(lblFreq, BorderLayout.EAST);

        // --- Center: Main Text ---
        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        lblText.setForeground(TEXT_PRIMARY);

        card.add(topRow, BorderLayout.NORTH);
        card.add(lblText, BorderLayout.CENTER);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (badgeText.equals("SEGMENT")) {
                        // Double-click a segment card
                        showSegmentDetailDialog(text, count); 
                    } else if (onDoubleClick != null) {
                        // Double-click other card types (Root, Lemma, Token)
                        onDoubleClick.run();
                    }
                } else {
                    selectionManager.selectCard(card);
                }
            }
        });

        return card;
    }

    // New interface for generalized back button action
    private interface BackAction {
        void goBack(String targetId);
    }
    
    private JButton createStyledBackButton(BackAction action, String targetId) {
        JButton btn = new JButton("← Back");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PRIMARY_COLOR);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_COLOR);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(PRIMARY_COLOR);
            }
        });
        btn.addActionListener(e -> action.goBack(targetId));
        return btn;
    }

    private static class CardSelectionManager {
        private JPanel selectedCard = null;
        public void selectCard(JPanel card) {
            if (selectedCard != null) {
                selectedCard.putClientProperty("selected", false);
                selectedCard.repaint();
            }
            selectedCard = card;
            selectedCard.putClientProperty("selected", true);
            selectedCard.repaint();
        }
        public void clearSelection() {
            if (selectedCard != null) {
                selectedCard.putClientProperty("selected", false);
                selectedCard.repaint();
                selectedCard = null;
            }
        }
    }
    
    
    /**
     * 🔍 Shows a detailed modal dialog for a specific segmented morphology.
     */
    private void showSegmentDetailDialog(String htmlContent, int count) {
        // A simple way to extract the structured data is to parse the HTML string.
        
        // 1. Extract Token
        String token = extractHtmlValue(htmlContent, "Token: <b>", "</b>");
        
        // 2. Extract Components
        String prefix = extractHtmlValue(htmlContent, "Prefix:</td><td style='font-size:9px'>", "</td>");
        String stem = extractHtmlValue(htmlContent, "Stem:</td><td style='font-size:9px'><b>", "</b></td>");
        String lemma = extractHtmlValue(htmlContent, "Lemma:</td><td style='font-size:9px'>", "</td>");
        String root = extractHtmlValue(htmlContent, "Root:</td><td style='font-size:9px'>", "</td>");

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Segment Breakdown");
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);

        // ===== HEADER: Token & Frequency =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 245, 255));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel tokenLabel = new JLabel(token, SwingConstants.CENTER);
        tokenLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        tokenLabel.setForeground(TEXT_PRIMARY);
        tokenLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        headerPanel.add(tokenLabel, BorderLayout.CENTER);

        JLabel freqLabel = new JLabel("Frequency: x" + count, SwingConstants.RIGHT);
        freqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        freqLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(freqLabel, BorderLayout.NORTH);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== DATA BODY: Components Grid =====
        JPanel dataGrid = new JPanel(new GridLayout(4, 1, 10, 10));
        dataGrid.setBackground(CARD_BG);
        dataGrid.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Data Row Helper 
        dataGrid.add(createStyledDataRow("ROOT (جذر)", root, new Color(232, 245, 233), new Color(46, 125, 50)));
        dataGrid.add(createStyledDataRow("LEMMA (المجرد)", lemma, new Color(255, 243, 224), new Color(239, 108, 0)));
        dataGrid.add(createStyledDataRow("STEM (الساق)", stem, new Color(225, 245, 254), new Color(1, 87, 155)));
        dataGrid.add(createStyledDataRow("PREFIX (البادئة)", prefix, new Color(243, 230, 255), new Color(106, 27, 154)));
        
        contentPanel.add(dataGrid, BorderLayout.CENTER);

        // ===== FOOTER: Close Button =====
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(CARD_BG);
        footerPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.addActionListener(e -> dialog.dispose());
        footerPanel.add(closeBtn);
        
        contentPanel.add(footerPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    // Helper method to parse HTML embedded in the card text
    private String extractHtmlValue(String html, String startTag, String endTag) {
        int start = html.indexOf(startTag);
        if (start != -1) {
            start += startTag.length();
            int end = html.indexOf(endTag, start);
            if (end != -1) {
                return html.substring(start, end).trim().replaceAll("</b>", "").replaceAll("<b>", "");
            }
        }
        return "?";
    }

    // Helper method for styling the rows in the detail dialog
    private JPanel createStyledDataRow(String label, String value, Color bgColor, Color textColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(bgColor);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel title = new JLabel(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(textColor.darker());
        
        JLabel content = new JLabel(value, SwingConstants.RIGHT);
        content.setFont(new Font("Segoe UI", Font.BOLD, 18));
        content.setForeground(textColor);
        content.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); // RTL support
        
        row.add(title, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);
        return row;
    }
}