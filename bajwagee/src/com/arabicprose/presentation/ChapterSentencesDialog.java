package com.arabicprose.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

public class ChapterSentencesDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private ChapterDTO currentChapter;
    private JTable sentencesTable;
    private JButton addSentenceBtn, editSentenceBtn, deleteSentenceBtn, processTextBtn, closeBtn;
    private List<SentenceDTO> currentSentences;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(231, 76, 60);

    public ChapterSentencesDialog(IBusinessLayerFacade facade, BookDTO book, ChapterDTO chapter) {
        this.facade = facade;
        this.currentBook = book;
        this.currentChapter = chapter;
        setTitle("Sentences - " + chapter.getChapterName() + " - " + book.getTitle());
        setModal(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        setupLayout();
        loadSentences();
        setupEventListeners();
    }

    private void initializeComponents() {
        // Create table with custom renderer for proper text wrapping
        sentencesTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Set custom row height for wrapped text
        sentencesTable.setRowHeight(60);
        sentencesTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        sentencesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        sentencesTable.setShowGrid(true);
        sentencesTable.setGridColor(new Color(240, 240, 240));
        
        addSentenceBtn = createStyledButton("Add Sentence", ACCENT_COLOR);
        editSentenceBtn = createStyledButton("Edit Sentence", PRIMARY_COLOR);
        deleteSentenceBtn = createStyledButton("Delete Sentence", WARNING_COLOR);
        processTextBtn = createStyledButton("Process Text", new Color(155, 89, 182));
        closeBtn = createStyledButton("Close", new Color(149, 165, 166));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void setupLayout() {
        // Header
        JLabel headerLabel = new JLabel("<html><center>Sentences in: " + currentChapter.getChapterName() + 
            "<br>Book: " + currentBook.getTitle() + "</center></html>");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Table with proper wrapping
        JScrollPane tableScroll = new JScrollPane(sentencesTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Sentences List (Text wraps like real book)"));
        add(tableScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addSentenceBtn);
        buttonPanel.add(editSentenceBtn);
        buttonPanel.add(deleteSentenceBtn);
        buttonPanel.add(processTextBtn);
        buttonPanel.add(closeBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSentences() {
        try {
            // Get all sentences for the book and filter by chapter
            List<SentenceDTO> allSentences = facade.getSentencesByBookId(currentBook.getBookId());
            currentSentences = allSentences.stream()
                .filter(s -> s.getChapterId() == currentChapter.getChapterId())
                .sorted((s1, s2) -> Integer.compare(s1.getSentenceNumber(), s2.getSentenceNumber()))
                .collect(Collectors.toList());
            
            DefaultTableModel model = new DefaultTableModel(
                new Object[0][0],
                new String[]{"#", "Arabic Text", "Translation", "Diacritized", "Notes"}
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
                
                @Override
                public Class<?> getColumnClass(int column) {
                    return String.class;
                }
            };
            
            for (SentenceDTO sentence : currentSentences) {
                model.addRow(new Object[]{
                    String.valueOf(sentence.getSentenceNumber()),
                    sentence.getText(),
                    sentence.getTranslation() != null ? sentence.getTranslation() : "",
                    sentence.getTextDiacritized() != null ? sentence.getTextDiacritized() : "",
                    sentence.getNotes() != null ? sentence.getNotes() : ""
                });
            }
            
            sentencesTable.setModel(model);
            
            // Set up text wrapping for all columns
            setupTextWrapping();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading sentences: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTextWrapping() {
        // Create a custom cell renderer that wraps text
        TableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set Arabic font and right-to-left for Arabic text column
                if (column == 1) { // Arabic Text column
                    setFont(new Font("Traditional Arabic", Font.PLAIN, 16));
                    setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);
                } else if (column == 2 || column == 3) { // Translation and Diacritized columns
                    setFont(new Font("Arial", Font.PLAIN, 12));
                } else if (column == 4) { // Notes column
                    setFont(new Font("Arial", Font.ITALIC, 11));
                    setForeground(Color.GRAY);
                } else { // Sentence number column
                    setFont(new Font("Arial", Font.BOLD, 12));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBackground(new Color(245, 245, 245));
                }
                
                // Enable text wrapping
                setText("<html><body style='width: 100%; padding: 5px;'>" + 
                       (value != null ? value.toString() : "") + "</body></html>");
                
                return this;
            }
        };

        // Apply the renderer to all columns
        for (int i = 0; i < sentencesTable.getColumnCount(); i++) {
            sentencesTable.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }

        // Set column widths for better readability
        TableColumn column;
        column = sentencesTable.getColumnModel().getColumn(0); // Sentence #
        column.setPreferredWidth(50);
        column.setMaxWidth(80);
        
        column = sentencesTable.getColumnModel().getColumn(1); // Arabic Text
        column.setPreferredWidth(300);
        
        column = sentencesTable.getColumnModel().getColumn(2); // Translation
        column.setPreferredWidth(250);
        
        column = sentencesTable.getColumnModel().getColumn(3); // Diacritized
        column.setPreferredWidth(200);
        
        column = sentencesTable.getColumnModel().getColumn(4); // Notes
        column.setPreferredWidth(150);
    }

    private void setupEventListeners() {
        addSentenceBtn.addActionListener(e -> addSentence());
        editSentenceBtn.addActionListener(e -> editSentence());
        deleteSentenceBtn.addActionListener(e -> deleteSentence());
        processTextBtn.addActionListener(e -> processText());
        closeBtn.addActionListener(e -> dispose());
    }

    private void addSentence() {
        SentenceDialog dialog = new SentenceDialog(facade, currentBook, currentChapter, null);
        dialog.setVisible(true);
        loadSentences(); // Refresh the list
    }

    private void editSentence() {
        int row = sentencesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sentence to edit.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SentenceDTO sentence = currentSentences.get(row);
        SentenceDialog dialog = new SentenceDialog(facade, currentBook, currentChapter, sentence);
        dialog.setVisible(true);
        loadSentences(); // Refresh the list
    }

    private void deleteSentence() {
        int row = sentencesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sentence to delete.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SentenceDTO sentence = currentSentences.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this sentence?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.deleteSentence(sentence.getSentenceId());
                loadSentences();
                JOptionPane.showMessageDialog(this, "Sentence deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting sentence: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processText() {
        TextProcessingDialog dialog = new TextProcessingDialog(facade, currentBook, currentChapter);
        dialog.setVisible(true);
        loadSentences(); // Refresh the list
    }
}