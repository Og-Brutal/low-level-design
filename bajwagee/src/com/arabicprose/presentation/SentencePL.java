package com.arabicprose.presentation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.AnalysisResultDTO;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

/**
 * Modern Sentences Management Panel with Simplified Workflow
 */
public class SentencePL extends JPanel {
    private IBusinessLayerFacade facade;
    private JPanel cardsContainer;
    private List<SentenceDTO> currentSentences;
    private BrowserPL browserPL;

    // Enhanced Color Palette
    private final Color BG_COLOR = new Color(248, 250, 252); 
    private final Color HEADER_START = new Color(99, 102, 241); 
    private final Color HEADER_END = new Color(139, 92, 246);   
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_LIGHT = new Color(100, 116, 139);
    private final Color CARD_BG = Color.WHITE;
    private final Color CARD_SHADOW = new Color(0, 0, 0, 20);
    private final Color CARD_HOVER_SHADOW = new Color(0, 0, 0, 40);
    
    // Button Colors
    private final Color BTN_ADD_BG = new Color(34, 197, 94); 
    private final Color BTN_ADD_HOVER = new Color(22, 163, 74);
    private final Color BTN_MODIFY_BG = new Color(59, 130, 246);
    private final Color BTN_MODIFY_HOVER = new Color(37, 99, 235);
    
    // Accent Colors
    private final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color INFO_COLOR = new Color(59, 130, 246);
    
    // Analysis Colors
    private final Color ANALYSIS_COLOR = new Color(168, 85, 247);
    private final Color ANALYSIS_HOVER = new Color(147, 51, 234);

    public SentencePL(IBusinessLayerFacade facade) {
        this.facade = facade;
        initializePanel();
    }

    public void setBrowserPL(BrowserPL browserPL) {
        this.browserPL = browserPL;
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        showMainMenu();
    }

