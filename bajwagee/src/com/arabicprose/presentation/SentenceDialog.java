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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

public class SentenceDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private ChapterDTO currentChapter;
    private SentenceDTO currentSentence;
    private JTextField sentenceNumberField;
    private JTextArea textArea, textDiacritizedArea, translationArea, notesArea;
    private JButton saveBtn, cancelBtn;
    
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(231, 76, 60);

    public SentenceDialog(IBusinessLayerFacade facade, BookDTO book, ChapterDTO chapter, SentenceDTO sentence) {
        this.facade = facade;
        this.currentBook = book;
        this.currentChapter = chapter;
        this.currentSentence = sentence;
        
        setTitle(sentence == null ? "Add New Sentence" : "Edit Sentence");
        setModal(true);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (sentence != null) {
            populateFields(sentence);
        } else {
            // Auto-generate sentence number for new sentences
            try {
                int nextNumber = facade.getNextSentenceNumber(currentBook.getBookId());
                sentenceNumberField.setText(String.valueOf(nextNumber));
            } catch (SQLException e) {
                sentenceNumberField.setText("1");
            }
        }
    }

    private void initializeComponents() {
        sentenceNumberField = new JTextField();
        
        textArea = createTextArea("Enter Arabic text", 3);
        textDiacritizedArea = createTextArea("Enter diacritized text", 3);
        translationArea = createTextArea("Enter translation", 3);
        notesArea = createTextArea("Enter notes", 2);
        
        saveBtn = new JButton(currentSentence == null ? "Add Sentence" : "Update Sentence");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(WARNING_COLOR);
        cancelBtn.setForeground(Color.WHITE);
    }

    private JTextArea createTextArea(String placeholder, int rows) {
        JTextArea textArea = new JTextArea(rows, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        return textArea;
    }

    private void setupLayout() {
        // Header
        String headerText = currentSentence == null ? 
            "Add New Sentence to: " + currentChapter.getChapterName() : 
            "Edit Sentence in: " + currentChapter.getChapterName();
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        formPanel.add(createFormLabel("Sentence Number:"));
        formPanel.add(sentenceNumberField);
        formPanel.add(createFormLabel("Arabic Text:"));
        formPanel.add(new JScrollPane(textArea));
        formPanel.add(createFormLabel("Diacritized Text:"));
        formPanel.add(new JScrollPane(textDiacritizedArea));
        formPanel.add(createFormLabel("Translation:"));
        formPanel.add(new JScrollPane(translationArea));
        formPanel.add(createFormLabel("Notes:"));
        formPanel.add(new JScrollPane(notesArea));
        
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
        saveBtn.addActionListener(e -> saveSentence());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void populateFields(SentenceDTO sentence) {
        sentenceNumberField.setText(String.valueOf(sentence.getSentenceNumber()));
        textArea.setText(sentence.getText());
        textDiacritizedArea.setText(sentence.getTextDiacritized() != null ? sentence.getTextDiacritized() : "");
        translationArea.setText(sentence.getTranslation() != null ? sentence.getTranslation() : "");
        notesArea.setText(sentence.getNotes() != null ? sentence.getNotes() : "");
    }

    private void saveSentence() {
        String text = textArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Arabic text is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int sentenceNumber;
        try {
            sentenceNumber = Integer.parseInt(sentenceNumberField.getText().trim());
            if (sentenceNumber <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid sentence number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if (currentSentence == null) {
                // Add new sentence
                SentenceDTO newSentence = new SentenceDTO();
                newSentence.setBookId(currentBook.getBookId());
                newSentence.setChapterId(currentChapter.getChapterId());
                newSentence.setSentenceNumber(sentenceNumber);
                newSentence.setText(text);
                newSentence.setTextDiacritized(textDiacritizedArea.getText().trim());
                newSentence.setTranslation(translationArea.getText().trim());
                newSentence.setNotes(notesArea.getText().trim());
                
                facade.addSentence(newSentence);
                JOptionPane.showMessageDialog(this, "Sentence added successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Update existing sentence
                currentSentence.setSentenceNumber(sentenceNumber);
                currentSentence.setText(text);
                currentSentence.setTextDiacritized(textDiacritizedArea.getText().trim());
                currentSentence.setTranslation(translationArea.getText().trim());
                currentSentence.setNotes(notesArea.getText().trim());
                
                facade.updateSentence(currentSentence);
                JOptionPane.showMessageDialog(this, "Sentence updated successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving sentence: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}