package main.java.com.apm.pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;

public class SearchTokenPanel extends JPanel {

    private static final Color BG = new Color(248, 250, 253);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_LIGHT = new Color(199, 194, 255);
    private static final Color TEXT = new Color(30, 30, 50);
    private static final Color TEXT_LIGHT = new Color(120, 120, 140);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color DANGER = new Color(239, 68, 68);

    private final IBussinessLayerFasade bl;

    private JTextField searchField;
    private JRadioButton radioExact, radioPartial;
    private DefaultListModel<String> resultListModel;
    private JList<String> resultList;
    private JLabel statusLabel;
    private String currentQuery = "";
    private JButton searchBtn;

    public SearchTokenPanel(IBussinessLayerFasade bl) {
        this.bl = bl;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(20, 30, 30, 30));

        add(createModernHeader(), BorderLayout.NORTH);
        add(createResultsPanel(), BorderLayout.CENTER);
    }

    private JPanel createModernHeader() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 235), 1),
            new EmptyBorder(32, 40, 32, 40)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Fake shadow
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 8, 0, new Color(0, 0, 0, 20)),
            card.getBorder()
        ));

        JLabel title = new JLabel("Search Corpus Tokens");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Find sentences containing exact or pattern-matched tokens");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(TEXT_LIGHT);

        JPanel titlePanel = new JPanel(new BorderLayout(0, 8));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        // Search Controls
        JPanel searchBox = new JPanel(new BorderLayout(12, 0));
        searchBox.setOpaque(false);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 210), 2, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        searchField.setPreferredSize(new Dimension(340, 52));

        // Placeholder
        JLabel placeholder = new JLabel("Enter token or pattern...");
        placeholder.setForeground(TEXT_LIGHT);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        placeholder.setBorder(new EmptyBorder(0, 10, 0, 0));
        searchField.setLayout(new BorderLayout());
        searchField.add(placeholder, BorderLayout.WEST);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { placeholder.setVisible(false); }
            public void focusLost(java.awt.event.FocusEvent e) { placeholder.setVisible(searchField.getText().isEmpty()); }
        });
        placeholder.setVisible(true);
        searchField.addActionListener(e -> performSearch());

        // Search Button
        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setPreferredSize(new Dimension(130, 52));

        searchBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { searchBtn.setBackground(new Color(67, 56, 202)); }
            public void mouseExited(MouseEvent e) { searchBtn.setBackground(PRIMARY); }
        });
        searchBtn.addActionListener(e -> performSearch());

        searchBox.add(searchField, BorderLayout.CENTER);
        searchBox.add(searchBtn, BorderLayout.EAST);

        // Radio buttons
        radioExact = createStyledRadio("Exact Match");
        radioPartial = createStyledRadio("Pattern Match");
        radioExact.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(radioExact);
        group.add(radioPartial);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(radioExact);
        radioPanel.add(radioPartial);

        JPanel controls = new JPanel(new BorderLayout(0, 16));
        controls.setOpaque(false);
        controls.add(searchBox, BorderLayout.NORTH);
        controls.add(radioPanel, BorderLayout.SOUTH);

        card.add(titlePanel, BorderLayout.WEST);
        card.add(controls, BorderLayout.CENTER);

        return card;
    }

    private JRadioButton createStyledRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rb.setForeground(TEXT);
        rb.setBackground(CARD_BG);
        rb.setFocusPainted(false);
        rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(30, 0, 0, 0));

        statusLabel = new JLabel("Start typing to search sentences...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(TEXT_LIGHT);

        resultListModel = new DefaultListModel<>();
        resultList = new JList<>(resultListModel);
        resultList.setBackground(CARD_BG);
        resultList.setSelectionBackground(PRIMARY_LIGHT);
        resultList.setSelectionForeground(TEXT);
        resultList.setFixedCellHeight(90);

        // PERFECT RTL + RIGHT NUMBERING + ALL TOKENS HIGHLIGHTED
    // PERFECT RTL + RIGHT NUMBERING (numbers on the right side)
resultList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
    JPanel item = new JPanel(new BorderLayout());
    item.setBackground(isSelected ? PRIMARY_LIGHT : CARD_BG);
    item.setBorder(new EmptyBorder(18, 28, 18, 28));

    String highlighted = highlightToken(value.toString(), currentQuery, radioPartial.isSelected());

    JLabel textLabel = new JLabel("<html>" + highlighted + "</html>");
    textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    textLabel.setForeground(isSelected ? TEXT : new Color(40, 40, 60));

    // Full RTL + right alignment
    textLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    // Numbering on the RIGHT side (Arabic style)
    JLabel indexLabel = new JLabel("." + (index + 1) );
    indexLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    indexLabel.setForeground(PRIMARY);
    indexLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    // Put number on the RIGHT, sentence on the LEFT (so number appears at the end)
    item.add(indexLabel, BorderLayout.EAST);   // Number on the right
    item.add(textLabel, BorderLayout.CENTER);

    return item;
});

        JScrollPane scroll = new JScrollPane(resultList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(CARD_BG);

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // PERFECT HIGHLIGHT: ALL tokens highlighted, RTL, right-aligned
    private String highlightToken(String sentence, String query, boolean isPattern) {
        if (query == null || query.trim().isEmpty()) {
            return "<div style='direction: rtl; text-align: right; unicode-bidi: embed; font-family: \"Segoe UI\", Tahoma;'>" 
                   + escapeHtml(sentence) + "</div>";
        }

        String cleanQuery = query.trim();
        String regex = isPattern ? cleanQuery : "\\b" + Pattern.quote(cleanQuery) + "\\b";

        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(sentence);

            StringBuffer sb = new StringBuffer();
            sb.append("<div style='direction: rtl; text-align: right; unicode-bidi: embed; line-height: 1.6; font-family: \"Segoe UI\", Tahoma;'>");

            int lastEnd = 0;
            boolean found = false;

            while (matcher.find()) {
                found = true;
                // Add text before match
                sb.append(escapeHtml(sentence.substring(lastEnd, matcher.start())));

                // Highlight the match
                String match = matcher.group();
                sb.append("<span style='background: linear-gradient(120deg, #fef3c7, #fde68a); ")
                  .append("color: #b45309; font-weight: bold; padding: 4px 10px; ")
                  .append("border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); ")
                  .append("display: inline-block; margin: 0 2px;'>")
                  .append(escapeHtml(match))
                  .append("</span>");

                lastEnd = matcher.end();
            }

            // Add remaining text
            sb.append(escapeHtml(sentence.substring(lastEnd)));
            sb.append("</div>");

            return sb.toString();

        } catch (Exception e) {
            return "<div style='direction: rtl; text-align: right;'>" + escapeHtml(sentence) + "</div>";
        }
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // SEARCH WITH LOADING ANIMATION
  private void performSearch() {
    currentQuery = searchField.getText().trim();

    if (currentQuery.isEmpty()) {
        statusLabel.setText("Please enter a token");
        statusLabel.setForeground(WARNING);
        resultListModel.clear();
        return;
    }

    // Show loading state
    resultListModel.clear();
    statusLabel.setText("Searching in corpus...");
    statusLabel.setForeground(TEXT_LIGHT);
    searchBtn.setEnabled(false);
    searchBtn.setText("Searching...");

    // Fully written SwingWorker (no <> to avoid compilation issues)
    SwingWorker<ArrayList<String>, Void> worker = new SwingWorker<ArrayList<String>, Void>() {
        @Override
        protected ArrayList<String> doInBackground() throws Exception {
            if (radioExact.isSelected()) {
                return bl.getSentencesByToken(currentQuery);
            } else {
                return bl.getSentencesByTokenPattern(currentQuery);
            }
        }

        @Override
protected void done() {
    try {
        ArrayList<String> results = get();
        int count = results != null ? results.size() : 0;

        resultListModel.clear(); // Clear first

        if (count > 0) {
            statusLabel.setText("Found " + count + " sentence" + (count > 1 ? "s" : ""));
            statusLabel.setForeground(SUCCESS);

            // Add all sentences to model
            for (String sentence : results) {
                resultListModel.addElement(sentence);
            }
        } else {
            statusLabel.setText("No sentences found for \"" + currentQuery + "\"");
            statusLabel.setForeground(WARNING);
            resultListModel.addElement("No results found");
        }

        // Force full repaint — this triggers cellRenderer with correct currentQuery
        resultList.revalidate();
        resultList.repaint();

    } catch (Exception ex) {
        statusLabel.setText("Search failed");
        statusLabel.setForeground(DANGER);
        resultListModel.clear();
        resultListModel.addElement("Error occurred");
        ex.printStackTrace();
    } finally {
        searchBtn.setEnabled(true);
        searchBtn.setText("Search");
    }
}
    };

    worker.execute();
} 
}