    private void showMainMenu() {
        removeAll();
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Options Panel
        JPanel mainPanel = createMainOptionsPanel();
        add(mainPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, HEADER_START, getWidth(), getHeight(), HEADER_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < getWidth(); i += 20) {
                    g2d.drawLine(i, 0, i, getHeight());
                }
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 150));
        headerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Sentence Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createMainOptionsPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(50, 20, 50, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;

        // Welcome message
        JLabel welcomeLabel = new JLabel("What Would You Like To Do !");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel, gbc);

        // Add Sentence Card
        JPanel addCard = createOptionCard(
            "Add New Sentence", 
            "Create A New Sentence With Arabic Text, Translation, And Notes", 
            BTN_ADD_BG, 
            createAddSentenceIcon(),
            e -> showBookSelectionDialog(true)
        );
        mainPanel.add(addCard, gbc);

        // Modify Sentence Card
        JPanel modifyCard = createOptionCard(
            "Modify Sentences", 
            "Edit Or Delete Existing Sentences By Book And Chapter", 
            BTN_MODIFY_BG, 
            createModifySentencesIcon(),
            e -> showBookSelectionDialog(false)
        );
        mainPanel.add(modifyCard, gbc);

        return mainPanel;
    }

    private JPanel createOptionCard(String title, String description, Color color, Icon icon, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setPreferredSize(new Dimension(400, 200));
        card.setMaximumSize(new Dimension(400, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setIcon(icon);
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(15);

        JLabel descLabel = new JLabel("<html><div style='text-align:center; width:300px;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_LIGHT);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(28, 38, 28, 38)
                ));
            }
            
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(30, 40, 30, 40));
            }
            
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        return card;
    }

    // Create add sentence icon for option card
    private Icon createAddSentenceIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                
                // Document background with subtle gradient
                GradientPaint gradient = new GradientPaint(
                    x, y, lighter(BTN_ADD_BG, 0.3), 
                    x + size, y + size, BTN_ADD_BG
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(x + 3, y + 3, size - 6, size - 6, 8, 8);
                
                // Document border
                g2d.setColor(darker1(BTN_ADD_BG, 0.3));
                g2d.setStroke(new BasicStroke(1.8f));
                g2d.drawRoundRect(x + 3, y + 3, size - 6, size - 6, 8, 8);
                
                // Document fold effect (top-right corner)
                g2d.setColor(darker1(BTN_ADD_BG, 0.2));
                g2d.fillPolygon(
                    new int[]{x + size - 8, x + size - 3, x + size - 3},
                    new int[]{y + 3, y + 3, y + 8},
                    3
                );
                
                // Document lines with varying lengths for natural look
                g2d.setColor(darker1(BTN_ADD_BG, 0.6));
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawLine(x + 8, y + 10, x + size - 10, y + 10);  // Full line
                g2d.drawLine(x + 8, y + 16, x + size - 15, y + 16);  // Shorter line
                g2d.drawLine(x + 8, y + 22, x + size - 12, y + 22);  // Medium line
                
                // Plus sign with circular background
                g2d.setColor(BTN_ADD_BG);
                g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
                
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int plusSize = 7;
                g2d.drawLine(centerX - plusSize, centerY, centerX + plusSize, centerY);
                g2d.drawLine(centerX, centerY - plusSize, centerX, centerY + plusSize);
                
                // Subtle shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(x + 5, y + 5, size - 6, size - 6, 8, 8);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createModifySentencesIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = 32;
                
                // Background document (stacked behind)
                GradientPaint bgGradient = new GradientPaint(
                    x, y, lighter(BTN_MODIFY_BG, 0.4), 
                    x + size, y + size, lighter(BTN_MODIFY_BG, 0.2)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(x + 2, y + 6, size - 4, size - 8, 6, 6);
                
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.4));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(x + 2, y + 6, size - 4, size - 8, 6, 6);
                
                // Background document lines
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.3));
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 2; i++) {
                    g2d.drawLine(x + 6, y + 14 + i*6, x + size - 8, y + 14 + i*6);
                }
                
                // Foreground document (stacked in front)
                GradientPaint fgGradient = new GradientPaint(
                    x, y, lighter(BTN_MODIFY_BG, 0.2), 
                    x + size, y + size, BTN_MODIFY_BG
                );
                g2d.setPaint(fgGradient);
                g2d.fillRoundRect(x + 6, y + 2, size - 8, size - 8, 8, 8);
                
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.3));
                g2d.setStroke(new BasicStroke(1.8f));
                g2d.drawRoundRect(x + 6, y + 2, size - 8, size - 8, 8, 8);
                
                // Foreground document fold effect
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.2));
                g2d.fillPolygon(
                    new int[]{x + size - 10, x + size - 4, x + size - 4},
                    new int[]{y + 2, y + 2, y + 8},
                    3
                );
                
                // Foreground document lines
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.6));
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawLine(x + 10, y + 10, x + size - 6, y + 10);
                g2d.drawLine(x + 10, y + 16, x + size - 10, y + 16);
                
                // Pencil icon with better design
                g2d.setColor(darker1(BTN_MODIFY_BG, 0.8));
                
                // Pencil body
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + size - 12, y + 6, x + size - 4, y + 2);
                
                // Pencil tip
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(x + size - 4, y + 2, x + size - 2, y + 4);
                
                // Pencil lead effect
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawLine(x + size - 3, y + 3, x + size - 2, y + 4);
                
                // Subtle shadow for depth
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(x + 8, y + 4, size - 8, size - 8, 8, 8);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    // Helper methods for color manipulation
    private Color lighter(Color color, double factor) {
        return new Color(
            Math.min((int)(color.getRed() + (255 - color.getRed()) * factor), 255),
            Math.min((int)(color.getGreen() + (255 - color.getGreen()) * factor), 255),
            Math.min((int)(color.getBlue() + (255 - color.getBlue()) * factor), 255)
        );
    }

    private Color darker1(Color color, double factor) {
        return new Color(
            Math.max((int)(color.getRed() * (1 - factor)), 0),
            Math.max((int)(color.getGreen() * (1 - factor)), 0),
            Math.max((int)(color.getBlue() * (1 - factor)), 0)
        );
    }
    // ==================== ADD SENTENCE WORKFLOW ====================

    private void showBookSelectionDialog(boolean isAddMode) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog selectionDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null, 
            isAddMode ? "Add Sentence - Select Book & Chapter" : "Modify Sentences - Select Book & Chapter", 
            true);
        selectionDialog.setSize(700, 600);
        selectionDialog.setLocationRelativeTo(this);
        selectionDialog.setLayout(new BorderLayout());
        selectionDialog.getContentPane().setBackground(BG_COLOR);

        // Header with custom icon
        JPanel headerPanel = createDialogHeader(
            isAddMode ? "Select Book & Chapter" : "Select Book & Chapter", 
            80
        );
        
        // Add custom icon to header
        JLabel headerIconLabel = new JLabel(createBookSelectionIcon(isAddMode));
        headerIconLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(headerIconLabel, BorderLayout.NORTH);
        
        selectionDialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(Color.WHITE);

        // Instruction with custom icon
        JLabel instructionLabel = new JLabel(isAddMode ? 
            "Select A Book And Chapter To Add A New Sentence" : 
            "Select A Book And Chapter To Modify Sentences");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_DARK);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionLabel.setIcon(createInstructionIcon(isAddMode));
        instructionLabel.setIconTextGap(10);

        // Book selection with custom icon
        JLabel bookLabel = new JLabel("Book:");
        bookLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookLabel.setIcon(createBookIcon());
        bookLabel.setIconTextGap(8);

        JComboBox<Object> bookComboBox = createStyledComboBox();
        loadBooksIntoCombo(bookComboBox);

        // Chapter selection with custom icon
        JLabel chapterLabel = new JLabel("Chapter:");
        chapterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chapterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        chapterLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
        chapterLabel.setIcon(createChapterIcon());
        chapterLabel.setIconTextGap(8);

        JComboBox<Object> chapterComboBox = createStyledComboBox();
        chapterComboBox.setEnabled(false);

        // Load chapters when book is selected
        bookComboBox.addActionListener(e -> {
            if (bookComboBox.getSelectedItem() instanceof BookDTO) {
                loadChaptersIntoCombo(chapterComboBox, (BookDTO) bookComboBox.getSelectedItem());
            }
        });

        JButton proceedBtn = new RoundedButton("Proceed", 
            isAddMode ? BTN_ADD_BG : BTN_MODIFY_BG, Color.WHITE, 
            isAddMode ? BTN_ADD_HOVER : BTN_MODIFY_HOVER);
        proceedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedBtn.setIcon(createProceedIcon(isAddMode));
        proceedBtn.setIconTextGap(8);

        proceedBtn.addActionListener(e -> {
            BookDTO selectedBook = (BookDTO) bookComboBox.getSelectedItem();
            ChapterDTO selectedChapter = (ChapterDTO) chapterComboBox.getSelectedItem();
            
            if (selectedBook == null || selectedBook.getBookId() <= 0) {
                showErrorDialog("Please select a valid book.");
                return;
            }
            
            if (selectedChapter == null || selectedChapter.getChapterId() <= 0) {
                showErrorDialog("Please select a valid chapter.");
                return;
            }

            selectionDialog.dispose();
            
            if (isAddMode) {
                // CHANGED: Show full screen window instead of dialog
                showAddSentenceWindow(selectedBook, selectedChapter);
            } else {
                showModifySentencesView(selectedBook, selectedChapter);
            }
        });

        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(bookLabel);
        contentPanel.add(bookComboBox);
        contentPanel.add(chapterLabel);
        contentPanel.add(chapterComboBox);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(proceedBtn);

        selectionDialog.add(contentPanel, BorderLayout.CENTER);
        selectionDialog.setVisible(true);
    }

    // NEW METHOD: Show add sentence in a full window instead of dialog
    private void showAddSentenceWindow(BookDTO book, ChapterDTO chapter) {
        // Create a new JFrame for full-screen experience
        JFrame addFrame = new JFrame("Add New Sentence - " + book.getTitle() + " - " + chapter.getChapterName());
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        addFrame.setMinimumSize(new Dimension(1024, 768));
        
        // Create the main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        // Header Panel (reusing your existing header creation)
        JPanel headerPanel = createWindowHeader(book, chapter);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel (reusing your existing dialog content with adjustments)
        JScrollPane contentPanel = createAddSentenceContent(book, chapter, addFrame);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        addFrame.add(mainPanel);
        addFrame.setLocationRelativeTo(this);
        addFrame.setVisible(true);
    }

    // NEW METHOD: Create header for the window
    private JPanel createWindowHeader(BookDTO book, ChapterDTO chapter) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_START);
        headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));

        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);
        
        JLabel headerIcon = new JLabel(createAddSentenceWindowIcon());
        JLabel titleLabel = new JLabel("Add New Sentence");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(headerIcon);
        titlePanel.add(titleLabel);

        // Book and chapter info
        JLabel infoLabel = new JLabel("Book: " + book.getTitle() + " | Chapter: " + chapter.getChapterName());
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);

        // Back button
        JButton backBtn = new RoundedButton("Back to Main Menu", 
            new Color(107, 114, 128), Color.WHITE, new Color(75, 85, 99));
        backBtn.setIcon(createBackIcon());
        backBtn.setIconTextGap(8);
        backBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(headerPanel);
            if (window != null) {
                window.dispose();
            }
            showMainMenu();
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(infoLabel, BorderLayout.CENTER);

        headerPanel.add(leftPanel, BorderLayout.CENTER);
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    // NEW METHOD: Create the content for add sentence window
    private JScrollPane createAddSentenceContent(BookDTO book, ChapterDTO chapter, JFrame parentFrame) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(40, 80, 40, 80));
        contentPanel.setBackground(Color.WHITE);

        // Arabic Text with icon
        JLabel arabicLabel = new JLabel("Arabic Text : ");
        arabicLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        arabicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        arabicLabel.setIcon(createArabicTextIcon());
        arabicLabel.setIconTextGap(8);

        JTextArea arabicTextArea = createModernTextArea("Enter Arabic text here...", 4);
        arabicTextArea.setPreferredSize(new Dimension(800, 100));
        arabicTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Diacritized Text with icon
        JLabel diacritizedLabel = new JLabel("Diacritized Text : ");
        diacritizedLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        diacritizedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diacritizedLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        diacritizedLabel.setIcon(createDiacritizedIcon());
        diacritizedLabel.setIconTextGap(8);

        JTextArea diacritizedTextArea = createModernTextArea("Enter diacritized text here...", 3);
        diacritizedTextArea.setPreferredSize(new Dimension(800, 80));
        diacritizedTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Translation with icon
        JLabel translationLabel = new JLabel("Translation :");
        translationLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        translationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        translationLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        translationLabel.setIcon(createTranslationIcon());
        translationLabel.setIconTextGap(8);

        JTextArea translationTextArea = createModernTextArea("Enter translation here...", 3);
        translationTextArea.setPreferredSize(new Dimension(800, 80));
        translationTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Notes with icon
        JLabel notesLabel = new JLabel("Notes :");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        notesLabel.setIcon(createNotesIcon());
        notesLabel.setIconTextGap(8);

        JTextArea notesTextArea = createModernTextArea("Enter notes here...", 3);
        notesTextArea.setPreferredSize(new Dimension(800, 80));
        notesTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Auto-analysis checkbox
        JCheckBox autoAnalyzeCheckbox = new JCheckBox("Perform Automatic Morphological Analysis");
        autoAnalyzeCheckbox.setSelected(true);
        autoAnalyzeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        autoAnalyzeCheckbox.setBackground(Color.WHITE);
        autoAnalyzeCheckbox.setForeground(TEXT_DARK);
        autoAnalyzeCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        autoAnalyzeCheckbox.setBorder(new EmptyBorder(25, 0, 25, 0));
        autoAnalyzeCheckbox.setIcon(createAnalysisIcon());
        autoAnalyzeCheckbox.setHorizontalAlignment(SwingConstants.LEFT);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));

        JButton saveBtn = new RoundedButton("Save Sentence", BTN_ADD_BG, Color.WHITE, BTN_ADD_HOVER);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setIcon(createSaveIcon());
        saveBtn.setIconTextGap(10);
        saveBtn.setPreferredSize(new Dimension(180, 50));

        JButton cancelBtn = new RoundedButton("Cancel", 
            new Color(107, 114, 128), Color.WHITE, new Color(75, 85, 99));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setIcon(createCancelIcon());
        cancelBtn.setIconTextGap(10);
        cancelBtn.setPreferredSize(new Dimension(150, 50));

        saveBtn.addActionListener(e -> {
            String arabicText = arabicTextArea.getText().trim();
            if (arabicText.isEmpty()) {
                showErrorDialog("Arabic text is required.");
                return;
            }

            try {
                SentenceDTO sentenceDTO = new SentenceDTO();
                sentenceDTO.setBookId(book.getBookId());
                sentenceDTO.setChapterId(chapter.getChapterId());
                sentenceDTO.setText(arabicText);
                sentenceDTO.setTextDiacritized(diacritizedTextArea.getText().trim());
                sentenceDTO.setTranslation(translationTextArea.getText().trim());
                sentenceDTO.setNotes(notesTextArea.getText().trim());

                facade.addSentence(sentenceDTO);
                SentenceDTO addedSentence = facade.getSentenceByTextAndBook(arabicText, book.getBookId());
                
                if (addedSentence != null && addedSentence.getSentenceId() > 0) {
                    if (autoAnalyzeCheckbox.isSelected()) {
                        performAutomaticAnalysis(addedSentence);
                        parentFrame.dispose();
                    } else {
                        showSuccessDialog("Sentence added successfully!\n\nMorphological analysis was not performed.");
                        parentFrame.dispose();
                        showMainMenu();
                    }
                } else {
                    showSuccessDialog("Sentence added successfully, but could not retrieve it for automatic analysis.");
                    parentFrame.dispose();
                    showMainMenu();
                }
                
            } catch (SQLException | IllegalArgumentException ex) {
                showErrorDialog("Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> {
            parentFrame.dispose();
            showMainMenu();
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        // Add components to content panel
        contentPanel.add(arabicLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(arabicTextArea);
        contentPanel.add(diacritizedLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(diacritizedTextArea);
        contentPanel.add(translationLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(translationTextArea);
        contentPanel.add(notesLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(notesTextArea);
        contentPanel.add(autoAnalyzeCheckbox);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(buttonPanel);

        // Wrap in scroll pane for smaller screens
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // NEW METHOD: Create icon for add sentence window header
    private Icon createAddSentenceWindowIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 40;
                
                // Document with plus sign
                g2d.setStroke(new BasicStroke(2.5f));
                
                // Document outline
                g2d.drawRoundRect(x + 6, y + 6, size - 12, size - 12, 6, 6);
                
                // Document lines
                g2d.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < 2; i++) {
                    g2d.drawLine(x + 10, y + 15 + i*8, x + size - 10, y + 15 + i*8);
                }
                
                // Plus sign in circle
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int circleSize = 16;
                int circleX = x + size - circleSize - 4;
                int circleY = y + 4;
                
                g2d.drawOval(circleX, circleY, circleSize, circleSize);
                
                int plusSize = 6;
                int centerX = circleX + circleSize/2;
                int centerY = circleY + circleSize/2;
                g2d.drawLine(centerX - plusSize, centerY, centerX + plusSize, centerY);
                g2d.drawLine(centerX, centerY - plusSize, centerX, centerY + plusSize);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 40; }

            @Override
            public int getIconHeight() { return 40; }
        };
    }

    // NEW METHOD: Create cancel icon
    private Icon createCancelIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 16;
                
                // X icon
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + 4, y + 4, x + size - 4, y + size - 4);
                g2d.drawLine(x + size - 4, y + 4, x + 4, y + size - 4);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // UPDATE the existing showAddSentenceDialog method to keep it for other uses if neede
// Custom icon creation methods
   private Icon createBookSelectionIcon(boolean isAddMode) {
	    return new Icon() {
	        @Override
	        public void paintIcon(Component c, Graphics g, int x, int y) {
	            Graphics2D g2d = (Graphics2D) g.create();
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            
	            int size = 48;
	            Color mainColor = isAddMode ? BTN_ADD_BG : BTN_MODIFY_BG;
	            Color shelfColor = darker(mainColor, 0.4);
	            
	            // Bookshelf with 3D effect
	            GradientPaint shelfGradient = new GradientPaint(
	                x, y, darker(shelfColor, 0.2),
	                x + size, y + size, shelfColor
	            );
	            g2d.setPaint(shelfGradient);
	            
	            // Main shelf body
	            g2d.fillRoundRect(x + 6, y + 10, size - 12, size - 18, 5, 5);
	            
	            // Shelf details
	            g2d.setColor(darker(shelfColor, 0.3));
	            g2d.setStroke(new BasicStroke(2f));
	            g2d.drawRoundRect(x + 6, y + 10, size - 12, size - 18, 5, 5);
	            
	            // Shelf dividers with depth
	            g2d.setStroke(new BasicStroke(1.5f));
	            g2d.drawLine(x + 8, y + 22, x + size - 8, y + 22);
	            g2d.drawLine(x + 8, y + 32, x + size - 8, y + 32);
	            
	            // Books with varied colors and spines
	            Color[] bookColors = {
	                mainColor,
	                lighter(mainColor, 0.3),
	                darker(mainColor, 0.2),
	                lighter(mainColor, 0.1)
	            };
	            
	            // Book 1 - Leftmost
	            drawBook(g2d, x + 10, y + 14, 6, 16, bookColors[0], true);
	            
	            // Book 2
	            drawBook(g2d, x + 18, y + 16, 7, 14, bookColors[1], false);
	            
	            // Book 3
	            drawBook(g2d, x + 27, y + 13, 8, 17, bookColors[2], true);
	            
	            // Book 4 - Rightmost
	            drawBook(g2d, x + 37, y + 15, 6, 15, bookColors[3], false);
	            
	            // Action symbol (plus or pencil) with circular background
	            int symbolSize = 16;
	            int symbolX = x + size - symbolSize - 4;
	            int symbolY = y + 4;
	            
	            // Circular background for action symbol
	            g2d.setColor(mainColor);
	            g2d.fillOval(symbolX, symbolY, symbolSize, symbolSize);
	            
	            g2d.setColor(Color.WHITE);
	            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	            
	            if (isAddMode) {
	                // Plus sign
	                int plusSize = 7;
	                int centerX = symbolX + symbolSize/2;
	                int centerY = symbolY + symbolSize/2;
	                g2d.drawLine(centerX - plusSize, centerY, centerX + plusSize, centerY);
	                g2d.drawLine(centerX, centerY - plusSize, centerX, centerY + plusSize);
	            } else {
	                // Pencil icon
	                int pencilX = symbolX + 4;
	                int pencilY = symbolY + 4;
	                
	                // Pencil body
	                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	                g2d.drawLine(pencilX, pencilY + 8, pencilX + 6, pencilY + 2);
	                
	                // Pencil tip
	                g2d.setStroke(new BasicStroke(1.5f));
	                g2d.drawLine(pencilX + 6, pencilY + 2, pencilX + 8, pencilY + 4);
	                
	                // Pencil lead
	                g2d.setColor(new Color(80, 80, 80));
	                g2d.setStroke(new BasicStroke(1f));
	                g2d.drawLine(pencilX + 7, pencilY + 3, pencilX + 8, pencilY + 4);
	            }
	            
	            // Subtle shadow under bookshelf
	            g2d.setColor(new Color(0, 0, 0, 40));
	            g2d.fillRoundRect(x + 8, y + size - 8, size - 16, 4, 2, 2);
	            
	            g2d.dispose();
	        }

	        @Override
	        public int getIconWidth() { return 48; }

	        @Override
	        public int getIconHeight() { return 48; }
	    };
	}

	// Helper method to draw individual books with details
	private void drawBook(Graphics2D g2d, int x, int y, int width, int height, Color color, boolean hasTitle) {
	    // Book cover with gradient
	    GradientPaint bookGradient = new GradientPaint(
	        x, y, lighter(color, 0.2),
	        x + width, y + height, color
	    );
	    g2d.setPaint(bookGradient);
	    g2d.fillRect(x, y, width, height);
	    
	    // Book spine
	    g2d.setColor(darker(color, 0.3));
	    g2d.setStroke(new BasicStroke(1f));
	    g2d.drawLine(x, y, x, y + height);
	    
	    // Book pages (right edge)
	    g2d.setColor(Color.WHITE);
	    g2d.setStroke(new BasicStroke(0.8f));
	    for (int i = 1; i < 4; i++) {
	        g2d.drawLine(x + width - i, y + 2, x + width - i, y + height - 2);
	    }
	    
	    // Book title/decoration if needed
	    if (hasTitle) {
	        g2d.setColor(darker(color, 0.6));
	        g2d.setStroke(new BasicStroke(0.8f));
	        g2d.drawLine(x + 2, y + height/2 - 1, x + width - 3, y + height/2 - 1);
	        g2d.drawLine(x + 2, y + height/2 + 1, x + width - 3, y + height/2 + 1);
	    }
	    
	    // Book shadow
	    g2d.setColor(new Color(0, 0, 0, 30));
	    g2d.fillRect(x + 1, y + height, width - 1, 1);
	}
private Icon createInstructionIcon(boolean isAddMode) {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int size = 24;
            
            // Main bubble background
            g2d.setColor(isAddMode ? BTN_ADD_BG : BTN_MODIFY_BG);
            g2d.fillRoundRect(x + 3, y + 3, size - 6, size - 6, 8, 8);
            
            // Bubble border
            g2d.setColor(isAddMode ? darker1(BTN_ADD_BG, 0.2) : darker1(BTN_MODIFY_BG, 0.2));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x + 3, y + 3, size - 6, size - 6, 8, 8);
            
            // Speech bubble tail
            int[] tailX = {x + 10, x + 14, x + 18};
            int[] tailY = {y + size - 3, y + size + 2, y + size - 3};
            g2d.fillPolygon(tailX, tailY, 3);
            
            // Info "i" symbol - white and centered
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int centerX = x + size/2;
            // Dot of the "i"
            g2d.fillOval(centerX - 1, y + 7, 3, 3);
            // Stem of the "i"
            g2d.drawLine(centerX, y + 11, centerX, y + 15);
            
            // Optional: Add a subtle shine effect
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillRoundRect(x + 5, y + 4, 8, 4, 3, 3);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 24; }

        @Override
        public int getIconHeight() { return 26; } // Slightly taller to accommodate tail
    };
}

