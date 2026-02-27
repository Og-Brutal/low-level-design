package com.arabicprose.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

public class TextProcessingDialog extends JDialog {
    private IBusinessLayerFacade facade;
    private BookDTO currentBook;
    private ChapterDTO currentChapter;
    private JTextArea inputTextArea;
    private JButton processBtn, cancelBtn;
    
    private static final Pattern SENTENCE_DELIMITERS = Pattern.compile("[.!?]");

    public TextProcessingDialog(IBusinessLayerFacade facade, BookDTO book, ChapterDTO chapter) {
        this.facade = facade;
        this.currentBook = book;
        this.currentChapter = chapter;
        setTitle("Process Text to Sentences - " + chapter.getChapterName());
        setModal(true);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        inputTextArea = new JTextArea(15, 50);
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        
        processBtn = new JButton("Process Sentences");
        processBtn.setBackground(new Color(46, 204, 113));
        processBtn.setForeground(Color.WHITE);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
    }

    private void setupLayout() {
        // Header
        JLabel headerLabel = new JLabel("Enter text for chapter: " + currentChapter.getChapterName());
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        // Text area
        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Input Text (will be split by . ! ?)"));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(processBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        processBtn.addActionListener(e -> processSentences());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void processSentences() {
        String text = inputTextArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text to process.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Split text into sentences
        String[] sentences = SENTENCE_DELIMITERS.split(text);
        List<String> validSentences = new ArrayList<>();
        
        // Filter out empty sentences and trim
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                validSentences.add(trimmed);
            }
        }

        if (validSentences.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid sentences found in the text.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Found " + validSentences.size() + " sentences. Add them to the chapter?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                addSentencesToChapter(validSentences);
                JOptionPane.showMessageDialog(this, 
                    "Successfully added " + validSentences.size() + " sentences to the chapter!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding sentences: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addSentencesToChapter(List<String> sentences) throws SQLException {
        int nextSentenceNumber = facade.getNextSentenceNumber(currentBook.getBookId());
        
        for (int i = 0; i < sentences.size(); i++) {
            SentenceDTO sentence = new SentenceDTO();
            sentence.setBookId(currentBook.getBookId());
            sentence.setChapterId(currentChapter.getChapterId());
            sentence.setSentenceNumber(nextSentenceNumber + i);
            sentence.setText(sentences.get(i));
            sentence.setTextDiacritized(""); // Can be filled later
            sentence.setTranslation(""); // Can be filled later
            sentence.setNotes("Auto-generated from chapter text");
            
            facade.addSentence(sentence);
        }
    }
}