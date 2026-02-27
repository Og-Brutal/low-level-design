package com.arabicprose.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;

public class BookChaptersDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private JTable chaptersTable;
    private JButton addChapterBtn, editChapterBtn, deleteChapterBtn, viewSentencesBtn, closeBtn;
    private List<ChapterDTO> currentChapters;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(231, 76, 60);

    public BookChaptersDialog(IBusinessLayerFacade facade, BookDTO book) {
        this.facade = facade;
        this.currentBook = book;
        setTitle("Chapters - " + book.getTitle());
        setModal(true);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        setupLayout();
        loadChapters();
        setupEventListeners();
    }

    private void initializeComponents() {
        chaptersTable = new JTable();
        chaptersTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        chaptersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chaptersTable.setRowHeight(30);
        
        addChapterBtn = createStyledButton("Add Chapter", ACCENT_COLOR);
        editChapterBtn = createStyledButton("Edit Chapter", PRIMARY_COLOR);
        deleteChapterBtn = createStyledButton("Delete Chapter", WARNING_COLOR);
        viewSentencesBtn = createStyledButton("View Sentences", new Color(155, 89, 182));
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
        JLabel headerLabel = new JLabel("Chapters in: " + currentBook.getTitle());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Table
        JScrollPane tableScroll = new JScrollPane(chaptersTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Chapters List"));
        add(tableScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(addChapterBtn);
        buttonPanel.add(editChapterBtn);
        buttonPanel.add(deleteChapterBtn);
        buttonPanel.add(viewSentencesBtn);
        buttonPanel.add(closeBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadChapters() {
        try {
            currentChapters = facade.getChaptersByBookId(currentBook.getBookId());
            
            DefaultTableModel model = new DefaultTableModel(
                new Object[0][0],
                new String[]{"Chapter Order", "Chapter Name", "Description", "Sentences Count"}
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (ChapterDTO chapter : currentChapters) {
                int sentencesCount = 0;
                try {
                    sentencesCount = facade.getSentencesByBookId(currentBook.getBookId()).stream()
                        .filter(s -> s.getChapterId() == chapter.getChapterId())
                        .toArray().length;
                } catch (SQLException e) {
                    // If we can't get sentences count, use 0
                }
                
                model.addRow(new Object[]{
                    chapter.getChapterOrder(),
                    chapter.getChapterName(),
                    chapter.getDescription() != null ? chapter.getDescription() : "",
                    sentencesCount + " sentences"
                });
            }
            
            chaptersTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading chapters: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupEventListeners() {
        addChapterBtn.addActionListener(e -> addChapter());
        editChapterBtn.addActionListener(e -> editChapter());
        deleteChapterBtn.addActionListener(e -> deleteChapter());
        viewSentencesBtn.addActionListener(e -> viewSentences());
        closeBtn.addActionListener(e -> dispose());
    }

    private void addChapter() {
        ChapterDialog dialog = new ChapterDialog(facade, currentBook, null);
        dialog.setVisible(true);
        loadChapters(); // Refresh the list
    }

    private void editChapter() {
        int row = chaptersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to edit.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        ChapterDialog dialog = new ChapterDialog(facade, currentBook, chapter);
        dialog.setVisible(true);
        loadChapters(); // Refresh the list
    }

    private void deleteChapter() {
        int row = chaptersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to delete.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        
        // Check if chapter has sentences
        try {
            int sentencesCount = facade.getSentencesByBookId(currentBook.getBookId()).stream()
                .filter(s -> s.getChapterId() == chapter.getChapterId())
                .toArray().length;
            
            if (sentencesCount > 0) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "This chapter has " + sentencesCount + " sentences. Deleting the chapter will remove these sentences. Continue?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        } catch (SQLException e) {
            // Continue with deletion even if we can't check sentences
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete chapter: " + chapter.getChapterName() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.deleteChapter(chapter.getChapterId());
                loadChapters();
                JOptionPane.showMessageDialog(this, "Chapter deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting chapter: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewSentences() {
        int row = chaptersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to view its sentences.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        ChapterSentencesDialog sentencesDialog = new ChapterSentencesDialog(facade, currentBook, chapter);
        sentencesDialog.setVisible(true);
    }
}