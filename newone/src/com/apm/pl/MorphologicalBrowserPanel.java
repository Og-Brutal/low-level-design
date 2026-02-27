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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.LemmaDTO;
import com.apm.dto.RootDTO;
import com.apm.dto.TokenDTO;

public class MorphologicalBrowserPanel extends JPanel {
    
    // --- Professional Color Palette (Material/Modern Web) ---
    private static final Color BG_COLOR = new Color(245, 247, 250);      // Light Grey/Blue Web Background
    private static final Color HEADER_BG = Color.WHITE;                  // Pure White Header
    private static final Color CARD_BG = Color.WHITE;                    // Card Background
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);  // Professional Blue
    private static final Color ACCENT_DARK = new Color(41, 128, 185);    // Darker Blue for hovers
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);     // Dark Charcoal (Headings)
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);// Grey (Subtitles)
    private static final Color BORDER_COLOR = new Color(224, 224, 224);  // Subtle Border

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

    // ================= MAIN MENU (DASHBOARD STYLE) ====================
    private JPanel createMainMenuPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BG_COLOR);

        // 1. Dashboard Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(25, 40, 25, 40)
        ));
        
        JLabel title = new JLabel("Morphological Dictionary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        
        JLabel subtitle = new JLabel("Select a category to begin browsing the corpus");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(HEADER_BG);
        textPanel.add(title);
        textPanel.add(subtitle);
        header.add(textPanel, BorderLayout.CENTER);

        // 2. Tile Grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 30, 30));
        grid.setBackground(BG_COLOR);
        grid.setBorder(new EmptyBorder(40, 60, 40, 60));

        grid.add(createDashboardTile("Browse Roots", "View all logical roots", "ROOT", this::showRoots));
        grid.add(createDashboardTile("Browse Lemmas", "View dictionary forms", "LEMMA", this::showLemmas));
        grid.add(createDashboardTile("Browse Tokens", "View individual occurrences", "TOKEN", this::showTokens));
        grid.add(createDashboardTile("Analysis Segments", "Deep morphological breakdown", "SEGMENT", 
                () -> JOptionPane.showMessageDialog(this, "Module under construction.")));

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
                
                // Shadow / Elevation effect
                if(isHover) {
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 15, 15);
                }

                // Card Body
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);

                // Border
                g2d.setColor(isHover ? PRIMARY_COLOR : BORDER_COLOR);
                g2d.setStroke(new BasicStroke(isHover ? 2f : 1f));
                g2d.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 15, 15);
                
                // Accent Bar (Left side)
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, 6, getHeight()-5, 15, 15);
                // Fix corner of accent bar to be square on right
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

    // ================= DATA LOADING METHODS ====================
    private void showRoots() {
        ArrayList<RootDTO> roots = bl.getAllRoots();
        JPanel panel = createBrowserPage("Root Index", "Comprehensive list of all roots", "MAIN");
        
        JPanel grid = createCardGrid();
        if (roots != null) {
            for (RootDTO r : roots) {
                grid.add(createDataCard(r.getRoot(), "ROOT", () -> showLemmasByRoot(r.getRoot())));
            }
        }
        
        finalizeBrowserPage(panel, grid, "ROOTS");
    }

    private void showLemmasByRoot(String root) {
        ArrayList<LemmaDTO> lemmas = bl.getLemmaByRoot(root);
        JPanel panel = createBrowserPage("Lemmas", "Derived from root: " + root, "ROOTS");
        
        JPanel grid = createCardGrid();
        if (lemmas != null) {
            for (LemmaDTO lemma : lemmas) {
                grid.add(createDataCard(lemma.getLemma(), "LEMMA", () -> showTokensByLemma(lemma.getLemma(), root)));
            }
        }
        finalizeBrowserPage(panel, grid, "LEMMA_BY_ROOT_" + root);
    }

    private void showLemmas() {
        ArrayList<LemmaDTO> lemmas = bl.getAllLemmas();
        JPanel panel = createBrowserPage("Lemma Index", "All dictionary forms", "MAIN");
        
        JPanel grid = createCardGrid();
        if (lemmas != null) {
            for (LemmaDTO lemma : lemmas) {
                grid.add(createDataCard(lemma.getLemma(), "LEMMA", () -> showTokensByLemma(lemma.getLemma(), null)));
            }
        }
        finalizeBrowserPage(panel, grid, "LEMMAS");
    }

    private void showTokens() {
        ArrayList<TokenDTO> tokens = bl.getAllTokens();
        JPanel panel = createBrowserPage("Token Index", "All distinct tokens in corpus", "MAIN");
        
        JPanel grid = createCardGrid();
        if (tokens != null) {
            for (TokenDTO t : tokens) {
                grid.add(createDataCard(t.getToken(), "TOKEN", null));
            }
        }
        finalizeBrowserPage(panel, grid, "TOKENS");
    }

    private void showTokensByLemma(String lemma, String root) {
        ArrayList<TokenDTO> tokens = bl.getTokensByLemma(lemma);
        String backTarget = (root == null) ? "LEMMAS" : "LEMMA_BY_ROOT_" + root;
        
        JPanel panel = createBrowserPage("Tokens", "Instances of lemma: " + lemma, backTarget);
        
        JPanel grid = createCardGrid();
        if (tokens != null) {
            for (TokenDTO t : tokens) {
                grid.add(createDataCard(t.getToken(), "TOKEN", null));
            }
        }
        finalizeBrowserPage(panel, grid, "TOKENS_BY_" + lemma);
    }

    // ================= LAYOUT HELPERS ====================
    
    private JPanel createBrowserPage(String title, String subtitle, String backTarget) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // Professional Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(15, 20, 15, 20)
        ));

        // Back Button
        JButton backBtn = createStyledBackButton(backTarget);
        
        // Text Container
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setBackground(HEADER_BG);
        titles.setBorder(new EmptyBorder(0, 20, 0, 0)); // Spacing between back button and text
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(TEXT_SECONDARY);
        
        titles.add(lblTitle);
        titles.add(lblSub);

        // Left side wrapper
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
        // GridLayout(0, 4) ensures 4 columns, infinite rows
        JPanel grid = new JPanel(new GridLayout(0, 5, 15, 15)); 
        grid.setBackground(BG_COLOR);
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Click whitespace to deselect
        grid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectionManager.clearSelection();
            }
        });
        return grid;
    }

    // ================= PROFESSIONAL CARD RENDERER ====================
    private JPanel createDataCard(String text, String badgeText, Runnable onDoubleClick) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Boolean selected = (Boolean) getClientProperty("selected");
                boolean isSelected = (selected != null && selected);
                
                // Dimensions
                int w = getWidth();
                int h = getHeight();
                int arc = 12;

                // Background
                g2d.setColor(isSelected ? new Color(235, 245, 251) : CARD_BG);
                g2d.fillRoundRect(0, 0, w-1, h-1, arc, arc);

                // Border
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

        // Card Layout
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 90)); // Fixed size for grid
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Badge (Top Right)
        JLabel lblBadge = new JLabel(badgeText, SwingConstants.RIGHT);
        lblBadge.setFont(new Font("SansSerif", Font.BOLD, 9));
        lblBadge.setForeground(Color.LIGHT_GRAY);
        
        // Main Text (Center)
        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblText.setForeground(TEXT_PRIMARY);

        card.add(lblBadge, BorderLayout.NORTH);
        card.add(lblText, BorderLayout.CENTER);

        // Interactions
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
            
            @Override
            public void mouseEntered(MouseEvent e) {
               if(!Boolean.TRUE.equals(card.getClientProperty("selected"))) {
                   // Simple hover visual could be added here if desired
                   // For now, we rely on cursor change
               }
            }
        });

        return card;
    }

    // ================= STYLED CONTROLS ====================
    private JButton createStyledBackButton(String targetId) {
        JButton btn = new JButton("← Back");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PRIMARY_COLOR);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));
        btn.setPreferredSize(new Dimension(90, 35));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Simple hover effect
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

    // ================= SELECTION MANAGER ====================
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