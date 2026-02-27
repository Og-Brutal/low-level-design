package com.apm.pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.SentenceDTO;
import com.mysql.cj.x.protobuf.MysqlxNotice.Frame;

/**
 * Modern Arabic Prose Management System with Professional UI
 * Features: Smooth animations, hover effects, gradient backgrounds, rounded corners
 */
public class Arabic_Prose_User_Interface extends JFrame {

    private IBussinessLayerFasade BussinessLayerFasade;
    
    
 // Add this to your color constants section
    private static final Color ONE_TAP_PURPLE = new Color(106, 27, 154);
    private static final Color ONE_TAP_PURPLE_HOVER = new Color(142, 36, 170);
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
        JLabel titleLabel = new JLabel("APM");

        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        //=========Add Favicon ==========
        try {
            ImageIcon icon = new ImageIcon("bin/Openbook.png");
            Image img = icon.getImage();
            setIconImage(img);  // Sets window/taskbar icon

            // Optionally display in title bar
            Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImg));

            // Combine icon + title in a panel
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            titlePanel.setOpaque(false);
            titlePanel.add(iconLabel);
            titlePanel.add(titleLabel);

            titleBar.add(titlePanel, BorderLayout.WEST);
        } catch (Exception e) {
            System.out.println("❌ Could not load favicon: " + e.getMessage());
            titleBar.add(titleLabel, BorderLayout.WEST);
        }

        
        // ======== CONTROL BUTTONS ========
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);

        JButton minimizeBtn = createTitleButton("–", new Color(180, 180, 180));  // Sleek en-dash
        JButton maximizeBtn = createTitleButton("□", new Color(180, 180, 180)); // Balanced square
        JButton closeBtn = createTitleButton("×", new Color(180, 180, 180));

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
   private JPanel createBooksPanel() {
    JPanel panel = new JPanel(new BorderLayout(20, 20));
    panel.setBackground(BACKGROUND);
    panel.setBorder(new EmptyBorder(25, 25, 25, 25));

    // ===== ONE-TAP ADD BUTTON =====
    JPanel oneTapPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
    oneTapPanel.setBackground(BACKGROUND);

    AnimatedButton oneTapBtn = new AnimatedButton("🚀 One-Tap Add Book", 
        new Color(106, 27, 154), new Color(142, 36, 170)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Special gradient for one-tap button
            GradientPaint gp = new GradientPaint(0, 0, new Color(106, 27, 154), 
                                                getWidth(), getHeight(), new Color(142, 36, 170));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Glow effect
            if (isHovered) {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 23, 23);
            }
            
            super.paintComponent(g);
        }
    };

    oneTapBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    oneTapBtn.setPreferredSize(new Dimension(250, 55));
    oneTapBtn.addActionListener(e -> oneTapAddBook());

    oneTapPanel.add(oneTapBtn);
    panel.add(oneTapPanel, BorderLayout.NORTH);

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

    // ===== Modern List =====
    bookList = createModernList(bookListModel);

    // ===== Custom Renderer with Light Row + Gap =====
    bookList.setCellRenderer(new DefaultListCellRenderer() {
        private final Color lightRow = new Color(245, 248, 255); // soft light blue
        private final Color hoverColor = new Color(230, 240, 255);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setHorizontalAlignment(SwingConstants.RIGHT); // align right
            label.setOpaque(true);

            // Background logic
            if (isSelected) {
                label.setBackground(new Color(200, 220, 255)); // selected row
                label.setForeground(new Color(20, 40, 90));
            } else {
                label.setBackground(lightRow); // every row light color
                label.setForeground(new Color(30, 30, 30));
            }

            // Add soft gap between rows
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(4, 8, 4, 8), // inner padding
                    BorderFactory.createMatteBorder(0, 0, 6, 0, CARD_BG) // gap separator
            ));

            return label;
        }
    });

    // ===== Selection Logic =====
    bookList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int index = bookList.getSelectedIndex();
            if (index >= 0) {
                String entry = bookListModel.getElementAt(index);
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

            // Deselect if not on a visible book row
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

    // ===== Deselect when clicking outside the list =====
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

    panel.add(listCard, BorderLayout.CENTER);

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
  // ==================== 📑 CHAPTERS PANEL ====================
private void openChaptersPanel() {
    JDialog chapterDialog = createModernDialog("Chapters - " + selectedBook.getTitle(), 1000, 700); // Increased height

    JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
    contentPanel.setBackground(BACKGROUND);
    contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

    // ===== ONE-TAP ADD CHAPTER BUTTON =====
    JPanel oneTapChapterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
    oneTapChapterPanel.setBackground(BACKGROUND);

    AnimatedButton oneTapChapterBtn = new AnimatedButton("📁 One-Tap Add Chapter",new Color(106, 27, 154), new Color(142, 36, 170)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Special gradient for one-tap button
            GradientPaint gp = new GradientPaint(0, 0, new Color(106, 27, 154), 
                                                getWidth(), getHeight(), new Color(142, 36, 170));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Glow effect
            if (isHovered) {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 23, 23);
            }
            
            super.paintComponent(g);
        }
    };

    oneTapChapterBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    oneTapChapterBtn.setPreferredSize(new Dimension(280, 55));
    oneTapChapterBtn.addActionListener(e -> oneTapAddChapter(selectedBook.getTitle()));

    oneTapChapterPanel.add(oneTapChapterBtn);
    contentPanel.add(oneTapChapterPanel, BorderLayout.NORTH);

    // ===== Chapters Card =====
    JPanel listCard = createModernCard();
    listCard.setLayout(new BorderLayout(10, 10));

    // ===== Header Panel with Book Icon + Book Name on Right =====
    JPanel titlePanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(220, 235, 255),
                    getWidth(), getHeight(), new Color(245, 250, 255)
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
        }
    };
    titlePanel.setOpaque(false);
    titlePanel.setBorder(new EmptyBorder(12, 15, 12, 15));

    JLabel bookLabel;
    try {
        ImageIcon bookIcon = new ImageIcon("bin/icons/book.jpeg");
        Image scaledIcon = bookIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        bookIcon = new ImageIcon(scaledIcon);

        bookLabel = new JLabel(bookIcon);
    } catch (Exception e) {
        System.out.println("❌ Could not load book icon: " + e.getMessage());
        bookLabel = new JLabel();
    }

    JLabel titleLabel = new JLabel(selectedBook.getTitle());
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(PRIMARY);

    // Combine icon and text in a panel, aligned to right
    JPanel titleContent = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    titleContent.setOpaque(false);
    titleContent.add(titleLabel);
    titleContent.add(bookLabel);

    titlePanel.add(titleContent, BorderLayout.CENTER);
    listCard.add(titlePanel, BorderLayout.NORTH);

    // ===== Load Chapters =====
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

    // Selection logic
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
            if (e.getClickCount() == 2) { // double-click
                int index = chapterList.locationToIndex(e.getPoint());
                if (index >= 0 && chapters != null && index < chapters.size()) {
                    selectedChapter = chapters.get(index); // update selectedChapter
                    openSentencesPanel(chapterDialog); // now open sentences panel
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int index = chapterList.locationToIndex(e.getPoint());
            Rectangle cellBounds = (index != -1) ? chapterList.getCellBounds(index, index) : null;

            // Deselect if clicked outside a visible row
            if (index == -1 || cellBounds == null || !cellBounds.contains(e.getPoint())) {
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



/**
 * 📁 One-Tap Add Chapter with File Selection for Sentence Extraction
 */
private void oneTapAddChapter(String bookName) {
    JDialog oneTapDialog = createModernDialog("One-Tap Add Chapter - " + bookName, 600, 500);
    oneTapDialog.setLayout(new BorderLayout(20, 20));
    
    // ===== MODERN CARD CONTAINER =====
    JPanel cardPanel = new JPanel(new BorderLayout(20, 20)) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Shadow effect
            g2d.setColor(new Color(0, 0, 0, 25));
            g2d.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 30, 30);
            
            // Card background with gradient
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255), 
                                                getWidth(), getHeight(), new Color(245, 248, 255));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);
            
            // Border
            g2d.setColor(new Color(220, 230, 255));
            g2d.drawRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);
        }
    };
    cardPanel.setOpaque(false);
    cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
    
    // ===== HEADER WITH ICON =====
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    
    JLabel titleLabel;
    try {
        ImageIcon chapterIcon = new ImageIcon("bin/icons/chapter.png");
        Image scaledChapter = chapterIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        titleLabel = new JLabel("  One-Tap Chapter Import", new ImageIcon(scaledChapter), JLabel.LEFT);
    } catch (Exception e) {
        titleLabel = new JLabel("  📁 One-Tap Chapter Import");
    }
    
    titleLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
    titleLabel.setForeground(PRIMARY_DARK);
    headerPanel.add(titleLabel, BorderLayout.WEST);
    
    JLabel subtitle = new JLabel("Upload a text file to automatically extract sentences for: " + bookName);
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitle.setForeground(TEXT_SECONDARY);
    headerPanel.add(subtitle, BorderLayout.SOUTH);
    
    cardPanel.add(headerPanel, BorderLayout.NORTH);
    
    // ===== FILE SELECTION AREA =====
    JPanel filePanel = createChapterFileSelectionArea(oneTapDialog, bookName);
    cardPanel.add(filePanel, BorderLayout.CENTER);
    
    // ===== PROGRESS INDICATOR =====
    JPanel progressPanel = createChapterProgressIndicator();
    cardPanel.add(progressPanel, BorderLayout.SOUTH);
    
    oneTapDialog.add(cardPanel, BorderLayout.CENTER);
    oneTapDialog.setVisible(true);
}

