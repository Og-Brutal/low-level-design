package com.apm.pl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;
import com.apm.dto.SegmentedTokenDTO;
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
     * Used to satisfy the requirement: "add frequency column if row have same stem, root and lemma"
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
            grid.add(createDataCard(rootText, count, "ROOT", () -> showLemmasByRoot(rootText)));
        });

        finalizeBrowserPage(panel, grid, "ROOTS");
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

    private void showLemmas() {
        ArrayList<LemmaDTO> rawLemmas = bl.getAllLemmas();
        ArrayList<String> lemmaStrings = new ArrayList<>();
        if(rawLemmas != null) for(LemmaDTO l : rawLemmas) lemmaStrings.add(l.getLemma());

        Map<String, Integer> data = aggregateFrequencies(lemmaStrings);

        JPanel panel = createBrowserPage("Lemma Index", "Total Dictionary Forms: " + data.size(), "MAIN");
        JPanel grid = createCardGrid();

        data.forEach((lemmaText, count) -> {
            grid.add(createDataCard(lemmaText, count, "LEMMA", () -> showTokensByLemma(lemmaText, null)));
        });

        finalizeBrowserPage(panel, grid, "LEMMAS");
    }

    private void showTokens() {
        ArrayList<TokenDTO> rawTokens = bl.getAllTokens();
        ArrayList<String> tokenStrings = new ArrayList<>();
        if(rawTokens != null) for(TokenDTO t : rawTokens) tokenStrings.add(t.getToken());

        Map<String, Integer> data = aggregateFrequencies(tokenStrings);

        JPanel panel = createBrowserPage("Token Index", "Total Unique Tokens: " + data.size(), "MAIN");
        JPanel grid = createCardGrid();

        data.forEach((tokenText, count) -> {
            grid.add(createDataCard(tokenText, count, "TOKEN", null));
        });

        finalizeBrowserPage(panel, grid, "TOKENS");
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

 // --- UPDATED IMPLEMENTATION FOR SEGMENTS (With Token Column) ---
    private void showSegments() {
        ArrayList<SegmentedTokenDTO> rawSegments = bl.getAllSegments();
        ArrayList<String> segmentStrings = new ArrayList<>();

        if (rawSegments != null) {
            for (SegmentedTokenDTO s : rawSegments) {
                // 1. Extract components safely
                // IMPORTANT: Ensure s.getTokenText() exists in your DTO. 
                // If your DTO field is named 'token', use s.getToken().
                String token  = (bl.getTokenText(s.getTokenId()) == null || bl.getTokenText(s.getTokenId()).isEmpty()) ? "?" : bl.getTokenText(s.getTokenId());
                
                String prefix = (s.getPrefix() == null || s.getPrefix().isEmpty()) ? "-" : s.getPrefix();
                String stem   = (s.getStem() == null   || s.getStem().isEmpty())   ? "-" : s.getStem();
                String lemma  = (s.getLemma() == null  || s.getLemma().isEmpty())  ? "-" : s.getLemma();
                String root   = (s.getRoot() == null   || s.getRoot().isEmpty())   ? "-" : s.getRoot();

                // 2. Create composite key: Token :: Prefix :: Stem :: Lemma :: Root
                String compositeKey = token + "::" + prefix + "::" + stem + "::" + lemma + "::" + root;
                segmentStrings.add(compositeKey);
            }
        }

        // 3. Aggregate frequencies
        Map<String, Integer> data = aggregateFrequencies(segmentStrings);

        JPanel panel = createBrowserPage("Segments", "Token & Morphological Breakdown", "MAIN");
        JPanel grid = createCardGrid();

        // 4. Generate Cards with HTML Layout
        data.forEach((compositeKey, count) -> {
            String[] parts = compositeKey.split("::");
            // Extract parts based on the order defined in step 2
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
                    "<table width='100%' cellspacing='0' cellpadding='1'>" +
                    "  <tr><td width='45' style='color:#95a5a6; font-size:8px'>Prefix:</td><td style='font-size:9px'>" + pfx + "</td></tr>" +
                    "  <tr><td style='color:#95a5a6; font-size:8px'>Stem:</td><td style='font-size:9px'><b>" + stm + "</b></td></tr>" +
                    "  <tr><td style='color:#95a5a6; font-size:8px'>Lemma:</td><td style='font-size:9px'>" + lem + "</td></tr>" +
                    "  <tr><td style='color:#95a5a6; font-size:8px'>Root:</td><td style='font-size:9px'>" + rt + "</td></tr>" +
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

        JButton backBtn = createStyledBackButton(backTarget);

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
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Arabic text may need bigger font
        lblText.setForeground(TEXT_PRIMARY);

        card.add(topRow, BorderLayout.NORTH);
        card.add(lblText, BorderLayout.CENTER);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && onDoubleClick != null) {
                    onDoubleClick.run();
                } else {
                    selectionManager.selectCard(card);
                }
            }
        });

        return card;
    }

    private JButton createStyledBackButton(String targetId) {
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
        btn.addActionListener(e -> cardLayout.show(cardsPanel, targetId));
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
}