// Helper method to create darker shades
private Color darker(Color color, double factor) {
    return new Color(
        Math.max((int)(color.getRed() * factor), 0),
        Math.max((int)(color.getGreen() * factor), 0),
        Math.max((int)(color.getBlue() * factor), 0)
    );
}

private Icon createBookIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 20;
            
            // Book icon
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Book cover
            g2d.drawRoundRect(x + 4, y + 2, size - 8, size - 4, 3, 3);
            
            // Book spine
            g2d.drawLine(x + 4, y + 2, x + 4, y + size - 2);
            
            // Pages
            g2d.setStroke(new BasicStroke(1f));
            for (int i = 0; i < 3; i++) {
                g2d.drawLine(x + 6 + i, y + 4, x + 6 + i, y + size - 4);
            }
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 20; }

        @Override
        public int getIconHeight() { return 20; }
    };
}

private Icon createChapterIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 20;
            
            // Stacked documents for chapters
            g2d.setStroke(new BasicStroke(1.5f));
            
            // First document
            g2d.drawRoundRect(x + 6, y + 4, size - 12, size - 8, 2, 2);
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawLine(x + 8, y + 8, x + size - 4, y + 8);
            g2d.drawLine(x + 8, y + 10, x + size - 4, y + 10);
            
            // Second document (behind)
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x + 2, y + 2, size - 12, size - 8, 2, 2);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 20; }

        @Override
        public int getIconHeight() { return 20; }
    };
}

