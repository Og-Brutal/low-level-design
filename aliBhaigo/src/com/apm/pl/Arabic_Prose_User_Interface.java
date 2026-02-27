package com.apm.pl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
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
import java.awt.geom.RoundRectangle2D;
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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.apm.bll.BussinessLayerFasade;
import com.apm.bll.IBussinessLayerFasade;
import com.apm.dto.AuthorDTO;
import com.apm.dto.BookDTO;
import com.apm.dto.ChapterDTO;
import com.apm.dto.IndexRow;
import com.apm.dto.SegmentedTokenDTO;
import com.apm.dto.SentenceDTO;
import com.apm.dto.SimilarityResultDTO;
import com.apm.observers.IObserver;

/**
 * Modern Arabic Prose Management System with Professional UI
 * Features: Smooth animations, hover effects, gradient backgrounds, rounded corners
 */
public class Arabic_Prose_User_Interface extends JFrame implements IObserver {

    private IBussinessLayerFasade BussinessLayerFasade;
    private JDialog activeChaptersDialog = null;
    private JDialog activeSentencesDialog = null;
    
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

public Arabic_Prose_User_Interface(BussinessLayerFasade blf) {
    super("Arabic Prose Application");
    this.BussinessLayerFasade = blf; // save the reference

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 700); // Increased size slightly for better layout
    setLocationRelativeTo(null);
    
    // Set main background color (optional, keeps consistency)
    getContentPane().setBackground(new Color(245, 247, 250));

    // Create panels
    JPanel bookPanel = createBooksPanel();
    JPanel authorPanel = createAuthorsPanel();
    JPanel browsePanel = createBrowsePanel();
    
    // ✅ Create the new Search Panel
    // Passing the facade 'blf' as required by the SearchTokenPanel constructor
    JPanel searchPanel = new SearchTokenPanel(blf);

    // ✅ Create the new Indexing Panel
   
    // Create and configure tabbed pane
    JTabbedPane tabbedPane = createModernTabbedPane(); // Use your custom styling method if available
    // OR simple instantiation if using default: JTabbedPane tabbedPane = new JTabbedPane();

//    tabbedPane.addTab("Books", bookPanel);
//    tabbedPane.addTab("Authors", authorPanel);
    tabbedPane.addTab("Book",bookPanel);
    
    tabbedPane.addTab("Author", authorPanel);
   
    tabbedPane.addTab("Index", browsePanel);
    
    // ✅ Add the Search Tab
    tabbedPane.addTab("Search", searchPanel);
    
    
    
    // ✅ Add the Indexing Tab

    // Add tabbed pane to frame
    getContentPane().add(tabbedPane);

