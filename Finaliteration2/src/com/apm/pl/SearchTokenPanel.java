package com.apm.pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;

/**
 * Professional Search Panel for Tokens.
 * Supports Exact Match and Partial Match (Pattern) searching.
 */
public class SearchTokenPanel extends JPanel {

    // --- Theme Constants (Consistent with your existing UI) ---
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color HEADER_BG = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181); // Indigo
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color CARD_BG = Color.WHITE;

    private final IBussinessLayerFasade bl;
    
    // UI Components
    private JTextField searchField;
    private JRadioButton radioExact;
    private JRadioButton radioPartial;
    private DefaultListModel<String> resultListModel;
    private JList<String> resultList;
    private JLabel statusLabel;

    public SearchTokenPanel(IBussinessLayerFasade bl) {
        this.bl = bl;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        
        // 1. Create the Top Search Control Bar
        add(createSearchHeader(), BorderLayout.NORTH);

        // 2. Create the Results Display Area
        add(createResultsArea(), BorderLayout.CENTER);
    }

    private JPanel createSearchHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Title
        JLabel title = new JLabel("Search Corpus");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        
        // Controls Container
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controls.setOpaque(false);

        // Search Field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.setPreferredSize(new Dimension(250, 35));
        // Trigger search on Enter key
        searchField.addActionListener(e -> performSearch());

        // Radio Buttons
        radioExact = new JRadioButton("Exact Match");
        radioPartial = new JRadioButton("Partial Pattern");
        
        styleRadioButton(radioExact);
        styleRadioButton(radioPartial);
        radioExact.setSelected(true); // Default

        ButtonGroup group = new ButtonGroup();
        group.add(radioExact);
        group.add(radioPartial);

        // Search Button
        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn, PRIMARY_COLOR, Color.WHITE);
        searchBtn.addActionListener(e -> performSearch());

        // Assemble controls
        controls.add(new JLabel("Token:"));
        controls.add(searchField);
        controls.add(radioExact);
        controls.add(radioPartial);
        controls.add(searchBtn);

        // Layout Header
        headerPanel.add(title, BorderLayout.NORTH);
        
        // Spacer between title and controls
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(1, 10));
        headerPanel.add(spacer, BorderLayout.CENTER);
        
        headerPanel.add(controls, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createResultsArea() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(BG_COLOR);
        resultsPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Status Label
        statusLabel = new JLabel("Enter a token above to search associated sentences.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Result List
        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        resultList.setFixedCellHeight(40);
        resultList.setBackground(CARD_BG);
        resultList.setSelectionBackground(new Color(235, 242, 255));
        resultList.setSelectionForeground(PRIMARY_COLOR);
        
        // Custom Cell Renderer for cleaner look
        resultList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                // Use HTML for subtle styling of the numbering
                label.setText("<html><font color='#95a5a6'><b>" + (index + 1) + ".</b></font> " + value.toString() + "</html>");
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(CARD_BG);

        resultsPanel.add(statusLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        return resultsPanel;
    }

    // ================= 🔍 SEARCH LOGIC ====================

    private void performSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            statusLabel.setText("⚠️ Please enter a token to search.");
            statusLabel.setForeground(new Color(231, 76, 60)); // Red
            return;
        }

        // Clear previous results
        resultListModel.clear();
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setText("Searching...");

        ArrayList<String> results;

        try {
            if (radioExact.isSelected()) {
                // 1. Exact Match from Facade
                results = bl.getSentencesByToken(query);
                statusLabel.setText("Found " + (results != null ? results.size() : 0) + " sentences for token: \"" + query + "\" (Exact)");
            } else {
                // 2. Partial Match from Facade
                results = bl.getSentencesByTokenPattern(query);
                statusLabel.setText("Found " + (results != null ? results.size() : 0) + " sentences matching pattern: \"" + query + "\"");
            }

            // Populate List
            if (results != null && !results.isEmpty()) {
                for (String sentence : results) {
                    resultListModel.addElement(sentence);
                }
            } else {
                resultListModel.addElement("No results found.");
            }

        } catch (Exception e) {
            statusLabel.setText("❌ Error occurred during search.");
            statusLabel.setForeground(new Color(231, 76, 60));
            e.printStackTrace();
        }
    }

    // ================= 🎨 STYLING HELPERS ====================

    private void styleRadioButton(JRadioButton rb) {
        rb.setBackground(HEADER_BG);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Simple Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }
}