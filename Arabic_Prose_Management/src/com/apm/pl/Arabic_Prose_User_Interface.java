package com.apm.pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.SentenceDTO;

/**
 * Modern Arabic Prose Management System with Professional UI
 * Features: Smooth animations, hover effects, gradient backgrounds, rounded corners
 */
public class Arabic_Prose_User_Interface extends JFrame {

    private IBussinessLayerFasade BussinessLayerFasade;
    
    
    
    // 🎨 Modern Color Palette
    private static final Color PRIMARY_DARK = new Color(31, 58, 147);      // Deep Blue
    private static final Color PRIMARY = new Color(63, 81, 181);           // Indigo
    private static final Color PRIMARY_LIGHT = new Color(92, 107, 192);    // Light Indigo
    private static final Color ACCENT = new Color(255, 64, 129);           // Pink Accent
    
    private static final Color SUCCESS = new Color(76, 175, 80);           // Green
    private static final Color SUCCESS_HOVER = new Color(104, 195, 108);
    
    private static final Color INFO = new Color(33, 150, 243);             // Blue
    private static final Color INFO_HOVER = new Color(66, 165, 245);
    
    private static final Color WARNING = new Color(255, 152, 0);           // Orange
    private static final Color WARNING_HOVER = new Color(255, 167, 38);
    
    private static final Color DANGER = new Color(244, 67, 54);            // Red
    private static final Color DANGER_HOVER = new Color(239, 83, 80);
    
    private static final Color BACKGROUND = new Color(250, 251, 255);      // Off-white
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    
    // Selected items
    private BookDTO selectedBook = null;
    private ChapterDTO selectedChapter = null;
    private SentenceDTO selectedSentence = null;
    
    // UI Components
    private JList<String> bookList;
    private DefaultListModel<String> bookListModel;
    
    private JList<String> authorList;
    private DefaultListModel<String> authorListModel;
    private AuthorDTO selectedAuthor = null;