    setVisible(true);
}


	private JPanel createBrowsePanel() {
	    // MorphologicalBrowserPanel already handles all UI and interactions
	    return new Morphological_IndexingPanel(BussinessLayerFasade);
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
    /**
 * 🎨 Create Professional Tabbed Pane with Glassmorphism Effect
 */
private JTabbedPane createModernTabbedPane() {
    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Glassmorphism background
            GradientPaint gp = new GradientPaint(0, 0, new Color(248, 250, 252),
                                                getWidth(), getHeight(), new Color(241, 245, 249));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    
    // Professional styling
    tabbedPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
    tabbedPane.setForeground(new Color(30, 41, 59));
    tabbedPane.setBackground(new Color(241, 245, 249));
    tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    // Custom UI with advanced effects
    tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
        private final Color ACTIVE_TAB = new Color(59, 130, 246);
        private final Color HOVER_TAB = new Color(96, 165, 250);
        private final Color INACTIVE_TAB = new Color(148, 163, 184);
        private final Color TAB_BG = new Color(255, 255, 255, 220);
        
        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                         int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Tab shape with rounded corners
            RoundRectangle2D tabShape = new RoundRectangle2D.Float(x + 2, y + 2, 
                w - 4, h - 2, 12, 12);
            
            if (isSelected) {
                // Selected tab - gradient with border
                GradientPaint selectedGradient = new GradientPaint(
                    x, y, new Color(239, 246, 255),
                    x, y + h, new Color(219, 234, 254)
                );
                g2d.setPaint(selectedGradient);
                g2d.fill(tabShape);
                
                // Border for selected tab
                g2d.setColor(ACTIVE_TAB);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(tabShape);
                
                // Subtle shadow effect
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fill(new RoundRectangle2D.Float(x + 1, y + 3, w - 4, h - 2, 12, 12));
            } else {
                // Inactive tab - subtle gradient
                GradientPaint inactiveGradient = new GradientPaint(
                    x, y, new Color(255, 255, 255, 220),
                    x, y + h, new Color(248, 250, 252, 220)
                );
                g2d.setPaint(inactiveGradient);
                g2d.fill(tabShape);
                
                // Subtle border
                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.draw(tabShape);
            }
        }
        
        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font,
                                FontMetrics metrics, int tabIndex, String title,
                                Rectangle textRect, boolean isSelected) {
            g.setFont(font.deriveFont(isSelected ? Font.BOLD : Font.PLAIN));
            
            if (isSelected) {
                g.setColor(new Color(30, 41, 59));
            } else {
                g.setColor(new Color(100, 116, 139));
            }
            
            // Center text with icon spacing
            int x = textRect.x + (textRect.width - metrics.stringWidth(title)) / 2;
            int y = textRect.y + metrics.getAscent() + 2;
            g.drawString(title, x, y);
        }
        
        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                     int x, int y, int w, int h, boolean isSelected) {
            // Border already painted in paintTabBackground
        }
        
        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                          Rectangle[] rects, int tabIndex,
                                          Rectangle iconRect, Rectangle textRect,
                                          boolean isSelected) {
            // Custom focus indicator
            if (isSelected && tabbedPane.hasFocus()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(59, 130, 246, 100));
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new RoundRectangle2D.Float(
                    rects[tabIndex].x + 2, rects[tabIndex].y + 2,
                    rects[tabIndex].width - 4, rects[tabIndex].height - 2, 12, 12
                ));
            }
        }
    });
    
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
 // ==================== 📑 CHAPTERS PANEL (UPDATED WITH INDEX TABS) ====================
    private void openChaptersPanel() {
        // Create the main dialog
        JDialog chapterDialog = createModernDialog("Book Details - " + selectedBook.getTitle(), 1100, 750);
        activeChaptersDialog = chapterDialog; // Store reference for refresh logic

        chapterDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                activeChaptersDialog = null; // Clear when closed
            }
        });

        // 1. Create the Main Tabbed Pane
        JTabbedPane mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainTabbedPane.setBackground(BACKGROUND);
        mainTabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2. Create the "Chapter List" Panel (Moved existing logic here)
        JPanel chapterListPanel = createChapterListPanel(chapterDialog);

        // 3. Create the "Index" Panel (New Logic)
        JPanel indexPanel = createIndexPanel();

        // 4. Add tabs
        mainTabbedPane.addTab("   📁 Chapter List   ", chapterListPanel);
        mainTabbedPane.addTab("   🗂️ Browse Book   ", indexPanel);

        // 5. Add to Dialog
        chapterDialog.add(mainTabbedPane, BorderLayout.CENTER);
        chapterDialog.setVisible(true);
    }

    
    
 // ==================== 📁 EXISTING: Chapter List Panel Logic ====================
    private JPanel createChapterListPanel(JDialog parentDialog) {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- One-Tap Add Button ---
        JPanel oneTapChapterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        oneTapChapterPanel.setBackground(BACKGROUND);
        AnimatedButton oneTapChapterBtn = new AnimatedButton("📁 One-Tap Add Chapter", new Color(106, 27, 154), new Color(142, 36, 170));
        oneTapChapterBtn.setPreferredSize(new Dimension(280, 55));
        oneTapChapterBtn.addActionListener(e -> oneTapAddChapter(selectedBook.getTitle()));
        oneTapChapterPanel.add(oneTapChapterBtn);
        contentPanel.add(oneTapChapterPanel, BorderLayout.NORTH);

        // --- List Card ---
        JPanel listCard = createModernCard();
        listCard.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Chapters: " + selectedBook.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY);
        titlePanel.add(titleLabel);
        listCard.add(titlePanel, BorderLayout.NORTH);

        // Load Chapters
        DefaultListModel<String> chapterListModel = new DefaultListModel<>();
        ArrayList<ChapterDTO> chapters = BussinessLayerFasade.retrieveChapters(selectedBook.getTitle());
        if (chapters != null) {
            for (ChapterDTO chapter : chapters) {
                chapterListModel.addElement(chapter.getChapterName());
            }
        }

        JList<String> chapterList = createModernList(chapterListModel);
        chapterList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Logic to select chapter
        final ArrayList<ChapterDTO> finalChapters = chapters;
        chapterList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = chapterList.getSelectedIndex();
                if (index >= 0 && finalChapters != null) selectedChapter = finalChapters.get(index);
            }
        });

        // Double click to open
        chapterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = chapterList.locationToIndex(e.getPoint());
                    if (index >= 0 && finalChapters != null) {
                        selectedChapter = finalChapters.get(index);
                        openSentencesPanel(parentDialog);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(chapterList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        listCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(listCard, BorderLayout.CENTER);

        // CRUD Buttons
        JPanel buttonPanel = createAnimatedCRUDPanel("Chapter");
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    
 // ==================== 🗂️ NEW: Indexing Panel ====================
    private JPanel createIndexPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header
        JLabel header = new JLabel("<html><center>Browse for: " + selectedBook.getTitle() + "<br><span style='font-size:12px; font-weight:normal'>Select a tab below to browse index terms</span></center></html>", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        header.setForeground(PRIMARY_DARK);
        panel.add(header, BorderLayout.NORTH);

        // 2. Tabs for Roots, Lemmas, Tokens
        JTabbedPane indexTabs = new JTabbedPane(JTabbedPane.TOP);
        indexTabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        indexTabs.setBackground(CARD_BG);

        // Fetch Data using Business Layer
        // Note: Using Title because BLL provided in context takes String, though prompt asked for ID. 
        // This maps correctly to the available BLL functions.
        ArrayList<String> roots = BussinessLayerFasade.getAllRootsByBook(selectedBook.getTitle());
        ArrayList<String> lemmas = BussinessLayerFasade.getAllLemmasByBook(selectedBook.getTitle());
        ArrayList<String> tokens = BussinessLayerFasade.getAllTokensByBook(selectedBook.getTitle());

        // Add Tabs
        indexTabs.addTab("  Roots (جذور)  ", createIndexListPanel(roots, "Root"));
        indexTabs.addTab("  Lemmas (مجردات)  ", createIndexListPanel(lemmas, "Lemma"));
        indexTabs.addTab("  Tokens (كلمات)  ", createIndexListPanel(tokens, "Token"));

        panel.add(indexTabs, BorderLayout.CENTER);
        return panel;
    }
    
 // ==================== 🗂️ NEW: Generic Index List Builder ====================
    private JPanel createIndexListPanel(ArrayList<String> dataList, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (dataList == null || dataList.isEmpty()) {
            JLabel noData = new JLabel("No " + type + "s found for this book.", SwingConstants.CENTER);
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            panel.add(noData, BorderLayout.CENTER);
            return panel;
        }

        // Populate Model
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String item : dataList) {
            model.addElement(item);
        }

        // Create List using existing styling method
        JList<String> list = createModernList(model);
        list.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // Add Click Listener to open Table
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 || e.getClickCount() == 2) { // Allow single or double click
                    String selectedValue = list.getSelectedValue();
                    if (selectedValue != null) {
                        // Remove numbering if present (e.g., "1. Value" -> "Value")
                        String cleanValue = selectedValue;
                        if(selectedValue.contains(". ")) {
                             cleanValue = selectedValue.substring(selectedValue.indexOf(". ") + 2).trim();
                        }
                        showIndexDetails(cleanValue, type);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("Click on a " + type + " to view occurrences.", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setBorder(new EmptyBorder(5,0,0,0));
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
    }
    
    
 // ==================== 🗂️ NEW: Index Detail Table (Drill Down) ====================
   // ==================== 🗂️ UPDATED: Index Detail Table (With Navigation) ====================
private void showIndexDetails(String value, String type) {
    JDialog detailDialog = createModernDialog(type + " Index: " + value, 700, 500);
    JPanel content = new JPanel(new BorderLayout(15, 15));
    content.setBackground(BACKGROUND);
    content.setBorder(new EmptyBorder(20, 20, 20, 20));

    // 1. Title
    JLabel title = new JLabel("Occurrences of " + type + ": " + value, SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI Black", Font.PLAIN, 20));
    title.setForeground(PRIMARY);
    content.add(title, BorderLayout.NORTH);

    // 2. Fetch Data
    ArrayList<IndexRow> rows = null;
    switch (type) {
        case "Root":
            rows = BussinessLayerFasade.getIndexRowsByRootId(value); 
            break;
        case "Lemma":
            rows = BussinessLayerFasade.getIndexRowsByLemmaId(value);
            break;
        case "Token":
            rows = BussinessLayerFasade.getIndexRowsByTokenText(value);
            break;
    }

    // 3. Create Table Model
    // We make cells non-editable
    javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(new String[]{"Chapter Name", "Sentence Number"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    if (rows != null && !rows.isEmpty()) {
        for (IndexRow row : rows) {
            tableModel.addRow(new Object[]{row.getChapterName(), row.getSentenceNumber()});
        }
    }

    javax.swing.JTable table = new javax.swing.JTable(tableModel);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.setRowHeight(30);
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    table.getTableHeader().setBackground(new Color(230, 240, 255));
    table.setFillsViewportHeight(true);
    table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    
    // Center Align the Sentence Number
    javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

    // 4. ADD MOUSE LISTENER FOR NAVIGATION
    table.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { // Double-click to navigate
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String chapterName = (String) table.getValueAt(selectedRow, 0);
                    int sentenceNum = (int) table.getValueAt(selectedRow, 1);
                    
                    navigateToChapterSentence(chapterName, sentenceNum);
                }
            }
        }
    });

    JScrollPane tableScroll = new JScrollPane(table);
    
    if (rows == null || rows.isEmpty()) {
        content.add(new JLabel("No occurrences found.", SwingConstants.CENTER), BorderLayout.CENTER);
    } else {
        content.add(tableScroll, BorderLayout.CENTER);
        
        JLabel hintLabel = new JLabel("💡 Double-click a row to jump to the sentence.", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(TEXT_SECONDARY);
        content.add(hintLabel, BorderLayout.SOUTH);
    }

    // 5. Close Button wrapper (if needed, otherwise the hint is at SOUTH)
    // You can add the close button to a separate panel if you want both hint and button.
    
    detailDialog.add(content);
    detailDialog.setVisible(true);
}

// Helper method to handle navigation logic
private void navigateToChapterSentence(String chapterName, int sentenceNumber) {
    // 1. Find the ChapterDTO corresponding to the name
    ArrayList<ChapterDTO> chapters = BussinessLayerFasade.retrieveChapters(selectedBook.getTitle());
    if (chapters != null) {
        for (ChapterDTO ch : chapters) {
            if (ch.getChapterName().equals(chapterName)) {
                selectedChapter = ch; // Set the global selected chapter
                
                // 2. Open the Sentences Panel with highlighting
                // We pass the activeChaptersDialog as parent so it stacks correctly
                openSentencesPanel(activeChaptersDialog, sentenceNumber);
                return;
            }
        }
    }
    showErrorMessage("Could not find chapter: " + chapterName);
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


////////////////////////////////////////////////////////////////////////////////
//==================== 📝 SENTENCES PANEL (UPDATED - REINTRODUCING TABS) ====================
//==================== 📝 SENTENCES PANEL (UPDATED) ====================

//Original method calls the new overload with -1 (no highlight)
private void openSentencesPanel(JDialog parentDialog) {
 openSentencesPanel(parentDialog, -1);
}

//New overload that accepts a sentence number to highlight
private void openSentencesPanel(JDialog parentDialog, int highlightSentenceNumber) {
 JDialog sentenceDialog = createModernDialog("Sentences - " + selectedChapter.getChapterName(), 1200, 750); 
 activeSentencesDialog = sentenceDialog; 

 sentenceDialog.addWindowListener(new java.awt.event.WindowAdapter() {
     @Override
     public void windowClosed(java.awt.event.WindowEvent e) {
         activeSentencesDialog = null;
     }
 });

 // Load sentences 
 ArrayList<SentenceDTO> sentences = BussinessLayerFasade.retrieveSentence(selectedChapter.getChapterName());
 final ArrayList<SentenceDTO> chapterSentences = (sentences != null) ? sentences : new ArrayList<>();

 JTabbedPane sentencesTabbedPane = new JTabbedPane(); 
 sentencesTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
 sentencesTabbedPane.setBorder(new EmptyBorder(0, 0, 0, 0)); 

 // Pass the highlight number to the list panel creator
 JPanel sentenceListPanel = createSentenceListPanel(chapterSentences, sentenceDialog, highlightSentenceNumber);
 JPanel similarityCheckPanel = createSimilarityCheckPanel(chapterSentences, sentenceDialog);

 sentencesTabbedPane.addTab("   📄 Sentence List   ", sentenceListPanel);
 sentencesTabbedPane.addTab("   ⚖️ Check Similarity   ", similarityCheckPanel);
 
 sentenceDialog.add(sentencesTabbedPane, BorderLayout.CENTER);
 sentenceDialog.setVisible(true);
}

//==================== 📝 NEW: Create Sentence List Panel (Standard View) ====================
//==================== 📝 NEW: Create Sentence List Panel (With Highlighting) ====================
private JPanel createSentenceListPanel(ArrayList<SentenceDTO> chapterSentences, JDialog parentDialog, int highlightNumber) {
 JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
 contentPanel.setBackground(BACKGROUND);
 contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

 // ===== Sentences Card =====
 JPanel listCard = createModernCard();
 listCard.setLayout(new BorderLayout(10, 10));

 // ... [Header creation code remains same as before] ...
 JPanel titlePanel = new JPanel(new BorderLayout());
 titlePanel.setOpaque(false);
 titlePanel.setBorder(new EmptyBorder(12, 15, 12, 15));
 JLabel chapterIconLabel = new JLabel(new ImageIcon(new ImageIcon("bin/icons/chapter.png").getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
 JLabel titleLabel = new JLabel(selectedChapter.getChapterName());
 titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
 titleLabel.setForeground(PRIMARY);
 JPanel titleContent = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
 titleContent.setOpaque(false);
 titleContent.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
 titleContent.add(titleLabel);
 titleContent.add(chapterIconLabel);
 titlePanel.add(titleContent, BorderLayout.CENTER);
 listCard.add(titlePanel, BorderLayout.NORTH);

 // ===== Load sentences into model =====
 DefaultListModel<String> sentenceListModel = new DefaultListModel<>();
 for (SentenceDTO s : chapterSentences) {
     sentenceListModel.addElement(s.getText());
 }

 // ===== Modern list =====
 JList<String> sentenceList = new JList<>(sentenceListModel);
 sentenceList.setBackground(new Color(240, 245, 250));
 sentenceList.setSelectionBackground(new Color(255, 235, 59)); // Highlight color (Yellowish)
 sentenceList.setSelectionForeground(Color.BLACK);
 sentenceList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
 sentenceList.setBorder(null);

 // Custom Renderer
 sentenceList.setCellRenderer(new DefaultListCellRenderer() {
     private final Color lightRow = new Color(245, 248, 255);
     private final Color separatorColor = new Color(210, 210, 210);

     @Override
     public Component getListCellRendererComponent(JList<?> list, Object value,
                                                   int index, boolean isSelected, boolean cellHasFocus) {
         JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
         label.setHorizontalAlignment(SwingConstants.RIGHT);
         label.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
         label.setText("<html><body dir='rtl' style='text-align: right; font-family: Segoe UI;'><strong>[" + (index + 1) + "]</strong> " + value.toString() + "</body></html>");
         label.setOpaque(true);

         if (isSelected) {
             label.setBackground(new Color(255, 249, 196)); // Light Yellow for selection
             label.setForeground(Color.BLACK);
             label.setBorder(BorderFactory.createLineBorder(new Color(251, 192, 45), 1));
         } else {
             label.setBackground(lightRow);
             label.setForeground(new Color(30, 30, 30));
             label.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createEmptyBorder(8, 15, 8, 15),
                 BorderFactory.createMatteBorder(0, 0, 1, 0, separatorColor)
             ));
         }
         return label;
     }
 });
 sentenceList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

 // ===== AUTO-HIGHLIGHT LOGIC =====
 if (highlightNumber != -1) {
     // Find the index of the sentence with the matching sentenceNumber
     int indexToSelect = -1;
     for (int i = 0; i < chapterSentences.size(); i++) {
         if (chapterSentences.get(i).getSentenceNumber() == highlightNumber) {
             indexToSelect = i;
             break;
         }
     }
     
     if (indexToSelect != -1) {
         sentenceList.setSelectedIndex(indexToSelect);
         final int idx = indexToSelect;
         // Scroll to view
         SwingUtilities.invokeLater(() -> sentenceList.ensureIndexIsVisible(idx));
     }
 }

 // ===== Listeners =====
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
     public void mouseClicked(MouseEvent e) {
         if (e.getClickCount() == 2) {
             int index = sentenceList.locationToIndex(e.getPoint());
             if (index >= 0 && index < chapterSentences.size()) {
                 Rectangle cellBounds = sentenceList.getCellBounds(index, index);
                 if (cellBounds != null && cellBounds.contains(e.getPoint())) {
                     SentenceDTO clickedSentence = chapterSentences.get(index);
                     openTokenAnalysisPanel(clickedSentence);
                 }
             }
         }
     }
 });

 JScrollPane scrollPane = new JScrollPane(sentenceList);
 scrollPane.setBorder(null);
 scrollPane.getViewport().setBackground(CARD_BG);
 scrollPane.getViewport().setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
 
 listCard.add(scrollPane, BorderLayout.CENTER);
 contentPanel.add(listCard, BorderLayout.CENTER);

 JPanel buttonPanel = createAnimatedCRUDPanel("Sentence");
 contentPanel.add(buttonPanel, BorderLayout.SOUTH);

 return contentPanel;
}

//==================== ⚖️ NEW: Create Similarity Check Panel (The Similarity Tab) ====================
// In createSimilarityCheckPanel method

// ==================== 1. MAIN CONTAINER (The Tabs) ====================
// ==================== ⚖️ ADVANCED SIMILARITY & SEARCH PANEL ====================
    
// ==================== ⚖️ ADVANCED SIMILARITY & SEARCH MODULE ====================

    /**
     * Creates the main container for Similarity and Search features.
     */
    private JPanel createSimilarityCheckPanel(ArrayList<SentenceDTO> chapterSentences, JDialog parentDialog) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // 1. Professional Tab Styling
        JTabbedPane simTabs = new JTabbedPane();
        simTabs.setFont(new Font("Segoe UI", Font.BOLD, 15));
        simTabs.setBackground(BACKGROUND);
        simTabs.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2. Tab 1: Interactive Comparison (Select 2 sentences)
        JPanel compareTwoPanel = createCompareTwoSentencesPanel(chapterSentences, parentDialog);
        simTabs.addTab("   ⚖️ Compare Analysis   ", compareTwoPanel);

        // 3. Tab 2: Global Semantic Search (Search whole DB)
        JPanel findSimilarPanel = createFindMostSimilarPanel(chapterSentences);
        simTabs.addTab("   🔍 Smart Search   ", findSimilarPanel);

        mainPanel.add(simTabs, BorderLayout.CENTER);
        return mainPanel;
    }

    // ==================== TAB 1: COMPARE TWO SENTENCES ====================

    private JPanel createCompareTwoSentencesPanel(ArrayList<SentenceDTO> chapterSentences, JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BACKGROUND);

        final SentenceDTO[] selectedSentences = new SentenceDTO[2];
        final JLabel[] textLabels = new JLabel[2]; // References to update text in slots

        // --- Top Area: Selection Slots ---
        JPanel slotsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        slotsPanel.setBackground(BACKGROUND);
        slotsPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        slotsPanel.setPreferredSize(new Dimension(0, 180));

        // Slot 1 (Green Accent)
        JPanel slot1 = createSelectionSlot("Sentence A", new Color(232, 245, 233), new Color(46, 125, 50));
        textLabels[0] = (JLabel) ((JPanel) slot1.getComponent(1)).getComponent(0);
        slotsPanel.add(slot1);

        // Slot 2 (Blue Accent)
        JPanel slot2 = createSelectionSlot("Sentence B", new Color(227, 242, 253), new Color(21, 101, 192));
        textLabels[1] = (JLabel) ((JPanel) slot2.getComponent(1)).getComponent(0);
        slotsPanel.add(slot2);

        panel.add(slotsPanel, BorderLayout.NORTH);

        // --- Center Area: Selection List ---
        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setBackground(BACKGROUND);
        listContainer.setBorder(new EmptyBorder(0, 30, 10, 30));

        // Header for List
        JPanel listHeaderPanel = new JPanel(new BorderLayout());
        listHeaderPanel.setBackground(Color.WHITE);
        listHeaderPanel.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, new Color(230,230,230)));
        JLabel listHeader = new JLabel("  Select sentences from the chapter:");
        listHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        listHeader.setForeground(TEXT_SECONDARY);
        listHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        listHeaderPanel.add(listHeader, BorderLayout.CENTER);

        // List Data
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (SentenceDTO s : chapterSentences) {
            listModel.addElement(s.getText());
        }

        // List Component
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                l.setBorder(new EmptyBorder(8, 15, 8, 15));
                if (isSelected) {
                    l.setBackground(new Color(224, 247, 250)); // Light Cyan
                    l.setForeground(new Color(0, 96, 100));
                } else {
                    l.setBackground(index % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                    l.setForeground(TEXT_PRIMARY);
                }
                return l;
            }
        });

        // Click Logic
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = list.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    SentenceDTO s = chapterSentences.get(idx);
                    
                    // Logic to fill first empty slot, or cycle if full
                    if (selectedSentences[0] == null) {
                        selectedSentences[0] = s;
                        updateSlotUI(textLabels[0], s.getText(), true);
                    } else if (selectedSentences[1] == null) {
                         if (!s.getText().equals(selectedSentences[0].getText())) {
                            selectedSentences[1] = s;
                            updateSlotUI(textLabels[1], s.getText(), true);
                         }
                    } else {
                        // Reset and start over at A
                        selectedSentences[0] = s;
                        selectedSentences[1] = null;
                        updateSlotUI(textLabels[0], s.getText(), true);
                        updateSlotUI(textLabels[1], "(Select Second Sentence)", false);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel cardContent = new JPanel(new BorderLayout());
        cardContent.add(listHeaderPanel, BorderLayout.NORTH);
        cardContent.add(scrollPane, BorderLayout.CENTER);
        
        listContainer.add(cardContent, BorderLayout.CENTER);
        panel.add(listContainer, BorderLayout.CENTER);

        // --- Bottom Area: Actions ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionPanel.setBackground(BACKGROUND);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setBackground(Color.WHITE);
        resetBtn.setPreferredSize(new Dimension(100, 45));
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> {
            selectedSentences[0] = null;
            selectedSentences[1] = null;
            updateSlotUI(textLabels[0], "(Select First Sentence)", false);
            updateSlotUI(textLabels[1], "(Select Second Sentence)", false);
            list.clearSelection();
        });

        AnimatedButton compareBtn = new AnimatedButton("📊 Calculate Similarity", PRIMARY, PRIMARY_LIGHT);
        compareBtn.setPreferredSize(new Dimension(220, 45));
        compareBtn.addActionListener(e -> {
            if (selectedSentences[0] != null && selectedSentences[1] != null) {
                double score = BussinessLayerFasade.checkSimilarity(selectedSentences[0].getText(), selectedSentences[1].getText());
                showSimilarityResultDialog(score);
            } else {
                showWarningMessage("Please select two sentences from the list.");
            }
        });

        actionPanel.add(resetBtn);
        actionPanel.add(compareBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper: Creates the visual box for a selected sentence
    private JPanel createSelectionSlot(String title, Color bg, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(accent, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(accent.darker());
        
        // Use HTML for wrapping text
        JLabel lblText = new JLabel("<html><i style='color:#757575'>(Click list to select)</i></html>");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblText.setVerticalAlignment(SwingConstants.TOP);
        
        JPanel textWrapper = new JPanel(new BorderLayout());
        textWrapper.setOpaque(false);
        textWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        textWrapper.add(lblText, BorderLayout.CENTER);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(textWrapper, BorderLayout.CENTER);
        return card;
    }
    
    // Helper: Updates the text inside a slot
    private void updateSlotUI(JLabel label, String text, boolean isSelected) {
        if(isSelected) {
            label.setText("<html><span style='color:#212121'>" + text + "</span></html>");
        } else {
            label.setText("<html><i style='color:#757575'>" + text + "</i></html>");
        }
    }
    
    // Helper: Shows the result donut chart
    private void showSimilarityResultDialog(double score) {
        JDialog d = createModernDialog("Similarity Result", 450, 400);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        // Custom Painted Component for Circular Score
        JLabel scoreLbl = new JLabel(String.format("%.0f%%", score * 100), SwingConstants.CENTER) {
             @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int dim = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - dim) / 2;
                int y = (getHeight() - dim) / 2;
                
                // Track
                g2.setStroke(new java.awt.BasicStroke(12f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(245, 245, 245));
                g2.drawOval(x, y, dim, dim);
                
                // Progress
                Color c = (score > 0.7) ? SUCCESS : (score > 0.4 ? WARNING : DANGER);
                g2.setColor(c);
                int angle = (int)(360 * score);
                g2.drawArc(x, y, dim, dim, 90, -angle);
            }
        };
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 52));
        scoreLbl.setForeground(TEXT_PRIMARY);
        scoreLbl.setPreferredSize(new Dimension(180, 180));
        
        JPanel centerP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerP.setBackground(Color.WHITE);
        centerP.add(scoreLbl);
        
        JLabel textLbl = new JLabel((score > 0.7 ? "High Match" : (score > 0.4 ? "Partial Match" : "Low Match")), SwingConstants.CENTER);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        textLbl.setForeground(TEXT_SECONDARY);
        
        p.add(centerP, BorderLayout.CENTER);
        p.add(textLbl, BorderLayout.SOUTH);
        p.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        d.add(p);
        d.setVisible(true);
    }

    // ==================== TAB 2: ADVANCED SEARCH (The Professional Part) ====================
    
    private JPanel createFindMostSimilarPanel(ArrayList<SentenceDTO> chapterSentences) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BACKGROUND);

        // --- 1. Professional Search Header ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(Color.WHITE);
        // Subtle bottom border
        headerPanel.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        // Inner Container
        JPanel topContainer = new JPanel(new BorderLayout(15, 10));
        topContainer.setBackground(Color.WHITE);
        topContainer.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Title Block
        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setBackground(Color.WHITE);
        JLabel titleLbl = new JLabel("Semantic Database Search");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLbl.setForeground(PRIMARY_DARK);
        JLabel subTitle = new JLabel("Find conceptually similar sentences across the entire database.");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(TEXT_SECONDARY);
        titleBlock.add(titleLbl);
        titleBlock.add(subTitle);
        
        // Search Input Area
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setOpaque(false);
        inputContainer.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Use a placeholder via ToolTip as fallback, or simple Swing logic
        searchField.setToolTipText("Type a sentence here..."); 
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JButton searchBtn = new JButton("Search Database");
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(160, 45));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputContainer.add(searchField, BorderLayout.CENTER);
        inputContainer.add(searchBtn, BorderLayout.EAST);
        
        // Settings (Threshold Slider)
        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(new EmptyBorder(10, 5, 0, 0));
        
        JLabel threshLabel = new JLabel("Similarity Threshold: 30%");
        threshLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        threshLabel.setForeground(new Color(100, 100, 100));
        
        javax.swing.JSlider slider = new javax.swing.JSlider(0, 100, 30);
        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(200, 20));
        slider.addChangeListener(e -> threshLabel.setText("Similarity Threshold: " + slider.getValue() + "%"));
        
        settingsPanel.add(threshLabel);
        settingsPanel.add(slider);

        topContainer.add(titleBlock, BorderLayout.NORTH);
        topContainer.add(inputContainer, BorderLayout.CENTER);
        topContainer.add(settingsPanel, BorderLayout.SOUTH);
        
        headerPanel.add(topContainer, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. Results Area (Professional Cards) ---
        DefaultListModel<com.apm.dto.SimilarityResultDTO> resultModel = new DefaultListModel<>();
        JList<com.apm.dto.SimilarityResultDTO> resultList = new JList<>(resultModel);
        
        resultList.setCellRenderer(new ResultCardRenderer());
        resultList.setBackground(new Color(250, 252, 255)); // Off-white background
        resultList.setBorder(new EmptyBorder(15, 40, 15, 40));

        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(250, 252, 255));
        
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- 3. Search Logic (Asynchronous) ---
        searchBtn.addActionListener(e -> {
            String text = searchField.getText().trim();
            double threshold = slider.getValue() / 100.0;
            
            if(text.isEmpty()) {
                showWarningMessage("Please enter a sentence to search.");
                return;
            }

            // Set Loading UI
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            searchBtn.setEnabled(false);
            searchBtn.setText("Searching...");
            
            // Run in background thread to keep UI responsive
            new Thread(() -> {
                try {
                    ArrayList<SimilarityResultDTO> results = 
                        BussinessLayerFasade.findSimilarSentences(text, threshold);

                    SwingUtilities.invokeLater(() -> {
                        resultModel.clear();
                        if (results == null || results.isEmpty()) {
                            // Empty State
                            SimilarityResultDTO emptyMsg = new SimilarityResultDTO(
                                "No matches found. Try lowering the threshold or ensure the database is indexed.", 0, ""
                            );
                            resultModel.addElement(emptyMsg);
                        } else {
                            for (SimilarityResultDTO res : results) {
                                resultModel.addElement(res);
                            }
                        }
                        // Reset UI
                        panel.setCursor(Cursor.getDefaultCursor());
                        searchBtn.setEnabled(true);
                        searchBtn.setText("Search Database");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorMessage("Search Error: " + ex.getMessage());
                        panel.setCursor(Cursor.getDefaultCursor());
                        searchBtn.setEnabled(true);
                        searchBtn.setText("Search Database");
                    });
                }
            }).start();
        });

        return panel;
    }

    /**
     * 🎨 Custom Renderer: Displays search results as elevated "Cards"
     * Features: Color-coded score bar, clear text typography, and source path.
     */
    private class ResultCardRenderer extends JPanel implements javax.swing.ListCellRenderer<com.apm.dto.SimilarityResultDTO> {
        private JLabel scoreVal = new JLabel();
        private JLabel textLabel = new JLabel();
        private JLabel sourceLabel = new JLabel();
        private JPanel indicatorBar;

        public ResultCardRenderer() {
            setLayout(new BorderLayout(0, 0));
            // Outer margin + Inner card border
            setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(8, 0, 8, 0), 
                BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(230, 230, 230), 1, true),
                    new EmptyBorder(0, 0, 0, 0)
                )
            ));
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(0, 100));

            // Color Indicator (Left Side)
            indicatorBar = new JPanel();
            indicatorBar.setPreferredSize(new Dimension(6, 0));
            add(indicatorBar, BorderLayout.WEST);

            // Content Container
            JPanel content = new JPanel(new BorderLayout(15, 5));
            content.setBackground(Color.WHITE);
            content.setBorder(new EmptyBorder(15, 15, 15, 15));

            // Score (Right Side)
            scoreVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
            scoreVal.setHorizontalAlignment(SwingConstants.RIGHT);
            
            // Text (Center)
            textLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
            textLabel.setForeground(new Color(33, 33, 33));
            
            // Source (Bottom)
            sourceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            sourceLabel.setForeground(new Color(120, 120, 120));
            
            // Middle Layout
            JPanel centerText = new JPanel(new GridLayout(2, 1, 0, 5));
            centerText.setBackground(Color.WHITE);
            centerText.add(textLabel);
            centerText.add(sourceLabel);

            content.add(centerText, BorderLayout.CENTER);
            content.add(scoreVal, BorderLayout.EAST);

            add(content, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SimilarityResultDTO> list,
                                                      SimilarityResultDTO value, int index, boolean isSelected, boolean cellHasFocus) {
            
            // 1. Text
            textLabel.setText(value.getText());
            
            // 2. Source & State Handling
            if (value.getSource() == null || value.getSource().isEmpty()) {
                // "No Results" State
                sourceLabel.setText("");
                scoreVal.setVisible(false);
                indicatorBar.setBackground(Color.LIGHT_GRAY);
                textLabel.setForeground(Color.GRAY);
                textLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            } else {
                // Normal Result State
                sourceLabel.setText("📍 " + value.getSource());
                scoreVal.setVisible(true);
                textLabel.setForeground(new Color(33, 33, 33));
                textLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 18));
                
                // 3. Dynamic Color Coding
                int percent = (int)(value.getScore()*100.0);
                scoreVal.setText(percent + "%");
                
                Color c;
                if (percent >= 75) c = new Color(46, 125, 50);      // High (Green)
                else if (percent >= 45) c = new Color(255, 143, 0); // Med (Orange)
                else c = new Color(211, 47, 47);                    // Low (Red)
                
                scoreVal.setForeground(c);
                indicatorBar.setBackground(c);
            }
            
            // Hover/Focus visual feedback
            if(isSelected) {
                setBackground(new Color(245, 245, 245));
                setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(8, 0, 8, 0), 
                    new javax.swing.border.LineBorder(PRIMARY, 1, true)
                ));
            } else {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(8, 0, 8, 0), 
                    new javax.swing.border.LineBorder(new Color(230, 230, 230), 1, true)
                ));
            }

            return this;
        }
    }
