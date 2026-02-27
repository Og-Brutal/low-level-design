package com.arabicprose.presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

import com.arabicprose.bll.*;
import com.arabicprose.dto.*;
import java.util.regex.PatternSyntaxException;

public class SearchPL extends JPanel {

    private final IBusinessLayerFacade facade;
    private final ISearchBO searchBO;

    // Modern Color Palette
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color REGEX_COLOR = new Color(139, 92, 246);
    private final Color SIMILARITY_COLOR = new Color(34, 197, 94);
    private final Color BACKGROUND_GRADIENT_TOP = new Color(249, 250, 251);
    private final Color BACKGROUND_GRADIENT_BOTTOM = new Color(243, 244, 246);
    private final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private final Color BORDER_COLOR = new Color(226, 232, 240);

    public SearchPL(IBusinessLayerFacade facade) {
        this.facade = facade;
        this.searchBO = new SearchBO(facade);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRADIENT_TOP);
        setupLayout();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_GRADIENT_TOP, 0, getHeight(), BACKGROUND_GRADIENT_BOTTOM);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    private void setupLayout() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel headerPanel = createHeaderPanel();
        JPanel buttonsPanel = createButtonsPanel();

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(buttonsPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, SIMILARITY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        panel.setPreferredSize(new Dimension(0, 140));

        JLabel titleLabel = new JLabel("Sentence Search");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

      
       

        JPanel textPanel = new JPanel(new BorderLayout(0, 15));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.CENTER);
        

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(textPanel, BorderLayout.CENTER);

        panel.add(centerContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

        JButton exactMatchBtn = createLargeButton("Exact Match", "Find Exact Text Matches", PRIMARY_COLOR, createExactMatchIcon());
        JButton regexSearchBtn = createLargeButton("Regex Search", "Search Using Regular Expressions", REGEX_COLOR, createRegexSearchIcon());
        JButton similarSentencesBtn = createLargeButton("Similar Sentences", "Find Similar Sentences Using N-Grams", SIMILARITY_COLOR, createSimilarityIcon());

        exactMatchBtn.addActionListener(e -> openExactMatchDialog());
        regexSearchBtn.addActionListener(e -> openRegexSearchDialog());
        similarSentencesBtn.addActionListener(e -> openSimilarSentencesDialog());

        panel.add(exactMatchBtn);
        panel.add(regexSearchBtn);
        panel.add(similarSentencesBtn);

        return panel;
    }

    private JButton createLargeButton(String title, String description, Color bgColor, Icon icon) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, bgColor, getWidth(), getHeight(), bgColor.darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(bgColor.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setLayout(new BorderLayout(20, 20));
        button.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        button.setPreferredSize(new Dimension(300, 250));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(255, 255, 255, 230));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setOpaque(false);
        content.add(iconLabel, BorderLayout.NORTH);
        content.add(titleLabel, BorderLayout.CENTER);
        content.add(descLabel, BorderLayout.SOUTH);

        button.add(content, BorderLayout.CENTER);

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setLocation(button.getX(), button.getY() - 5); }
            @Override public void mouseExited(MouseEvent e) { button.setLocation(button.getX(), button.getY() + 5); }
        });

        return button;
    }

    // ==================== YOUR ORIGINAL BEAUTIFUL CUSTOM ICONS ====================

    private Icon createExactMatchIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int size = 80;

                // Target background
                GradientPaint targetGradient = new GradientPaint(x, y, new Color(255, 255, 255, 220),
                        x + size, y + size, new Color(255, 255, 255, 180));
                g2d.setPaint(targetGradient);
                g2d.fillOval(x + 5, y + 5, size - 10, size - 10);

                // Target circles
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawOval(x + 10, y + 10, size - 20, size - 20);
                g2d.drawOval(x + 20, y + 20, size - 40, size - 40);
                g2d.drawOval(x + 30, y + 30, size - 60, size - 60);

                // Bullseye
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x + size/2 - 5, y + size/2 - 5, 10, 10);
                g2d.setColor(new Color(200, 50, 50));
                g2d.fillOval(x + size/2 - 3, y + size/2 - 3, 6, 6);

                // Magnifying glass
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawOval(x + 20, y + 15, size - 30, size - 30);
                g2d.drawLine(x + size - 15, y + size - 20, x + size - 5, y + size - 10);

                // Shine
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval(x + 25, y + 20, 15, 10);

                g2d.dispose();
            }
            @Override public int getIconWidth() { return 80; }
            @Override public int getIconHeight() { return 80; }
        };
    }

    private Icon createRegexSearchIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int size = 80;

                GradientPaint circuitGradient = new GradientPaint(x, y, new Color(255, 255, 255, 220),
                        x + size, y + size, new Color(255, 255, 255, 180));
                g2d.setPaint(circuitGradient);
                g2d.fillRoundRect(x + 5, y + 5, size - 10, size - 10, 15, 15);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(x + 5, y + 5, size - 10, size - 10, 15, 15);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawLine(x + 15, y + 20, x + size - 15, y + 20);
                g2d.drawLine(x + 15, y + 40, x + size - 15, y + 40);
                g2d.drawLine(x + 15, y + 60, x + size - 15, y + 60);
                g2d.drawLine(x + 30, y + 20, x + 30, y + 40);
                g2d.drawLine(x + 50, y + 40, x + 50, y + 60);
                g2d.drawLine(x + 70, y + 20, x + 70, y + 60);

                g2d.setColor(new Color(100, 200, 255));
                g2d.fillOval(x + 15, y + 20, 6, 6);
                g2d.fillOval(x + 30, y + 20, 6, 6);
                g2d.fillOval(x + 50, y + 40, 6, 6);
                g2d.fillOval(x + 70, y + 20, 6, 6);
                g2d.fillOval(x + 70, y + 60, 6, 6);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
                g2d.drawString(".*", x + 35, y + 35);
                g2d.drawString("+", x + 55, y + 55);
                g2d.drawString("?", x + 25, y + 55);

                g2d.setColor(new Color(255, 255, 100));
                int[] lx = {x + 60, x + 65, x + 55, x + 60, x + 50, x + 55};
                int[] ly = {y + 25, y + 30, y + 35, y + 30, y + 25, y + 20};
                g2d.fillPolygon(lx, ly, 6);

                g2d.setColor(new Color(100, 200, 255, 80));
                g2d.fillOval(x + 20, y + 15, 40, 40);

                g2d.dispose();
            }
            @Override public int getIconWidth() { return 80; }
            @Override public int getIconHeight() { return 80; }
        };
    }

    private Icon createSimilarityIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int size = 80;

                GradientPaint bg = new GradientPaint(x, y, new Color(255, 255, 255, 220),
                        x + size, y + size, new Color(255, 255, 255, 180));
                g2d.setPaint(bg);
                g2d.fillRoundRect(x + 5, y + 5, size - 10, size - 10, 20, 20);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(x + 5, y + 5, size - 10, size - 10, 20, 20);

                int c1x = x + 25, c2x = x + 45, cy = y + 40, cs = 30;
                g2d.setColor(new Color(100, 200, 255, 180));
                g2d.fillOval(c1x, cy, cs, cs);
                g2d.setColor(new Color(255, 150, 100, 180));
                g2d.fillOval(c2x, cy, cs, cs);
                g2d.setColor(new Color(100, 255, 150, 200));
                g2d.fillOval(c1x + 10, cy, cs, cs);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawOval(c1x, cy, cs, cs);
                g2d.drawOval(c2x, cy, cs, cs);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2d.drawString("N=2", x + 35, y + 25);
                g2d.drawString("N=3", x + 55, y + 65);

                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("75%", x + 40, y + 45);

                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(c1x + 5, cy + 15, c2x + 25, cy + 15);
                g2d.drawLine(c1x + 5, cy + 25, c2x + 25, cy + 25);

                g2d.dispose();
            }
            @Override public int getIconWidth() { return 80; }
            @Override public int getIconHeight() { return 80; }
        };
    }

    // Dialog openers
    private void openExactMatchDialog() { new SearchDialog(null, "Exact Match Search", false).setVisible(true); }
    private void openRegexSearchDialog() { new SearchDialog(null, "Regex Search", true).setVisible(true); }
    private void openSimilarSentencesDialog() { new SimilarSentencesDialog(null, "Similar Sentences Search").setVisible(true); }

    // ====================== EXACT / REGEX SEARCH DIALOG ======================
    private class SearchDialog extends JDialog {
        private final JTextField searchField = new JTextField(30);
        private final JButton searchButton = new JButton("Search");
        private final JTable resultsTable = new JTable();
        private final boolean isRegexMode;

        public SearchDialog(Frame parent, String title, boolean regexMode) {
            super(parent, title, true);
            this.isRegexMode = regexMode;
            setSize(1000, 650);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            initComponents();
            setupLayout();
        }

        private void initComponents() {
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            searchField.addActionListener(e -> performSearch());

            searchButton.setBackground(isRegexMode ? REGEX_COLOR : PRIMARY_COLOR);
            searchButton.setForeground(Color.WHITE);
            searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            searchButton.addActionListener(e -> performSearch());

            resultsTable.setRowHeight(50);
            resultsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            styleTable(resultsTable);
        }

        private void setupLayout() {
            JPanel main = new JPanel(new BorderLayout(15, 15));
            main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            main.setBackground(Color.WHITE);

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            top.setBackground(Color.WHITE);
            top.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR), "Search Term" + (isRegexMode ? " (Regex)" : ""),
                    0, 0, new Font("Segoe UI", Font.BOLD, 13), TEXT_PRIMARY));
            top.add(searchField);
            top.add(searchButton);

            JScrollPane scroll = new JScrollPane(resultsTable);
            scroll.setBorder(BorderFactory.createEmptyBorder());

            main.add(top, BorderLayout.NORTH);
            main.add(scroll, BorderLayout.CENTER);
            add(main);
        }

        private void performSearch() {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                SearchDTO dto = new SearchDTO();
                dto.setSearchText(text);
                dto.setRegex(isRegexMode);

                List<SentenceDTO> results = searchBO.performTextSearch(dto);

                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No results found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    resultsTable.setModel(new DefaultTableModel());
                    return;
                }

                displayResults(results);

            } catch (PatternSyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Invalid regex: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void displayResults(List<SentenceDTO> sentences) {
            String[] cols = {"Book", "Chapter", "Sentence #", "Arabic Text", "Translation", "Notes"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            for (SentenceDTO s : sentences) {
                String bookTitle = "Unknown";
                String chapterName = "";
                try {
                    BookDTO b = facade.getBookById(s.getBookId());
                    if (b != null) bookTitle = b.getTitle();
                    if (s.getChapterId() != 0) {
                        ChapterDTO c = facade.getChapterById(s.getChapterId());
                        if (c != null) chapterName = c.getChapterName();
                    }
                } catch (SQLException ignored) {}

                model.addRow(new Object[]{
                    bookTitle,
                    chapterName,
                    s.getSentenceNumber(),
                    s.getText() != null ? s.getText() : "",
                    s.getTranslation() != null ? s.getTranslation() : "",
                    s.getNotes() != null ? s.getNotes() : ""
                });
            }

            resultsTable.setModel(model);
            resultsTable.getColumnModel().getColumn(3).setPreferredWidth(400);
            resultsTable.getColumnModel().getColumn(4).setPreferredWidth(350);
        }
    }

    // ====================== SIMILAR SENTENCES DIALOG ======================
    private class SimilarSentencesDialog extends JDialog {
        private final JTextField inputField = new JTextField(40);
        private final JComboBox<String> nGramBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        private final JSlider thresholdSlider = new JSlider(0, 100, 60);
        private final JLabel thresholdLabel = new JLabel("60%");
        private final JTable resultsTable = new JTable();

        public SimilarSentencesDialog(Frame parent, String title) {
            super(parent, title, true);
            setSize(1100, 700);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            nGramBox.setSelectedIndex(1); // default bigrams
            thresholdSlider.addChangeListener(e -> thresholdLabel.setText(thresholdSlider.getValue() + "%"));
            resultsTable.setRowHeight(55);
            styleTable(resultsTable);

            setupUI();
        }

        private void setupUI() {
            JPanel main = new JPanel(new BorderLayout(15, 15));
            main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            main.setBackground(Color.WHITE);

            // Controls
            JPanel controls = new JPanel(new GridBagLayout());
            controls.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0; gbc.gridy = 0;
            controls.add(new JLabel("Input Sentence:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            controls.add(inputField, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
            controls.add(new JLabel("N-Gram Size:"), gbc);
            gbc.gridx = 1;
            controls.add(nGramBox, gbc);

            gbc.gridx = 2;
            controls.add(new JLabel("Threshold:"), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            controls.add(thresholdSlider, gbc);
            gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE;
            controls.add(thresholdLabel, gbc);

            JButton searchBtn = new JButton("Find Similar Sentences");
            searchBtn.setBackground(SIMILARITY_COLOR);
            searchBtn.setForeground(Color.WHITE);
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            searchBtn.addActionListener(e -> performSearch());

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            top.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Search Configuration"));
            top.add(controls, BorderLayout.CENTER);
            top.add(searchBtn, BorderLayout.SOUTH);

            JScrollPane scroll = new JScrollPane(resultsTable);

            main.add(top, BorderLayout.NORTH);
            main.add(scroll, BorderLayout.CENTER);
            add(main);
        }

        private void performSearch() {
            String text = inputField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a sentence.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int n = Integer.parseInt((String) nGramBox.getSelectedItem());
            double threshold = thresholdSlider.getValue() / 100.0;

            try {
                SearchDTO dto = new SearchDTO();
                dto.setSearchText(text);
                dto.setNGramSize(n);
                dto.setSimilarityThreshold(threshold);

                List<SentenceBO.SimilarSentenceResult> results = searchBO.performSimilaritySearch(dto);

                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No similar sentences found.\nTry lowering the threshold.", "No Results", JOptionPane.INFORMATION_MESSAGE);
                    resultsTable.setModel(new DefaultTableModel());
                    return;
                }

                displayResults(results);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void displayResults(List<SentenceBO.SimilarSentenceResult> results) {
            String[] cols = {"Similarity", "Book", "Chapter", "Sentence #", "Arabic Text", "Translation"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            for (SentenceBO.SimilarSentenceResult r : results) {
                SentenceDTO s = r.getSentence();
                String book = "Unknown", chapter = "";
                try {
                    BookDTO b = facade.getBookById(s.getBookId());
                    if (b != null) book = b.getTitle();
                    if (s.getChapterId() != 0) {
                        ChapterDTO c = facade.getChapterById(s.getChapterId());
                        if (c != null) chapter = c.getChapterName();
                    }
                } catch (SQLException ignored) {}

                model.addRow(new Object[]{
                    String.format("%.2f%%", r.getSimilarityScore() * 100),
                    book,
                    chapter,
                    s.getSentenceNumber(),
                    s.getText(),
                    s.getTranslation()
                });
            }

            resultsTable.setModel(model);
            resultsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            resultsTable.getColumnModel().getColumn(4).setPreferredWidth(400);
        }
    }

    // Shared table styling
    private void styleTable(JTable table) {
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s) comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return comp;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(71, 85, 105));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}