    public Arabic_Prose_User_Interface(IBussinessLayerFasade BussinessLayerFasade) {
        this.BussinessLayerFasade = BussinessLayerFasade;

        // Must be undecorated BEFORE adding custom title bar
        setUndecorated(true);

        setTitle("Arabic Prose Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // Apply modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ========= Custom Title Bar (Close, Minimize, Maximize) =========
        JPanel titleBar = createCustomTitleBar(this);

        // ========= Animated Header =========
        JPanel header = createModernHeader();

        // Combine title bar + header into one top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(header, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // ========= Modern Tabbed Pane =========
        JTabbedPane tabbedPane = createModernTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // ========= Fade-in Animation =========
        setOpacity(0.0f);
        setVisible(true);
        fadeIn();
    }
    /**
     * 🧭 Create Custom Title Bar with Close, Minimize, and Maximize Buttons
     */
    private JPanel createCustomTitleBar(JFrame frame) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));
        titleBar.setBackground(new Color(30, 30, 30)); // Dark modern background

        // ======== APP TITLE ========
        JLabel titleLabel = new JLabel("📚 Arabic Prose Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // ======== CONTROL BUTTONS ========
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);

        JButton minimizeBtn = createTitleButton("—", Color.GRAY);
        JButton maximizeBtn = createTitleButton("▢", Color.GRAY);
        JButton closeBtn = createTitleButton("✕", Color.GRAY);

        // Hover effects
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeBtn.setBackground(Color.RED);
                closeBtn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeBtn.setBackground(new Color(30, 30, 30));
                closeBtn.setForeground(Color.GRAY);
            }
        });

        // Button actions
        minimizeBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
        maximizeBtn.addActionListener(e -> {
            if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
                frame.setExtendedState(JFrame.NORMAL);
            else
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
        closeBtn.addActionListener(e -> System.exit(0));

        controlPanel.add(minimizeBtn);
        controlPanel.add(maximizeBtn);
        controlPanel.add(closeBtn);
        titleBar.add(controlPanel, BorderLayout.EAST);

        // ======== ENABLE DRAGGING WINDOW ========
        final Point mouseDownCompCoords = new Point();
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords.setLocation(e.getPoint());
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });

        return titleBar;
    }

    /**
     * 🎭 Fade-in animation for window
     */
    private void fadeIn() {
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            float opacity = 0.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    timer.stop();
                }
                setOpacity(opacity);
            }
        });
        timer.start();
    }

    /**
     * 🎨 Create gradient header with animation
     */
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 🎨 Professional Blue Gradient (Dark → Light)
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(10, 30, 90),       // Deep Navy Blue
                        getWidth(), getHeight(), new Color(0, 90, 200) // Soft Sky Blue
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setOpaque(false); // ✅ Let gradient show through

        // ====== Title Section ======
        JLabel titleLabel = new JLabel("Arabic Prose Management System", new ImageIcon("bin/Openbook.png"), JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 25, 15, 0));

        // ====== Tagline or version ======
        JLabel subtitle = new JLabel("created by HackOps team", JLabel.RIGHT);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(new Color(220, 230, 250)); // Soft bluish-white
        subtitle.setBorder(new EmptyBorder(0, 0, 0, 25));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);

        // ====== Subtle bottom divider ======
        JPanel divider = new JPanel();
        divider.setBackground(new Color(0, 70, 150)); // Subtle darker blue line
        divider.setPreferredSize(new Dimension(getWidth(), 2));
        header.add(divider, BorderLayout.SOUTH);

        return header;
    }
    /**
     * 🎨 Helper function to create a flat title bar button
     */
    private JButton createTitleButton(String text, Color fgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(45, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(50, 50, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(30, 30, 30));
            }
        });

        return button;
    }
    /**
     * 🗂️ Create modern tabbed pane with animations
     */
    private JTabbedPane createModernTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public void setSelectedIndex(int index) {
                int oldIndex = getSelectedIndex();
                super.setSelectedIndex(index);
                
                // Slide animation between tabs
                if (oldIndex != index) {
                    Component comp = getComponentAt(index);
                    animateTabTransition(comp);
                }
            }
        };
        
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Custom tab UI with hover effects
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2d.setColor(PRIMARY);
                } else {
                    g2d.setColor(CARD_BG);
                }
                
                g2d.fillRoundRect(x, y, w, h - 2, 15, 15);
                
                if (isSelected) {
                    g2d.setColor(ACCENT);
                    g2d.fillRoundRect(x, y + h - 4, w, 4, 4, 4);
                }
            }
            
            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, 
                                    FontMetrics metrics, int tabIndex, String title, 
                                    Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                g.setColor(isSelected ? Color.WHITE : TEXT_PRIMARY);
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });
        
        tabbedPane.addTab("   📖 Books   ", createBooksPanel());
        tabbedPane.addTab("   ✍️ Authors   ", createAuthorsPanel());
        
        return tabbedPane;
    }

    /**
     * 🎬 Animate tab transition
     */
    private void animateTabTransition(Component comp) {
        comp.setVisible(false);
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            float alpha = 0.0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.1f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    timer.stop();
                }
                comp.setVisible(true);
                comp.repaint();
            }
        });
        timer.start();
    }

    // ==================== 📖 BOOKS PANEL ====================
 // ==================== 📖 BOOKS PANEL ====================
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // ===== Modern Card =====
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));

        // ===== Header =====
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 235, 255),
                        getWidth(), getHeight(), new Color(245, 250, 255));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel listTitle;
        try {
            ImageIcon rawIcon = new ImageIcon("bin/icons/book.jpeg");
            Image scaledImage = rawIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            listTitle = new JLabel("  Books Library", scaledIcon, JLabel.LEFT);
        } catch (Exception e) {
            listTitle = new JLabel("  Books Library");
        }

        listTitle.setFont(new Font("Segoe UI Black", Font.PLAIN, 24));
        listTitle.setForeground(new Color(25, 60, 120));

        JLabel hintLabel = new JLabel("💡 Double-click a book to open");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(90, 110, 150));

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(listTitle, BorderLayout.WEST);
        titleWrapper.add(hintLabel, BorderLayout.SOUTH);

        titlePanel.add(titleWrapper, BorderLayout.CENTER);
        listCard.add(titlePanel, BorderLayout.NORTH);

        // ===== Load Books =====
        bookListModel = new DefaultListModel<>();
        loadBooks();

        // Add bullet points (•) before each book name
        DefaultListModel<String> formattedModel = new DefaultListModel<>();
        for (int i = 0; i < bookListModel.size(); i++) {
            formattedModel.addElement("•  " + bookListModel.get(i));
        }
        bookListModel = formattedModel;

        // ===== Use your modern list style =====
        bookList = createModernList(bookListModel);

        // ===== Custom Renderer (Right-aligned + line separator) =====
        bookList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220))); // separator line
                label.setHorizontalAlignment(SwingConstants.RIGHT); // move to right side
                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(new Color(200, 220, 255));
                    label.setForeground(new Color(20, 40, 90));
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(new Color(30, 30, 30));
                }
                return label;
            }
        });

        // ===== Selection Logic =====
        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = bookList.getSelectedIndex();
                if (index >= 0) {
                    String entry = bookListModel.getElementAt(index);
                    // Remove the bullet and spaces
                    String bookTitle = entry.replaceFirst("•\\s+", "").trim();
                    selectedBook = BussinessLayerFasade.retrieveBook(bookTitle);
                }
            }
        });

        // ===== Mouse Actions =====
        bookList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedBook != null) {
                    openChaptersPanel();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int index = bookList.locationToIndex(e.getPoint());
                Rectangle cellBounds = (index != -1) ? bookList.getCellBounds(index, index) : null;

                // If clicked NOT on a visible row → deselect
                if (index == -1 || cellBounds == null || !cellBounds.contains(e.getPoint())) {
                    bookList.clearSelection();
                    selectedBook = null;
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        listCard.add(scrollPane, BorderLayout.CENTER);

        panel.add(listCard, BorderLayout.CENTER);

        // ===== Deselect on outside click =====
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(panel, e.getPoint(), bookList);
                if (!bookList.getVisibleRect().contains(p)) {
                    bookList.clearSelection();
                    selectedBook = null;
                }
            }
        });

        // ===== CRUD Buttons =====
        JPanel buttonPanel = createAnimatedCRUDPanel("Book");
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    /**
     * 📥 Load books from business layer
     */
    private void loadBooks() {
        bookListModel.clear();
        ArrayList<BookDTO> books = BussinessLayerFasade.getAllBooks();
        if (books != null) {
            for (BookDTO book : books) {
                bookListModel.addElement(book.getTitle());
            }
        }
    }

    // ==================== 📑 CHAPTERS PANEL ====================
    private void openChaptersPanel() {
        JDialog chapterDialog = createModernDialog("Chapters - " + selectedBook.getTitle(), 1000, 650);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
     // Chapters card
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));
        
        JLabel listTitle = null;
        try {
            ImageIcon bookIcon = new ImageIcon("bin/icons/book.jpeg"); // or book.png if renamed
            listTitle = new JLabel(" Books Library", bookIcon, JLabel.LEFT);
        } catch (Exception e) {
            System.out.println("❌ Could not load book icon: " + e.getMessage());
            listTitle = new JLabel(" Books Library");
        }

        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        listTitle.setForeground(PRIMARY);
        listTitle.setBorder(new EmptyBorder(10, 15, 10, 15));






        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        listTitle.setForeground(PRIMARY);
        listTitle.setBorder(new EmptyBorder(10, 15, 10, 15));
        listCard.add(listTitle, BorderLayout.NORTH);
        // Load chapters
        DefaultListModel<String> chapterListModel = new DefaultListModel<>();
        ArrayList<ChapterDTO> chapters = BussinessLayerFasade.retrieveChapters(selectedBook.getTitle());
        
        if (chapters != null && !chapters.isEmpty()) {
            for (ChapterDTO chapter : chapters) {
                chapterListModel.addElement(chapter.getChapterName());
            }
        } else {
            chapterListModel.addElement("No chapters available");
        }
        
        JList<String> chapterList = createModernList(chapterListModel);
        
        // Selection and double-click handlers
        chapterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = chapterList.getSelectedIndex();
                if (index >= 0 && chapters != null && index < chapters.size()) {
                    selectedChapter = chapters.get(index);
                }
            }
        });
        
        chapterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedChapter != null) {
                    openSentencesPanel(chapterDialog);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (chapterList.locationToIndex(e.getPoint()) == -1) {
                    chapterList.clearSelection();
                    selectedChapter = null;
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(chapterList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        listCard.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(listCard, BorderLayout.CENTER);
        
        // CRUD buttons
        JPanel buttonPanel = createAnimatedCRUDPanel("Chapter");
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        chapterDialog.add(contentPanel);
        chapterDialog.setVisible(true);
    }

    // ==================== 📝 SENTENCES PANEL ====================
    private void openSentencesPanel(JDialog parentDialog) {
        JDialog sentenceDialog = createModernDialog("Sentences - " + selectedChapter.getChapterName(), 1100, 650);
        
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Sentences card
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));
        
        JLabel listTitle = new JLabel("📝 Sentences");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        listTitle.setForeground(PRIMARY);
        listTitle.setBorder(new EmptyBorder(10, 15, 10, 15));
        listCard.add(listTitle, BorderLayout.NORTH);
        
        // Load sentences
        DefaultListModel<String> sentenceListModel = new DefaultListModel<>();
        ArrayList<SentenceDTO> sentences = BussinessLayerFasade.retrieveSentence(selectedChapter.getChapterName());
        
        ArrayList<SentenceDTO> chapterSentences = new ArrayList<>();
        if (sentences != null) {
            for (SentenceDTO s : sentences) {
                chapterSentences.add(s);
                sentenceListModel.addElement(s.getText());
            }
        }
        
        JList<String> sentenceList = createModernList(sentenceListModel);
        
        // Selection handler
        sentenceList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = sentenceList.getSelectedIndex();
                if (index >= 0 && index < chapterSentences.size()) {
                    selectedSentence = chapterSentences.get(index);
                }
            }
        });
        
        sentenceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (sentenceList.locationToIndex(e.getPoint()) == -1) {
                    sentenceList.clearSelection();
                    selectedSentence = null;
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(sentenceList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        listCard.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(listCard, BorderLayout.CENTER);
        
        // CRUD buttons
        JPanel buttonPanel = createAnimatedCRUDPanel("Sentence");
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        sentenceDialog.add(contentPanel);
        sentenceDialog.setVisible(true);
    }

    // ==================== ✍️ AUTHORS PANEL ====================
    private JPanel createAuthorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Authors list card
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));
        
        JLabel listTitle = new JLabel("✍️ Authors Directory");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        listTitle.setForeground(PRIMARY);
        listTitle.setBorder(new EmptyBorder(10, 15, 10, 15));
        listCard.add(listTitle, BorderLayout.NORTH);

        // Load authors
        authorListModel = new DefaultListModel<>();
        loadAuthors();
        
        authorList = createModernList(authorListModel);
        
        // Selection listener
        authorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = authorList.getSelectedIndex();
                if (index >= 0) {
                    String authorName = authorListModel.getElementAt(index);
                    selectedAuthor = BussinessLayerFasade.retrieveAuthor(authorName);
                }
            }
        });
        
        authorList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (authorList.locationToIndex(e.getPoint()) == -1) {
                    authorList.clearSelection();
                    selectedAuthor = null;
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(authorList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        listCard.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(listCard, BorderLayout.CENTER);
        
        // CRUD Buttons
        JPanel buttonPanel = createAnimatedCRUDPanel("Author");
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * 📥 Load authors from business layer
     */
    private void loadAuthors() {
        authorListModel.clear();
        ArrayList<AuthorDTO> authors = BussinessLayerFasade.getAllAuthors();
        if (authors != null) {
            for (AuthorDTO author : authors) {
                authorListModel.addElement(author.getName());
            }
        }
    }

    // ==================== 🎨 UI HELPER METHODS ====================
    
    /**
     * Create modern card panel with shadow effect
     */
    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
                
                // Card background
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(5, 5, 5, 5));
        return card;
    }

    /**
     * Create modern list with hover effects
     */
    private JList<String> createModernList(DefaultListModel<String> model) {
        JList<String> list = new JList<>(model);
        list.setBackground(new Color(240, 245, 250));
        list.setSelectionBackground(new Color(100, 149, 237, 60));
        list.setSelectionForeground(Color.BLACK);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        list.setBorder(null);

        // ✅ Custom cell renderer for numbering and separator lines
        list.setCellRenderer(new DefaultListCellRenderer() {
            private final Color separatorColor = new Color(210, 210, 210);

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Add numbering
                label.setText((index + 1) + ".  " + value.toString());
                label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
                label.setBorder(new EmptyBorder(8, 10, 8, 10));

                // Background & text color
                if (isSelected) {
                    label.setBackground(new Color(70, 130, 180)); // Steel Blue
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(new Color(40, 40, 40));
                }

                // Draw separator line
                label.setOpaque(true);
                return new JPanel(new BorderLayout()) {{
                    setBackground(label.getBackground());
                    add(label, BorderLayout.CENTER);
                    JPanel line = new JPanel();
                    line.setPreferredSize(new Dimension(1, 1));
                    line.setBackground(separatorColor);
                    add(line, BorderLayout.SOUTH);
                }};
            }
        });

        return list;
    }

    /**
     * Create modern dialog
     */
    private JDialog createModernDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND);
        dialog.setUndecorated(false);
        
        return dialog;
    }

    /**
     * 🎬 Create animated CRUD button panel
     */
    private JPanel createAnimatedCRUDPanel(String type) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        AnimatedButton addBtn = new AnimatedButton("➕ Add " + type, SUCCESS, SUCCESS_HOVER);
        AnimatedButton retrieveBtn = new AnimatedButton("🔍 Retrieve " + type, INFO, INFO_HOVER);
        AnimatedButton updateBtn = new AnimatedButton("✏️ Update " + type, WARNING, WARNING_HOVER);
        AnimatedButton deleteBtn = new AnimatedButton("🗑️ Delete " + type, DANGER, DANGER_HOVER);
        
        panel.add(addBtn);
        panel.add(retrieveBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        
        // Setup CRUD actions
        setupCRUDActions(type, addBtn, retrieveBtn, updateBtn, deleteBtn);
        
        return panel;
    }

    /**
     * 🎨 Animated Button with smooth hover and press effects
     */
    class AnimatedButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color currentColor;
        private Timer hoverTimer;
        private float scale = 1.0f;
        private boolean isHovered = false;
        
        public AnimatedButton(String text, Color normalColor, Color hoverColor) {
            super(text);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.currentColor = normalColor;
            
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(180, 50));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Hover animation
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    animateColor(hoverColor);
                    animateScale(1.05f);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    animateColor(normalColor);
                    animateScale(1.0f);
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    animateScale(0.95f);
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isHovered) {
                        animateScale(1.05f);
                    } else {
                        animateScale(1.0f);
                    }
                }
            });
        }
        
        private void animateColor(Color target) {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }
            
            hoverTimer = new Timer(20, new ActionListener() {
                float progress = 0f;
                Color startColor = currentColor;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    progress += 0.1f;
                    if (progress >= 1.0f) {
                        progress = 1.0f;
                        hoverTimer.stop();
                    }
                    
                    currentColor = interpolateColor(startColor, target, progress);
                    repaint();
                }
            });
            hoverTimer.start();
        }
        
        private void animateScale(float targetScale) {
            Timer scaleTimer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Math.abs(scale - targetScale) < 0.01f) {
                        scale = targetScale;
                        ((Timer) e.getSource()).stop();
                    } else {
                        scale += (targetScale - scale) * 0.3f;
                    }
                    repaint();
                }
            });
            scaleTimer.start();
        }
        
        private Color interpolateColor(Color c1, Color c2, float ratio) {
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
            return new Color(r, g, b);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Calculate scaled dimensions
            int scaledW = (int) (w * scale);
            int scaledH = (int) (h * scale);
            int x = (w - scaledW) / 2;
            int y = (h - scaledH) / 2;
            
            // Shadow
            if (isHovered) {
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(x + 2, y + 4, scaledW, scaledH, 25, 25);
            }
            
            // Button background
            g2d.setColor(currentColor);
            g2d.fillRoundRect(x, y, scaledW, scaledH, 25, 25);
            
            g2d.dispose();
            
            super.paintComponent(g);
        }
    }

    // ==================== 🔧 CRUD ACTION HANDLERS ====================
    
    private void setupCRUDActions(String type, JButton addBtn, JButton retrieveBtn, 
                                   JButton updateBtn, JButton deleteBtn) {
        String lowerType = type.toLowerCase();
        
        // ADD ACTION
        addBtn.addActionListener(e -> {
            switch (lowerType) {
                case "book": addBook(); break;
                case "author": addAuthor(); break;
                case "chapter": addChapter(); break;
                case "sentence": addSentence(); break;
            }
        });
        
        // RETRIEVE ACTION
        retrieveBtn.addActionListener(e -> {
            switch (lowerType) {
                case "book": retrieveBook(); break;
                case "author": retrieveAuthor(); break;
                case "chapter": retrieveChapter(); break;
                case "sentence": retrieveSentence(); break;
            }
        });
        
        // UPDATE ACTION
        updateBtn.addActionListener(e -> {
            switch (lowerType) {
                case "book": updateBook(); break;
                case "author": updateAuthor(); break;
                case "chapter": updateChapter(); break;
                case "sentence": updateSentence(); break;
            }
        });
        
        // DELETE ACTION
        deleteBtn.addActionListener(e -> {
            switch (lowerType) {
                case "book": deleteBook(); break;
                case "author": deleteAuthor(); break;
                case "chapter": deleteChapter(); break;
                case "sentence": deleteSentence(); break;
            }
        });
    }

    // ==================== 📖 BOOK OPERATIONS ====================
    
    private void addBook() {
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(3, 2, 15, 15));
        
        JTextField titleField = createStyledTextField();
        JTextField eraField = createStyledTextField();
        
        ArrayList<AuthorDTO> authors = BussinessLayerFasade.getAllAuthors();
        String[] authorNames = new String[authors.size()];
        for (int i = 0; i < authors.size(); i++) {
            authorNames[i] = authors.get(i).getName();
        }
        JComboBox<String> authorCombo = createStyledComboBox(authorNames);
        
        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Era:"));
        panel.add(eraField);
        panel.add(createLabel("Author:"));
        panel.add(authorCombo);
        
        int result = showModernDialog(panel, "Add Book");
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String era = eraField.getText().trim();
            String author = (String) authorCombo.getSelectedItem();
            
            if (!title.isEmpty() && !era.isEmpty() && author != null) {
                boolean success = BussinessLayerFasade.createBook(title, author, era);
                if (success) {
                    showSuccessMessage("Book added successfully!");
                    loadBooks();
                    selectedBook = null;
                } else {
                    showErrorMessage("Failed to add book!");
                }
            } else {
                showWarningMessage("Please fill all fields!");
            }
        }
    }

    private void retrieveBook() {
        if (selectedBook == null) {
            showWarningMessage("Please select a book first!");
            return;
        }
        
        String authorName = BussinessLayerFasade.getAuthorById(selectedBook.getAuthorId());
        String message = String.format(
            "<html><body style='padding:10px; font-family:Segoe UI;'>" +
            "<h2 style='color:#3F51B5; margin-bottom:15px;'>📖 Book Details</h2>" +
            "<table cellpadding='8' style='border-collapse:collapse;'>" +
            "<tr><td style='font-weight:bold; color:#555;'>Title:</td><td style='color:#333;'>%s</td></tr>" +
            "<tr><td style='font-weight:bold; color:#555;'>Author:</td><td style='color:#333;'>%s</td></tr>" +
            "<tr><td style='font-weight:bold; color:#555;'>Era:</td><td style='color:#333;'>%s</td></tr>" +
            "</table></body></html>",
            selectedBook.getTitle(), authorName, selectedBook.getEra()
        );
        
        showInfoMessage(message, "Book Details");
    }

    private void updateBook() {
        if (selectedBook == null) {
            showWarningMessage("Please select a book first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(3, 2, 15, 15));
        
        JTextField titleField = createStyledTextField();
        titleField.setText(selectedBook.getTitle());
        JTextField eraField = createStyledTextField();
        eraField.setText(selectedBook.getEra());
        
        ArrayList<AuthorDTO> authors = BussinessLayerFasade.getAllAuthors();
        String[] authorNames = new String[authors.size()];
        for (int i = 0; i < authors.size(); i++) {
            authorNames[i] = authors.get(i).getName();
        }
        JComboBox<String> authorCombo = createStyledComboBox(authorNames);
        
        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Era:"));
        panel.add(eraField);
        panel.add(createLabel("Author:"));
        panel.add(authorCombo);
        
        int result = showModernDialog(panel, "Update Book");
        
        if (result == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            String newEra = eraField.getText().trim();
            String newAuthor = (String) authorCombo.getSelectedItem();
            
            if (!newTitle.isEmpty() && !newEra.isEmpty() && newAuthor != null) {
                boolean success = BussinessLayerFasade.updateBook(
                    selectedBook.getTitle(), newTitle, newAuthor, newEra
                );
                if (success) {
                    showSuccessMessage("Book updated successfully!");
                    loadBooks();
                    selectedBook = null;
                } else {
                    showErrorMessage("Failed to update book!");
                }
            }
        }
    }

    private void deleteBook() {
        if (selectedBook == null) {
            showWarningMessage("Please select a book first!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete '" + selectedBook.getTitle() + "'?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = BussinessLayerFasade.deleteBook(selectedBook.getTitle());
            if (success) {
                showSuccessMessage("Book deleted successfully!");
                loadBooks();
                selectedBook = null;
            } else {
                showErrorMessage("Failed to delete book!");
            }
        }
    }

    // ==================== ✍️ AUTHOR OPERATIONS ====================
    
    private void addAuthor() {
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(2, 2, 15, 15));
        
        JTextField nameField = createStyledTextField();
        JTextArea bioArea = createStyledTextArea(4, 20);
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(createFocusBorder());
        
        panel.add(createLabel("Name:"));
        panel.add(nameField);
        panel.add(createLabel("Biography:"));
        panel.add(bioScroll);
        
        int result = showModernDialog(panel, "Add Author");
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String bio = bioArea.getText().trim();
            
            if (!name.isEmpty() && !bio.isEmpty()) {
                boolean success = BussinessLayerFasade.createAuthor(name, bio);
                if (success) {
                    showSuccessMessage("Author added successfully!");
                    loadAuthors();
                    selectedAuthor = null;
                } else {
                    showErrorMessage("Failed to add author!");
                }
            } else {
                showWarningMessage("Please fill all fields!");
            }
        }
    }

    private void retrieveAuthor() {
        if (selectedAuthor == null) {
            showWarningMessage("Please select an author first!");
            return;
        }
        
        String message = String.format(
            "<html><body style='padding:10px; font-family:Segoe UI;'>" +
            "<h2 style='color:#3F51B5; margin-bottom:15px;'>✍️ Author Details</h2>" +
            "<table cellpadding='8' style='border-collapse:collapse;'>" +
            "<tr><td style='font-weight:bold; color:#555;'>Name:</td><td style='color:#333;'>%s</td></tr>" +
            "<tr><td style='font-weight:bold; color:#555;'>Biography:</td><td style='color:#333;'>%s</td></tr>" +
            "</table></body></html>",
            selectedAuthor.getName(), selectedAuthor.getBiography()
        );
        
        showInfoMessage(message, "Author Details");
    }

    private void updateAuthor() {
        if (selectedAuthor == null) {
            showWarningMessage("Please select an author first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(2, 2, 15, 15));
        
        JTextField nameField = createStyledTextField();
        nameField.setText(selectedAuthor.getName());
        JTextArea bioArea = createStyledTextArea(4, 20);
        bioArea.setText(selectedAuthor.getBiography());
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setBorder(createFocusBorder());
        
        panel.add(createLabel("Name:"));
        panel.add(nameField);
        panel.add(createLabel("Biography:"));
        panel.add(bioScroll);
        
        int result = showModernDialog(panel, "Update Author");
        
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newBio = bioArea.getText().trim();
            
            if (!newName.isEmpty() && !newBio.isEmpty()) {
                boolean success = BussinessLayerFasade.updateAuthor(
                    selectedAuthor.getName(), newName, newBio
                );
                if (success) {
                    showSuccessMessage("Author updated successfully!");
                    loadAuthors();
                    selectedAuthor = null;
                } else {
                    showErrorMessage("Failed to update author!");
                }
            }
        }
    }

    private void deleteAuthor() {
        if (selectedAuthor == null) {
            showWarningMessage("Please select an author first!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete '" + selectedAuthor.getName() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = BussinessLayerFasade.deleteAuthor(selectedAuthor.getName());
            if (success) {
                showSuccessMessage("Author deleted successfully!");
                loadAuthors();
                selectedAuthor = null;
            } else {
                showErrorMessage("Failed to delete author!");
            }
        }
    }

    // ==================== 📑 CHAPTER OPERATIONS ====================
    
    private void addChapter() {
        if (selectedBook == null) {
            showWarningMessage("Please select a book first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(1, 2, 15, 15));
        
        JTextField chapterNameField = createStyledTextField();
        
        panel.add(createLabel("Chapter Name:"));
        panel.add(chapterNameField);
        
        int result = showModernDialog(panel, "Add Chapter");
        
        if (result == JOptionPane.OK_OPTION) {
            String chapterName = chapterNameField.getText().trim();
            
            if (!chapterName.isEmpty()) {
                boolean success = BussinessLayerFasade.createChapter(
                    selectedBook.getTitle(), chapterName
                );
                if (success) {
                    showSuccessMessage("Chapter added successfully!");
                } else {
                    showErrorMessage("Failed to add chapter!");
                }
            } else {
                showWarningMessage("Please enter chapter name!");
            }
        }
    }

    private void retrieveChapter() {
        if (selectedChapter == null) {
            showWarningMessage("Please select a chapter first!");
            return;
        }
        
        String message = String.format(
            "<html><body style='padding:10px; font-family:Segoe UI;'>" +
            "<h2 style='color:#3F51B5; margin-bottom:15px;'>📑 Chapter Details</h2>" +
            "<table cellpadding='8' style='border-collapse:collapse;'>" +
            "<tr><td style='font-weight:bold; color:#555;'>Book:</td><td style='color:#333;'>%s</td></tr>" +
            "<tr><td style='font-weight:bold; color:#555;'>Chapter:</td><td style='color:#333;'>%s</td></tr>" +
            "</table></body></html>",
            selectedBook.getTitle(), selectedChapter.getChapterName()
        );
        
        showInfoMessage(message, "Chapter Details");
    }

    private void updateChapter() {
        if (selectedChapter == null) {
            showWarningMessage("Please select a chapter first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(1, 2, 15, 15));
        
        JTextField chapterNameField = createStyledTextField();
        chapterNameField.setText(selectedChapter.getChapterName());
        
        panel.add(createLabel("New Chapter Name:"));
        panel.add(chapterNameField);
        
        int result = showModernDialog(panel, "Update Chapter");
        
        if (result == JOptionPane.OK_OPTION) {
            String newName = chapterNameField.getText().trim();
            
            if (!newName.isEmpty()) {
                boolean success = BussinessLayerFasade.updateChapter(
                    selectedBook.getTitle(), 
                    selectedChapter.getChapterName(), 
                    newName
                );
                if (success) {
                    showSuccessMessage("Chapter updated successfully!");
                } else {
                    showErrorMessage("Failed to update chapter!");
                }
            }
        }
    }

    private void deleteChapter() {
        if (selectedChapter == null) {
            showWarningMessage("Please select a chapter first!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete chapter '" + selectedChapter.getChapterName() + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = BussinessLayerFasade.deleteChapter(
                selectedBook.getTitle(), 
                selectedChapter.getChapterName()
            );
            if (success) {
                showSuccessMessage("Chapter deleted successfully!");
                selectedChapter = null;
            } else {
                showErrorMessage("Failed to delete chapter!");
            }
        }
    }

    // ==================== 📝 SENTENCE OPERATIONS ====================
    
    private void addSentence() {
        if (selectedChapter == null) {
            showWarningMessage("Please select a book first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(4, 2, 15, 15));
        
        JTextArea textArea = createStyledTextArea(3, 20);
        JTextArea diaArea = createStyledTextArea(3, 20);
        JTextArea transArea = createStyledTextArea(3, 20);
        JTextArea notesArea = createStyledTextArea(3, 20);
        
        panel.add(createLabel("Text:"));
        panel.add(new JScrollPane(textArea));
        panel.add(createLabel("Diacritized:"));
        panel.add(new JScrollPane(diaArea));
        panel.add(createLabel("Translation:"));
        panel.add(new JScrollPane(transArea));
        panel.add(createLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));
        
        int result = showModernDialog(panel, "Add Sentence");
        
        if (result == JOptionPane.OK_OPTION) {
            String text = textArea.getText().trim();
            String dia = diaArea.getText().trim();
            String trans = transArea.getText().trim();
            String notes = notesArea.getText().trim();
            
            if (!text.isEmpty()) {
                boolean success = BussinessLayerFasade.createSentence(
                    selectedChapter.getChapterName(),
                    text,
                    dia.isEmpty() ? null : dia,
                    trans.isEmpty() ? null : trans,
                    notes.isEmpty() ? null : notes
                );
                if (success) {
                    showSuccessMessage("Sentence added successfully!");
                } else {
                    showErrorMessage("Failed to add sentence!");
                }
            } else {
                showWarningMessage("Text field cannot be empty!");
            }
        }
    }

    private void retrieveSentence() {
        if (selectedSentence == null) {
            showWarningMessage("Please select a sentence first!");
            return;
        }
        
	        String message = String.format(
	            "<html><body style='padding:10px; font-family:Segoe UI;'>" +
	            "<h2 style='color:#3F51B5; margin-bottom:15px;'>📝 Sentence Details</h2>" +
	            "<table cellpadding='8' style='border-collapse:collapse;'>" +
	            "<tr><td style='font-weight:bold; color:#555;'>Number:</td><td style='color:#333;'>%d</td></tr>" +
	            "<tr><td style='font-weight:bold; color:#555;'>Text:</td><td style='color:#333;'>%s</td></tr>" +
	            "<tr><td style='font-weight:bold; color:#555;'>Diacritized:</td><td style='color:#333;'>%s</td></tr>" +
	            "<tr><td style='font-weight:bold; color:#555;'>Translation:</td><td style='color:#333;'>%s</td></tr>" +
	            "<tr><td style='font-weight:bold; color:#555;'>Notes:</td><td style='color:#333;'>%s</td></tr>" +
	            "</table></body></html>",
	            selectedSentence.getSentenceNumber(),
	            selectedSentence.getText(),
	            selectedSentence.getTextDiacritized(),
	            selectedSentence.getTranslation(),
	            selectedSentence.getNotes()
	        );
        
        showInfoMessage(message, "Sentence Details");
    }

    private void updateSentence() {
        if (selectedSentence == null || selectedChapter == null) {
            showWarningMessage("Please select a sentence first!");
            return;
        }
        
        JPanel panel = createFormPanel();
        panel.setLayout(new GridLayout(4, 2, 15, 15));
        
        JTextArea textArea = createStyledTextArea(3, 20);
        textArea.setText(selectedSentence.getText());
        JTextArea diaArea = createStyledTextArea(3, 20);
        diaArea.setText(selectedSentence.getTextDiacritized());
        JTextArea transArea = createStyledTextArea(3, 20);
        transArea.setText(selectedSentence.getTranslation());
        JTextArea notesArea = createStyledTextArea(3, 20);
        notesArea.setText(selectedSentence.getNotes());
        
        panel.add(createLabel("Text:"));
        panel.add(new JScrollPane(textArea));
        panel.add(createLabel("Diacritized:"));
        panel.add(new JScrollPane(diaArea));
        panel.add(createLabel("Translation:"));
        panel.add(new JScrollPane(transArea));
        panel.add(createLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));
        
        int result = showModernDialog(panel, "Update Sentence");
        
        if (result == JOptionPane.OK_OPTION) {
            String text = textArea.getText().trim();
            String dia = diaArea.getText().trim();
            String trans = transArea.getText().trim();
            String notes = notesArea.getText().trim();
            
            boolean success = BussinessLayerFasade.updateSenetence(
            		selectedChapter.getChapterName(),
            		selectedSentence.getSentenceNumber(),
                text,
                dia,
                trans,
                notes
            );
            if (success) {
                showSuccessMessage("Sentence updated successfully!");
            } else {
                showErrorMessage("Failed to update sentence!");
            }
        }
    }

    private void deleteSentence() {
        if (selectedSentence == null || selectedChapter == null) {
            showWarningMessage("Please select a sentence first!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete sentence #" + selectedSentence.getSentenceNumber() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = BussinessLayerFasade.deleteSentence(
            		selectedChapter.getChapterName(),
                selectedSentence.getSentenceNumber()
            );
            if (success) {
                showSuccessMessage("Sentence deleted successfully!");
                selectedSentence = null;
            } else {
                showErrorMessage("Failed to delete sentence!");
            }
        }
    }

    // ==================== 🎨 STYLED COMPONENTS ====================
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(createFocusBorder());
        field.setPreferredSize(new Dimension(250, 35));
        
        // Focus glow effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2, true),
                    new EmptyBorder(5, 10, 5, 10)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(createFocusBorder());
            }
        });
        
        return field;
    }

    private JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        return area;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(CARD_BG);
        combo.setPreferredSize(new Dimension(250, 35));
        return combo;
    }

    private Border createFocusBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        );
    }

    // ==================== 💬 MESSAGE DIALOGS ====================
    
    private int showModernDialog(JPanel panel, String title) {
        return JOptionPane.showConfirmDialog(
            this,
            panel,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }

    private void showInfoMessage(String message, String title) {
        JOptionPane.showMessageDialog(
            this,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}