// ==================== 2. COMPARE TWO (Your Old Logic Refactored) ====================


// ==================== 3. FIND MOST SIMILAR (The New Logic) ====================


// 🟢 NEW UI METHOD


//==================== ⚖️ HELPER: Selection Area UI ====================
private JPanel createSentenceSelectionArea(final SentenceDTO[] selectedSentences) {
  JPanel panel = createModernCard();
  panel.setLayout(new GridLayout(2, 2, 10, 10)); // 2 rows, 2 columns

  // 0: Sentence 1 Label
  JLabel s1Label = createLabel("Sentence 1: (Click to Select)");
  s1Label.setForeground(new Color(46, 125, 50)); // Greenish tone
  panel.add(s1Label); 
  
  // 1: Sentence 2 Label
  JLabel s2Label = createLabel("Sentence 2: (Click to Select)");
  s2Label.setForeground(new Color(1, 87, 155)); // Bluish tone
  panel.add(s2Label);

  // 2: Placeholder for Reset Button
  AnimatedButton resetBtn = new AnimatedButton("🔄 Reset Selection", WARNING, WARNING_HOVER);
  resetBtn.addActionListener(e -> {
      selectedSentences[0] = null;
      selectedSentences[1] = null;
      updateSentenceSelectionArea(panel, selectedSentences, 0); // Reset all
      // Repaint the selection list (requires accessing the JList in the other tab)
      if (activeSentencesDialog != null) {
          // This is complex, but required to visually update the JList in the other tab
          JTabbedPane tabbedPane = findTabbedPane(activeSentencesDialog.getContentPane());
          if (tabbedPane != null && tabbedPane.getComponentCount() > 1) {
              // Assuming the similarity panel is the second component (index 1)
              Component similarityPanel = tabbedPane.getComponentAt(1);
              JList<?> list = findJList(similarityPanel);
              if (list != null) {
                  list.repaint();
              }
          }
      }
  });
  panel.add(resetBtn);

  // 3: Check Similarity Button
  AnimatedButton checkBtn = new AnimatedButton("📊 Check Similarity", PRIMARY, PRIMARY_LIGHT);
  checkBtn.setEnabled(false); // Disabled until two sentences are selected
  checkBtn.addActionListener(e -> checkSimilarityAction(selectedSentences));
  panel.add(checkBtn);
  
  // Initial State
  updateSentenceSelectionArea(panel, selectedSentences, 0);
  
  return panel;
}

