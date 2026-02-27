package com.arabicprose.presentation;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.FrequencyDTO;

public class FrequencyAnalysisPL extends JPanel {
    private IBusinessLayerFacade facade;

    // Modern Color Palette
    private final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private final Color TOKEN_COLOR = new Color(34, 197, 94);
    private final Color LEMMA_COLOR = new Color(234, 88, 12);
    private final Color ROOT_COLOR = new Color(168, 85, 247);
    private final Color BACKGROUND_COLOR = new Color(249, 250, 251);
    private final Color CARD_BACKGROUND = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color TABLE_HEADER_BG = new Color(248, 250, 252);
    private final Color TABLE_HOVER_COLOR = new Color(241, 245, 249);
    private final Color TABLE_SELECTION_COLOR = new Color(219, 234, 254);

    // UI Components
    private JComboBox<BookDTO> bookComboBox;
    private JComboBox<ChapterDTO> chapterComboBox;
    private JComboBox<String> analysisTypeComboBox;
    private JComboBox<String> levelComboBox;
    private JButton analyzeButton;
    private JTable resultsTable;
    private JLabel statsLabel;
    private JScrollPane resultsScrollPane;

    public FrequencyAnalysisPL(IBusinessLayerFacade facade) {
        this.facade = facade;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        initializeUI();
        loadBooks();
    }