/**
 * 📁 Create animated file selection area for chapters
 */
private JPanel createChapterFileSelectionArea(JDialog parentDialog, String bookName) {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setOpaque(false);
    
    // File selection card
    JPanel fileCard = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background with subtle gradient
            GradientPaint gp = new GradientPaint(0, 0, new Color(250, 252, 255), 
                                                getWidth(), getHeight(), new Color(240, 245, 255));
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Dashed border
            g2d.setColor(new Color(200, 210, 255));
            g2d.setStroke(new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, 
                                                  java.awt.BasicStroke.JOIN_ROUND, 0, new float[]{8}, 0));
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);
        }
    };
    fileCard.setOpaque(false);
    fileCard.setPreferredSize(new Dimension(400, 180));
    fileCard.setBorder(new EmptyBorder(30, 30, 30, 30));
    
    // File icon and text
    JLabel fileIcon;
    try {
        ImageIcon fileImg = new ImageIcon("bin/icons/file.png");
        Image scaledFile = fileImg.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        fileIcon = new JLabel(new ImageIcon(scaledFile));
    } catch (Exception e) {
        fileIcon = new JLabel("📄");
        fileIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
    }
    
    fileIcon.setHorizontalAlignment(SwingConstants.CENTER);
    
    JLabel dragText = new JLabel("Click to select or drag a text file here");
    dragText.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
    dragText.setForeground(TEXT_PRIMARY);
    dragText.setHorizontalAlignment(SwingConstants.CENTER);
    
    JLabel formatText = new JLabel("Supports: .txt files | Book: " + bookName);
    formatText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    formatText.setForeground(TEXT_SECONDARY);
    formatText.setHorizontalAlignment(SwingConstants.CENTER);
    
    JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    textPanel.setOpaque(false);
    textPanel.add(dragText);
    textPanel.add(formatText);
    
    JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
    contentPanel.setOpaque(false);
    contentPanel.add(fileIcon, BorderLayout.CENTER);
    contentPanel.add(textPanel, BorderLayout.SOUTH);
    
    fileCard.add(contentPanel, BorderLayout.CENTER);
    
    // File selection logic with animation
    fileCard.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            animateClick(fileCard);
            openChapterFileChooser(parentDialog, bookName);
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            animateHover(fileCard, 1.05f);
            fileCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            animateHover(fileCard, 1.0f);
            fileCard.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    });
    
    // Enable drag and drop
    fileCard.setTransferHandler(new TransferHandler() {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            try {
                java.util.List<?> files = (java.util.List<?>) support.getTransferable()
                    .getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                if (files.size() > 0) {
                    java.io.File file = (java.io.File) files.get(0);
                    if (file.getName().toLowerCase().endsWith(".txt")) {
                        processSelectedChapterFile(file, bookName, parentDialog);
                    } else {
                        showErrorMessage("Please select a .txt file");
                    }
                }
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    });
    
    panel.add(fileCard, BorderLayout.CENTER);
    return panel;
}

