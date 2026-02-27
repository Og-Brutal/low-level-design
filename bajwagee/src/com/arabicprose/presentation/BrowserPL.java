package com.arabicprose.presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.*;

public class BrowserPL extends JPanel {

    private final IBusinessLayerFacade facade;
    
    // Vibrant Color scheme
    private final Color PRIMARY = new Color(106, 76, 147);       // Purple
    private final Color SECONDARY = new Color(239, 87, 119);    // Pink
    private final Color ACCENT_1 = new Color(255, 159, 28);     // Orange
    private final Color ACCENT_2 = new Color(46, 196, 182);     // Teal
    private final Color BG = new Color(248, 249, 250);
    private final Color CARD_BG = new Color(255, 255, 255);
    private final Color CARD_SHADOW = new Color(0, 0, 0, 15);
    private final Color CARD_HOVER_SHADOW = new Color(0, 0, 0, 30);
    private final Color TEXT_DARK = new Color(51, 51, 51);
    private final Color TEXT_LIGHT = new Color(102, 102, 102);

    // Main panels
    private JPanel mainMenuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private String currentView = "";
    private String currentMode = ""; // "BROWSING" or "INDEXING"

    // Indexing navigation stack
    private java.util.Stack<IndexingState> indexingStack = new java.util.Stack<>();

    public BrowserPL(IBusinessLayerFacade facade) {
        this.facade = facade;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Create main content area with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(BG);

        // Create main menu and content views
        mainMenuPanel = createMainMenu();
        contentPanel.add(mainMenuPanel, "MAIN_MENU");

        add(contentPanel, BorderLayout.CENTER);

        // Load initial data
        loadInitialData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Arabic Morphology Browser", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(PRIMARY);

        // Back button (initially hidden)
        JButton backBtn = createSmallButton("← Back", PRIMARY);
        backBtn.setVisible(false);
        backBtn.addActionListener(e -> handleBackNavigation());

        header.add(backBtn, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        // Store back button reference
        header.putClientProperty("backBtn", backBtn);

        return header;
    }

    private JPanel createMainMenu() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel subtitle = new JLabel("Choose How You want To Explore Arabic Morphology !", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(TEXT_DARK);
        gbc.insets = new Insets(0, 50, 30, 50);
        menuPanel.add(subtitle, gbc);

        // Create mode selection cards
        gbc.insets = new Insets(10, 50, 10, 50);
        
        MenuOptionCard browsingCard = new MenuOptionCard("Browsing", 
            "Explore Arabic Morphology By Categories (Roots, Lemmas, Tokens, Segmentations)", 
            PRIMARY, createBrowsingIcon(), () -> showModeSelection("BROWSING"));
        menuPanel.add(browsingCard, gbc);

        MenuOptionCard indexingCard = new MenuOptionCard("Indexing", 
            "Navigate Hierarchically From Roots → Lemmas → Tokens → Sentences", 
            ACCENT_2, createIndexingIcon(), () -> showModeSelection("INDEXING"));
        menuPanel.add(indexingCard, gbc);

        return menuPanel;
    }

    private void showModeSelection(String mode) {
        currentMode = mode;
        
        if ("BROWSING".equals(mode)) {
            showBrowseCategories();
        } else if ("INDEXING".equals(mode)) {
            showIndexingRoots();
        }
    }

    private void showBrowseCategories() {
        JPanel browseCategoriesPanel = new JPanel(new GridBagLayout());
        browseCategoriesPanel.setBackground(BG);
        browseCategoriesPanel.setName("BROWSE_CATEGORIES");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel subtitle = new JLabel("Choose Category To Browse", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(TEXT_DARK);
        gbc.insets = new Insets(0, 50, 30, 50);
        browseCategoriesPanel.add(subtitle, gbc);

        // Create category cards (same as original browsing options)
        gbc.insets = new Insets(10, 50, 10, 50);
        
        MenuOptionCard rootsCard = new MenuOptionCard("Browse By Roots", 
            "Explore Arabic Words Starting From Their Root Forms", 
            ACCENT_2, createRootsIcon(), () -> showBrowseView("Roots"));
        browseCategoriesPanel.add(rootsCard, gbc);

        MenuOptionCard lemmasCard = new MenuOptionCard("Browse By Lemmas", 
            "Discover Words Through Their Dictionary Forms", 
            SECONDARY, createLemmasIcon(), () -> showBrowseView("Lemmas"));
        browseCategoriesPanel.add(lemmasCard, gbc);

        MenuOptionCard tokensCard = new MenuOptionCard("Browse By Tokens", 
            "Explore Individual Word Tokens From Texts", 
            ACCENT_1, createTokensIcon(), () -> showBrowseView("Tokens"));
        browseCategoriesPanel.add(tokensCard, gbc);

        MenuOptionCard segmentationsCard = new MenuOptionCard("Browse By Segmentations", 
            "Analyze Word Segmentations And Patterns", 
            PRIMARY, createSegmentationsIcon(), () -> showBrowseView("Segmentations"));
        browseCategoriesPanel.add(segmentationsCard, gbc);

        // Add to content panel and show
        contentPanel.add(browseCategoriesPanel, "BROWSE_CATEGORIES");
        cardLayout.show(contentPanel, "BROWSE_CATEGORIES");
        
        // Show back button
        showBackButton(true);
    }

    private void showIndexingRoots() {
        currentView = "IndexingRoots";
        indexingStack.clear();
        
        // Show back button
        showBackButton(true);

        // Create or show the roots view for indexing
        String cardName = "INDEXING_ROOTS";
        Component existingView = null;
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingView = comp;
                break;
            }
        }

        if (existingView == null) {
            JPanel indexingRootsView = createIndexingView("Roots");
            indexingRootsView.setName(cardName);
            contentPanel.add(indexingRootsView, cardName);
        }
        
        cardLayout.show(contentPanel, cardName);
        loadIndexingRoots();
    }