/**
* Updates the display in the Sentence Selection Area and enables/disables buttons.
*/
private void updateSentenceSelectionArea(JPanel panel, SentenceDTO[] selectedSentences, int lastSelected) {
JLabel s1Label = (JLabel) panel.getComponent(0);
JLabel s2Label = (JLabel) panel.getComponent(1);
JButton resetBtn = (JButton) panel.getComponent(2);
JButton checkBtn = (JButton) panel.getComponent(3);

// Sentence 1 Update
if (selectedSentences[0] != null) {
    // Use HTML for text wrapping in the display
    String text = selectedSentences[0].getText();
    s1Label.setText("<html>Sentence 1: <span dir='rtl'>" + text + "</span></html>");
    s1Label.setForeground(new Color(46, 125, 50).darker());
    resetBtn.setEnabled(true);
} else {
    s1Label.setText("Sentence 1: (Click to Select)");
    s1Label.setForeground(new Color(46, 125, 50));
}

// Sentence 2 Update
if (selectedSentences[1] != null) {
    // Use HTML for text wrapping in the display
    String text = selectedSentences[1].getText();
    s2Label.setText("<html>Sentence 2: <span dir='rtl'>" + text + "</span></html>");
    s2Label.setForeground(new Color(1, 87, 155).darker());
    checkBtn.setEnabled(true);
} else {
    s2Label.setText("Sentence 2: (Click to Select)");
    s2Label.setForeground(new Color(1, 87, 155));
    checkBtn.setEnabled(false);
}
}

