package com.arabicprose.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;

public class ChapterManagementDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private JTable chapterTable;
    private JTextField chapterNameField, chapterOrderField, descriptionField;
    private JButton addBtn, updateBtn, deleteBtn, processTextBtn;
    private List<ChapterDTO> currentChapters;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(231, 76, 60);

    public ChapterManagementDialog(IBusinessLayerFacade facade, BookDTO book) {
        this.facade = facade;
        this.currentBook = book;
        setTitle("Chapter Management - " + book.getTitle());
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
        chapterTable = new JTable();
        chapterTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        
        chapterNameField = new JTextField();
        chapterOrderField = new JTextField();
        descriptionField = new JTextField();
        
        addBtn = createStyledButton("Add Chapter", ACCENT_COLOR);
        updateBtn = createStyledButton("Update Chapter", PRIMARY_COLOR);
        deleteBtn = createStyledButton("Delete Chapter", WARNING_COLOR);
        processTextBtn = createStyledButton("Process Text to Sentences", new Color(155, 89, 182));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private void setupLayout() {
        // Header
        JLabel headerLabel = new JLabel("Chapters for: " + currentBook.getTitle());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Table
        JScrollPane tableScroll = new JScrollPane(chapterTable);
        add(tableScroll, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Chapter Details"));
        
        formPanel.add(new JLabel("Chapter Name:"));
        formPanel.add(chapterNameField);
        formPanel.add(new JLabel("Chapter Order:"));
        formPanel.add(chapterOrderField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(processTextBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadChapters() {
        try {
            currentChapters = facade.getChaptersByBookId(currentBook.getBookId());
            
            DefaultTableModel model = new DefaultTableModel(
                new Object[0][0],
                new String[]{"Order", "Chapter Name", "Description"}
            );
            
            for (ChapterDTO chapter : currentChapters) {
                model.addRow(new Object[]{
                    chapter.getChapterOrder(),
                    chapter.getChapterName(),
                    chapter.getDescription()
                });
            }
            
            chapterTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading chapters: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupEventListeners() {
        addBtn.addActionListener(e -> addChapter());
        updateBtn.addActionListener(e -> updateChapter());
        deleteBtn.addActionListener(e -> deleteChapter());
        processTextBtn.addActionListener(e -> showTextProcessingDialog());
        
        chapterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = chapterTable.getSelectedRow();
                if (row >= 0 && currentChapters != null && row < currentChapters.size()) {
                    ChapterDTO chapter = currentChapters.get(row);
                    chapterNameField.setText(chapter.getChapterName());
                    chapterOrderField.setText(String.valueOf(chapter.getChapterOrder()));
                    descriptionField.setText(chapter.getDescription());
                }
            }
        });
    }

    private void addChapter() {
        String name = chapterNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chapter name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int order;
        try {
            order = Integer.parseInt(chapterOrderField.getText().trim());
        } catch (NumberFormatException e) {
            order = 0; // Will be auto-generated
        }
        
        ChapterDTO chapter = new ChapterDTO();
        chapter.setBookId(currentBook.getBookId());
        chapter.setChapterName(name);
        chapter.setChapterOrder(order);
        chapter.setDescription(descriptionField.getText().trim());
        
        try {
            facade.addChapter(chapter);
            loadChapters();
            clearFields();
            JOptionPane.showMessageDialog(this, "Chapter added successfully!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding chapter: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateChapter() {
        int row = chapterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        String name = chapterNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chapter name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int order;
        try {
            order = Integer.parseInt(chapterOrderField.getText().trim());
        } catch (NumberFormatException e) {
            order = chapter.getChapterOrder();
        }
        
        chapter.setChapterName(name);
        chapter.setChapterOrder(order);
        chapter.setDescription(descriptionField.getText().trim());
        
        try {
            facade.updateChapter(chapter);
            loadChapters();
            JOptionPane.showMessageDialog(this, "Chapter updated successfully!", "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating chapter: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteChapter() {
        int row = chapterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete chapter: " + chapter.getChapterName() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.deleteChapter(chapter.getChapterId());
                loadChapters();
                clearFields();
                JOptionPane.showMessageDialog(this, "Chapter deleted successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting chapter: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showTextProcessingDialog() {
        int row = chapterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a chapter to add sentences to.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChapterDTO chapter = currentChapters.get(row);
        TextProcessingDialog dialog = new TextProcessingDialog(facade, currentBook, chapter);
        dialog.setVisible(true);
    }

    private void clearFields() {
        chapterNameField.setText("");
        chapterOrderField.setText("");
        descriptionField.setText("");
    }
}