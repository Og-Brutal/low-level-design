package com.arabicprose.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;

public class ChapterDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private ChapterDTO currentChapter;
    private JTextField nameField, orderField, descriptionField;
    private JButton saveBtn, cancelBtn;
    
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(231, 76, 60);

    public ChapterDialog(IBusinessLayerFacade facade, BookDTO book, ChapterDTO chapter) {
        this.facade = facade;
        this.currentBook = book;
        this.currentChapter = chapter;
        
        setTitle(chapter == null ? "Add New Chapter" : "Edit Chapter - " + chapter.getChapterName());
        setModal(true);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (chapter != null) {
            populateFields(chapter);
        }
    }

    private void initializeComponents() {
        nameField = new JTextField();
        orderField = new JTextField();
        descriptionField = new JTextField();
        
        saveBtn = new JButton(currentChapter == null ? "Add Chapter" : "Update Chapter");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(WARNING_COLOR);
        cancelBtn.setForeground(Color.WHITE);
    }

    private void setupLayout() {
        // Header
        JLabel headerLabel = new JLabel(currentChapter == null ? "Add New Chapter to: " + currentBook.getTitle() : 
            "Edit Chapter in: " + currentBook.getTitle());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        formPanel.add(createFormLabel("Chapter Name:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Chapter Order:"));
        formPanel.add(orderField);
        formPanel.add(createFormLabel("Description:"));
        formPanel.add(descriptionField);
        
        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private void setupEventListeners() {
        saveBtn.addActionListener(e -> saveChapter());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void populateFields(ChapterDTO chapter) {
        nameField.setText(chapter.getChapterName());
        orderField.setText(String.valueOf(chapter.getChapterOrder()));
        descriptionField.setText(chapter.getDescription() != null ? chapter.getDescription() : "");
    }

    private void saveChapter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chapter name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int order;
        try {
            order = Integer.parseInt(orderField.getText().trim());
            if (order <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            // Auto-generate order
            try {
                order = facade.getNextChapterOrder(currentBook.getBookId());
            } catch (SQLException ex) {
                order = 1; // Default to 1 if there's an error
            }
        }
        
        try {
            if (currentChapter == null) {
                // Add new chapter
                ChapterDTO newChapter = new ChapterDTO();
                newChapter.setBookId(currentBook.getBookId());
                newChapter.setChapterName(name);
                newChapter.setChapterOrder(order);
                newChapter.setDescription(descriptionField.getText().trim());
                
                facade.addChapter(newChapter);
                JOptionPane.showMessageDialog(this, "Chapter added successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing chapter
                currentChapter.setChapterName(name);
                currentChapter.setChapterOrder(order);
                currentChapter.setDescription(descriptionField.getText().trim());
                
                facade.updateChapter(currentChapter);
                JOptionPane.showMessageDialog(this, "Chapter updated successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving chapter: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}