//==================== ⚖️ CORE LOGIC: Similarity Calculation ====================
private void checkSimilarityAction(SentenceDTO[] selectedSentences) {
if (selectedSentences[0] == null || selectedSentences[1] == null) {
    showWarningMessage("Please select exactly two sentences to compare.");
    return;
}

// Extract the text strings
String s1 = selectedSentences[0].getText();
String s2 = selectedSentences[1].getText();

// Call the business layer facade
// The Jaccard Char N-Gram similarity is used here
double similarity = BussinessLayerFasade.checkSimilarity(s1, s2);

// Format the result as a percentage (e.g., 0.75 -> 75.00%)
String percentage = String.format("%.2f%%", similarity);

// Create and show the result dialog
String message = String.format(
    "<html><body style='padding:10px; font-family:Segoe UI; text-align:center;'>" +
    "<h2 style='color:#3F51B5; margin-bottom:15px;'>📊 Similarity Result</h2>" +
    "<div style='border:2px solid #90CAF9; padding:15px; border-radius:10px;'>" +
    "<h1 style='color:#4CAF50; font-size:48px; margin:0;'>%s</h1>" +
    "</div>" +
    "<h3 style='color:#555; margin-top:20px;'>Compared Sentences:</h3>" +
    "<p dir='rtl' style='text-align:right; border-bottom:1px solid #EEE; padding-bottom:5px;'>%s</p>" +
    "<p dir='rtl' style='text-align:right;'>%s</p>" +
    "</body></html>",
    percentage,
    s1,
    s2
);

showInfoMessage(message, "Similarity Check Complete");
}