private Icon createProceedIcon(boolean isAddMode) {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            
            int size = 16;
            
            // Arrow icon
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // Arrow body
            g2d.drawLine(x + 4, y + size/2, x + size - 4, y + size/2);
            
            // Arrow head
            g2d.drawLine(x + size - 6, y + 4, x + size - 4, y + size/2);
            g2d.drawLine(x + size - 6, y + size - 4, x + size - 4, y + size/2);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 16; }

        @Override
        public int getIconHeight() { return 16; }
    };
}

private void showAddSentenceDialog(BookDTO book, ChapterDTO chapter) {
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    JDialog addDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null, 
        "Add New Sentence", true);
    addDialog.setSize(1000, 750); // Increased height to accommodate analysis checkbox
    addDialog.setLocationRelativeTo(this);
    addDialog.setLayout(new BorderLayout());
    addDialog.getContentPane().setBackground(BG_COLOR);
    addDialog.setResizable(true);

    // Header with custom icon
    JPanel headerPanel = createDialogHeader("Add New Sentence", 100);
  
    
    JLabel subtitle = new JLabel("Book: " + book.getTitle() + " | Chapter: " + chapter.getChapterName());
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    subtitle.setForeground(Color.WHITE);
    subtitle.setHorizontalAlignment(SwingConstants.CENTER);
    headerPanel.add(subtitle, BorderLayout.SOUTH);
    addDialog.add(headerPanel, BorderLayout.NORTH);

    // Content
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
    contentPanel.setBackground(Color.WHITE);

    // Arabic Text with icon - Left aligned
    JLabel arabicLabel = new JLabel("Arabic Text : ");
    arabicLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    arabicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    arabicLabel.setIcon(createArabicTextIcon());
    arabicLabel.setIconTextGap(8);
    arabicLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JTextArea arabicTextArea = createModernTextArea("Enter Arabic text here...", 3);
    arabicTextArea.setPreferredSize(new Dimension(540, 80));
    arabicTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Diacritized Text with icon - Left aligned
    JLabel diacritizedLabel = new JLabel("Diacritized Text : ");
    diacritizedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    diacritizedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    diacritizedLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
    diacritizedLabel.setIcon(createDiacritizedIcon());
    diacritizedLabel.setIconTextGap(8);
    diacritizedLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JTextArea diacritizedTextArea = createModernTextArea("Enter diacritized text here...", 2);
    diacritizedTextArea.setPreferredSize(new Dimension(540, 60));
    diacritizedTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Translation with icon - Left aligned
    JLabel translationLabel = new JLabel("Translation :");
    translationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    translationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    translationLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
    translationLabel.setIcon(createTranslationIcon());
    translationLabel.setIconTextGap(8);
    translationLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JTextArea translationTextArea = createModernTextArea("Enter translation here...", 2);
    translationTextArea.setPreferredSize(new Dimension(540, 60));
    translationTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Notes with icon - Left aligned
    JLabel notesLabel = new JLabel("Notes :");
    notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    notesLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
    notesLabel.setIcon(createNotesIcon());
    notesLabel.setIconTextGap(8);
    notesLabel.setHorizontalAlignment(SwingConstants.LEFT);

    JTextArea notesTextArea = createModernTextArea("Enter notes here...", 2);
    notesTextArea.setPreferredSize(new Dimension(540, 60));
    notesTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Auto-analysis checkbox with custom icon - Left aligned
    JCheckBox autoAnalyzeCheckbox = new JCheckBox("Perform Automatic Morphological Analysis");
    autoAnalyzeCheckbox.setSelected(true);
    autoAnalyzeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    autoAnalyzeCheckbox.setBackground(Color.WHITE);
    autoAnalyzeCheckbox.setForeground(TEXT_DARK);
    autoAnalyzeCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
    autoAnalyzeCheckbox.setBorder(new EmptyBorder(15, 0, 15, 0));
    autoAnalyzeCheckbox.setIcon(createAnalysisIcon());
    autoAnalyzeCheckbox.setHorizontalAlignment(SwingConstants.LEFT);

    // Save Button - Center aligned
    JButton saveBtn = new RoundedButton("Save Sentence", BTN_ADD_BG, Color.WHITE, BTN_ADD_HOVER);
    saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    saveBtn.setIcon(createSaveIcon());
    saveBtn.setIconTextGap(8);
    saveBtn.setHorizontalAlignment(SwingConstants.CENTER);

    saveBtn.addActionListener(e -> {
        String arabicText = arabicTextArea.getText().trim();
        if (arabicText.isEmpty()) {
            showErrorDialog("Arabic text is required.");
            return;
        }

        try {
            SentenceDTO sentenceDTO = new SentenceDTO();
            sentenceDTO.setBookId(book.getBookId());
            sentenceDTO.setChapterId(chapter.getChapterId());
            sentenceDTO.setText(arabicText);
            sentenceDTO.setTextDiacritized(diacritizedTextArea.getText().trim());
            sentenceDTO.setTranslation(translationTextArea.getText().trim());
            sentenceDTO.setNotes(notesTextArea.getText().trim());

            facade.addSentence(sentenceDTO);
            SentenceDTO addedSentence = facade.getSentenceByTextAndBook(arabicText, book.getBookId());
            
            if (addedSentence != null && addedSentence.getSentenceId() > 0) {
                // Automatically perform morphological analysis if checkbox is selected
                if (autoAnalyzeCheckbox.isSelected()) {
                    performAutomaticAnalysis(addedSentence);
                } else {
                    showSuccessDialog("Sentence added successfully!\n\nMorphological analysis was not performed.");
                    addDialog.dispose();
                    showMainMenu();
                }
            } else {
                showSuccessDialog("Sentence added successfully, but could not retrieve it for automatic analysis.");
                addDialog.dispose();
                showMainMenu();
            }
            
        } catch (SQLException | IllegalArgumentException ex) {
            showErrorDialog("Error: " + ex.getMessage());
        }
    });

    // Add components to content panel
    contentPanel.add(arabicLabel);
    contentPanel.add(Box.createVerticalStrut(5));
    contentPanel.add(arabicTextArea);
    contentPanel.add(diacritizedLabel);
    contentPanel.add(Box.createVerticalStrut(5));
    contentPanel.add(diacritizedTextArea);
    contentPanel.add(translationLabel);
    contentPanel.add(Box.createVerticalStrut(5));
    contentPanel.add(translationTextArea);
    contentPanel.add(notesLabel);
    contentPanel.add(Box.createVerticalStrut(5));
    contentPanel.add(notesTextArea);
    contentPanel.add(autoAnalyzeCheckbox);
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(saveBtn);

    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(null);
    scrollPane.setPreferredSize(new Dimension(600, 500));
    addDialog.add(scrollPane, BorderLayout.CENTER);
    addDialog.setVisible(true);
}