    private void initializeUI() {
        // Main container with proper spacing
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(BACKGROUND_COLOR);
        mainContainer.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header Panel
        mainContainer.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Panel with proper layout
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(createControlPanel(), BorderLayout.NORTH);
        centerPanel.add(createResultsPanel(), BorderLayout.CENTER);

        mainContainer.add(centerPanel, BorderLayout.CENTER);
        add(mainContainer);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerPanel.setPreferredSize(new Dimension(0, 100));

        JLabel titleLabel = new JLabel("Frequency Analysis");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Analyze Token, Lemma, And Root Frequencies");
        subtitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        textPanel.setBackground(PRIMARY_COLOR);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = createCardPanel("Analysis Configuration");
        controlPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // First Row - Book and Chapter
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        controlPanel.add(createFormLabel("Book:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1;
        bookComboBox = (JComboBox<BookDTO>) createStyledComboBox();
        bookComboBox.addActionListener(e -> onBookSelected());
        controlPanel.add(bookComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        controlPanel.add(createFormLabel("Chapter:"), gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1;
        chapterComboBox = (JComboBox<ChapterDTO>) createStyledComboBox();
        controlPanel.add(chapterComboBox, gbc);

        // Second Row - Analysis Type and Level
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        controlPanel.add(createFormLabel("Analysis Type:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1;
        analysisTypeComboBox = (JComboBox<String>) createStyledComboBox();
        analysisTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Tokens", "Lemmas", "Roots"}));
        controlPanel.add(analysisTypeComboBox, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0;
        controlPanel.add(createFormLabel("Analysis Level:"), gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1;
        levelComboBox = (JComboBox<String>) createStyledComboBox();
        levelComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Book Level", "Chapter Level"}));
        levelComboBox.addActionListener(e -> onLevelChanged());
        controlPanel.add(levelComboBox, gbc);

        // Third Row - Button centered
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 15, 10, 15);
        analyzeButton = createPrimaryButton("Analyze Frequencies");
        analyzeButton.addActionListener(e -> performFrequencyAnalysis());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.add(analyzeButton);
        controlPanel.add(buttonPanel, gbc);

        return controlPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsPanel = createCardPanel("Frequency Results");
        resultsPanel.setLayout(new BorderLayout(0, 15));

        // Create enhanced table
        resultsTable = createEnhancedTable();
        
        // Create scroll pane with better styling
        resultsScrollPane = new JScrollPane(resultsTable);
        resultsScrollPane.setPreferredSize(new Dimension(0, 400)); // Increased height
        resultsScrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        
        // Remove the default scroll pane border
        resultsScrollPane.setViewportBorder(null);
        
        // Custom viewport for better appearance
        JViewport viewport = resultsScrollPane.getViewport();
        viewport.setBackground(Color.WHITE);

        // Stats panel with proper spacing
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(CARD_BACKGROUND);
        statsPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        statsLabel = new JLabel("Select Analysis Parameters And Click 'Analyze Frequencies' To See Results");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsLabel.setForeground(TEXT_PRIMARY);
        statsPanel.add(statsLabel, BorderLayout.CENTER);

        resultsPanel.add(resultsScrollPane, BorderLayout.CENTER);
        resultsPanel.add(statsPanel, BorderLayout.SOUTH);

        return resultsPanel;
    }

    private JTable createEnhancedTable() {
        JTable table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            // Add hover effect
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    
                    // Add tooltip for long text
                    String value = getValueAt(row, column) != null ? getValueAt(row, column).toString() : "";
                    if (value.length() > 20) {
                        jc.setToolTipText(value);
                    } else {
                        jc.setToolTipText(null);
                    }
                    
                    // Hover effect
                    if (isRowSelected(row)) {
                        jc.setBackground(TABLE_SELECTION_COLOR);
                    } else {
                        if (row % 2 == 0) {
                            jc.setBackground(Color.WHITE);
                        } else {
                            jc.setBackground(new Color(250, 250, 250));
                        }
                    }
                }
                return c;
            }
        };
        
        styleEnhancedTable(table);
        return table;
    }

    private void styleEnhancedTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Slightly larger font
        table.setRowHeight(42); // Increased row height for better readability
        table.setSelectionBackground(TABLE_SELECTION_COLOR);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Enhanced header
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(new Color(71, 85, 105));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 50)); // Taller header
        header.setReorderingAllowed(false);

        // Custom header renderer with better styling
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(TABLE_HEADER_BG);
                setForeground(new Color(71, 85, 105));
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 2, 0, new Color(226, 232, 240)),
                    new EmptyBorder(12, 15, 12, 15)
                ));
                return this;
            }
        });

        // Custom cell renderer for better content presentation
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(new EmptyBorder(0, 15, 0, 15));
                
                if (column == 0) {
                    // First column (Token/Lemma/Root) - left aligned
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                } else {
                    // Frequency and Percentage columns - center aligned
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    
                    // Style percentage column differently
                    if (column == 2 && value instanceof Number) {
                        double percentage = ((Number) value).doubleValue();
                        setText(String.format("%.2f%%", percentage));
                        
                        // Optional: Add color coding for high percentages
                        if (percentage > 5.0) {
                            setForeground(new Color(34, 197, 94)); // Green for high frequency
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                        }
                    }
                }
                
                return this;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Create titled border with proper spacing
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP
        );
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        titledBorder.setTitleColor(TEXT_PRIMARY);
        panel.setBorder(new CompoundBorder(
            titledBorder,
            new EmptyBorder(15, 15, 15, 15)
        ));

        return panel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setPreferredSize(new Dimension(120, 30));
        return label;
    }

    private JComboBox<?> createStyledComboBox() {
        JComboBox<Object> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(180, 35));
        comboBox.setMaximumSize(new Dimension(180, 35));
        return comboBox;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_COLOR.darker(), 1),
            new EmptyBorder(12, 30, 12, 30)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void loadBooks() {
        try {
            List<BookDTO> books = facade.getAllBooks();
            bookComboBox.removeAllItems();
            for (BookDTO book : books) {
                bookComboBox.addItem(book);
            }
            if (!books.isEmpty()) {
                onBookSelected();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading books: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBookSelected() {
        BookDTO selectedBook = (BookDTO) bookComboBox.getSelectedItem();
        if (selectedBook != null) {
            loadChapters(selectedBook.getBookId());
        }
    }

    private void loadChapters(int bookId) {
        try {
            List<ChapterDTO> chapters = facade.getChaptersByBookId(bookId);
            chapterComboBox.removeAllItems();

            // Add a placeholder item
            ChapterDTO placeholder = new ChapterDTO();
            placeholder.setChapterName("Select a Chapter");
            chapterComboBox.addItem(placeholder);

            for (ChapterDTO chapter : chapters) {
                chapterComboBox.addItem(chapter);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading chapters: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLevelChanged() {
        String selectedLevel = (String) levelComboBox.getSelectedItem();
        chapterComboBox.setEnabled("Chapter Level".equals(selectedLevel));
    }

    private void performFrequencyAnalysis() {
        BookDTO selectedBook = (BookDTO) bookComboBox.getSelectedItem();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a book first.",
                "Selection Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String analysisType = (String) analysisTypeComboBox.getSelectedItem();
        String level = (String) levelComboBox.getSelectedItem();

        try {
            List<FrequencyDTO> frequencies;
            String context;

            if ("Chapter Level".equals(level)) {
                ChapterDTO selectedChapter = (ChapterDTO) chapterComboBox.getSelectedItem();

                if (selectedChapter == null || selectedChapter.getChapterId() <= 0 ||
                    "Select a Chapter".equals(selectedChapter.getChapterName())) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a valid chapter for chapter-level analysis.",
                        "Selection Required",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if ("Tokens".equals(analysisType)) {
                    frequencies = facade.getTokenFrequenciesByChapter(selectedChapter.getChapterId());
                } else if ("Lemmas".equals(analysisType)) {
                    frequencies = facade.getLemmaFrequenciesByChapter(selectedChapter.getChapterId());
                } else {
                    frequencies = facade.getRootFrequenciesByChapter(selectedChapter.getChapterId());
                }
                context = "Chapter: " + selectedChapter.getChapterName();
            } else {
                if ("Tokens".equals(analysisType)) {
                    frequencies = facade.getTokenFrequencies(selectedBook.getBookId());
                } else if ("Lemmas".equals(analysisType)) {
                    frequencies = facade.getLemmaFrequencies(selectedBook.getBookId());
                } else {
                    frequencies = facade.getRootFrequencies(selectedBook.getBookId());
                }
                context = "Book: " + selectedBook.getTitle();
            }

            displayResults(frequencies, analysisType, context);
            updateStatistics(frequencies, analysisType, context);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error performing frequency analysis: " + ex.getMessage() +
                "\n\nPlease check if the required database tables exist.",
                "Analysis Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayResults(List<FrequencyDTO> frequencies, String analysisType, String context) {
        DefaultTableModel model = new DefaultTableModel(
            new Object[0][0],
            new String[]{
                analysisType.substring(0, analysisType.length() - 1),
                "Frequency",
                "Percentage"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) return Integer.class;
                if (column == 2) return Double.class;
                return String.class;
            }
        };

        for (FrequencyDTO frequency : frequencies) {
            model.addRow(new Object[]{
                frequency.getItem(),
                frequency.getFrequency(),
                frequency.getPercentage()
            });
        }

        resultsTable.setModel(model);

        // Set optimized column widths
        if (resultsTable.getColumnModel().getColumnCount() >= 3) {
            resultsTable.getColumnModel().getColumn(0).setPreferredWidth(350); // Wider for text
            resultsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
            resultsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
            
            // Auto-resize to fill space
            resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
    }

    private void updateStatistics(List<FrequencyDTO> frequencies, String analysisType, String context) {
        if (frequencies.isEmpty()) {
            statsLabel.setText("No " + analysisType.toLowerCase() + " found for " + context);
            return;
        }

        int totalItems = frequencies.stream().mapToInt(FrequencyDTO::getFrequency).sum();
        int uniqueItems = frequencies.size();
        FrequencyDTO mostFrequent = frequencies.get(0);

        String statsText = String.format(
            "<html><b>%s</b> | Total %s: <b>%d</b> | Unique %s: <b>%d</b> | Most Frequent: <b>%s</b> (%d times, %.1f%%)</html>",
            context,
            analysisType.toLowerCase(),
            totalItems,
            analysisType.toLowerCase(),
            uniqueItems,
            mostFrequent.getItem(),
            mostFrequent.getFrequency(),
            mostFrequent.getPercentage()
        );

        statsLabel.setText(statsText);

        // Color coding based on analysis type
        Color statsColor;
        if ("Tokens".equals(analysisType)) {
            statsColor = TOKEN_COLOR;
        } else if ("Lemmas".equals(analysisType)) {
            statsColor = LEMMA_COLOR;
        } else {
            statsColor = ROOT_COLOR;
        }

        statsLabel.setForeground(statsColor);
    }
}