    private JPanel createIndexingView(String type) {
        JPanel indexingPanel = new JPanel(new BorderLayout());
        indexingPanel.setBackground(BG);
        indexingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header with title and breadcrumb
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Breadcrumb navigation
        JPanel breadcrumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        breadcrumbPanel.setBackground(BG);
        
        JLabel breadcrumbLabel = new JLabel(buildBreadcrumb());
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumbLabel.setForeground(TEXT_LIGHT);
        
        breadcrumbPanel.add(breadcrumbLabel);

        // Title with custom icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG);
        
        JLabel indexingIconLabel = new JLabel(createIndexingHeaderIcon(type));
        JLabel titleTextLabel = new JLabel(getIndexingTitle(type));
        titleTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleTextLabel.setForeground(getTypeColor(type));

        titlePanel.add(indexingIconLabel);
        titlePanel.add(titleTextLabel);

        headerPanel.add(breadcrumbPanel, BorderLayout.NORTH);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        indexingPanel.add(headerPanel, BorderLayout.NORTH);

        // Cards container
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG);
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG);
        
        indexingPanel.add(scrollPane, BorderLayout.CENTER);

        // Store the cards container for later use
        indexingPanel.putClientProperty("cardsContainer", cardsContainer);
        indexingPanel.putClientProperty("type", type);

        return indexingPanel;
    }

    private String buildBreadcrumb() {
        if (indexingStack.isEmpty()) {
            return "Indexing > Roots";
        }
        
        StringBuilder breadcrumb = new StringBuilder("Indexing");
        for (IndexingState state : indexingStack) {
            breadcrumb.append(" > ").append(state.getType());
        }
        breadcrumb.append(" > ").append(currentView.replace("Indexing", ""));
        return breadcrumb.toString();
    }

    private String getIndexingTitle(String type) {
        switch (type) {
            case "Roots": return "Select a Root to Explore";
            case "Lemmas": 
                IndexingState currentState = indexingStack.peek();
                return "Lemmas for Root: " + currentState.getData();
            case "Tokens":
                currentState = indexingStack.peek();
                return "Tokens for Lemma: " + currentState.getData();
            case "Sentences":
                currentState = indexingStack.peek();
                return "Sentences for Token: " + currentState.getData();
            default: return type;
        }
    }

    // ==================== INDEXING DATA LOADING METHODS ====================

    private void loadIndexingRoots() {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            List<RootDTO> list = facade.getAllRoots();
            Set<String> unique = new HashSet<>();
            for (RootDTO r : list) {
                if (r.getRoot() != null && !r.getRoot().trim().isEmpty()) {
                    unique.add(r.getRoot().trim().toLowerCase());
                }
            }

            if (unique.isEmpty()) {
                container.add(createEmptyCard("No roots found"));
            } else {
                unique.stream()
                    .sorted()
                    .map(this::formatRoot)
                    .forEach(root -> container.add(new IndexingDataCard(root, "Roots", ACCENT_2, 
                        () -> showLemmasForRoot(extractRootValue(root)))));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) {
            showError("Failed to load roots: " + e.getMessage());
        }
    }

    private void showLemmasForRoot(String rootValue) {
        // Push current state to stack
    	System.out.println("lemma for this root is : ");
        indexingStack.push(new IndexingState("Roots", rootValue));
        currentView = "IndexingLemmas";
        
        // Create or show the lemmas view for indexing
        String cardName = "INDEXING_LEMMAS";
        Component existingView = null;
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingView = comp;
                break;
            }
        }

        if (existingView == null) {
            JPanel indexingLemmasView = createIndexingView("Lemmas");
            indexingLemmasView.setName(cardName);
            contentPanel.add(indexingLemmasView, cardName);
        }
        
        cardLayout.show(contentPanel, cardName);
        System.out.println("lemma for this root is : ");
        loadLemmasForRoot(rootValue);
    }

    private void loadLemmasForRoot(String rootValue) {
        System.out.println("lemma for this root is : ");
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            System.out.println("lemma for this root is : ");
            // Get all lemmas associated with this root using morphological analysis
            Set<String> lemmas = getLemmasForRoot(rootValue);
            
            if (lemmas.isEmpty()) {
                container.add(createEmptyCard("No lemmas found for root: " + rootValue));
            } else {
                lemmas.stream()
                    .sorted()
                    .forEach(lemma -> container.add(new IndexingDataCard(lemma, "Lemmas", SECONDARY, 
                        () -> showTokensForLemma(lemma))));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) {
            showError("Failed to load lemmas: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    private void loadTokensForLemma(String lemmaValue) {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            // Get all tokens associated with this lemma
            Set<String> tokens = getTokensForLemma(lemmaValue);
            
            if (tokens.isEmpty()) {
                container.add(createEmptyCard("No tokens found for lemma: " + lemmaValue));
            } else {
                tokens.stream()
                    .sorted()
                    .forEach(token -> container.add(new IndexingDataCard(token, "Tokens", ACCENT_1, 
                        () -> showSentencesForToken(token))));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) {
            showError("Failed to load tokens: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Set<String> getTokensForLemma(String lemmaValue) throws SQLException {
        Set<String> tokens = new HashSet<>();
        
        System.out.println("=== Getting tokens for lemma: '" + lemmaValue + "' ===");
        
        // Method 1: Get tokens through analysis
        List<SentenceDTO> allSentences = facade.getAllSentences();
        
        for (SentenceDTO sentence : allSentences) {
            try {
                List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                if (analyses == null || analyses.isEmpty()) {
                    continue;
                }
                
                for (AnalysisResultDTO analysis : analyses) {
                    List<TokenizationDTO> analysisTokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                    List<TokenizationDTO> analysisLemmas = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                    
                    // Create mapping of token positions to lemmas
                    Map<Integer, String> tokenPosToLemma = new HashMap<>();
                    for (TokenizationDTO lemma : analysisLemmas) {
                        if (lemma != null && lemma.getTokenId() != 0 && lemma.getLemma() != null) {
                            // Find the token for this lemma
                            for (TokenizationDTO token : analysisTokens) {
                                if (token.getTokenId() == lemma.getTokenId()) {
                                    tokenPosToLemma.put(token.getTokenId(), lemma.getLemma());
                                    break;
                                }
                            }
                        }
                    }
                    
                    // Check if this analysis contains our lemma
                    for (TokenizationDTO token : analysisTokens) {
                        String tokenLemma = tokenPosToLemma.get(token.getTokenId());
                        if (tokenLemma != null && tokenLemma.equalsIgnoreCase(lemmaValue)) {
                            if (token.getToken() != null && !token.getToken().trim().isEmpty()) {
                                tokens.add(token.getToken().trim());
                                System.out.println("Found token: " + token.getToken() + " for lemma: " + lemmaValue);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Skip sentences without proper analysis
                continue;
            }
        }
        
        // Method 2: Try direct relationships as fallback
        if (tokens.isEmpty()) {
            List<TokenizationDTO> allTokens = facade.getAllTokens();
            List<LemmatizationDTO> allLemmas = facade.getAllLemmas();
            
            // Create mapping of token IDs to lemmas
            Map<Integer, String> tokenIdToLemma = new HashMap<>();
            for (LemmatizationDTO lemma : allLemmas) {
                if (lemma != null && lemma.getTokenId() != 0 && lemma.getLemma() != null) {
                    tokenIdToLemma.put(lemma.getTokenId(), lemma.getLemma());
                }
            }
            
            // Find tokens that have our lemma
            for (TokenizationDTO token : allTokens) {
                if (token != null && token.getTokenId() != 0 && token.getToken() != null) {
                    String tokenLemma = tokenIdToLemma.get(token.getTokenId());
                    if (tokenLemma != null && tokenLemma.equalsIgnoreCase(lemmaValue)) {
                        tokens.add(token.getToken().trim());
                        System.out.println("Found token via direct mapping: " + token.getToken() + " for lemma: " + lemmaValue);
                    }
                }
            }
        }
        
        System.out.println("Total tokens found for lemma '" + lemmaValue + "': " + tokens.size());
        return tokens;
    }


   

    private void showTokensForLemma(String lemmaValue) {
        // Push current state to stack
        indexingStack.push(new IndexingState("Lemmas", lemmaValue));
        currentView = "IndexingTokens";
        
        // Create or show the tokens view for indexing
        String cardName = "INDEXING_TOKENS";
        Component existingView = null;
        
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingView = comp;
                break;
            }
        }

        if (existingView == null) {
            JPanel indexingTokensView = createIndexingView("Tokens");
            indexingTokensView.setName(cardName);
            contentPanel.add(indexingTokensView, cardName);
        }
        
        cardLayout.show(contentPanel, cardName);
        loadTokensForLemma(lemmaValue);
    }

  

    private void showSentencesForToken(String tokenValue) {
        // Push current state to stack
        indexingStack.push(new IndexingState("Tokens", tokenValue));
        currentView = "IndexingSentences";
        
        // Use existing method to show sentences
        List<String> sentences = Collections.emptyList();
        try {
            sentences = getSentencesForToken(tokenValue);
        } catch (SQLException e) {
            showError("Failed to load sentences: " + e.getMessage());
        }
        
        showIndexingSentencesDialog(tokenValue, sentences);
    }

    private void showIndexingSentencesDialog(String token, List<String> sentences) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Sentences containing: " + token, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // Header with custom icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG);
        
        JLabel titleIcon = new JLabel(createSentencesDialogIcon("Tokens"));
        JLabel titleLabel = new JLabel("Sentences containing: " + token);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_1);
        
        titlePanel.add(titleIcon);
        titlePanel.add(titleLabel);
        
        JLabel countLabel = new JLabel(sentences.size() + " sentences found");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_LIGHT);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Sentences list
        JPanel sentencesPanel = new JPanel();
        sentencesPanel.setLayout(new BoxLayout(sentencesPanel, BoxLayout.Y_AXIS));
        sentencesPanel.setBackground(BG);
        
        if (sentences.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(BG);
            emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            
            JLabel emptyIcon = new JLabel(createEmptySentencesIcon());
            emptyIcon.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel noSentencesLabel = new JLabel("No sentences found containing this token", JLabel.CENTER);
            noSentencesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noSentencesLabel.setForeground(TEXT_LIGHT);
            
            emptyPanel.add(emptyIcon, BorderLayout.NORTH);
            emptyPanel.add(noSentencesLabel, BorderLayout.CENTER);
            sentencesPanel.add(emptyPanel);
        } else {
            for (String sentence : sentences) {
                sentencesPanel.add(createSentenceCard(sentence, ACCENT_1));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(sentencesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Close button with icon
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = createSmallButton("Close", PRIMARY);
        closeBtn.setIcon(createCloseIcon());
        closeBtn.setIconTextGap(8);
        closeBtn.addActionListener(e -> dialog.dispose());
        footerPanel.add(closeBtn);
        
        dialog.add(footerPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==================== NAVIGATION HANDLING ====================

    private void handleBackNavigation() {
        if ("INDEXING".equals(currentMode) && !indexingStack.isEmpty()) {
            // Pop the current state and go back
            indexingStack.pop();
            
            if (indexingStack.isEmpty()) {
                // Back to roots view
                showIndexingRoots();
            } else {
                IndexingState previousState = indexingStack.peek();
                switch (previousState.getType()) {
                    case "Roots":
                        showIndexingRoots();
                        break;
                    case "Lemmas":
                    	System.out.println(previousState.getData());
                        showLemmasForRoot(previousState.getData());
                        break;
                    case "Tokens":
                        showTokensForLemma(previousState.getData());
                        break;
                }
            }
        } else {
            // Back to main menu
            showMainMenu();
        }
    }

    private void showBackButton(boolean visible) {
        JButton backBtn = (JButton) ((JPanel) getComponent(0)).getClientProperty("backBtn");
        if (backBtn != null) {
            backBtn.setVisible(visible);
        }
    }
    private String extractRootValue(String formattedRoot) {
        // Format: "غ - ي - ر (غير)" or "غ - ي - ر (gry)"
        // We need the Arabic root from the first part, not the Buckwalter in parentheses
        
        // First, try to get the Arabic characters before the parentheses
        // The format is: Arabic letters separated by " - " then " (Buckwalter)"
        
        // Extract Arabic part (before parentheses)
        String arabicPart = formattedRoot.split("\\(")[0].trim();
        
        // Remove the " - " separators to get the Arabic root
        String arabicRoot = arabicPart.replaceAll("\\s*-\\s*", "");
        
        // If we have Arabic characters, return them
        if (arabicRoot.matches(".*[\\u0600-\\u06FF].*")) {
            return arabicRoot;
        }
        
        // If no Arabic characters found, fall back to extracting from parentheses
        String extracted = formattedRoot.replaceAll(".*\\(([^)]+)\\).*", "$1").trim();
        
        // If the extracted value contains Arabic, return it as-is
        if (extracted.matches(".*[\\u0600-\\u06FF].*")) {
            return extracted;
        }
        
        // If it's Buckwalter, try to convert to Arabic for database lookup
        if (extracted.matches("[A-Za-z]+")) {
            try {
                return net.oujda_nlp_team.util.Transliteration.getInstance().getBuckWalterToArabic(extracted);
            } catch (Exception e) {
                // If conversion fails, return the Buckwalter value
                return extracted;
            }
        }
        
        return extracted.toLowerCase();
    }
    // ==================== NEW ICONS FOR INDEXING ====================

    private Icon createBrowsingIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Multiple category icons
                g2d.setColor(PRIMARY);
                g2d.setStroke(new BasicStroke(2f));
                
                // Folder 1
                g2d.drawRoundRect(x + 4, y + 6, 10, 12, 3, 3);
                g2d.fillRect(x + 4, y + 6, 3, 12);
                
                // Folder 2
                g2d.drawRoundRect(x + 12, y + 4, 10, 12, 3, 3);
                g2d.fillRect(x + 12, y + 4, 3, 12);
                
                // Folder 3
                g2d.drawRoundRect(x + 20, y + 8, 10, 12, 3, 3);
                g2d.fillRect(x + 20, y + 8, 3, 12);
                
                // Magnifying glass over all
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawOval(x + 18, y + 2, 8, 8);
                g2d.drawLine(x + 23, y + 7, x + 26, y + 10);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createIndexingIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Hierarchical tree structure
                g2d.setColor(ACCENT_2);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Root node
                g2d.fillOval(x + size/2 - 4, y + 4, 8, 8);
                
                // Branch to middle nodes
                g2d.drawLine(x + size/2, y + 12, x + 8, y + 20);
                g2d.drawLine(x + size/2, y + 12, x + size - 8, y + 20);
                
                // Middle nodes
                g2d.fillOval(x + 4, y + 18, 8, 8);
                g2d.fillOval(x + size - 12, y + 18, 8, 8);
                
                // Branches to leaf nodes
                g2d.drawLine(x + 8, y + 26, x + 4, y + 32);
                g2d.drawLine(x + 8, y + 26, x + 12, y + 32);
                g2d.drawLine(x + size - 8, y + 26, x + size - 12, y + 32);
                g2d.drawLine(x + size - 8, y + 26, x + size - 4, y + 32);
                
                // Leaf nodes
                g2d.fillOval(x + 2, y + 30, 4, 4);
                g2d.fillOval(x + 10, y + 30, 4, 4);
                g2d.fillOval(x + size - 14, y + 30, 4, 4);
                g2d.fillOval(x + size - 6, y + 30, 4, 4);
                
                // Arrow indicating flow
                g2d.setColor(PRIMARY);
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + 2, y + 2, x + 6, y + 2);
                g2d.drawLine(x + 6, y + 2, x + 4, y + 4);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createIndexingHeaderIcon(String type) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 24;
                Color color = getTypeColor(type);
                
                // Hierarchical icon with current level highlighted
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                switch (type) {
                    case "Roots":
                        // Single highlighted root node
                        g2d.fillOval(x + 8, y + 8, 8, 8);
                        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                        g2d.drawLine(x + 12, y + 16, x + 6, y + 20);
                        g2d.drawLine(x + 12, y + 16, x + 18, y + 20);
                        break;
                    case "Lemmas":
                        // Root node with highlighted lemma nodes
                        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                        g2d.fillOval(x + 8, y + 4, 8, 8);
                        g2d.setColor(color);
                        g2d.fillOval(x + 4, y + 16, 6, 6);
                        g2d.fillOval(x + 14, y + 16, 6, 6);
                        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                        g2d.drawLine(x + 12, y + 12, x + 7, y + 16);
                        g2d.drawLine(x + 12, y + 12, x + 17, y + 16);
                        break;
                    case "Tokens":
                        // Full hierarchy with tokens highlighted
                        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                        g2d.fillOval(x + 8, y + 4, 8, 8);
                        g2d.fillOval(x + 4, y + 12, 6, 6);
                        g2d.fillOval(x + 14, y + 12, 6, 6);
                        g2d.setColor(color);
                        g2d.fillOval(x + 2, y + 20, 4, 4);
                        g2d.fillOval(x + 8, y + 20, 4, 4);
                        g2d.fillOval(x + 14, y + 20, 4, 4);
                        g2d.fillOval(x + 20, y + 20, 4, 4);
                        break;
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 24; }

            @Override
            public int getIconHeight() { return 24; }
        };
    }

    // ==================== INDEXING DATA CARD ====================

    private class IndexingDataCard extends JPanel {
        private boolean isHovered = false;
        private final String data;
        private final String type;
        private final Color cardColor;
        private final Runnable action;
        
        public IndexingDataCard(String data, String type, Color color, Runnable action) {
            this.data = data;
            this.type = type;
            this.cardColor = color;
            this.action = action;
            initCard();
        }
        
        private void initCard() {
            setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
            setPreferredSize(new Dimension(getWidth() - 40, 100));
            setLayout(new BorderLayout(15, 10));
            setBackground(CARD_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(15, 25, 15, 20));
            setOpaque(false);

            // Text content
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JTextArea textArea = new JTextArea(data);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 18));
            textArea.setBackground(new Color(250, 250, 250));
            textArea.setForeground(TEXT_DARK);
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
            textArea.setFocusable(false);
            
            JScrollPane textScroll = new JScrollPane(textArea);
            textScroll.setPreferredSize(new Dimension(getWidth() - 200, 70));
            textScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            textScroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            textPanel.add(textScroll, BorderLayout.CENTER);

            // Navigation arrow
            JLabel arrowLabel = new JLabel("→");
            arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            arrowLabel.setForeground(cardColor);
            arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);
            arrowLabel.setPreferredSize(new Dimension(40, 70));

            add(textPanel, BorderLayout.CENTER);
            add(arrowLabel, BorderLayout.EAST);
            
            // Mouse listener
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
                
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 12;
            int shadowOffset = isHovered ? 5 : 2;
            Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;
            
            // Draw shadow
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius);
            
            // Draw main card
            g2.setColor(isHovered ? new Color(252, 252, 252) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Draw colored accent on left
            g2.setColor(cardColor);
            g2.fillRoundRect(0, 0, 6, getHeight(), cornerRadius, cornerRadius);
            
            // Draw border
            g2.setColor(isHovered ? cardColor : new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    // ==================== INDEXING STATE CLASS ====================

    private class IndexingState {
        private String type;
        private String data;
        
        public IndexingState(String type, String data) {
            this.type = type;
            this.data = data;
        }
        
        public String getType() { return type; }
        public String getData() { return data; }
    }

   
    private void showMainMenu() {
        cardLayout.show(contentPanel, "MAIN_MENU");
        currentView = "";
        currentMode = "";
        indexingStack.clear();
        
        // Hide back button
        showBackButton(false);
    }
    
    private void showBrowseView(String type) {
        currentView = type;
        
        // Show back button
        JButton backBtn = (JButton) ((JPanel) getComponent(0)).getClientProperty("backBtn");
        if (backBtn != null) {
            backBtn.setVisible(true);
        }

        // Create or show the browse view for this type
        String cardName = type.toUpperCase() + "_VIEW";
        Component existingView = null;
        
        // Check if view already exists
        for (Component comp : contentPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(cardName)) {
                existingView = comp;
                break;
            }
        }

        if (existingView == null) {
            // Create new browse view
            JPanel browseView = createBrowseView(type);
            browseView.setName(cardName);
            contentPanel.add(browseView, cardName);
        }
        
        cardLayout.show(contentPanel, cardName);
        
        // Load data for this view
        switch (type) {
            case "Roots": loadRootsCards(); break;
            case "Lemmas": loadLemmasCards(); break;
            case "Tokens": loadTokensCards(); break;
            case "Segmentations": loadSegmentationsCards(); break;
        }
    }

    private JPanel createBrowseView(String type) {
        JPanel browsePanel = new JPanel(new BorderLayout());
        browsePanel.setBackground(BG);
        browsePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header with title and refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title with custom browse icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG);
        
        JLabel browseIconLabel = new JLabel(createBrowseHeaderIcon(type));
        JLabel titleTextLabel = new JLabel("Browsing: " + type);
        titleTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleTextLabel.setForeground(getTypeColor(type));

        titlePanel.add(browseIconLabel);
        titlePanel.add(titleTextLabel);

        // Refresh button with custom icon
//        JButton refreshBtn = createSmallButton("Refresh", getTypeColor(type));
//        refreshBtn.setIcon(createRefreshIcon());
//        refreshBtn.setIconTextGap(8);
//        refreshBtn.addActionListener(e -> {
//            switch (type) {
//                case "Roots": loadRootsCards(); break;
//                case "Lemmas": loadLemmasCards(); break;
//                case "Tokens": loadTokensCards(); break;
//                case "Segmentations": loadSegmentationsCards(); break;
//            }
//            showInfo(type + " data refreshed!");
//        });

        headerPanel.add(titlePanel, BorderLayout.WEST);
//        headerPanel.add(refreshBtn, BorderLayout.EAST);
        browsePanel.add(headerPanel, BorderLayout.NORTH);

        // Cards container
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG);
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BG);
        
        browsePanel.add(scrollPane, BorderLayout.CENTER);

        // Store the cards container for later use
        browsePanel.putClientProperty("cardsContainer", cardsContainer);
        browsePanel.putClientProperty("type", type);

        return browsePanel;
    }

    // ==================== CUSTOM ICONS FOR BROWSE VIEW ====================

    private Icon createBrowseHeaderIcon(String type) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                Color color = getTypeColor(type);
                
                // Main browsing icon - folder with magnifying glass
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f));
                
                // Folder base
                g2d.drawRoundRect(x + 6, y + 8, size - 12, size - 12, 6, 6);
                
                // Folder tab
                g2d.fillRoundRect(x + 8, y + 6, size - 16, 6, 3, 3);
                
                // Folder lines
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 10, y + 15 + i*6, x + size - 10, y + 15 + i*6);
                }
                
                // Magnifying glass overlay
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int glassSize = 12;
                g2d.drawOval(x + size - 16, y + 10, glassSize, glassSize);
                g2d.drawLine(x + size - 6, y + 18, x + size - 2, y + 22);
                
                // Content type indicator based on browse type
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(1.5f));
                
                switch (type) {
                    case "Roots":
                        // Small root symbol
                        g2d.drawLine(x + 12, y + 12, x + 15, y + 15);
                        g2d.drawLine(x + 12, y + 12, x + 9, y + 15);
                        break;
                    case "Lemmas":
                        // Small book symbol
                        g2d.drawRect(x + 10, y + 10, 6, 4);
                        break;
                    case "Tokens":
                        // Small text symbol
                        g2d.drawLine(x + 10, y + 12, x + 16, y + 12);
                        g2d.drawLine(x + 10, y + 14, x + 14, y + 14);
                        break;
                    case "Segmentations":
                        // Small scissors symbol
                        g2d.drawLine(x + 10, y + 10, x + 14, y + 14);
                        g2d.drawLine(x + 14, y + 10, x + 10, y + 14);
                        break;
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createRefreshIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 16;
                
                // Circular arrow refresh symbol
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Main circle
                g2d.drawOval(x + 2, y + 2, size - 4, size - 4);
                
                // Arrow head
                g2d.drawLine(x + size - 4, y + 4, x + size - 1, y + 2);
                g2d.drawLine(x + size - 1, y + 2, x + size - 3, y + 5);
                
                // Circular path
                g2d.drawArc(x + 4, y + 4, size - 8, size - 8, 45, 270);
                
                // Sparkle effect dots
                g2d.setStroke(new BasicStroke(1f));
                g2d.fillOval(x + 6, y + 6, 2, 2);
                g2d.fillOval(x + 10, y + 4, 1, 1);
                g2d.fillOval(x + 8, y + 10, 1, 1);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(120, 35));
        return button;
    }
    private String getBrowseIcon(String type) {
        switch (type) {
            case "Roots": return "🌱";
            case "Lemmas": return "📖";
            case "Tokens": return "🔤";
            case "Segmentations": return "✂️";
            default: return "📁";
        }
    }

    private Color getTypeColor(String type) {
        switch (type) {
            case "Roots": return ACCENT_2;
            case "Lemmas": return SECONDARY;
            case "Tokens": return ACCENT_1;
            case "Segmentations": return PRIMARY;
            default: return PRIMARY;
        }
    }

    // ==================== CUSTOM ICONS FOR MENU OPTIONS ====================

    private Icon createRootsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Plant root system
                g2d.setColor(ACCENT_2);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Main root trunk
                g2d.drawLine(x + size/2, y + 8, x + size/2, y + size - 8);
                
                // Root branches
                g2d.drawLine(x + size/2, y + 15, x + 8, y + 20);
                g2d.drawLine(x + size/2, y + 15, x + size - 8, y + 20);
                g2d.drawLine(x + size/2, y + 22, x + 10, y + 28);
                g2d.drawLine(x + size/2, y + 22, x + size - 10, y + 28);
                g2d.drawLine(x + size/2, y + 28, x + 12, y + size - 5);
                g2d.drawLine(x + size/2, y + 28, x + size - 12, y + size - 5);
                
                // Small root hairs
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 8 + i*2, y + 20, x + 6 + i*2, y + 22);
                    g2d.drawLine(x + size - 8 - i*2, y + 20, x + size - 6 - i*2, y + 22);
                }
                
                // Plant stem above ground
                g2d.setColor(new Color(86, 171, 89));
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + size/2, y + 2, x + size/2, y + 8);
                
                // Leaves
                g2d.setColor(new Color(106, 191, 109));
                g2d.fillOval(x + size/2 - 6, y, 12, 8);
                g2d.fillOval(x + size/2 - 2, y - 4, 8, 6);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createLemmasIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Dictionary/book
                g2d.setColor(SECONDARY);
                g2d.setStroke(new BasicStroke(2f));
                
                // Book cover
                g2d.drawRoundRect(x + 4, y + 2, size - 8, size - 4, 4, 4);
                
                // Book spine
                g2d.fillRect(x + 4, y + 2, 4, size - 4);
                
                // Pages
                g2d.setColor(new Color(220, 220, 220));
                for (int i = 0; i < 5; i++) {
                    g2d.drawLine(x + 8 + i, y + 4, x + 8 + i, y + size - 2);
                }
                
                // Dictionary tabs
                g2d.setColor(SECONDARY);
                g2d.setStroke(new BasicStroke(1.5f));
                
                // Tab A
                g2d.fillRect(x + 10, y + 6, 6, 4);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 6));
                g2d.drawString("A", x + 11, y + 9);
                
                // Tab K
                g2d.setColor(SECONDARY);
                g2d.fillRect(x + 18, y + 10, 6, 4);
                g2d.setColor(Color.WHITE);
                g2d.drawString("K", x + 19, y + 13);
                
                // Tab S
                g2d.setColor(SECONDARY);
                g2d.fillRect(x + 26, y + 14, 6, 4);
                g2d.setColor(Color.WHITE);
                g2d.drawString("S", x + 27, y + 17);
                
                // Text lines
                g2d.setColor(TEXT_DARK);
                g2d.setStroke(new BasicStroke(0.8f));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 12, y + 20 + i*4, x + size - 6, y + 20 + i*4);
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createTokensIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Word tokens
                g2d.setColor(ACCENT_1);
                
                // Token 1
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(x + 6, y + 6, 8, 6, 2, 2);
                g2d.drawArc(x + 7, y + 8, 2, 2, 0, 180);
                g2d.drawArc(x + 10, y + 8, 2, 2, 0, 180);
                
                // Token 2
                g2d.drawRoundRect(x + 16, y + 12, 8, 6, 2, 2);
                g2d.drawArc(x + 17, y + 14, 2, 2, 0, 180);
                g2d.drawLine(x + 20, y + 13, x + 22, y + 15);
                
                // Token 3
                g2d.drawRoundRect(x + 10, y + 20, 8, 6, 2, 2);
                g2d.drawArc(x + 11, y + 22, 2, 2, 0, 180);
                g2d.drawArc(x + 14, y + 22, 2, 2, 0, 180);
                
                // Connecting lines
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.setColor(new Color(200, 200, 200));
                g2d.drawLine(x + 14, y + 12, x + 16, y + 12);
                g2d.drawLine(x + 24, y + 15, x + 26, y + 18);
                g2d.drawLine(x + 18, y + 18, x + 10, y + 20);
                
                // Token highlight
                g2d.setColor(new Color(ACCENT_1.getRed(), ACCENT_1.getGreen(), ACCENT_1.getBlue(), 50));
                g2d.fillRoundRect(x + 15, y + 11, 10, 8, 3, 3);
                
                // Word count badge
                g2d.setColor(ACCENT_1);
                g2d.fillOval(x + size - 10, y + 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 6));
                g2d.drawString("3", x + size - 8, y + 9);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createSegmentationsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Word segmentation
                g2d.setColor(PRIMARY);
                g2d.setStroke(new BasicStroke(2f));
                
                // Main word block
                g2d.drawRoundRect(x + 6, y + 12, size - 12, 8, 2, 2);
                
                // Segmentation lines
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(x + 12, y + 12, x + 12, y + 20);
                g2d.drawLine(x + 20, y + 12, x + 20, y + 20);
                g2d.drawLine(x + 26, y + 12, x + 26, y + 20);
                
                // Puzzle connectors
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawArc(x + 11, y + 10, 2, 4, 0, 180);
                g2d.drawArc(x + 11, y + 18, 2, 4, 180, 180);
                g2d.drawArc(x + 19, y + 10, 2, 4, 0, 180);
                g2d.drawArc(x + 19, y + 18, 2, 4, 180, 180);
                g2d.drawArc(x + 25, y + 10, 2, 4, 0, 180);
                g2d.drawArc(x + 25, y + 18, 2, 4, 180, 180);
                
                // Morphological pieces
                g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 200));
                g2d.fillRoundRect(x + 8, y + 4, 6, 6, 2, 2);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 5));
                g2d.drawString("PR", x + 9, y + 8);
                
                g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 200));
                g2d.fillRoundRect(x + 16, y + 4, 6, 6, 2, 2);
                g2d.setColor(Color.WHITE);
                g2d.drawString("ST", x + 17, y + 8);
                
                g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 200));
                g2d.fillRoundRect(x + 24, y + 4, 6, 6, 2, 2);
                g2d.setColor(Color.WHITE);
                g2d.drawString("SF", x + 25, y + 8);
                
                // Connection lines
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + 11, y + 10, x + 11, y + 12);
                g2d.drawLine(x + 19, y + 10, x + 19, y + 12);
                g2d.drawLine(x + 27, y + 10, x + 27, y + 12);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    // ==================== MENU OPTION CARD ====================

    private class MenuOptionCard extends JPanel {
        private boolean isHovered = false;
        private final Color cardColor;
        private final Icon icon;
        
        public MenuOptionCard(String title, String description, Color color, Icon icon, Runnable action) {
            this.cardColor = color;
            this.icon = icon;
            setLayout(new BorderLayout(15, 10));
            setBackground(CARD_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(20, 25, 20, 25));
            setOpaque(false);
            setPreferredSize(new Dimension(400, 100));
            setMaximumSize(new Dimension(500, 100));

            // Left content with icon
            JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
            leftPanel.setOpaque(false);
            
            JLabel iconLabel = new JLabel(icon);
            
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(TEXT_DARK);
            
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(TEXT_LIGHT);
            
            textPanel.add(titleLabel, BorderLayout.NORTH);
            textPanel.add(descLabel, BorderLayout.CENTER);
            
            leftPanel.add(iconLabel, BorderLayout.WEST);
            leftPanel.add(textPanel, BorderLayout.CENTER);

            // Right arrow with color
            JLabel arrowLabel = new JLabel("→");
            arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            arrowLabel.setForeground(cardColor);
            arrowLabel.setHorizontalAlignment(SwingConstants.CENTER);

            add(leftPanel, BorderLayout.CENTER);
            add(arrowLabel, BorderLayout.EAST);

            // Mouse listener
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
                
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 16;
            int shadowOffset = isHovered ? 6 : 3;
            Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;
            
            // Draw shadow
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius);
            
            // Draw main card
            g2.setColor(isHovered ? new Color(253, 253, 253) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Draw colored accent on left side
            g2.setColor(cardColor);
            g2.fillRoundRect(0, 0, 8, getHeight(), cornerRadius, cornerRadius);
            
            // Draw border
            g2.setColor(isHovered ? cardColor : new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(isHovered ? 2.5f : 1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    // ==================== DATA CARD (SIMPLIFIED - NO BUTTONS) ====================

    private class DataCard extends JPanel {
        private boolean isHovered = false;
        private final String data;
        private final String type;
        private final Color cardColor;
        
        public DataCard(String data, String type, Color color) {
            this.data = data;
            this.type = type;
            this.cardColor = color;
            initCard();
        }
        
        private void initCard() {
            setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
            setPreferredSize(new Dimension(getWidth() - 40, 100));
            setLayout(new BorderLayout(15, 10));
            setBackground(CARD_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(15, 25, 15, 20));
            setOpaque(false);

            // Text content
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JTextArea textArea = new JTextArea(data);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 18));
            textArea.setBackground(new Color(250, 250, 250));
            textArea.setForeground(TEXT_DARK);
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
            textArea.setFocusable(false);
            
            JScrollPane textScroll = new JScrollPane(textArea);
            textScroll.setPreferredSize(new Dimension(getWidth() - 200, 70));
            textScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            textScroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            textPanel.add(textScroll, BorderLayout.CENTER);

            // Type indicator with custom icon
            JLabel typeLabel = new JLabel(getTypeIcon(type));
            typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            typeLabel.setForeground(cardColor);
            typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            typeLabel.setPreferredSize(new Dimension(40, 70));

            add(textPanel, BorderLayout.CENTER);
            add(typeLabel, BorderLayout.EAST);
            
            // Mouse listener - CLICK SHOWS SENTENCES
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showSentencesForItem(type, data);
                }
                
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
            
            textPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showSentencesForItem(type, data);
                }
            });
            
            textScroll.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showSentencesForItem(type, data);
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 12;
            int shadowOffset = isHovered ? 5 : 2;
            Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;
            
            // Draw shadow
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius);
            
            // Draw main card
            g2.setColor(isHovered ? new Color(252, 252, 252) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Draw colored accent on left
            g2.setColor(cardColor);
            g2.fillRoundRect(0, 0, 6, getHeight(), cornerRadius, cornerRadius);
            
            // Draw border
            g2.setColor(isHovered ? cardColor : new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    private Icon getTypeIcon(String type) {
        switch (type) {
            case "Roots": return createRootsIcon();
            case "Lemmas": return createLemmasIcon();
            case "Tokens": return createTokensIcon();
            case "Segmentations": return createSegmentationsIcon();
            default: return createDefaultIcon();
        }
    }

    private Icon createDefaultIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY);
                
                int size = 24;
                
                // Simple document icon
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(x + 4, y + 2, size - 8, size - 4, 4, 4);
                
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 6, y + 6 + i*4, x + size - 6, y + 6 + i*4);
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 24; }

            @Override
            public int getIconHeight() { return 24; }
        };
    }
    // ==================== SENTENCES DISPLAY ====================

    private void showSentencesForItem(String type, String item) {
        try {
            List<String> sentences = new ArrayList<>();
            
            switch (type) {
                case "Roots":
                    String rootValue = extractRoot(item);
                    sentences = getSentencesForRoot(rootValue);
                    break;
                case "Lemmas":
                    String lemmaValue = extractLemma(item);
                    sentences = getSentencesForLemma(lemmaValue);
                    break;
                case "Tokens":
                    sentences = getSentencesForToken(item);
                    break;
                case "Segmentations":
                    sentences = getSentencesForSegmentation(item);
                    break;
            }
            
            showSentencesDialog(type, item, sentences);
            
        } catch (Exception ex) {
            showError("Failed to load sentences: " + ex.getMessage());
        }
    }

    private List<String> getSentencesForRoot(String root) throws SQLException {
        List<String> sentences = new ArrayList<>();
        Set<String> uniqueSentences = new HashSet<>(); // To avoid duplicates
        
        try {
            System.out.println("=== Searching for root: '" + root + "' ===");
            
            // Step 1: Get all RootDTOs for this root value (try different cases)
            List<RootDTO> roots = facade.getRootsByValue(root);
            if (roots == null || roots.isEmpty()) {
                // Try lowercase if original didn't work
                roots = facade.getRootsByValue(root.toLowerCase());
            }
            if (roots == null || roots.isEmpty()) {
                // Try uppercase
                roots = facade.getRootsByValue(root.toUpperCase());
            }
            
            System.out.println("Found " + (roots != null ? roots.size() : 0) + " root entries");
            
            if (roots == null || roots.isEmpty()) {
                System.out.println("No roots found for: " + root);
                return sentences; // Return empty list if no roots found
            }
            
            // Step 2: Collect all token IDs from these roots
            Set<Integer> tokenIds = new HashSet<>();
            for (RootDTO r : roots) {
                if (r.getTokenId() != 0) {
                    tokenIds.add(r.getTokenId());
                    System.out.println("  Root entry: tokenId=" + r.getTokenId() + ", root=" + r.getRoot());
                }
            }
            
            System.out.println("Found " + tokenIds.size() + " unique token IDs");
            
            if (tokenIds.isEmpty()) {
                System.out.println("No token IDs found");
                return sentences; // Return empty if no token IDs found
            }
            
            // Step 3: Iterate through all sentences and check their analyses
            List<SentenceDTO> allSentences = facade.getAllSentences();
            System.out.println("Checking " + allSentences.size() + " sentences for matching tokens");
            
            int checkedSentences = 0;
            for (SentenceDTO sentence : allSentences) {
                try {
                    List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                    if (analyses == null || analyses.isEmpty()) {
                        continue; // Skip sentences without analysis
                    }
                    
                    checkedSentences++;
                    for (AnalysisResultDTO analysis : analyses) {
                        // Get all tokens for this analysis
                        List<TokenizationDTO> tokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                        for (TokenizationDTO token : tokens) {
                            // Check if this token's ID is in our set
                            if (tokenIds.contains(token.getTokenId())) {
                                // This sentence contains a token with our root!
                                String text = sentence.getText();
                                if (text != null && !text.trim().isEmpty()) {
                                    uniqueSentences.add(text.trim());
                                    System.out.println("  ✓ Found sentence #" + sentence.getSentenceId() + ": " + 
                                        text.substring(0, Math.min(80, text.length())) + (text.length() > 80 ? "..." : ""));
                                }
                                break; // Found one, no need to check other tokens in this analysis
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip if analysis doesn't exist for this sentence
                }
            }
            
            System.out.println("Checked " + checkedSentences + " sentences with analyses");
            System.out.println("Total unique sentences found: " + uniqueSentences.size());
            
            // Convert set to list
            sentences = new ArrayList<>(uniqueSentences);
            
        } catch (Exception e) {
            System.err.println("Error getting sentences for root '" + root + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return sentences;
    }

    private List<String> getSentencesForLemma(String lemma) throws SQLException {
        List<String> sentences = new ArrayList<>();
        Set<String> uniqueSentences = new HashSet<>();
        
        try {
            System.out.println("=== Searching for lemma: '" + lemma + "' ===");
            
            // Step 1: Get all LemmatizationDTOs for this lemma
            List<LemmatizationDTO> lemmas = facade.getLemmasByValue(lemma);
            if (lemmas == null || lemmas.isEmpty()) {
                System.out.println("No lemmas found for: " + lemma);
                return sentences;
            }
            
            System.out.println("Found " + lemmas.size() + " lemma entries");
            
            // Step 2: Collect all token IDs from these lemmas
            Set<Integer> tokenIds = new HashSet<>();
            for (LemmatizationDTO l : lemmas) {
                if (l.getTokenId() != 0) {
                    tokenIds.add(l.getTokenId());
                    System.out.println("  Lemma entry: tokenId=" + l.getTokenId() + ", lemma=" + l.getLemma());
                }
            }
            
            System.out.println("Found " + tokenIds.size() + " unique token IDs");
            
            if (tokenIds.isEmpty()) {
                System.out.println("No token IDs found");
                return sentences;
            }
            
            // Step 3: Iterate through all sentences and check their analyses
            List<SentenceDTO> allSentences = facade.getAllSentences();
            System.out.println("Checking " + allSentences.size() + " sentences for matching tokens");
            
            int checkedSentences = 0;
            for (SentenceDTO sentence : allSentences) {
                try {
                    List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                    if (analyses == null || analyses.isEmpty()) {
                        continue;
                    }
                    
                    checkedSentences++;
                    for (AnalysisResultDTO analysis : analyses) {
                        List<TokenizationDTO> tokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                        for (TokenizationDTO token : tokens) {
                            if (tokenIds.contains(token.getTokenId())) {
                                String text = sentence.getText();
                                if (text != null && !text.trim().isEmpty()) {
                                    uniqueSentences.add(text.trim());
                                    System.out.println("  ✓ Found sentence #" + sentence.getSentenceId() + ": " + 
                                        text.substring(0, Math.min(80, text.length())) + (text.length() > 80 ? "..." : ""));
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip if analysis doesn't exist
                }
            }
            
            System.out.println("Checked " + checkedSentences + " sentences with analyses");
            System.out.println("Total unique sentences found: " + uniqueSentences.size());
            
            sentences = new ArrayList<>(uniqueSentences);
            
        } catch (Exception e) {
            System.err.println("Error getting sentences for lemma '" + lemma + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return sentences;
    }

    private List<String> getSentencesForToken(String token) throws SQLException {
        List<String> sentences = new ArrayList<>();
        Set<String> uniqueSentences = new HashSet<>();
        
        try {
            System.out.println("=== Searching for token: '" + token + "' ===");
            
            // Step 1: Get all TokenizationDTOs for this token
            List<TokenizationDTO> tokens = facade.getTokensByValue(token);
            if (tokens == null || tokens.isEmpty()) {
                System.out.println("No tokens found for: " + token);
                return sentences;
            }
            
            System.out.println("Found " + tokens.size() + " token entries");
            
            // Step 2: Collect all token IDs
            Set<Integer> tokenIds = new HashSet<>();
            for (TokenizationDTO t : tokens) {
                if (t.getTokenId() != 0) {
                    tokenIds.add(t.getTokenId());
                    System.out.println("  Token entry: tokenId=" + t.getTokenId() + ", token=" + t.getToken());
                }
            }
            
            System.out.println("Found " + tokenIds.size() + " unique token IDs");
            
            if (tokenIds.isEmpty()) {
                System.out.println("No token IDs found");
                return sentences;
            }
            
            // Step 3: Iterate through all sentences and check their analyses
            List<SentenceDTO> allSentences = facade.getAllSentences();
            System.out.println("Checking " + allSentences.size() + " sentences for matching tokens");
            
            int checkedSentences = 0;
            for (SentenceDTO sentence : allSentences) {
                try {
                    List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                    if (analyses == null || analyses.isEmpty()) {
                        continue;
                    }
                    
                    checkedSentences++;
                    for (AnalysisResultDTO analysis : analyses) {
                        List<TokenizationDTO> sentenceTokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                        for (TokenizationDTO sentenceToken : sentenceTokens) {
                            if (tokenIds.contains(sentenceToken.getTokenId())) {
                                String text = sentence.getText();
                                if (text != null && !text.trim().isEmpty()) {
                                    uniqueSentences.add(text.trim());
                                    System.out.println("  ✓ Found sentence #" + sentence.getSentenceId() + ": " + 
                                        text.substring(0, Math.min(80, text.length())) + (text.length() > 80 ? "..." : ""));
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip if analysis doesn't exist
                }
            }
            
            System.out.println("Checked " + checkedSentences + " sentences with analyses");
            System.out.println("Total unique sentences found: " + uniqueSentences.size());
            
            sentences = new ArrayList<>(uniqueSentences);
            
        } catch (Exception e) {
            System.err.println("Error getting sentences for token '" + token + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return sentences;
    }

    private List<String> getSentencesForSegmentation(String segmentation) throws SQLException {
        List<String> sentences = new ArrayList<>();
        Set<String> uniqueSentences = new HashSet<>();
        
        try {
            System.out.println("=== Searching for segmentation: '" + segmentation + "' ===");
            
            // Extract parts from segmentation display (format: "prefix | stem | suffix")
            String[] parts = segmentation.split("\\|");
            if (parts.length != 3) {
                System.out.println("Invalid segmentation format: " + segmentation);
                return sentences;
            }
            
            String prefix = parts[0].trim();
            String stem = parts[1].trim();
            String suffix = parts[2].trim();
            
            // Clean the parts (remove dashes if they represent empty values)
            prefix = prefix.equals("—") ? "" : prefix;
            stem = stem.equals("—") ? "" : stem;
            suffix = suffix.equals("—") ? "" : suffix;
            
            System.out.println("Searching for segmentation - Prefix: '" + prefix + "', Stem: '" + stem + "', Suffix: '" + suffix + "'");
            
            // Step 1: Get all Segmentations that match this pattern
            List<SegmentationDTO> segmentations = facade.getAllSegmentations();
            if (segmentations == null || segmentations.isEmpty()) {
                System.out.println("No segmentations found in database");
                return sentences;
            }
            
            Set<Integer> tokenIds = new HashSet<>();
            for (SegmentationDTO seg : segmentations) {
                if (seg != null) {
                    String[] safeSeg = SegmentationDTO.getSafeSegmentation(seg);
                    String segPrefix = cleanCliticValue(safeSeg[0]);
                    String segStem = cleanValue(safeSeg[1]);
                    String segSuffix = cleanCliticValue(safeSeg[2]);
                    
                    // Check if this segmentation matches our pattern
                    boolean prefixMatch = (prefix.isEmpty() && (segPrefix == null || segPrefix.isEmpty())) ||
                                         (prefix.equals(segPrefix));
                    boolean stemMatch = (stem.isEmpty() && (segStem == null || segStem.isEmpty())) ||
                                       (stem.equals(segStem));
                    boolean suffixMatch = (suffix.isEmpty() && (segSuffix == null || segSuffix.isEmpty())) ||
                                         (suffix.equals(segSuffix));
                    
                    if (prefixMatch && stemMatch && suffixMatch) {
                        if (seg.getTokenId() != 0) {
                            tokenIds.add(seg.getTokenId());
                            System.out.println("  Matching segmentation: tokenId=" + seg.getTokenId() + 
                                ", prefix='" + segPrefix + "', stem='" + segStem + "', suffix='" + segSuffix + "'");
                        }
                    }
                }
            }
            
            System.out.println("Found " + tokenIds.size() + " matching token IDs");
            
            if (tokenIds.isEmpty()) {
                System.out.println("No matching token IDs found");
                return sentences;
            }
            
            // Step 2: Find sentences containing these tokens
            List<SentenceDTO> allSentences = facade.getAllSentences();
            System.out.println("Checking " + allSentences.size() + " sentences for matching tokens");
            
            int checkedSentences = 0;
            for (SentenceDTO sentence : allSentences) {
                try {
                    List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                    if (analyses == null || analyses.isEmpty()) {
                        continue;
                    }
                    
                    checkedSentences++;
                    for (AnalysisResultDTO analysis : analyses) {
                        List<TokenizationDTO> tokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                        for (TokenizationDTO token : tokens) {
                            if (tokenIds.contains(token.getTokenId())) {
                                String text = sentence.getText();
                                if (text != null && !text.trim().isEmpty()) {
                                    uniqueSentences.add(text.trim());
                                    System.out.println("  ✓ Found sentence #" + sentence.getSentenceId() + ": " + 
                                        text.substring(0, Math.min(80, text.length())) + (text.length() > 80 ? "..." : ""));
                                }
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip if analysis doesn't exist
                }
            }
            
            System.out.println("Checked " + checkedSentences + " sentences with analyses");
            System.out.println("Total unique sentences found: " + uniqueSentences.size());
            
            sentences = new ArrayList<>(uniqueSentences);
            
        } catch (Exception e) {
            System.err.println("Error getting sentences for segmentation '" + segmentation + "': " + e.getMessage());
            e.printStackTrace();
        }
        
        return sentences;
    }

    private void showSentencesDialog(String type, String item, List<String> sentences) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Sentences containing: " + item, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        // Header with custom icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BG);
        
        JLabel titleIcon = new JLabel(createSentencesDialogIcon(type));
        JLabel titleLabel = new JLabel("Sentences containing: " + item);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(getTypeColor(type));
        
        titlePanel.add(titleIcon);
        titlePanel.add(titleLabel);
        
        JLabel countLabel = new JLabel(sentences.size() + " sentences found");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(TEXT_LIGHT);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Sentences list
        JPanel sentencesPanel = new JPanel();
        sentencesPanel.setLayout(new BoxLayout(sentencesPanel, BoxLayout.Y_AXIS));
        sentencesPanel.setBackground(BG);
        
        if (sentences.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(BG);
            emptyPanel.setBorder(new EmptyBorder(50, 20, 50, 20));
            
            JLabel emptyIcon = new JLabel(createEmptySentencesIcon());
            emptyIcon.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel noSentencesLabel = new JLabel("No sentences found containing this " + type.toLowerCase(), JLabel.CENTER);
            noSentencesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noSentencesLabel.setForeground(TEXT_LIGHT);
            
            emptyPanel.add(emptyIcon, BorderLayout.NORTH);
            emptyPanel.add(noSentencesLabel, BorderLayout.CENTER);
            sentencesPanel.add(emptyPanel);
        } else {
            for (String sentence : sentences) {
                sentencesPanel.add(createSentenceCard(sentence, getTypeColor(type)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(sentencesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        // Close button with icon
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(BG);
        footerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = createSmallButton("Close", PRIMARY);
        closeBtn.setIcon(createCloseIcon());
        closeBtn.setIconTextGap(8);
        closeBtn.addActionListener(e -> dialog.dispose());
        footerPanel.add(closeBtn);
        
        dialog.add(footerPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createSentenceCard(String sentence, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        // Add sentence icon on the right
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(Color.WHITE);
        iconPanel.setPreferredSize(new Dimension(30, 0));
        
        JLabel sentenceIcon = new JLabel(createSentenceItemIcon());
        sentenceIcon.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(sentenceIcon, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea(sentence);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 16));
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(TEXT_DARK);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add colored left border
        JPanel leftBorder = new JPanel();
        leftBorder.setPreferredSize(new Dimension(5, 0));
        leftBorder.setBackground(color);
        
        card.add(leftBorder, BorderLayout.WEST);
        card.add(new JScrollPane(textArea), BorderLayout.CENTER);
        card.add(iconPanel, BorderLayout.EAST);
        
        return card;
    }

    // ==================== CUSTOM ICONS FOR SENTENCES DIALOG ====================

    private Icon createSentencesDialogIcon(String type) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 24;
                Color color = getTypeColor(type);
                
                // Document with search/magnifying glass
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f));
                
                // Document outline
                g2d.drawRoundRect(x + 4, y + 2, size - 8, size - 4, 4, 4);
                
                // Document lines
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 2; i++) {
                    g2d.drawLine(x + 8, y + 6 + i*4, x + size - 8, y + 6 + i*4);
                }
                
                // Magnifying glass overlay
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int glassSize = 10;
                g2d.drawOval(x + size - 12, y + 4, glassSize, glassSize);
                g2d.drawLine(x + size - 4, y + 10, x + size, y + 14);
                
                // Highlight effect on text
                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                g2d.fillRect(x + 10, y + 8, 8, 3);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 24; }

            @Override
            public int getIconHeight() { return 24; }
        };
    }

    private Icon createEmptySentencesIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 48;
                
                // Empty document with question mark
                g2d.setColor(TEXT_LIGHT);
                g2d.setStroke(new BasicStroke(2f));
                
                // Document outline
                g2d.drawRoundRect(x + 8, y + 6, size - 16, size - 12, 6, 6);
                
                // Document lines (faint)
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 12, y + 12 + i*6, x + size - 12, y + 12 + i*6);
                }
                
                // Question mark
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawOval(x + size/2 - 4, y + 20, 8, 8);
                g2d.drawLine(x + size/2, y + 28, x + size/2, y + 32);
                g2d.fillOval(x + size/2 - 1, y + 34, 2, 2);
                
                // Search/magnifying glass overlay
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x + 10, y + 8, 12, 12);
                g2d.drawLine(x + 18, y + 16, x + 22, y + 20);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 48; }

            @Override
            public int getIconHeight() { return 48; }
        };
    }

    private Icon createSentenceItemIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 16;
                
                // Simple speech bubble/text icon
                g2d.setColor(new Color(150, 150, 150));
                g2d.setStroke(new BasicStroke(1.5f));
                
                // Speech bubble shape
                g2d.drawRoundRect(x + 2, y + 2, size - 4, size - 6, 3, 3);
                
                // Text lines
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawLine(x + 4, y + 6, x + size - 4, y + 6);
                g2d.drawLine(x + 4, y + 9, x + size - 6, y + 9);
                g2d.drawLine(x + 4, y + 12, x + size - 8, y + 12);
                
                // Speech bubble tail
                g2d.drawLine(x + 6, y + size - 4, x + 4, y + size - 2);
                g2d.drawLine(x + 4, y + size - 2, x + 6, y + size);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private Icon createCloseIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 14;
                
                // X mark icon
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // X shape
                g2d.drawLine(x + 3, y + 3, x + size - 3, y + size - 3);
                g2d.drawLine(x + size - 3, y + 3, x + 3, y + size - 3);
                
                // Optional: Circle background (commented out as it might not look good on colored button)
                // g2d.drawOval(x + 1, y + 1, size - 2, size - 2);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 14; }

            @Override
            public int getIconHeight() { return 14; }
        };
    }

    // ==================== REMAINING METHODS ====================

    private void loadRootsCards() {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            List<RootDTO> list = facade.getAllRoots();
            Set<String> unique = new HashSet<>();
            for (RootDTO r : list) {
                if (r.getRoot() != null && !r.getRoot().trim().isEmpty()) {
                    unique.add(r.getRoot().trim().toLowerCase());
                }
            }

            if (unique.isEmpty()) {
                container.add(createEmptyCard("No roots found"));
            } else {
                unique.stream()
                    .sorted()
                    .map(this::formatRoot)
                    .forEach(root -> container.add(new DataCard(root, "Roots", ACCENT_2)));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) {
            showError("Failed to load roots: " + e.getMessage());
        }
    }

    private void loadLemmasCards() {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            List<LemmatizationDTO> list = facade.getAllLemmas();
            Set<String> unique = new HashSet<>();
            for (LemmatizationDTO l : list) {
                if (l.getLemma() != null) unique.add(l.getLemma().trim());
            }

            if (unique.isEmpty()) {
                container.add(createEmptyCard("No lemmas found"));
            } else {
                unique.stream().sorted().forEach(lemma -> 
                    container.add(new DataCard(lemma, "Lemmas", SECONDARY)));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) { 
            showError("Failed to load lemmas"); 
        }
    }

    private void loadTokensCards() {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            List<TokenizationDTO> list = facade.getAllTokens();
            Set<String> unique = new HashSet<>();
            for (TokenizationDTO t : list) {
                if (t.getToken() != null) unique.add(t.getToken().trim());
            }

            if (unique.isEmpty()) {
                container.add(createEmptyCard("No tokens found"));
            } else {
                unique.stream().sorted().forEach(token -> 
                    container.add(new DataCard(token, "Tokens", ACCENT_1)));
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) { 
            showError("Failed to load tokens"); 
        }
    }

    private void loadSegmentationsCards() {
        try {
            JPanel currentViewPanel = getCurrentViewPanel();
            if (currentViewPanel == null) return;
            
            JPanel container = (JPanel) currentViewPanel.getClientProperty("cardsContainer");
            container.removeAll();
            
            List<SegmentationDTO> list = facade.getAllSegmentations();
            
            if (list == null || list.isEmpty()) {
                container.add(createEmptyCard("No segmentations found"));
            } else {
                // Create unique segmentation display strings
                Set<String> uniqueSegmentations = new HashSet<>();
                for (SegmentationDTO seg : list) {
                    if (seg != null) {
                        // Use safe segmentation method to prevent ArrayIndexOutOfBoundsException
                        String[] safeSeg = SegmentationDTO.getSafeSegmentation(seg);
                        String displayText = formatSegmentation(safeSeg[0], safeSeg[1], safeSeg[2]);
                        if (displayText != null && !displayText.trim().isEmpty()) {
                            uniqueSegmentations.add(displayText);
                        }
                    }
                }
                
                if (uniqueSegmentations.isEmpty()) {
                    container.add(createEmptyCard("No valid segmentations found"));
                } else {
                    uniqueSegmentations.stream()
                        .sorted()
                        .forEach(segText -> container.add(new DataCard(segText, "Segmentations", PRIMARY)));
                }
            }
            
            container.revalidate();
            container.repaint();
        } catch (Exception e) {
            System.err.println("Error loading segmentations: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load segmentations: " + e.getMessage());
        }
    }
    
    /**
     * Formats segmentation parts into a display string.
     * Safely handles null or empty values and cleans Java object references.
     * 
     * @param prefix The prefix part (can be null or empty)
     * @param stem The stem part (can be null or empty)
     * @param suffix The suffix part (can be null or empty)
     * @return Formatted string showing prefix | stem | suffix
     */
    private String formatSegmentation(String prefix, String stem, String suffix) {
        // Clean object references from each part
        String p = cleanCliticValue(prefix);
        String s = cleanValue(stem);
        String suf = cleanCliticValue(suffix);
        
        p = (p != null && !p.trim().isEmpty()) ? p : "—";
        s = (s != null && !s.trim().isEmpty()) ? s : "—";
        suf = (suf != null && !suf.trim().isEmpty()) ? suf : "—";
        
        return String.format("%s | %s | %s", p, s, suf);
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
            java.util.regex.Pattern arabicPattern = java.util.regex.Pattern.compile("[\\u0600-\\u06FF]+");
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
            java.util.regex.Pattern arabicPattern = java.util.regex.Pattern.compile("[\\u0600-\\u06FF]+");
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

    private JPanel getCurrentViewPanel() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                return (JPanel) comp;
            }
        }
        return null;
    }
    private Set<String> getLemmasForRoot(String rootValue) throws SQLException {
        System.out.println("lemma for this root is : ");
        Set<String> lemmas = new HashSet<>();
        
        System.out.println("=== Getting lemmas for root: '" + rootValue + "' ===");
        
        try {
            // Use the new BLL method (most efficient and direct)
            List<LemmatizationDTO> lemmaDTOs = facade.getLemmasByRootValue(rootValue);
            System.out.println("lemma for this root is : ");
            System.out.println(lemmaDTOs);
            System.out.println("Found " + lemmaDTOs.size() + " lemmas via direct database query");
            
            for (LemmatizationDTO lemma : lemmaDTOs) {
                if (lemma != null && lemma.getLemma() != null && !lemma.getLemma().trim().isEmpty()) {
                    lemmas.add(lemma.getLemma().trim());
                    System.out.println("  Lemma: " + lemma.getLemma() + 
                                     " (confidence: " + lemma.getConfidence() + 
                                     ", tokenId: " + lemma.getTokenId() + ")");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error using direct database method: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to the original method if direct method fails
            System.out.println("Falling back to original method...");
            
            // Method 2: Try direct database relationships as fallback
            List<LemmatizationDTO> allLemmas = facade.getAllLemmas();
            List<RootDTO> allRoots = facade.getAllRoots();
            
            // Create a mapping of token IDs to roots
            Map<Integer, String> tokenIdToRoot = new HashMap<>();
            for (RootDTO root : allRoots) {
                if (root != null && root.getTokenId() != 0 && root.getRoot() != null) {
                    tokenIdToRoot.put(root.getTokenId(), root.getRoot().toLowerCase());
                }
            }
            
            // Find lemmas that share tokens with our root
            for (LemmatizationDTO lemma : allLemmas) {
                if (lemma != null && lemma.getTokenId() != 0 && lemma.getLemma() != null) {
                    String tokenRoot = tokenIdToRoot.get(lemma.getTokenId());
                    if (tokenRoot != null && tokenRoot.equalsIgnoreCase(rootValue)) {
                        lemmas.add(lemma.getLemma().trim());
                        System.out.println("Found lemma via token mapping: " + lemma.getLemma() + " for root: " + rootValue);
                    }
                }
            }
        }
        
        // If still empty, try the analysis-based approach as last resort
        if (lemmas.isEmpty()) {
            System.out.println("Database methods returned empty, trying analysis-based approach...");
            
            // Method 3: Get lemmas through analysis (original fallback method)
            List<SentenceDTO> allSentences = facade.getAllSentences();
            
            for (SentenceDTO sentence : allSentences) {
                try {
                    List<AnalysisResultDTO> analyses = facade.getAnalysisHistory(sentence.getSentenceId());
                    if (analyses == null || analyses.isEmpty()) {
                        continue;
                    }
                    
                    for (AnalysisResultDTO analysis : analyses) {
                        // Get all data for this analysis
                        List<TokenizationDTO> tokens = facade.getAllTokensByAnalysis(analysis.getAnalysisId());
                        List<LemmatizationDTO> tokenLemmas = facade.getLemmatizationsByAnalysis(analysis.getAnalysisId());
                        List<RootDTO> tokenRoots = facade.getRootsByAnalysis(analysis.getAnalysisId());
                        
                        // Check if this analysis contains our root
                        boolean hasRoot = false;
                        for (RootDTO root : tokenRoots) {
                            if (root != null && root.getRoot() != null && 
                                root.getRoot().equalsIgnoreCase(rootValue)) {
                                hasRoot = true;
                                break;
                            }
                        }
                        
                        if (hasRoot) {
                            // Add all lemmas from this analysis that belong to tokens with this root
                            for (LemmatizationDTO lemma : tokenLemmas) {
                                if (lemma != null && lemma.getLemma() != null && 
                                    !lemma.getLemma().trim().isEmpty()) {
                                    lemmas.add(lemma.getLemma().trim());
                                    System.out.println("Found lemma via analysis: " + lemma.getLemma() + " for root: " + rootValue);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip sentences without proper analysis
                    continue;
                }
            }
        }
        
        System.out.println("Total unique lemmas found for root '" + rootValue + "': " + lemmas.size());
        return lemmas;
    }

    private String extractRoot(String display) {
        // Extract Buckwalter root from format: "غ - ي - ر (غير)" -> "غير"
        // Or from format: "غ - ي - ر (gry)" -> "gry"
        String extracted = display.replaceAll(".*\\(([^)]+)\\).*", "$1").trim();
        
        // If it contains Arabic characters, it might be the Arabic form
        // Try to extract only Latin letters (Buckwalter)
        String buckwalter = extracted.replaceAll("[^A-Za-z]", "").trim();
        
        if (buckwalter.isEmpty()) {
            // If no Latin letters, return the extracted value as-is (might be Arabic)
            return extracted.toLowerCase();
        }
        
        return buckwalter.toLowerCase();
    }

    private String extractLemma(String lemma) {
        // For lemmas, just return the cleaned value
        return lemma.trim();
    }

    private String formatRoot(String bw) {
        try {
            String arabic = net.oujda_nlp_team.util.Transliteration.getInstance().getBuckWalterToArabic(bw);
            StringBuilder sb = new StringBuilder();
            for (char c : arabic.toCharArray()) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(c);
            }
            sb.append(" (").append(bw.toUpperCase()).append(")");
            return sb.toString();
        } catch (Exception e) {
            return bw.toUpperCase();
        }
    }

    private JPanel createEmptyCard(String message) {
        JPanel emptyCard = new JPanel(new BorderLayout());
        emptyCard.setPreferredSize(new Dimension(400, 80));
        emptyCard.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
        emptyCard.setOpaque(false);
        
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        messageLabel.setForeground(TEXT_LIGHT);
        
        emptyCard.add(messageLabel, BorderLayout.CENTER);
        return emptyCard;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadInitialData() {
        // Data will be loaded when user selects an option
    }

    public void refreshAll() {
        if (!currentView.isEmpty()) {
            switch (currentView) {
                case "Roots": loadRootsCards(); break;
                case "Lemmas": loadLemmasCards(); break;
                case "Tokens": loadTokensCards(); break;
                case "Segmentations": loadSegmentationsCards(); break;
            }
        }
    }
}