// ==================== CUSTOM ICONS FOR ADD SENTENCE DIALOG ====================


private Icon createArabicTextIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 18;
            
            // Arabic text symbol
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Curved Arabic-style characters
            g2d.drawArc(x + 4, y + 6, 4, 3, 0, 180);
            g2d.drawArc(x + 8, y + 6, 4, 3, 0, 180);
            g2d.drawArc(x + 12, y + 6, 4, 3, 0, 180);
            
            // Dot below (characteristic of Arabic)
            g2d.fillOval(x + 6, y + 12, 2, 2);
            g2d.fillOval(x + 12, y + 12, 2, 2);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 18; }

        @Override
        public int getIconHeight() { return 18; }
    };
}

private Icon createDiacritizedIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 18;
            
            // Text with diacritics (dots above)
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Base line
            g2d.drawLine(x + 4, y + 10, x + size - 4, y + 10);
            
            // Diacritic dots above
            g2d.fillOval(x + 6, y + 5, 2, 2);
            g2d.fillOval(x + 10, y + 5, 2, 2);
            g2d.fillOval(x + 14, y + 5, 2, 2);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 18; }

        @Override
        public int getIconHeight() { return 18; }
    };
}

private Icon createTranslationIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 18;
            
            // Translation symbol (two overlapping speech bubbles)
            g2d.setStroke(new BasicStroke(1.5f));
            
            // First speech bubble
            g2d.drawRoundRect(x + 2, y + 4, 8, 6, 2, 2);
            g2d.drawLine(x + 4, y + 10, x + 6, y + 12);
            
            // Second speech bubble (overlapping)
            g2d.drawRoundRect(x + 8, y + 2, 8, 6, 2, 2);
            g2d.drawLine(x + 12, y + 8, x + 14, y + 10);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 18; }

        @Override
        public int getIconHeight() { return 18; }
    };
}

private Icon createNotesIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(TEXT_DARK);
            
            int size = 18;
            
            // Notepad icon
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Notepad outline
            g2d.drawRoundRect(x + 3, y + 2, size - 6, size - 4, 2, 2);
            
            // Spiral binding
            g2d.drawLine(x + 5, y + 3, x + 5, y + size - 3);
            
            // Lines
            g2d.setStroke(new BasicStroke(1f));
            for (int i = 0; i < 3; i++) {
                g2d.drawLine(x + 7, y + 6 + i*4, x + size - 4, y + 6 + i*4);
            }
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 18; }

        @Override
        public int getIconHeight() { return 18; }
    };
}

private Icon createAnalysisIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(BTN_ADD_BG);
            
            int size = 16;
            
            // Magnifying glass over text
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Magnifying glass circle
            g2d.drawOval(x + 3, y + 3, 8, 8);
            
            // Magnifying glass handle
            g2d.drawLine(x + 10, y + 10, x + 13, y + 13);
            
            // Text lines underneath
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawLine(x + 5, y + 6, x + 7, y + 6);
            g2d.drawLine(x + 4, y + 8, x + 8, y + 8);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 16; }

        @Override
        public int getIconHeight() { return 16; }
    };
}

private Icon createSaveIcon() {
    return new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE);
            
            int size = 16;
            
            // Floppy disk icon
            g2d.setStroke(new BasicStroke(1.5f));
            
            // Disk body
            g2d.drawRect(x + 4, y + 3, size - 8, size - 6);
            
            // Disk center
            g2d.drawRect(x + 6, y + 5, 4, 4);
            
            // Label area
            g2d.drawLine(x + 6, y + 10, x + size - 6, y + 10);
            
            g2d.dispose();
        }

        @Override
        public int getIconWidth() { return 16; }

        @Override
        public int getIconHeight() { return 16; }
    };
}