//--- Utility for finding components in complex UI structure (for Reset button) ---
private JTabbedPane findTabbedPane(Container container) {
  if (container instanceof JTabbedPane) {
      return (JTabbedPane) container;
  }
  for (Component c : container.getComponents()) {
      if (c instanceof Container) {
          JTabbedPane tabbedPane = findTabbedPane((Container) c);
          if (tabbedPane != null) {
              return tabbedPane;
          }
      }
  }
  return null;
}

private JList<?> findJList(Component component) {
  if (component instanceof JList) {
      return (JList<?>) component;
  }
  if (component instanceof Container) {
      for (Component c : ((Container) component).getComponents()) {
          JList<?> list = findJList(c);
          if (list != null) {
              return list;
          }
      }
  }
  return null;
}
/////////////////////////////////////////////////

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
//    public void iterationsMember1(String text) {
//  		ArrayList<String> tokens=TextProcessingUtil.analyzeAndSegmentSentence(text);
//      	ArrayList<String> lemmas=TextProcessingUtil.getLemmaList(tokens);
//      	ArrayList<String> roots=TextProcessingUtil.getRootList(lemmas);
//      	BussinessLayerFasade.addRoots(roots);
//      	BussinessLayerFasade.addLemmas(lemmas);
//      	BussinessLayerFasade.addToken(text, tokens);
//  	}
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
            String chapterName=selectedChapter.getChapterName();
            int sentenceNumber=selectedSentence.getSentenceNumber();
            boolean success = BussinessLayerFasade.updateSenetence(
            		chapterName,
            		sentenceNumber,
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
 

    /**
     * 🧩 Token Analysis Panel - Displays tokens for a specific sentence
     */
  private void openTokenAnalysisPanel(SentenceDTO sentence) {

    JDialog tokenDialog = createModernDialog(
            "Token Analysis - Sentence #" + sentence.getSentenceNumber(),
            900, 
            600
    );


    JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
    contentPanel.setBackground(BACKGROUND);
    contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

    // ===== 1. Sentence Header Card =====
    JPanel headerCard = createModernCard();
    headerCard.setLayout(new BorderLayout(15, 15));
    headerCard.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Sentence Analysis");
    titleLabel.setFont(new Font("Segoe UI Black", Font.PLAIN, 20));
    titleLabel.setForeground(PRIMARY_DARK);

    JLabel sentenceTextLabel = new JLabel(
        "<html><div style='text-align: right; width: 600px;'>" +
        sentence.getText() + "</div></html>"
    );
    sentenceTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    sentenceTextLabel.setForeground(TEXT_PRIMARY);
    sentenceTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    sentenceTextLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    JLabel translationLabel = new JLabel(
        "<html><i>Translation: " +
        (sentence.getTranslation() != null ? sentence.getTranslation() : "N/A") +
        "</i></html>"
    );
    translationLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
    translationLabel.setForeground(TEXT_SECONDARY);
    translationLabel.setHorizontalAlignment(SwingConstants.RIGHT);

    JPanel textContainer = new JPanel(new GridLayout(2, 1, 5, 5));
    textContainer.setOpaque(false);
    textContainer.add(sentenceTextLabel);
    textContainer.add(translationLabel);

    headerCard.add(titleLabel, BorderLayout.NORTH);
    headerCard.add(textContainer, BorderLayout.CENTER);
    contentPanel.add(headerCard, BorderLayout.NORTH);

    // ===== 2. Tokens Flow Panel =====
    JPanel flowWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
    flowWrapper.setBackground(BACKGROUND);
    flowWrapper.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    String[] tokens = sentence.getText().split("\\s+"); 
    for (String tokenText : tokens) {
        JPanel tokenChip = createTokenChip(tokenText);
        flowWrapper.add(tokenChip);
    }

    JPanel container = new JPanel(new BorderLayout());
    container.setBackground(BACKGROUND);
    container.add(flowWrapper, BorderLayout.NORTH);

    JScrollPane scrollPane = new JScrollPane(container);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(BACKGROUND);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    contentPanel.add(scrollPane, BorderLayout.CENTER);

    // ===== 3. Footer =====
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    footerPanel.setOpaque(false);

    AnimatedButton closeBtn = new AnimatedButton("Close Analysis", DANGER, DANGER_HOVER);
    closeBtn.addActionListener(e -> tokenDialog.dispose());
    footerPanel.add(closeBtn);

    contentPanel.add(footerPanel, BorderLayout.SOUTH);

    tokenDialog.add(contentPanel);

    tokenDialog.setVisible(true);
  }


    /**
     * 🎨 Helper to create a visual "Chip" for a single token
     */
    private JPanel createTokenChip(String tokenText) {
        JPanel chip = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient Background for Chip
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 
                                                    0, getHeight(), new Color(225, 245, 254)); // Very light blue
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                
                // Border
                g2d.setColor(new Color(129, 212, 250));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        chip.setOpaque(false);
        chip.setPreferredSize(new Dimension(130, 85)); 
        chip.setBorder(new EmptyBorder(8, 8, 8, 8));
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Token Word
        JLabel wordLabel = new JLabel(tokenText, SwingConstants.CENTER);
        wordLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        wordLabel.setForeground(new Color(1, 87, 155)); // Deep Blue

        // Small "Click for details" hint
        JLabel infoLabel = new JLabel("<html><center><span style='font-size:9px; color:#90A4AE'>Click for Info</span></center></html>", SwingConstants.CENTER);
        
        chip.add(wordLabel, BorderLayout.CENTER);
        chip.add(infoLabel, BorderLayout.SOUTH);

        // ✅ CLICK EVENT - Opens the Detail Dialog
        chip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Hover Effect: Darker Border + Shadow hint
                chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2, true),
                    new EmptyBorder(6, 6, 6, 6)
                ));
                chip.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Reset Border
                chip.setBorder(new EmptyBorder(8, 8, 8, 8));
                chip.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 🚀 Trigger the detail window
                showTokenDetailDialog(tokenText);
            }
        });

        return chip;
    }

    /**
     * 🔍 Shows a detailed modal dialog with Root, Lemma, Stem, and Prefix information, 
     * fetching segmented data via the new BLF method.
     */
    private void showTokenDetailDialog(String tokenText) {
        JDialog dialog = createModernDialog("Morphological Analysis", 500, 550); // Increased height
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // --- Data Fetching Logic (CRITICAL) ---
        SegmentedTokenDTO analysis = BussinessLayerFasade.getSegmentedTokenDetails(tokenText);
        
        // --- Determine display values ---
        String rootText = (analysis != null && analysis.getRoot() != null) ? analysis.getRoot() : "Not Found"; 
        String lemmaText = (analysis != null && analysis.getLemma() != null) ? analysis.getLemma() : "Not Found"; 
        String stemText = (analysis != null && analysis.getStem() != null) ? analysis.getStem() : "Not Found"; 
        String prefixText = (analysis != null && analysis.getPrefix() != null) ? analysis.getPrefix() : "None"; 
        
        // ===== 1. HEADER (The Token) =====
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 98, 255), 
                                                    getWidth(), getHeight(), new Color(33, 150, 243));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(500, 120));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tokenLabel = new JLabel(tokenText, SwingConstants.CENTER);
        tokenLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        tokenLabel.setForeground(Color.WHITE);
        tokenLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        headerPanel.add(tokenLabel, BorderLayout.CENTER);

        JLabel subHeader = new JLabel("Selected Token", SwingConstants.CENTER);
        subHeader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeader.setForeground(new Color(220, 220, 220));
        headerPanel.add(subHeader, BorderLayout.SOUTH);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== 2. DATA BODY (Full Analysis) =====
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 12, 12)); // 4 rows
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Add Info Cards
        infoPanel.add(createDetailRow("ROOT (جذر)", rootText, new Color(232, 245, 233), new Color(46, 125, 50)));
        infoPanel.add(createDetailRow("LEMMA (المجرد)", lemmaText, new Color(255, 243, 224), new Color(239, 108, 0)));
        infoPanel.add(createDetailRow("STEM (الساق)", stemText, new Color(225, 245, 254), new Color(1, 87, 155)));
        infoPanel.add(createDetailRow("PREFIX (البادئة)", prefixText, new Color(243, 230, 255), new Color(106, 27, 154)));


        contentPanel.add(infoPanel, BorderLayout.CENTER);

        // ===== 3. CLOSE BUTTON =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        AnimatedButton closeBtn = new AnimatedButton("Close", DANGER, DANGER_HOVER);
        closeBtn.setPreferredSize(new Dimension(150, 45));
        closeBtn.addActionListener(e -> dialog.dispose());
        footer.add(closeBtn);
        
        contentPanel.add(footer, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    /**
     * 🎨 Helper to create a styled row in the detail dialog
     */
    private JPanel createDetailRow(String label, String value, Color bgColor, Color textColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(bgColor);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1, true),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel title = new JLabel(label);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(textColor.darker());
        
        JLabel content = new JLabel(value, SwingConstants.RIGHT);
        content.setFont(new Font("Segoe UI", Font.BOLD, 20));
        content.setForeground(textColor);
        
        row.add(title, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);
        return row;
    }

    /**
     * Refreshes the Books tab list (with bullet points)
     */
 // Store references to open dialogs
    

    /**
     * Called when opening Chapters dialog
     */
   

    /**
     * Called when opening Sentences dialog
     */
    
    
    /**
     * Refreshes only the Books tab list
     */
    private void refreshBooksPanel() {
        if (bookListModel != null) {
            loadBooks(); // Get latest books from database

            // Add bullet to each existing item
            for (int i = 0; i < bookListModel.size(); i++) {
                String title = bookListModel.get(i);
                bookListModel.set(i, "• " + title);
            }

            bookList.repaint();
        }
    }

    /**
     * Refreshes the chapter list inside the currently open Chapters dialog (keeps dialog open)
     */
    private void refreshOpenChaptersDialog() {
        if (activeChaptersDialog != null && activeChaptersDialog.isShowing() && selectedBook != null) {
            // Find the JList inside the dialog and refresh it
            findAndRefreshChapterList(activeChaptersDialog, selectedBook.getTitle());
        }
    }

    /**
     * Refreshes the sentence list inside the currently open Sentences dialog (keeps dialog open)
     */
    private void refreshOpenSentencesDialog() {
        if (activeSentencesDialog != null 
            && activeSentencesDialog.isShowing() 
            && activeSentencesDialog.isVisible()
            && selectedChapter != null) {
        	System.out.println("calling refresh");
            refreshSentenceListInDialog(activeSentencesDialog, selectedChapter.getChapterName());
        }
    }
    
    private void findAndRefreshChapterList(JDialog dialog, String bookTitle) {
        Component[] components = dialog.getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                refreshChapterListInPanel((JPanel) c, bookTitle);
                break;
            }
        }
    }

    private void findAndRefreshSentenceList(JDialog dialog, String chapterName) {
        Component[] components = dialog.getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                refreshSentenceListInPanel((JPanel) c, chapterName);
                break;
            }
        }
    }

    private void refreshSentenceListInPanel(JPanel panel, String chapterName) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                if (scroll.getViewport().getView() instanceof JList) {
                    @SuppressWarnings("unchecked")
                    JList<String> list = (JList<String>) scroll.getViewport().getView();

                    ArrayList<SentenceDTO> sentences = BussinessLayerFasade.retrieveSentence(chapterName);
                    DefaultListModel<String> model = new DefaultListModel<>();

                    if (sentences != null) {
                        for (int i = 0; i < sentences.size(); i++) {
                            SentenceDTO s = sentences.get(i);
                            model.addElement(s.getText());
                        }
                    }

                    list.setModel(model);
                    list.revalidate();
                    list.repaint();
                    return;
                }
            } else if (comp instanceof JPanel) {
                refreshSentenceListInPanel((JPanel) comp, chapterName);
            }
        }
    }
    private void refreshSentenceListInDialog(JDialog dialog, String chapterName) {
        refreshJListInDialog(dialog, () -> {
            // Load fresh sentences from database
            ArrayList<SentenceDTO> sentences = BussinessLayerFasade.retrieveSentence(chapterName);
            DefaultListModel<String> model = new DefaultListModel<>();

            if (sentences != null && !sentences.isEmpty()) {
                for (SentenceDTO s : sentences) {
                    model.addElement(s.getText());  // or however you display it
                }
            } else {
                model.addElement("No sentences in this chapter");
            }
            return model;
        });
    }
    private void refreshJListInDialog(JDialog dialog, java.util.function.Supplier<DefaultListModel<String>> modelSupplier) {
        java.util.Queue<Component> queue = new java.util.ArrayDeque<>();
        queue.add(dialog.getContentPane());

        while (!queue.isEmpty()) {
            Component c = queue.poll();

            if (c instanceof JScrollPane) {
                Component view = ((JScrollPane) c).getViewport().getView();
                if (view instanceof JList) {
                    @SuppressWarnings("unchecked")
                    JList<String> list = (JList<String>) view;
                    list.setModel(modelSupplier.get());
                    list.revalidate();
                    list.repaint();
                    return; // Done — we found and updated the list
                }
            }

            if (c instanceof Container) {
                for (Component child : ((Container) c).getComponents()) {
                    queue.add(child);
                }
            }
        }
    }
    private void refreshChapterListInPanel(JPanel panel, String bookTitle) {
        // Recursively search for JList in JScrollPane
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                if (scroll.getViewport().getView() instanceof JList) {
                    @SuppressWarnings("unchecked")
                    JList<String> list = (JList<String>) scroll.getViewport().getView();
                    DefaultListModel<String> model = new DefaultListModel<>();

                    ArrayList<ChapterDTO> chapters = BussinessLayerFasade.retrieveChapters(bookTitle);
                    if (chapters != null) {
                        for (ChapterDTO ch : chapters) {
                            model.addElement(ch.getChapterName());
                        }
                    } else {
                        model.addElement("No chapters available");
                    }

                    list.setModel(model);
                    list.revalidate();
                    list.repaint();
                    return;
                }
            } else if (comp instanceof JPanel) {
                refreshChapterListInPanel((JPanel) comp, bookTitle); // recursive
            }
        }
    }
 // Close all child dialogs safely
    private void safelyCloseAllChildDialogs() {
        safelyCloseChaptersDialog();
        safelyCloseSentencesDialog();
    }

    private void safelyCloseChaptersDialog() {
        if (activeChaptersDialog != null && activeChaptersDialog.isShowing()) {
            activeChaptersDialog.dispose();
            activeChaptersDialog = null;
        }
    }

    private void safelyCloseSentencesDialog() {
        if (activeSentencesDialog != null && activeSentencesDialog.isShowing()) {
            activeSentencesDialog.dispose();
            activeSentencesDialog = null;
        }
    }

    @Override
    public void autoRefresh(String updatedRequired) {
        // This method is called by the Observer pattern whenever data changes in the business layer
        // updatedRequired will be one of: "book", "chapter", "sentence" (or possibly others in the future)

        SwingUtilities.invokeLater(() -> {  // Always update UI on EDT
        	String type = updatedRequired.toLowerCase().trim();

            switch (type) {
                case "book":
                case "books":
                    refreshBooksPanel();           // Only refresh main Books tab

                    safelyCloseAllChildDialogs();     // Close chapters/sentences dialogs (data may be invalid)
                    break;

                case "chapter":
                case "chapters":
                    refreshBooksPanel();           // Book list might need visual update
                    refreshOpenChaptersDialog();   // Refresh chapter list if dialog is open
                    safelyCloseSentencesDialog();  // Sentences may belong to deleted/renamed chapter
                    break;

                case "sentence":
                case "sentences":
                    // MOST IMPORTANT: Only refresh the sentence list — keep dialog open!
                    refreshOpenSentencesDialog();
                    break;

                default:
                    // Safe fallback
                    refreshBooksPanel();
                    break;
            }
        });
    }


	
    
}