/**
 * 📊 Create progress indicator for chapter processing
 */
private JPanel createChapterProgressIndicator() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setVisible(false); // Initially hidden
    
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setPreferredSize(new Dimension(300, 8));
    progressBar.setForeground(SUCCESS);
    progressBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    
    JLabel progressLabel = new JLabel("Extracting sentences from file...");
    progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    progressLabel.setForeground(TEXT_SECONDARY);
    progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    panel.add(progressBar, BorderLayout.CENTER);
    panel.add(progressLabel, BorderLayout.SOUTH);
    
    return panel;
}

/**
 * 📂 Open file chooser dialog for chapter
 */
private void openChapterFileChooser(JDialog parentDialog, String bookName) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Text File for Chapter - " + bookName);
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
    
    // Customize file chooser appearance
    fileChooser.setPreferredSize(new Dimension(600, 400));
    
    int result = fileChooser.showOpenDialog(parentDialog);
    if (result == JFileChooser.APPROVE_OPTION) {
        java.io.File selectedFile = fileChooser.getSelectedFile();
        processSelectedChapterFile(selectedFile, bookName, parentDialog);
    }
}

/**
 * 🔄 Process selected chapter file and call business layer method
 */
private void processSelectedChapterFile(java.io.File file, String bookName, JDialog parentDialog) {
    // Show progress
    JPanel progressPanel = (JPanel) ((BorderLayout) ((JPanel) parentDialog.getContentPane().getComponent(0)).getLayout()).getLayoutComponent(BorderLayout.SOUTH);
    progressPanel.setVisible(true);
    parentDialog.revalidate();
    parentDialog.repaint();
    
    // Process the file using your business layer method
    Timer processTimer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get the absolute path of the selected file
                String filePath = file.getAbsolutePath();
                
                // Call your business layer method
                boolean success = BussinessLayerFasade.sentenceExtracter(bookName, filePath);
                
                progressPanel.setVisible(false);
                
                if (success) {
                    showSuccessMessage("Chapter created successfully!\n" +
                                     "Sentences extracted from: " + file.getName() + "\n" +
                                     "Added to book: " + bookName);
                    
                    parentDialog.dispose();
                    
                    // Refresh the chapters list if needed
                    // You might want to reload the chapters panel here
                    
                } else {
                    showErrorMessage("Failed to extract sentences from file.\n" +
                                   "Please check the file format and try again.");
                }
            } catch (Exception ex) {
                progressPanel.setVisible(false);
                showErrorMessage("Error processing file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    });
    processTimer.setRepeats(false);
    processTimer.start();
}

    //** ==================Sentence Panel=============

  private void openSentencesPanel(JDialog parentDialog) {
    JDialog sentenceDialog = createModernDialog("Sentences - " + selectedChapter.getChapterName(), 1100, 650);

    JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
    contentPanel.setBackground(BACKGROUND);
    contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

    // ===== Sentences Card =====
    JPanel listCard = createModernCard();
    listCard.setLayout(new BorderLayout(10, 10));

 // ===== Header with Chapter Icon + Title (Modern Style) =====
    JPanel titlePanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(225, 240, 255),
                getWidth(), getHeight(), new Color(245, 250, 255)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
        }
    };
    titlePanel.setOpaque(false);
    titlePanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // cleaner padding

    // Load chapter icon safely
    JLabel chapterIconLabel;
    try {
        ImageIcon icon = new ImageIcon("bin/icons/chapter.png");
        Image scaled = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        chapterIconLabel = new JLabel(new ImageIcon(scaled));
    } catch (Exception e) {
        System.err.println("⚠️ Chapter icon not found: " + e.getMessage());
        chapterIconLabel = new JLabel();
    }

    // Chapter title label
    JLabel titleLabel = new JLabel(selectedChapter.getChapterName());
    titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));
    titleLabel.setForeground(new Color(25, 60, 120));

    // Combine icon + title neatly aligned left
    JPanel titleContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
    titleContent.setOpaque(false);
    titleContent.add(chapterIconLabel);
    titleContent.add(titleLabel);

    titlePanel.add(titleContent, BorderLayout.WEST);
    listCard.add(titlePanel, BorderLayout.NORTH);


    // ===== Load sentences =====
    DefaultListModel<String> sentenceListModel = new DefaultListModel<>();
    ArrayList<SentenceDTO> sentences = BussinessLayerFasade.retrieveSentence(selectedChapter.getChapterName());

    ArrayList<SentenceDTO> chapterSentences = new ArrayList<>();
    if (sentences != null) {
        for (SentenceDTO s : sentences) {
            chapterSentences.add(s);
            sentenceListModel.addElement(s.getText());
        }
    }

 // ===== Modern List with Right Alignment =====
    JList<String> sentenceList = new JList<>(sentenceListModel);
    sentenceList.setBackground(new Color(240, 245, 250));
    sentenceList.setSelectionBackground(new Color(100, 149, 237, 60));
    sentenceList.setSelectionForeground(Color.BLACK);
    sentenceList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    sentenceList.setBorder(null);
    sentenceList.setFixedCellHeight(-1); // auto-adjust height

    // ===== Modern Renderer (Book List Style) =====
    sentenceList.setCellRenderer(new DefaultListCellRenderer() {
        private final Color lightRow = new Color(245, 248, 255); // soft row color
        private final Color hoverColor = new Color(230, 240, 255); // hover shade
        private final Color separatorColor = new Color(240, 245, 250); // matches CARD_BG

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            label.setOpaque(true);

            // Add numbering with right-to-left formatting
            String sentenceText = value.toString();
            label.setText("<html><body dir='rtl' style='text-align: right; font-family: Segoe UI;'>" +
                    (index + 1) + ". " + sentenceText + "</body></html>");

            // Background logic
            if (isSelected) {
                label.setBackground(new Color(200, 220, 255)); // selected row
                label.setForeground(new Color(20, 40, 90));
            } else {
                label.setBackground(lightRow); // default light row
                label.setForeground(new Color(30, 30, 30));
            }

            // Add soft padding + gap between rows
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 12, 6, 12), // inner padding
                    BorderFactory.createMatteBorder(0, 0, 6, 0, Color.WHITE) // white gap between rows
            ));

            return label;
        }
    });

   

    // Apply right-to-left orientation to the entire list
    sentenceList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    // Selection handler
    sentenceList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int index = sentenceList.getSelectedIndex();
            if (index >= 0 && index < chapterSentences.size()) {
                selectedSentence = chapterSentences.get(index);
            }
        }
    });

    // Deselect when clicking empty space
    sentenceList.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            int index = sentenceList.locationToIndex(e.getPoint());
            Rectangle cellBounds = (index != -1) ? sentenceList.getCellBounds(index, index) : null;

            if (index == -1 || cellBounds == null || !cellBounds.contains(e.getPoint())) {
                sentenceList.clearSelection();
                selectedSentence = null;
            }
        }
    });

    // Create scroll pane with right-aligned content
    JScrollPane scrollPane = new JScrollPane(sentenceList);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(CARD_BG);
    
    // Ensure the viewport also respects right-to-left orientation
    scrollPane.getViewport().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    
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

        // ===== Modern Card for Author List =====
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));

        // ===== Header Panel with Icon + Title =====
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(220, 235, 255),
                        getWidth(), getHeight(), new Color(245, 250, 255)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(8, 15, 8, 15));

        JLabel listTitle;
        try {
            ImageIcon rawIcon = new ImageIcon("bin/icons/author.png");
            Image scaledImage = rawIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            listTitle = new JLabel("  Authors Directory", scaledIcon, JLabel.LEFT);
        } catch (Exception e) {
            listTitle = new JLabel("  Authors Directory");
        }

        listTitle.setFont(new Font("Segoe UI Black", Font.PLAIN, 24));
        listTitle.setForeground(new Color(25, 60, 120));


        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(listTitle, BorderLayout.WEST);

        titlePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 230, 255), 1, true),
                        new EmptyBorder(10, 15, 10, 15)
                )
        ));

        titlePanel.add(titleWrapper, BorderLayout.CENTER);

        // Hover effect
        titlePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                titlePanel.setBackground(new Color(0x1976D2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                titlePanel.setCursor(Cursor.getDefaultCursor());
                titlePanel.setBackground(new Color(0x2196F3));
            }
        });

        listCard.add(titlePanel, BorderLayout.NORTH);

        // ===== Load Authors =====
        authorListModel = new DefaultListModel<>();
        loadAuthors();

        // Add bullet points for authors
        DefaultListModel<String> formattedModel = new DefaultListModel<>();
        for (int i = 0; i < authorListModel.size(); i++) {
            formattedModel.addElement("•  " + authorListModel.get(i));
        }
        authorListModel = formattedModel;

        authorList = createModernList(authorListModel);

        // ===== Custom Renderer for Light Rows + Row Gaps =====
        authorList.setCellRenderer(new DefaultListCellRenderer() {
            private final Color lightRow = new Color(245, 248, 255); // soft light blue

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(new Color(200, 220, 255));
                    label.setForeground(new Color(20, 40, 90));
                } else {
                    label.setBackground(lightRow);
                    label.setForeground(new Color(30, 30, 30));
                }

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8),
                        BorderFactory.createMatteBorder(0, 0, 6, 0, CARD_BG) // row gap
                ));

                return label;
            }
        });

        // ===== Selection Logic =====
        authorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = authorList.getSelectedIndex();
                if (index >= 0) {
                    String entry = authorListModel.getElementAt(index);
                    String authorName = entry.replaceFirst("•\\s+", "").trim();
                    selectedAuthor = BussinessLayerFasade.retrieveAuthor(authorName);
                }
            }
        });

        // ===== Mouse Actions =====
        authorList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = authorList.locationToIndex(e.getPoint());
                Rectangle cellBounds = (index != -1) ? authorList.getCellBounds(index, index) : null;

                // Deselect if clicked outside a visible row
                if (index == -1 || cellBounds == null || !cellBounds.contains(e.getPoint())) {
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

        // ===== Deselect on clicking panel outside list =====
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(panel, e.getPoint(), authorList);
                if (!authorList.getVisibleRect().contains(p)) {
                    authorList.clearSelection();
                    selectedAuthor = null;
                }
            }
        });

        // ===== CRUD Buttons =====
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
        list.setBackground(new Color(245, 248, 255)); // soft light blue
        list.setSelectionBackground(new Color(230, 240, 255)); // hover/selection tint
        list.setSelectionForeground(new Color(20, 40, 90));
        list.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        list.setBorder(null);
        list.setFixedCellHeight(40);

        // ===== Custom Renderer with Light Row + Gap (exact style from your snippet) =====
        list.setCellRenderer(new DefaultListCellRenderer() {
            private final Color lightRow = new Color(245, 248, 255); // soft light blue
            private final Color hoverColor = new Color(230, 240, 255);
            // CARD_BG is used for the gap separator color (assumes CARD_BG already defined in your class)
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                label.setHorizontalAlignment(SwingConstants.RIGHT); // align right
                label.setOpaque(true);

                // Background logic
                if (isSelected) {
                    label.setBackground(new Color(200, 220, 255)); // selected row
                    label.setForeground(new Color(20, 40, 90));
                } else {
                    label.setBackground(lightRow); // every row light color
                    label.setForeground(new Color(30, 30, 30));
                }

                // Add soft gap between rows using the exact CompoundBorder you pasted:
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(4, 8, 4, 8), // inner padding
                        BorderFactory.createMatteBorder(0, 0, 6, 0, CARD_BG) // gap separator
                ));

                return label;
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
        protected boolean isHovered = false;
        
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

        // Create styled modal dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("📝 Sentence Details");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 360);
        dialog.setLocationRelativeTo(null);

        // HTML content (centered)
        String htmlContent = String.format(
            "<html><body style='font-family:Segoe UI; color:#333; text-align:center; padding:20px;'>"
            + "<h2 style='color:#3F51B5; margin-top:0;'>Book Information</h2>"
            + "<div style='display:inline-block; text-align:left; margin-top:15px;'>"
            + "<table cellpadding='8' style='font-size:15px;'>"
            + "<tr><td style='font-weight:bold; color:#555;'>Title:</td><td>%s</td></tr>"
            + "<tr><td style='font-weight:bold; color:#555;'>Author:</td><td>%s</td></tr>"
            + "<tr><td style='font-weight:bold; color:#555;'>Era:</td><td>%s</td></tr>"
            + "</table>"
            + "</div>"
            + "</body></html>",
            selectedBook.getTitle(),
            authorName,
            selectedBook.getEra()
        );

        // Center content label
        JLabel label = new JLabel(htmlContent, SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Styled Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeButton.setBackground(new Color(63, 81, 181));
        closeButton.setForeground(Color.black);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        closeButton.addActionListener(e -> dialog.dispose());

        // Bottom button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(closeButton);

        // Add components
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
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

        // Create styled dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("📝 Sentence Details");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(12, 10));
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(null);

        // Panel content with HTML styling
        String htmlContent = String.format(
            "<html><body style='font-family:Segoe UI; padding:15px; color:#333;'>"
            + "<h2 style='color:#3F51B5; margin-top:0;'>Author Information</h2>"
            + "<table cellpadding='6' style='font-size:14px;'>"
            + "<tr><td style='font-weight:bold; color:#555;'>Name:</td><td>%s</td></tr>"
            + "<tr><td style='font-weight:bold; color:#555;'>Biography:</td><td style='width:280px;'>%s</td></tr>"
            + "</table>"
            + "</body></html>",
            selectedAuthor.getName(),
            selectedAuthor.getBiography()
        );

        JLabel label = new JLabel(htmlContent);
        label.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeButton.setBackground(new Color(63, 81, 181));
        closeButton.setForeground(Color.black);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        closeButton.addActionListener(e -> dialog.dispose());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        buttonPanel.add(closeButton);

        // Add to dialog
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
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

        // Create dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("📝 Chapter Details");
        dialog.setModal(true);

        dialog.setUndecorated(true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout(15, 15));

        // Rounded border and subtle shadow
        dialog.getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // === Header ===
        JLabel header = new JLabel("📖 Chapter Details", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(63, 81, 181));
        dialog.add(header, BorderLayout.NORTH);

        // === Info Panel ===
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblBook = new JLabel("📚 Book:");
        lblBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBook.setForeground(new Color(85, 85, 85));

        JLabel lblBookValue = new JLabel(selectedBook.getTitle());
        lblBookValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBookValue.setForeground(new Color(33, 33, 33));

        JLabel lblChapter = new JLabel("📄 Chapter:");
        lblChapter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChapter.setForeground(new Color(85, 85, 85));

        JLabel lblChapterValue = new JLabel(selectedChapter.getChapterName());
        lblChapterValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblChapterValue.setForeground(new Color(33, 33, 33));

        infoPanel.add(lblBook);
        infoPanel.add(lblBookValue);
        infoPanel.add(lblChapter);
        infoPanel.add(lblChapterValue);

        dialog.add(infoPanel, BorderLayout.CENTER);

        // === Close Button ===
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.black);
        closeButton.setBackground(new Color(63, 81, 181));
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setOpaque(true);

        // Hover animation
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setBackground(new Color(48, 63, 159));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setBackground(new Color(63, 81, 181));
            }
        });

        closeButton.addActionListener(ev -> fadeOutDialog(dialog));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // === Fade in ===
        dialog.setOpacity(0f);
        dialog.setVisible(true);
        fadeInDialog(dialog);
    }
    private void fadeInDialog(JDialog dialog) {
        new Thread(() -> {
            for (float i = 0; i <= 1.0f; i += 0.05f) {
                float opacity = Math.min(i, 1.0f);
                dialog.setOpacity(opacity);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void fadeOutDialog(JDialog dialog) {
        new Thread(() -> {
            for (float i = 1.0f; i >= 0f; i -= 0.05f) {
                float opacity = Math.max(i, 0f);
                dialog.setOpacity(opacity);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
            dialog.dispose();
        }).start();
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
    private void fadeIn(JDialog dialog) {
        new Thread(() -> {
            for (float i = 0; i <= 1.0; i += 0.05f) {
                final float opacity = i;
                SwingUtilities.invokeLater(() -> dialog.setOpacity(opacity));
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
            dialog.setVisible(true);
        }).start();
    }

    private void fadeOut(JDialog dialog) {
        new Thread(() -> {
            for (float i = 1.0f; i >= 0; i -= 0.05f) {
                final float opacity = i;
                SwingUtilities.invokeLater(() -> dialog.setOpacity(opacity));
                try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            }
            dialog.dispose();
        }).start();
    }

    private void addDetail(JPanel panel, GridBagConstraints gbc, String label, String value,Font labelFont, Font valueFont, Color labelColor, Color valueColor, int y) {
       gbc.gridx = 0;
       gbc.gridy = y;
       JLabel lbl = new JLabel(label);
       	lbl.setFont(labelFont);
       	lbl.setForeground(labelColor);
       	panel.add(lbl, gbc);

       	gbc.gridx = 1;
       	JLabel val = new JLabel("<html><div style='width:350px;'>" + value + "</div></html>");
       	val.setFont(valueFont);
       	val.setForeground(valueColor);
       	panel.add(val, gbc);
    }


    private void retrieveSentence() {
        if (selectedSentence == null) {
            showWarningMessage("Please select a sentence first!");
            return;
        }

        // ===== Create Modern Animated Dialog =====
        JDialog dialog = new JDialog();
        dialog.setTitle("📝 Sentence Details");
        dialog.setModal(true);

        dialog.setUndecorated(true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(63, 81, 181), 3));

        // ===== Background Panel with Gradient =====
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 255),
                        getWidth(), getHeight(), new Color(220, 230, 250));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        background.setLayout(new BorderLayout(15, 15));
        background.setBorder(new EmptyBorder(20, 25, 25, 25));

        // ===== Header =====
        JLabel header = new JLabel("📝 Sentence Details", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        header.setForeground(new Color(63, 81, 181));
        background.add(header, BorderLayout.NORTH);

        // ===== Details Panel =====
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(90, 90, 90);
        Color valueColor = new Color(30, 30, 30);

        addDetail(detailsPanel, gbc, "Number:", String.valueOf(selectedSentence.getSentenceNumber()), labelFont, valueFont, labelColor, valueColor, 0);
        addDetail(detailsPanel, gbc, "Text:", selectedSentence.getText(), labelFont, valueFont, labelColor, valueColor, 1);
        addDetail(detailsPanel, gbc, "Diacritized:", selectedSentence.getTextDiacritized(), labelFont, valueFont, labelColor, valueColor, 2);
        addDetail(detailsPanel, gbc, "Translation:", selectedSentence.getTranslation(), labelFont, valueFont, labelColor, valueColor, 3);
        addDetail(detailsPanel, gbc, "Notes:", selectedSentence.getNotes(), labelFont, valueFont, labelColor, valueColor, 4);

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        background.add(scrollPane, BorderLayout.CENTER);

        // ===== Close Button =====
        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(new Color(63, 81, 181));
        closeBtn.setForeground(Color.black);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> {
            fadeOut(dialog);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);
        background.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(background);
        fadeIn(dialog);
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
    
    /**
     * 🚀 One-Tap Add Book with File Selection and Automatic Chapter/Sentence Extraction
     */
    /**
     * 🚀 One-Tap Add Book with File Selection for Chapter Separation
     */
    private void oneTapAddBook() {
        JDialog oneTapDialog = createModernDialog("One-Tap Add Book", 600, 500);
        oneTapDialog.setLayout(new BorderLayout(20, 20));
        
        // ===== MODERN CARD CONTAINER =====
        JPanel cardPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 30, 30);
                
                // Card background with gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255), 
                                                    getWidth(), getHeight(), new Color(245, 248, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);
                
                // Border
                g2d.setColor(new Color(220, 230, 255));
                g2d.drawRoundRect(0, 0, getWidth()-8, getHeight()-8, 25, 25);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // ===== HEADER WITH ICON =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel;
        try {
            ImageIcon rocketIcon = new ImageIcon("bin/icons/rocket.png");
            Image scaledRocket = rocketIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            titleLabel = new JLabel("  One-Tap Add Book", new ImageIcon(scaledRocket), JLabel.LEFT);
        } catch (Exception e) {
            titleLabel = new JLabel("  🚀 One-Tap Add Book");
        }
        
        titleLabel.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_DARK);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel subtitle = new JLabel("Upload a text file to automatically separate chapters");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        
        // ===== FILE SELECTION AREA =====
        JPanel filePanel = createBookFileSelectionArea(oneTapDialog);
        cardPanel.add(filePanel, BorderLayout.CENTER);
        
        // ===== PROGRESS INDICATOR =====
        JPanel progressPanel = createBookProgressIndicator();
        cardPanel.add(progressPanel, BorderLayout.SOUTH);
        
        oneTapDialog.add(cardPanel, BorderLayout.CENTER);
        oneTapDialog.setVisible(true);
    }

    /**
     * 📁 Create animated file selection area for books
     */
    private JPanel createBookFileSelectionArea(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        
        // File selection card
        JPanel fileCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with subtle gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(250, 252, 255), 
                                                    getWidth(), getHeight(), new Color(240, 245, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Dashed border
                g2d.setColor(new Color(200, 210, 255));
                g2d.setStroke(new java.awt.BasicStroke(2, java.awt.BasicStroke.CAP_ROUND, 
                                                      java.awt.BasicStroke.JOIN_ROUND, 0, new float[]{8}, 0));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);
            }
        };
        fileCard.setOpaque(false);
        fileCard.setPreferredSize(new Dimension(400, 180));
        fileCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // File icon and text
        JLabel fileIcon;
        try {
            ImageIcon fileImg = new ImageIcon("bin/icons/file.png");
            Image scaledFile = fileImg.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            fileIcon = new JLabel(new ImageIcon(scaledFile));
        } catch (Exception e) {
            fileIcon = new JLabel("📄");
            fileIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        }
        
        fileIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel dragText = new JLabel("Click to select or drag a text file here");
        dragText.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 16));
        dragText.setForeground(TEXT_PRIMARY);
        dragText.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel formatText = new JLabel("Supports: .txt files | Automatic chapter separation");
        formatText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formatText.setForeground(TEXT_SECONDARY);
        formatText.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textPanel.setOpaque(false);
        textPanel.add(dragText);
        textPanel.add(formatText);
        
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(fileIcon, BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.SOUTH);
        
        fileCard.add(contentPanel, BorderLayout.CENTER);
        
        // File selection logic with animation
        fileCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                animateClick(fileCard);
                openBookFileChooser(parentDialog);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                animateHover(fileCard, 1.05f);
                fileCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                animateHover(fileCard, 1.0f);
                fileCard.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        // Enable drag and drop
        fileCard.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
            }
            
            @Override
            public boolean importData(TransferSupport support) {
                try {
                    java.util.List<?> files = (java.util.List<?>) support.getTransferable()
                        .getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                    if (files.size() > 0) {
                        java.io.File file = (java.io.File) files.get(0);
                        if (file.getName().toLowerCase().endsWith(".txt")) {
                            processSelectedBookFile(file, parentDialog);
                        } else {
                            showErrorMessage("Please select a .txt file");
                        }
                    }
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
        });
        
        panel.add(fileCard, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 📊 Create progress indicator for book processing
     */
    private JPanel createBookProgressIndicator() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setVisible(false); // Initially hidden
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 8));
        progressBar.setForeground(SUCCESS);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel progressLabel = new JLabel("Separating chapters from file...");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressLabel.setForeground(TEXT_SECONDARY);
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(progressLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    /**
     * 📂 Open file chooser dialog for book
     */
    private void openBookFileChooser(JDialog parentDialog) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Text File for Book");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        
        // Customize file chooser appearance
        fileChooser.setPreferredSize(new Dimension(600, 400));
        
        int result = fileChooser.showOpenDialog(parentDialog);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            processSelectedBookFile(selectedFile, parentDialog);
        }
    }

    /**
     * 🔄 Process selected book file and call business layer method
     */
    private void processSelectedBookFile(java.io.File file, JDialog parentDialog) {
        // Show progress
        JPanel progressPanel = (JPanel) ((BorderLayout) ((JPanel) parentDialog.getContentPane().getComponent(0)).getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        progressPanel.setVisible(true);
        parentDialog.revalidate();
        parentDialog.repaint();
        
        // Process the file using your business layer method
        Timer processTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the absolute path of the selected file
                    String filePath = file.getAbsolutePath();
                    
                    // Call your business layer method - chapterSeparater
                    boolean success = BussinessLayerFasade.chapterSeparater(filePath);
                    
                    progressPanel.setVisible(false);
                    
                    if (success) {
                        showSuccessMessage("Book created successfully!\n" +
                                         "Chapters separated from: " + file.getName() + "\n" +
                                         "File processed: " + filePath);
                     // Call this whenever you need to refresh the chapter list
                        
                        parentDialog.dispose();
                        
                        // Refresh the books list
                        loadBooks();
                        
                    } else {
                        showErrorMessage("Failed to separate chapters from file.\n" +
                                       "Please check the file format and try again.");
                    }
                } catch (Exception ex) {
                    progressPanel.setVisible(false);
                    showErrorMessage("Error processing file: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        processTimer.setRepeats(false);
        processTimer.start();
    }
    /**
     * 🎬 Animate hover effect for components
     */
    private void animateHover(JComponent comp, float targetScale) {
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            float currentScale = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentScale += (targetScale - currentScale) * 0.3f;
                if (Math.abs(currentScale - targetScale) < 0.01f) {
                    currentScale = targetScale;
                    timer.stop();
                }
                comp.repaint();
            }
        });
        timer.start();
    }

    /**
     * 🎬 Animate click effect for components
     */
    private void animateClick(final JComponent comp) {
        Timer timer = new Timer(5, null);
        timer.addActionListener(new ActionListener() {
            float scale = 1.0f;
            boolean shrinking = true;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shrinking) {
                    scale -= 0.1f;
                    if (scale <= 0.9f) {
                        shrinking = false;
                    }
                } else {
                    scale += 0.1f;
                    if (scale >= 1.0f) {
                        scale = 1.0f;
                        timer.stop();
                    }
                }
                comp.repaint();
            }
        });
        timer.start();
    }
    /**
     * 🔄 Refresh Methods for Real-time Updates
     */
    private void refreshBooksPanel() {
        if (bookListModel != null) {
            loadBooks();
            
            // Update the formatted model with bullet points
            DefaultListModel<String> formattedModel = new DefaultListModel<>();
            for (int i = 0; i < bookListModel.size(); i++) {
                formattedModel.addElement("•  " + bookListModel.get(i));
            }
            bookListModel = formattedModel;
            
            if (bookList != null) {
                bookList.setModel(bookListModel);
                bookList.repaint();
            }
        }
    }
    
//    private JList<String> createModernList(DefaultListModel<String> model) {
//        JList<String> list = new JList<>(model);
//        list.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        list.setBackground(new Color(245, 248, 255));
//        list.setSelectionBackground(new Color(200, 220, 255));
//        list.setSelectionForeground(Color.BLACK);
//        list.setFixedCellHeight(40);
//        list.setBorder(new EmptyBorder(5, 10, 5, 10));
//        list.setFocusable(false);
//        return list;
//    }   Code TO BE Deleted....



    
}