// ==================== AUTOMATIC MORPHOLOGICAL ANALYSIS ====================

    private void performAutomaticAnalysis(SentenceDTO sentence) {
        try {
            System.out.println("Starting automatic morphological analysis...");
            System.out.println("Sentence ID: " + sentence.getSentenceId());
            System.out.println("Text: " + sentence.getText());
            
            // Show analysis in progress dialog
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog progressDialog = createAnalysisProgressDialog(parentWindow);
            progressDialog.setVisible(true);
            
            // Perform analysis in background thread
            SwingWorker<AnalysisResultDTO, Void> worker = new SwingWorker<AnalysisResultDTO, Void>() {
                @Override
                protected AnalysisResultDTO doInBackground() throws Exception {
                    return facade.analyzeTextAutomatically(sentence.getSentenceId(), sentence.getText());
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    try {
                        AnalysisResultDTO result = get();
                        showAnalysisSuccess(sentence, result);
                    } catch (Exception e) {
                        showAnalysisError(sentence, e);
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception e) {
            showAnalysisError(sentence, e);
        }
    }

    private JDialog createAnalysisProgressDialog(Window parent) {
        JDialog progressDialog = new JDialog(parent, "Analyzing Sentence");
        progressDialog.setSize(400, 200);
        progressDialog.setLocationRelativeTo(parent);
        progressDialog.setLayout(new BorderLayout());
        progressDialog.setResizable(false);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🔬");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel textLabel = new JLabel("Performing Morphological Analysis...");
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        textLabel.setForeground(ANALYSIS_COLOR);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel("This may take a few moments");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(TEXT_LIGHT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(textLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(subLabel);
        
        progressDialog.add(contentPanel, BorderLayout.CENTER);
        return progressDialog;
    }

    private void showAnalysisSuccess(SentenceDTO sentence, AnalysisResultDTO result) {
        try {
            int tokenCount = facade.getAllTokensByAnalysis(result.getAnalysisId()).size();
            
            String message = String.format(
                "Sentence added and analyzed successfully!\n\n" +
                "Generated morphological data:\n" +
                "• %d tokens\n" +
                "• Lemmas, roots, and segments\n\n" +
                "You can view the results in the Browser tab.",
                tokenCount
            );
            
            showSuccessDialog(message);
            
            System.out.println("Analysis completed successfully");
            System.out.println("Sentence: " + sentence.getText());
            System.out.println("Tokens generated: " + tokenCount);
            
            // Close the add dialog and return to main menu
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof JDialog) {
                ((JDialog) parentWindow).dispose();
            }
            showMainMenu();
            
        } catch (SQLException e) {
            showSuccessDialog("Sentence added and analyzed, but could not retrieve statistics.");
            showMainMenu();
        }
    }

    private void showAnalysisError(SentenceDTO sentence, Exception e) {
        System.err.println("Analysis failed for sentence " + sentence.getSentenceId() + ": " + e.getMessage());
        
        String errorMessage = "Sentence added successfully, but morphological analysis failed.\n" +
                "You can try analyzing it manually later.\n\n" +
                "Error: " + e.getMessage();
        
        // Show option to retry analysis
        int option = JOptionPane.showConfirmDialog(this, 
            errorMessage + "\n\nWould you like to retry the analysis now?",
            "Analysis Failed - Retry?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            // Retry the analysis
            performAutomaticAnalysis(sentence);
        } else {
            showSuccessDialog("Sentence added successfully. You can retry analysis later from the Browser tab.");
            showMainMenu();
        }
    }

    // ==================== MODIFY SENTENCES WORKFLOW ====================

    private void showModifySentencesView(BookDTO book, ChapterDTO chapter) {
        removeAll();
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = createModifyHeader(book, chapter);
        add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = createSearchPanel(book, chapter);
        add(searchPanel, BorderLayout.CENTER);

        // Sentences Container - FULL SCREEN VERTICAL LAYOUT
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(BG_COLOR);
        cardsContainer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = createScrollPane();
        scrollPane.setPreferredSize(new Dimension(getWidth(), getHeight() - 200)); // Full screen height
        add(scrollPane, BorderLayout.SOUTH);

        // Load sentences
        loadSentencesForModify(book, chapter);

        revalidate();
        repaint();
    }

    private JPanel createModifyHeader(BookDTO book, ChapterDTO chapter) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_START);
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));

        // Create title panel with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel titleIcon = new JLabel(createModifyHeaderIcon());
        JLabel titleLabel = new JLabel("Modify Sentences");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleIcon);
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Book: " + book.getTitle() + " | Chapter: " + chapter.getChapterName());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JButton backBtn = new RoundedButton("Back to Main Menu", 
            new Color(107, 114, 128), Color.WHITE, new Color(75, 85, 99));
        backBtn.setIcon(createBackIcon());
        backBtn.setIconTextGap(8);
        backBtn.addActionListener(e -> showMainMenu());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(titlePanel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);

        headerPanel.add(leftPanel, BorderLayout.CENTER);
        headerPanel.add(backBtn, BorderLayout.EAST);

        return headerPanel;
    }

    // ==================== CUSTOM ICONS FOR MODIFY HEADER ====================

    private Icon createModifyHeaderIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 32;
                
                // Stacked documents with pencil
                g2d.setStroke(new BasicStroke(2f));
                
                // Background document
                g2d.drawRoundRect(x + 4, y + 6, size - 8, size - 12, 4, 4);
                
                // Foreground document
                g2d.drawRoundRect(x + 8, y + 2, size - 8, size - 12, 4, 4);
                
                // Pencil
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + size - 8, y + 6, x + size - 2, y + 2);
                g2d.drawLine(x + size - 2, y + 2, x + size, y + 4);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 32; }

            @Override
            public int getIconHeight() { return 32; }
        };
    }

    private Icon createBackIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 16;
                
                // Arrow pointing left
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + size - 4, y + 4, x + 4, y + size/2);
                g2d.drawLine(x + 4, y + size/2, x + size - 4, y + size - 4);
                g2d.drawLine(x + 6, y + size/2, x + size - 2, y + size/2);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private JPanel createSearchPanel(BookDTO book, ChapterDTO chapter) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        searchPanel.setBackground(BG_COLOR);
        searchPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        searchPanel.setPreferredSize(new Dimension(getWidth(), 70));

        JLabel searchLabel = new JLabel("Search Sentence:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_DARK);
        searchLabel.setIcon(createSearchLabelIcon());
        searchLabel.setIconTextGap(8);

        JTextField searchField = new JTextField(30);
        styleField(searchField);

        JButton searchBtn = new RoundedButton("Search", INFO_COLOR, Color.WHITE, new Color(37, 99, 235));
        searchBtn.setIcon(createSearchButtonIcon());
        searchBtn.setIconTextGap(8);

        JButton showAllBtn = new RoundedButton("Show All", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
        showAllBtn.setIcon(createShowAllIcon());
        showAllBtn.setIconTextGap(8);

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                showErrorDialog("Please enter search text.");
                return;
            }
            searchSentences(book, chapter, searchText);
        });

        showAllBtn.addActionListener(e -> loadSentencesForModify(book, chapter));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        return searchPanel;
    }

    // ==================== CUSTOM ICONS FOR SEARCH PANEL ====================

    private Icon createSearchLabelIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_DARK);
                
                int size = 16;
                
                // Magnifying glass
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x + 2, y + 2, size - 6, size - 6);
                g2d.drawLine(x + size - 4, y + size - 4, x + size, y + size);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private Icon createSearchButtonIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 14;
                
                // Magnifying glass
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x + 2, y + 2, size - 5, size - 5);
                g2d.drawLine(x + size - 3, y + size - 3, x + size, y + size);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 14; }

            @Override
            public int getIconHeight() { return 14; }
        };
    }

    private Icon createShowAllIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 14;
                
                // Stacked lines representing multiple items
                g2d.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 2, y + 4 + i*3, x + size - 2, y + 4 + i*3);
                }
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 14; }

            @Override
            public int getIconHeight() { return 14; }
        };
    }

    private void loadSentencesForModify(BookDTO book, ChapterDTO chapter) {
        cardsContainer.removeAll();
        
        JLabel loadingLabel = new JLabel("⏳ Loading sentences...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loadingLabel.setForeground(TEXT_LIGHT);
        loadingLabel.setBorder(new EmptyBorder(50, 50, 50, 50));
        cardsContainer.add(loadingLabel);
        cardsContainer.revalidate();
        cardsContainer.repaint();
        
        SwingUtilities.invokeLater(() -> {
            try {
                currentSentences = facade.getSentencesByChapterId(chapter.getChapterId());
                displaySentencesCards(currentSentences);
            } catch (SQLException ex) {
                showErrorDialog("Error loading sentences: " + ex.getMessage());
            }
        });
    }

    private void searchSentences(BookDTO book, ChapterDTO chapter, String searchText) {
        cardsContainer.removeAll();
        
        JLabel loadingLabel = new JLabel("🔍 Searching sentences...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loadingLabel.setForeground(TEXT_LIGHT);
        loadingLabel.setBorder(new EmptyBorder(50, 50, 50, 50));
        cardsContainer.add(loadingLabel);
        cardsContainer.revalidate();
        cardsContainer.repaint();
        
        SwingUtilities.invokeLater(() -> {
            try {
                List<SentenceDTO> allSentences = facade.getSentencesByChapterId(chapter.getChapterId());
                List<SentenceDTO> searchResults = new ArrayList<>();
                
                for (SentenceDTO sentence : allSentences) {
                    if (sentence.getText().toLowerCase().contains(searchText.toLowerCase()) ||
                        (sentence.getTranslation() != null && sentence.getTranslation().toLowerCase().contains(searchText.toLowerCase()))) {
                        searchResults.add(sentence);
                    }
                }
                
                if (searchResults.isEmpty()) {
                    showEmptySearchState(searchText);
                } else {
                    displaySentencesCards(searchResults);
                    showSuccessDialog("Found " + searchResults.size() + " sentence(s) matching: " + searchText);
                }
            } catch (SQLException ex) {
                showErrorDialog("Error searching sentences: " + ex.getMessage());
            }
        });
    }

    private void displaySentencesCards(List<SentenceDTO> sentences) {
        cardsContainer.removeAll();
        currentSentences = sentences;
        
        if (sentences.isEmpty()) {
            showEmptyState();
        } else {
            for (SentenceDTO sentence : sentences) {
                SentenceCard sentenceCard = new SentenceCard(sentence);
                sentenceCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardsContainer.add(sentenceCard);
                cardsContainer.add(Box.createVerticalStrut(10)); // Space between cards
            }
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void showEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.setPreferredSize(new Dimension(getWidth(), 300));
        
        JLabel emptyIcon = new JLabel("📝");
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyIcon.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel emptyLabel = new JLabel("No sentences found");
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        emptyLabel.setForeground(TEXT_DARK);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel emptySubLabel = new JLabel("This chapter doesn't have any sentences yet");
        emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emptySubLabel.setForeground(TEXT_LIGHT);
        emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emptyPanel.add(emptyIcon);
        emptyPanel.add(emptyLabel);
        emptyPanel.add(emptySubLabel);
        cardsContainer.add(emptyPanel);
    }

    private void showEmptySearchState(String searchText) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setOpaque(false);
        emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.setPreferredSize(new Dimension(getWidth(), 300));
        
        JLabel emptyIcon = new JLabel("🔍");
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyIcon.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel emptyLabel = new JLabel("No sentences found");
        emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        emptyLabel.setForeground(TEXT_DARK);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel emptySubLabel = new JLabel("No sentences match: \"" + searchText + "\"");
        emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emptySubLabel.setForeground(TEXT_LIGHT);
        emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emptyPanel.add(emptyIcon);
        emptyPanel.add(emptyLabel);
        emptyPanel.add(emptySubLabel);
        cardsContainer.add(emptyPanel);
    }

    // ==================== SENTENCE CARD WITH EDIT/DELETE ====================

    private class SentenceCard extends JPanel {
        private boolean isHovered = false;
        private boolean showButtons = false;
        private final SentenceDTO sentence;
        private JPanel buttonPanel;
        
        public SentenceCard(SentenceDTO sentence) {
            this.sentence = sentence;
            
            this.setMaximumSize(new Dimension(Short.MAX_VALUE, 150));
            this.setPreferredSize(new Dimension(getWidth() - 40, 150));
            this.setLayout(new BorderLayout(15, 10));
            this.setBackground(CARD_BG);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.setBorder(new EmptyBorder(15, 20, 15, 20));
            this.setOpaque(false);

            // Left side - Sentence text
            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);
            textPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            String displayText = sentence.getText();
            JTextArea textArea = new JTextArea(displayText);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 16));
            textArea.setBackground(new Color(250, 250, 250));
            textArea.setForeground(TEXT_DARK);
            textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            textArea.setFocusable(false);
            textArea.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggleButtons();
                }
            });
            
            JScrollPane textScroll = new JScrollPane(textArea);
            textScroll.setPreferredSize(new Dimension(getWidth() - 200, 120));
            textScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            textScroll.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            textScroll.getViewport().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggleButtons();
                }
            });
            
            textPanel.add(textScroll, BorderLayout.CENTER);

            // Right side - Action buttons (initially hidden)
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
            buttonPanel.setOpaque(false);
            buttonPanel.setPreferredSize(new Dimension(150, 120));
            buttonPanel.setVisible(false);
            
            JButton editBtn = createSmallButton("Edit", INFO_COLOR);
            JButton deleteBtn = createSmallButton("Delete", ERROR_COLOR);
            
            editBtn.setIcon(createEditIcon());
            editBtn.setIconTextGap(6);
            deleteBtn.setIcon(createDeleteIcon());
            deleteBtn.setIconTextGap(6);
            
            editBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            deleteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            editBtn.addActionListener(e -> showEditDialog(sentence));
            deleteBtn.addActionListener(e -> deleteSentence(sentence));
            
            buttonPanel.add(editBtn);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(deleteBtn);
            buttonPanel.add(Box.createVerticalGlue());

            add(textPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);
            
            // Add mouse listener to the entire card
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { 
                    isHovered = true; 
                    repaint(); 
                }
                
                public void mouseExited(MouseEvent e) { 
                    isHovered = false; 
                    repaint(); 
                }
                
                public void mouseClicked(MouseEvent e) { 
                    toggleButtons();
                }
            });
            
            textPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    toggleButtons();
                }
            });
        }

        private void toggleButtons() {
            showButtons = !showButtons;
            buttonPanel.setVisible(showButtons);
            revalidate();
            repaint();
            
            hideButtonsInOtherCards(SentenceCard.this);
        }

        public void hideButtons() {
            showButtons = false;
            buttonPanel.setVisible(false);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 12;
            int shadowOffset = isHovered ? 4 : 2;
            Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;
            
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius);
            
            g2.setColor(isHovered ? new Color(250, 250, 250) : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            g2.setColor(isHovered ? HEADER_START : new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
    }

    // ==================== CUSTOM ICONS FOR EDIT AND DELETE BUTTONS ====================

    private Icon createEditIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 12;
                
                // Pencil icon with detailed design
                g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Pencil body
                g2d.drawLine(x + 2, y + size - 2, x + size - 2, y + 2);
                
                // Pencil tip
                g2d.drawLine(x + size - 2, y + 2, x + size, y + 4);
                
                // Pencil eraser (top part)
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawLine(x + 1, y + size - 1, x + 3, y + size - 3);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 12; }

            @Override
            public int getIconHeight() { return 12; }
        };
    }

    private Icon createDeleteIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 12;
                
                // Trash can icon with detailed design
                g2d.setStroke(new BasicStroke(1.5f));
                
                // Trash can body
                g2d.drawRect(x + 2, y + 4, size - 4, size - 6);
                
                // Trash can lid
                g2d.drawLine(x + 1, y + 4, x + size - 1, y + 4);
                
                // Lid handles
                g2d.drawLine(x + 4, y + 2, x + 4, y + 4);
                g2d.drawLine(x + size - 4, y + 2, x + size - 4, y + 4);
                
                // Trash lines inside
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawLine(x + 4, y + 7, x + size - 4, y + 7);
                g2d.drawLine(x + 4, y + 9, x + size - 4, y + 9);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 12; }

            @Override
            public int getIconHeight() { return 12; }
        };
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(120, 40));
        return button;
    }

    // Helper method to hide buttons in all cards except the clicked one
    private void hideButtonsInOtherCards(SentenceCard clickedCard) {
        if (cardsContainer != null) {
            for (Component comp : cardsContainer.getComponents()) {
                if (comp instanceof SentenceCard && comp != clickedCard) {
                    ((SentenceCard) comp).hideButtons();
                }
            }
        }
    }

    private void showEditDialog(SentenceDTO sentence) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog editDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null, 
            "Edit Sentence", true);
        editDialog.setSize(1000, 700);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(new BorderLayout());
        editDialog.getContentPane().setBackground(BG_COLOR);
        editDialog.setResizable(true);

        // Header with icon
        JPanel headerPanel = createDialogHeader("Edit Sentence", 70);
        
        // Add header icon
        JLabel headerIconLabel = new JLabel(createEditDialogHeaderIcon());
        headerIconLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        headerIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(headerIconLabel, BorderLayout.NORTH);
        
        editDialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        // Arabic Text with icon
        JLabel arabicLabel = new JLabel("Arabic Text *:");
        arabicLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        arabicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        arabicLabel.setIcon(createArabicTextIcon());
        arabicLabel.setIconTextGap(8);

        JTextArea arabicTextArea = createModernTextArea("", 3);
        arabicTextArea.setText(sentence.getText());
        arabicTextArea.setPreferredSize(new Dimension(540, 80));
        arabicTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Diacritized Text with icon
        JLabel diacritizedLabel = new JLabel("Diacritized Text:");
        diacritizedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        diacritizedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diacritizedLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        diacritizedLabel.setIcon(createDiacritizedIcon());
        diacritizedLabel.setIconTextGap(8);

        JTextArea diacritizedTextArea = createModernTextArea("", 2);
        diacritizedTextArea.setText(sentence.getTextDiacritized() != null ? sentence.getTextDiacritized() : "");
        diacritizedTextArea.setPreferredSize(new Dimension(540, 60));
        diacritizedTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Translation with icon
        JLabel translationLabel = new JLabel("Translation:");
        translationLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        translationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        translationLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        translationLabel.setIcon(createTranslationIcon());
        translationLabel.setIconTextGap(8);

        JTextArea translationTextArea = createModernTextArea("", 2);
        translationTextArea.setText(sentence.getTranslation() != null ? sentence.getTranslation() : "");
        translationTextArea.setPreferredSize(new Dimension(540, 60));
        translationTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Notes with icon
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        notesLabel.setIcon(createNotesIcon());
        notesLabel.setIconTextGap(8);

        JTextArea notesTextArea = createModernTextArea("", 2);
        notesTextArea.setText(sentence.getNotes() != null ? sentence.getNotes() : "");
        notesTextArea.setPreferredSize(new Dimension(540, 60));
        notesTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Update Button with icon
        JButton updateBtn = new RoundedButton("Update Sentence", INFO_COLOR, Color.WHITE, new Color(37, 99, 235));
        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateBtn.setIcon(createUpdateIcon());
        updateBtn.setIconTextGap(8);

        updateBtn.addActionListener(e -> {
            String arabicText = arabicTextArea.getText().trim();
            if (arabicText.isEmpty()) {
                showErrorDialog("Arabic text is required.");
                return;
            }

            try {
                sentence.setText(arabicText);
                sentence.setTextDiacritized(diacritizedTextArea.getText().trim());
                sentence.setTranslation(translationTextArea.getText().trim());
                sentence.setNotes(notesTextArea.getText().trim());

                facade.updateSentence(sentence);
                showSuccessDialog("Sentence updated successfully!");
                editDialog.dispose();
                // Refresh the current view
                BookDTO currentBook = facade.getBookById(sentence.getBookId());
                ChapterDTO currentChapter = facade.getChapterById(sentence.getChapterId());
                if (currentBook != null && currentChapter != null) {
                    loadSentencesForModify(currentBook, currentChapter);
                }

            } catch (SQLException ex) {
                showErrorDialog("Error updating sentence: " + ex.getMessage());
            }
        });

        contentPanel.add(arabicLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(arabicTextArea);
        contentPanel.add(diacritizedLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(diacritizedTextArea);
        contentPanel.add(translationLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(translationTextArea);
        contentPanel.add(notesLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(notesTextArea);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(updateBtn);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(600, 430));
        editDialog.add(scrollPane, BorderLayout.CENTER);
        editDialog.setVisible(true);
    }

    // ==================== CUSTOM ICONS FOR EDIT DIALOG ====================

    private Icon createEditDialogHeaderIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 36;
                
                // Document with pencil (edit symbol)
                g2d.setStroke(new BasicStroke(2f));
                
                // Document outline
                g2d.drawRoundRect(x + 6, y + 4, size - 12, size - 8, 4, 4);
                
                // Document lines
                g2d.setStroke(new BasicStroke(1f));
                for (int i = 0; i < 2; i++) {
                    g2d.drawLine(x + 10, y + 10 + i*6, x + size - 10, y + 10 + i*6);
                }
                
                // Pencil for editing
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x + size - 10, y + 6, x + size - 4, y + 2);
                g2d.drawLine(x + size - 4, y + 2, x + size - 2, y + 4);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 36; }

            @Override
            public int getIconHeight() { return 36; }
        };
    }

    private Icon createUpdateIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 16;
                
                // Checkmark with circular arrow (update symbol)
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Circular arrow
                g2d.drawArc(x + 2, y + 2, size - 4, size - 4, 0, 270);
                
                // Checkmark
                g2d.drawLine(x + 4, y + size/2, x + size/2 - 1, y + size - 4);
                g2d.drawLine(x + size/2 - 1, y + size - 4, x + size - 2, y + 4);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // Reuse existing icons from your previous implementation



    private void deleteSentence(SentenceDTO sentence) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this sentence?\n\n\"" + 
            (sentence.getText().length() > 50 ? sentence.getText().substring(0, 47) + "..." : sentence.getText()) + "\"",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.deleteSentence(sentence.getSentenceId());
                showSuccessDialog("Sentence deleted successfully!");
                // Refresh the current view
                BookDTO currentBook = facade.getBookById(sentence.getBookId());
                ChapterDTO currentChapter = facade.getChapterById(sentence.getChapterId());
                if (currentBook != null && currentChapter != null) {
                    loadSentencesForModify(currentBook, currentChapter);
                }
            } catch (SQLException ex) {
                showErrorDialog("Error deleting sentence: " + ex.getMessage());
            }
        }
    }

    // ==================== UTILITY METHODS ====================

    private JPanel createDialogHeader(String title, int height) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_START);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(getWidth(), height));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200, 100);
                this.trackColor = new Color(240, 240, 240, 50);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) { 
                return createZeroButton(); 
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) { 
                return createZeroButton(); 
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        return scrollPane;
    }

    private void loadBooksIntoCombo(JComboBox<Object> bookComboBox) {
        try {
            List<BookDTO> books = facade.getAllBooks();
            bookComboBox.removeAllItems();
            if (books.isEmpty()) {
                bookComboBox.addItem(new BookDTO(0, "No books available", 0, ""));
                bookComboBox.setEnabled(false);
            } else {
                bookComboBox.setEnabled(true);
                for (BookDTO book : books) {
                    bookComboBox.addItem(book);
                }
                bookComboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof BookDTO) {
                            BookDTO book = (BookDTO) value;
                            setText(book.getTitle());
                        }
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        return this;
                    }
                });
            }
        } catch (SQLException ex) {
            bookComboBox.removeAllItems();
            bookComboBox.addItem(new BookDTO(0, "Error loading books", 0, ""));
            bookComboBox.setEnabled(false);
        }
    }

    private void loadChaptersIntoCombo(JComboBox<Object> chapterComboBox, BookDTO book) {
        try {
            List<ChapterDTO> chapters = facade.getChaptersByBookId(book.getBookId());
            chapterComboBox.removeAllItems();
            
            if (chapters.isEmpty()) {
                chapterComboBox.setEnabled(false);
            } else {
                chapterComboBox.setEnabled(true);
                for (ChapterDTO chapter : chapters) {
                    chapterComboBox.addItem(chapter);
                }
                chapterComboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof ChapterDTO) {
                            ChapterDTO chapter = (ChapterDTO) value;
                            setText(chapter.getChapterName());
                        }
                        setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        return this;
                    }
                });
            }
        } catch (SQLException ex) {
            chapterComboBox.removeAllItems();
            chapterComboBox.setEnabled(false);
        }
    }

    private JComboBox<Object> createStyledComboBox() {
        JComboBox<Object> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_DARK);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        combo.setPreferredSize(new Dimension(200, 42));
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return combo;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(new Color(250, 250, 250));
        field.setPreferredSize(new Dimension(250, 40));
    }

    private JTextArea createModernTextArea(String placeholder, int rows) {
        JTextArea textArea = new JTextArea(rows, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 16));
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(TEXT_DARK);
        textArea.setCaretColor(HEADER_START);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return textArea;
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "✅ Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "❌ Error", JOptionPane.ERROR_MESSAGE);
    }

    // RoundedButton class
    private class RoundedButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;

        public RoundedButton(String text, Color bg, Color fg, Color hover) {
            super(text);
            this.baseColor = bg;
            this.hoverColor = hover;
            
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 25, 12, 25));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color currentColor = isHovered ? hoverColor : baseColor;
            if (isHovered) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 35, 35);
            }
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}