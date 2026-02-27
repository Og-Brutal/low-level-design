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
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.AuthorDTO;
import com.arabicprose.dto.BookDTO;
import com.arabicprose.dto.ChapterDTO;
import com.arabicprose.dto.SentenceDTO;

public class BookPL extends JPanel implements EventListener,IAuthorChangeObserver  {

    private IBusinessLayerFacade facade;
    private JPanel cardsContainer;
    private JButton addBtn, searchBtn;
    private List<BookDTO> currentBooks;
    private IAuthorChangeObserver authorChangeObserver;

    // --- Enhanced Color Palette (Same as AuthorPL) ---
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
    private final Color BTN_REFRESH_BG = new Color(255, 255, 255);
    private final Color BTN_REFRESH_HOVER = new Color(241, 245, 249);
    private final Color BTN_SEARCH_BG = new Color(255, 255, 255);
    private final Color BTN_SEARCH_HOVER = new Color(241, 245, 249);

    // Accent Colors
    private final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color INFO_COLOR = new Color(59, 130, 246);
    private final Color EDIT_COLOR = new Color(59, 130, 246);
    private final Color DELETE_COLOR = new Color(239, 68, 68);
 // in BookPL class
    public void setAuthorChangeObserver(IAuthorChangeObserver observer) {
        this.authorChangeObserver = observer;
    }

    public BookPL(IBusinessLayerFacade facade) {
        this.facade = facade;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        initializeComponents();
        setupLayout();
        loadBooks();
        setupEventListeners();
    }

    private void initializeComponents() {
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        cardsContainer.setBackground(BG_COLOR);
        cardsContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        cardsContainer.setBorder(new EmptyBorder(30, 30, 30, 30));

        addBtn = new RoundedButton("Add New Book", BTN_ADD_BG, Color.WHITE, BTN_ADD_HOVER);
        addBtn.setIcon(createAddBookIcon1());
        addBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        addBtn.setIconTextGap(8);

        searchBtn = new RoundedButton("Search Books", BTN_SEARCH_BG, TEXT_DARK, BTN_SEARCH_HOVER);
        searchBtn.setIcon(createSearchIcon());
        searchBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        searchBtn.setIconTextGap(8);
    }
   

    // Create add book icon for button
    private Icon createAddBookIcon1() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);

                int size = 16;

                // Draw book with plus
                g2d.setStroke(new BasicStroke(1.5f));
                // Book cover
                g2d.drawRoundRect(x + 2, y + 2, size - 6, size - 4, 2, 2);
                // Book spine
                g2d.fillRect(x + 2, y + 2, 3, size - 4);
                // Pages
                for (int i = 0; i < 2; i++) {
                    g2d.drawLine(x + 6 + i, y + 4, x + 6 + i, y + size - 2);
                }

                // Plus sign
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int plusSize = 6;
                int plusX = x + size - 2;
                int plusY = y + size / 2;
                g2d.drawLine(plusX - plusSize/2, plusY, plusX + plusSize/2, plusY);
                g2d.drawLine(plusX, plusY - plusSize/2, plusX, plusY + plusSize/2);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 20; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }
    @Override
    public void onAuthorDeleted(int authorId) {
        // Remove books by this author from currentBooks list
        if (currentBooks != null) {
            currentBooks.removeIf(book -> book.getAuthorId() == authorId);
        }
        // Refresh the UI - USE EXISTING METHOD
        refreshBooks();
    }
    
    @Override
    public void onAuthorAdded() {
        // Refresh books to show any new books from new authors - USE EXISTING METHOD
        refreshBooks();
    }
    
    @Override
    public void onAuthorUpdated() {
        // Refresh to update author names - USE EXISTING METHOD
        refreshBooks();
    }
    
    // Create search icon
    private Icon createSearchIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_DARK);

                int size = 16;

                // Draw magnifying glass
                g2d.setStroke(new BasicStroke(2f));
                // Glass circle
                g2d.drawOval(x + 2, y + 2, size - 8, size - 8);
                // Handle
                g2d.drawLine(x + size - 4, y + size - 4, x + size - 1, y + size - 1);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private void setupLayout() {
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
        headerPanel.setPreferredSize(new Dimension(getWidth(), 130));
        headerPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JLabel titleLabel = new JLabel("Books Gallery");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel actionPanel = new JPanel(new BorderLayout(20, 0));
        actionPanel.setOpaque(false);

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftActions.setOpaque(false);
        leftActions.add(searchBtn);

        actionPanel.add(leftActions, BorderLayout.WEST);
        actionPanel.add(addBtn, BorderLayout.EAST);
        actionPanel.add(titleLabel, BorderLayout.CENTER);

        headerPanel.add(actionPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

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
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }

            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadBooks() {
        cardsContainer.removeAll();
        JLabel loadingLabel = new JLabel("⏳ Loading books...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loadingLabel.setForeground(TEXT_LIGHT);
        loadingLabel.setBorder(new EmptyBorder(50, 50, 50, 50));
        cardsContainer.add(loadingLabel);
        cardsContainer.revalidate();
        cardsContainer.repaint();

        SwingUtilities.invokeLater(() -> {
            try {
                currentBooks = facade.getAllBooks();
                cardsContainer.removeAll();

                if (currentBooks.isEmpty()) {
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                    emptyPanel.setOpaque(false);
                    emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel emptyIcon = new JLabel("📖");
                    emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 64));
                    emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyIcon.setBorder(new EmptyBorder(20, 0, 20, 0));

                    JLabel emptyLabel = new JLabel("No books found");
                    emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    emptyLabel.setForeground(TEXT_DARK);
                    emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel emptySubLabel = new JLabel("Click 'Add New Book' to begin your library");
                    emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    emptySubLabel.setForeground(TEXT_LIGHT);
                    emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    emptyPanel.add(emptyIcon);
                    emptyPanel.add(emptyLabel);
                    emptyPanel.add(emptySubLabel);
                    cardsContainer.add(emptyPanel);
                } else {
                    for (BookDTO book : currentBooks) {
                        String authorName = "Unknown Author";
                        int chaptersCount = 0;
                        try {
                            AuthorDTO author = facade.getAuthorById(book.getAuthorId());
                            if (author != null) {
                                authorName = author.getName();
                            }
                            List<ChapterDTO> chapters = facade.getChaptersByBookId(book.getBookId());
                            chaptersCount = chapters != null ? chapters.size() : 0;
                        } catch (Exception e) {
                            System.err.println("Error loading book details: " + e.getMessage());
                        }
                        cardsContainer.add(new BookCard(book, authorName, chaptersCount));
                    }
                }
            } catch (SQLException ex) {
                cardsContainer.removeAll();
                showErrorDialog("Error loading books: " + ex.getMessage());
                ex.printStackTrace();
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        });
    }

    // Auto-refresh method
    private void refreshBooks() {
        loadBooks();
    }

    private void openBookInputDialog(BookDTO bookToEdit) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog formDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null,
            bookToEdit == null ? "New Book" : "Edit Book", true);
        formDialog.setSize(500, 450);
        formDialog.setLocationRelativeTo(this);

        JPanel content = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));
        content.setOpaque(false);

        // Title with icon
        JLabel titleLabel = new JLabel(bookToEdit == null ? "Add New Book" : "Edit Book");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_START);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        titleLabel.setIcon(bookToEdit == null ? createAddBookIcon1() : createEditBookIcon());
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(10);

        // Book Title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        titlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel bookTitleLbl = new JLabel("Book Title:");
        bookTitleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookTitleLbl.setForeground(TEXT_DARK);

        JTextField titleField = new JTextField(bookToEdit != null ? bookToEdit.getTitle() : "");
        styleField(titleField);

        titlePanel.add(bookTitleLbl, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);

        // Era field
        JPanel eraPanel = new JPanel(new BorderLayout());
        eraPanel.setOpaque(false);
        eraPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        eraPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel eraLbl = new JLabel("Era:");
        eraLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        eraLbl.setForeground(TEXT_DARK);

        JTextField eraField = new JTextField(bookToEdit != null ? bookToEdit.getEra() : "");
        styleField(eraField);

        eraPanel.add(eraLbl, BorderLayout.NORTH);
        eraPanel.add(eraField, BorderLayout.CENTER);

        // Author combo box
        JPanel authorPanel = new JPanel(new BorderLayout());
        authorPanel.setOpaque(false);
        authorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        authorPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JLabel authorLbl = new JLabel("Author:");
        authorLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        authorLbl.setForeground(TEXT_DARK);

        JComboBox<AuthorDTO> authorCombo = new JComboBox<>();
        styleComboBox(authorCombo);
        populateAuthorsCombo(authorCombo);

        if (bookToEdit != null && authorCombo.getItemCount() > 0) {
            for (int i = 0; i < authorCombo.getItemCount(); i++) {
                AuthorDTO a = authorCombo.getItemAt(i);
                if (a.getAuthorId() == bookToEdit.getAuthorId()) {
                    authorCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        authorPanel.add(authorLbl, BorderLayout.NORTH);
        authorPanel.add(authorCombo, BorderLayout.CENTER);

        // Save button with icon
        JButton saveBtn = new RoundedButton("Save Book", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
        saveBtn.setIcon(createSaveTickIcon());
        saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveBtn.setIconTextGap(8);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setBorder(new EmptyBorder(12, 30, 12, 30));

        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                titleField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
                return;
            }

            AuthorDTO selectedAuthor = (AuthorDTO) authorCombo.getSelectedItem();
            if (selectedAuthor == null || selectedAuthor.getAuthorId() <= 0) {
                showErrorDialog("Please select a valid author.");
                return;
            }

            try {
                if (bookToEdit == null) {
                    BookDTO bookDTO = new BookDTO();
                    bookDTO.setTitle(title);
                    bookDTO.setEra(eraField.getText().trim());
                    bookDTO.setAuthorId(selectedAuthor.getAuthorId());
                    facade.addBook(bookDTO);
                    showSuccessMessage("Book added successfully!");
                } else {
                    bookToEdit.setTitle(title);
                    bookToEdit.setEra(eraField.getText().trim());
                    bookToEdit.setAuthorId(selectedAuthor.getAuthorId());
                    facade.updateBook(bookToEdit);
                    showSuccessMessage("Book updated successfully!");
                }
                refreshBooks(); // Auto-refresh after save/update
                if (authorChangeObserver != null) {
                    authorChangeObserver.onAuthorUpdated(); 
                }
                formDialog.dispose();
            } catch (SQLException | IllegalArgumentException ex) {
                showErrorDialog("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(titlePanel);
        content.add(eraPanel);
        content.add(authorPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(saveBtn);

        formDialog.add(content);
        formDialog.setVisible(true);
    }

    private Icon createSaveTickIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);

                int size = 16;
                int centerX = x + size / 2;
                int centerY = y + size / 2;

                // Draw checkmark
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(centerX - 5, centerY, centerX - 2, centerY + 3);
                g2d.drawLine(centerX - 2, centerY + 3, centerX + 4, centerY - 3);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // Create edit book icon
    private Icon createEditBookIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_START);

                int size = 24;

                // Draw book
                g2d.setStroke(new BasicStroke(2f));
                // Book cover
                g2d.drawRoundRect(x + 5, y + 3, size - 10, size - 6, 3, 3);
                // Book spine
                g2d.fillRect(x + 5, y + 3, 4, size - 6);
                // Pages
                g2d.setColor(new Color(HEADER_START.getRed(), HEADER_START.getGreen(), HEADER_START.getBlue(), 100));
                for (int i = 0; i < 3; i++) {
                    g2d.drawLine(x + 10 + i, y + 5, x + 10 + i, y + size - 4);
                }

                // Pencil icon overlay
                g2d.setColor(HEADER_START);
                g2d.setStroke(new BasicStroke(1.5f));
                // Pencil body
                g2d.drawLine(x + size - 5, y + 5, x + size - 2, y + 2);
                // Pencil tip
                g2d.drawLine(x + size - 2, y + 2, x + size, y + 4);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 28; }

            @Override
            public int getIconHeight() { return 24; }
        };
    }

    // Style combo box
    private void styleComboBox(JComboBox<AuthorDTO> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(250, 250, 250));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AuthorDTO) {
                    setText(((AuthorDTO) value).getName());
                }
                return this;
            }
        });
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        field.setBackground(new Color(250, 250, 250));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void populateAuthorsCombo(JComboBox<AuthorDTO> combo) {
        try {
            List<AuthorDTO> authors = facade.getAllAuthors();
            combo.removeAllItems();
            if (authors.isEmpty()) {
                combo.addItem(new AuthorDTO(0, "No authors available", ""));
                combo.setEnabled(false);
            } else {
                combo.setEnabled(true);
                for (AuthorDTO a : authors) {
                    combo.addItem(a);
                }
            }
            combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            combo.setBackground(new Color(250, 250, 250));
            combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof AuthorDTO) {
                        AuthorDTO author = (AuthorDTO) value;
                        setText(author.getName());
                    }
                    setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    return this;
                }
            });
        } catch (SQLException ex) {
            combo.removeAllItems();
            combo.addItem(new AuthorDTO(0, "Error loading authors", ""));
            combo.setEnabled(false);
            System.err.println("Error loading authors: " + ex.getMessage());
        }
    }

    private void showBookChaptersDialog(BookDTO book) {
        // Create a new JFrame for full-screen chapters view
        JFrame chaptersFrame = new JFrame("Chapters: " + book.getTitle());
        chaptersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chaptersFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        chaptersFrame.setLocationRelativeTo(null);
        
        // Create the chapters panel
        BookChaptersPanel chaptersPanel = new BookChaptersPanel(book, chaptersFrame);
        chaptersFrame.add(chaptersPanel);
        
        chaptersFrame.setVisible(true);
    }

    private void showSearchDialog() {
        // Simple search implementation
        String searchTerm = JOptionPane.showInputDialog(this, "Enter book title to search:", "Search Books", JOptionPane.QUESTION_MESSAGE);
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<BookDTO> results = facade.searchBooksByTitle(searchTerm.trim());
                if (results.isEmpty()) {
                    showInfoDialog("No books found matching: " + searchTerm, "Search Results");
                } else {
                    showInfoDialog("Found " + results.size() + " book(s) matching: " + searchTerm, "Search Results");
                }
            } catch (SQLException ex) {
                showErrorDialog("Error searching books: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "✅ Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "❌ Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void setupEventListeners() {
        addBtn.addActionListener(e -> openBookInputDialog(null));
        searchBtn.addActionListener(e -> showSearchDialog());
    }

    // RoundedButton class (same as AuthorPL)
    private class RoundedButton extends JButton {
        private Color baseColor;
        private Color hoverColor;
        private boolean isHovered = false;
        private boolean hasShadow = true;

        public RoundedButton(String text, Color bg, Color fg, Color hover) {
            super(text);
            this.baseColor = bg;
            this.hoverColor = hover;
            this.hasShadow = !bg.equals(Color.WHITE);

            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 30, 12, 30));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                public void mousePressed(MouseEvent e) { setLocation(getX(), getY() + 1); }
                public void mouseReleased(MouseEvent e) { setLocation(getX(), getY() - 1); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentColor = isHovered ? hoverColor : baseColor;
            if (hasShadow && isHovered) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 35, 35);
            }
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);

            if (baseColor.equals(Color.WHITE)) {
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // BookCard class with Edit/Delete buttons
    private class BookCard extends JPanel {
        private boolean isHovered = false;
        private final Color[] AVATAR_COLORS = {
            new Color(239, 68, 68), new Color(245, 158, 11), new Color(34, 197, 94),
            new Color(59, 130, 246), new Color(139, 92, 246), new Color(236, 72, 153),
            new Color(14, 165, 233), new Color(168, 85, 247)
        };
        private final BookDTO book;
        private final String authorName;
        private final int chaptersCount;

        public BookCard(BookDTO book, String authorName, int chaptersCount) {
            this.book = book;
            this.authorName = authorName;
            this.chaptersCount = chaptersCount;

            this.setPreferredSize(new Dimension(240, 320));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setBackground(CARD_BG);
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.setBorder(new EmptyBorder(25, 20, 25, 20));
            this.setOpaque(false);

            JPanel avatarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color avatarColor = AVATAR_COLORS[Math.abs(book.getTitle().hashCode()) % AVATAR_COLORS.length];
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 32, 2, 64, 64));
                    g2.setColor(avatarColor);
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 30, 0, 60, 60));
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 25, 5, 50, 25));
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    String initial = book.getTitle().isEmpty() ? "?" : book.getTitle().substring(0, 1).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2, ((60 - fm.getHeight()) / 2) + fm.getAscent());
                }
            };
            avatarPanel.setPreferredSize(new Dimension(200, 75));
            avatarPanel.setMaximumSize(new Dimension(200, 75));
            avatarPanel.setOpaque(false);

            JLabel titleLabel = new JLabel(book.getTitle());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(TEXT_DARK);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel authorLabel = new JLabel(authorName);
            authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            authorLabel.setForeground(TEXT_LIGHT);
            authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            authorLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Handle era display - same logic, just centered
            String eraText = (book.getEra() != null && !book.getEra().isEmpty())
                           ? book.getEra()
                           : "No era specified";
            JLabel eraLabel = new JLabel("<html><div style='text-align:center; width:200px;'>" + eraText + "</div></html>");
            eraLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            eraLabel.setForeground(TEXT_LIGHT);
            eraLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            eraLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel statsLabel = new JLabel(chaptersCount + " Chapters");
            statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            statsLabel.setForeground(HEADER_START);
            statsLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER_START, 2, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statsLabel.setOpaque(true);
            statsLabel.setBackground(new Color(HEADER_START.getRed(), HEADER_START.getGreen(), HEADER_START.getBlue(), 10));

            // Action buttons panel - same functionality, with custom icons
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            actionPanel.setOpaque(false);
            actionPanel.setMaximumSize(new Dimension(200, 40));

            JButton editBtn = createIconButton(createSmallEditIcon(), INFO_COLOR, "Edit Book");
            JButton deleteBtn = createIconButton(createSmallDeleteIcon(), ERROR_COLOR, "Delete Book");

            editBtn.addActionListener(e -> {
                openBookInputDialog(book);
            });

            deleteBtn.addActionListener(e -> {
                deleteBook(book);
            });

            actionPanel.add(editBtn);
            actionPanel.add(deleteBtn);

            add(avatarPanel);
            add(Box.createVerticalStrut(8));
            add(titleLabel);
            add(Box.createVerticalStrut(5));
            add(authorLabel);
            add(Box.createVerticalStrut(5));
            add(eraLabel);
            add(Box.createVerticalGlue());
            add(statsLabel);
            add(Box.createVerticalStrut(10));
            add(actionPanel);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        showBookChaptersDialog(book);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cornerRadius = 20;
            int shadowOffset = isHovered ? 8 : 4;
            Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;

            g2.setColor(shadowColor);
            g2.fill(new RoundRectangle2D.Double(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius));

            g2.setColor(isHovered ? new Color(250, 250, 250) : Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            g2.setColor(isHovered ? HEADER_START : new Color(229, 231, 235));
            g2.setStroke(new BasicStroke(isHovered ? 2.5f : 1.5f));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
        }
    }

    // Helper method to create icon buttons - updated to accept Icon instead of String
    private JButton createIconButton(Icon icon, Color color, String tooltip) {
        JButton button = new JButton(icon);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(35, 35));
        button.setToolTipText(tooltip);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // Create small edit icon
    private Icon createSmallEditIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);

                int size = 16;

                // Draw pencil icon
                g2d.setStroke(new BasicStroke(1.5f));
                // Pencil body
                g2d.drawLine(x + 3, y + size - 4, x + size - 3, y + 4);
                // Pencil tip
                g2d.drawLine(x + size - 3, y + 4, x + size - 1, y + 2);
                // Pencil eraser
                g2d.fillRect(x + 2, y + size - 5, 3, 3);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // Create small delete icon
    private Icon createSmallDeleteIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);

                int size = 16;
                int centerX = x + size / 2;
                int centerY = y + size / 2;

                // Draw trash can
                g2d.setStroke(new BasicStroke(1.5f));
                // Can body
                g2d.drawRect(centerX - 5, centerY - 3, 10, 8);
                // Lid
                g2d.drawLine(centerX - 6, centerY - 3, centerX + 6, centerY - 3);
                // Handle
                g2d.drawLine(centerX - 2, centerY - 5, centerX - 2, centerY - 3);
                g2d.drawLine(centerX + 2, centerY - 5, centerX + 2, centerY - 3);
                g2d.drawLine(centerX - 2, centerY - 5, centerX + 2, centerY - 5);

                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // Method to delete book
    private void deleteBook(BookDTO book) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete '" + book.getTitle() + "'?\nThis will also delete all chapters and sentences.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facade.deleteBook(book.getBookId());
                showSuccessMessage("Book deleted successfully!");
                refreshBooks(); // Auto-refresh after deletion
                if (authorChangeObserver != null) {
                    authorChangeObserver.onAuthorUpdated(); 
                }
            } catch (SQLException ex) {
                showErrorDialog("Error deleting book: " + ex.getMessage());
            }
        }
    }

    // BookChaptersPanel class for full-screen chapters display
    private class BookChaptersPanel extends JPanel {
        private final BookDTO book;
        private final JFrame parentFrame;
        private JPanel cardsContainer;
        private JButton closeBtn, addChapterBtn;

        public BookChaptersPanel(BookDTO book, JFrame parentFrame) {
            this.book = book;
            this.parentFrame = parentFrame;
            initializePanel();
            loadChapters();
        }

        private void initializePanel() {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);

            // Header
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
                }
            };
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setPreferredSize(new Dimension(getWidth(), 120));
            headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

            JLabel titleLabel = new JLabel("Chapters In: " + book.getTitle());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setIcon(createChaptersDialogIcon());
            titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            titleLabel.setIconTextGap(15);

            // Action buttons
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            actionPanel.setOpaque(false);

            addChapterBtn = new RoundedButton("Add Chapter", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
            addChapterBtn.setIcon(createAddChapterIcon());
            addChapterBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            addChapterBtn.setIconTextGap(8);

            closeBtn = new RoundedButton("Close", BTN_REFRESH_BG, TEXT_DARK, BTN_REFRESH_HOVER);
            closeBtn.setIcon(createCloseIcon());
            closeBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            closeBtn.setIconTextGap(8);

            closeBtn.addActionListener(e -> {
                parentFrame.dispose();
            });

            addChapterBtn.addActionListener(e -> openAddChapterDialog());

            actionPanel.add(addChapterBtn);
            actionPanel.add(closeBtn);

            headerPanel.add(titleLabel, BorderLayout.CENTER);
            headerPanel.add(actionPanel, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // Chapters container
            cardsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
            cardsContainer.setBackground(BG_COLOR);
            cardsContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            cardsContainer.setBorder(new EmptyBorder(30, 30, 30, 30));

            JScrollPane scrollPane = new JScrollPane(cardsContainer);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            add(scrollPane, BorderLayout.CENTER);
        }

        // Auto-refresh method for chapters
        private void refreshChapters() {
            loadChapters();
        }

        // Create chapters dialog icon for header
        private Icon createChaptersDialogIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);

                    int size = 32;

                    // Draw stacked books icon
                    g2d.setStroke(new BasicStroke(2f));

                    // First book
                    g2d.drawRoundRect(x + 5, y + 8, size - 15, size - 13, 3, 3);
                    g2d.fillRect(x + 5, y + 8, 4, size - 13);

                    // Second book (slightly offset)
                    g2d.drawRoundRect(x + 8, y + 5, size - 15, size - 13, 3, 3);
                    g2d.fillRect(x + 8, y + 5, 4, size - 13);

                    // Third book (slightly offset)
                    g2d.drawRoundRect(x + 11, y + 2, size - 15, size - 13, 3, 3);
                    g2d.fillRect(x + 11, y + 2, 4, size - 13);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 32; }

                @Override
                public int getIconHeight() { return 32; }
            };
        }

        // Create add chapter icon for button
        private Icon createAddChapterIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);

                    int size = 16;

                    // Draw book with plus
                    g2d.setStroke(new BasicStroke(1.5f));
                    // Book cover
                    g2d.drawRoundRect(x + 2, y + 2, size - 6, size - 4, 2, 2);
                    // Book spine
                    g2d.fillRect(x + 2, y + 2, 3, size - 4);
                    // Pages
                    for (int i = 0; i < 2; i++) {
                        g2d.drawLine(x + 6 + i, y + 4, x + 6 + i, y + size - 2);
                    }

                    // Plus sign
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int plusSize = 6;
                    int plusX = x + size - 2;
                    int plusY = y + size / 2;
                    g2d.drawLine(plusX - plusSize/2, plusY, plusX + plusSize/2, plusY);
                    g2d.drawLine(plusX, plusY - plusSize/2, plusX, plusY + plusSize/2);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 20; }

                @Override
                public int getIconHeight() { return 16; }
            };
        }

        // Create close icon for button
        private Icon createCloseIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(TEXT_DARK);

                    int size = 16;
                    int centerX = x + size / 2;
                    int centerY = y + size / 2;
                    int crossSize = 6;

                    // Draw X icon
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(centerX - crossSize/2, centerY - crossSize/2, centerX + crossSize/2, centerY + crossSize/2);
                    g2d.drawLine(centerX + crossSize/2, centerY - crossSize/2, centerX - crossSize/2, centerY + crossSize/2);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 16; }

                @Override
                public int getIconHeight() { return 16; }
            };
        }

        private void openAddChapterDialog() {
            JDialog addChapterDialog = new JDialog(parentFrame, "Add New Chapter", true);
            addChapterDialog.setSize(500, 550);
            addChapterDialog.setLocationRelativeTo(parentFrame);
            addChapterDialog.setLayout(new BorderLayout());
            addChapterDialog.getContentPane().setBackground(BG_COLOR);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(25, 25, 25, 25));
            content.setBackground(Color.WHITE);

            JLabel titleLabel = new JLabel("Add New Chapter");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(HEADER_START);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            titleLabel.setIcon(createAddChapterIcon());
            titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            titleLabel.setIconTextGap(10);

            JLabel nameLabel = new JLabel("Chapter Name:");
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField nameField = new JTextField();
            styleField(nameField);

            JLabel orderLabel = new JLabel("Chapter Order:");
            orderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            orderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            orderLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

            JTextField orderField = new JTextField();
            styleField(orderField);

            try {
                int nextOrder = facade.getNextChapterOrder(book.getBookId());
                orderField.setText(String.valueOf(nextOrder));
            } catch (SQLException ex) {
                orderField.setText("1");
            }

            JLabel descLabel = new JLabel("Description (Optional):");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            descLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            // Import File Button
            JButton importBtn = new RoundedButton("Import Text File", new Color(155, 89, 182), Color.WHITE, new Color(142, 68, 173));
            importBtn.setIcon(createImportIcon());
            importBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            importBtn.setIconTextGap(8);
            importBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            importBtn.setBorder(new EmptyBorder(10, 0, 10, 0));

            JButton saveBtn = new RoundedButton("Save Chapter", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
            saveBtn.setIcon(createSaveTickIcon());
            saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            saveBtn.setIconTextGap(8);
            saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            importBtn.addActionListener(e -> importTextFileForChapter(book));

            saveBtn.addActionListener(e -> {
                String chapterName = nameField.getText().trim();
                String orderText = orderField.getText().trim();
                String description = descArea.getText().trim();

                if (chapterName.isEmpty()) {
                    nameField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
                    JOptionPane.showMessageDialog(addChapterDialog, "Chapter name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int chapterOrder = Integer.parseInt(orderText);
                    if (chapterOrder <= 0) {
                        JOptionPane.showMessageDialog(addChapterDialog, "Chapter order must be a positive number!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check if chapter order already exists
                    List<ChapterDTO> existingChapters = facade.getChaptersByBookId(book.getBookId());
                    for (ChapterDTO chapter : existingChapters) {
                        if (chapter.getChapterOrder() == chapterOrder) {
                            JOptionPane.showMessageDialog(addChapterDialog, "Chapter order " + chapterOrder + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    ChapterDTO newChapter = new ChapterDTO();
                    newChapter.setBookId(book.getBookId());
                    newChapter.setChapterName(chapterName);
                    newChapter.setChapterOrder(chapterOrder);
                    newChapter.setDescription(description.isEmpty() ? null : description);

                    facade.addChapter(newChapter);
                    refreshChapters(); // Auto-refresh after adding chapter
                    refreshBooks();
                    addChapterDialog.dispose();
                    showSuccessMessage("Chapter added successfully!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addChapterDialog, "Please enter a valid number for chapter order!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(addChapterDialog, "Error adding chapter: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            content.add(titleLabel);
            content.add(nameLabel);
            content.add(nameField);
            content.add(orderLabel);
            content.add(orderField);
            content.add(descLabel);
            content.add(descScroll);
            content.add(Box.createVerticalStrut(20));
            content.add(importBtn);
            content.add(Box.createVerticalStrut(15));
            content.add(saveBtn);

            addChapterDialog.add(content, BorderLayout.CENTER);
            addChapterDialog.setVisible(true);
        }

        // Create import icon
        private Icon createImportIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);

                    int size = 16;

                    // Draw download/import icon
                    g2d.setStroke(new BasicStroke(2f));
                    // Arrow down
                    g2d.drawLine(x + size/2, y + 2, x + size/2, y + size - 4);
                    g2d.drawLine(x + size/2, y + size - 4, x + 3, y + size - 8);
                    g2d.drawLine(x + size/2, y + size - 4, x + size - 3, y + size - 8);
                    // Box
                    g2d.drawRect(x + 4, y + 2, size - 8, 4);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 16; }

                @Override
                public int getIconHeight() { return 16; }
            };
        }

        private void importTextFileForChapter(BookDTO selectedBook) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Text File to Import");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

            int result = fileChooser.showOpenDialog(parentFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                processTextFileForChapter(selectedFile, selectedBook);
            }
        }

        private void processTextFileForChapter(File file, BookDTO selectedBook) {
            try {
                int confirm = JOptionPane.showConfirmDialog(parentFrame,
                    "Import Details:\n" +
                    "Book: " + selectedBook.getTitle() + "\n" +
                    "File: " + file.getName() + "\n\n" +
                    "This will:\n" +
                    "1. Extract chapter name from first line\n" +
                    "2. Split text into sentences at Arabic punctuation (.!؟)\n" +
                    "3. Add all sentences to a new chapter\n\n" +
                    "Continue?",
                    "Confirm Text Import",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = facade.sentenceSplitter(file.getAbsolutePath(), selectedBook.getBookId());
                    if (success) {
                        showSuccessMessage("Text file imported successfully! A new chapter has been created.");
                        refreshChapters(); // Auto-refresh after import
                        refreshBooks();
                    } else {
                        showErrorDialog("Failed to import text file. Please check the file format.");
                    }
                }
            } catch (Exception ex) {
                showErrorDialog("Error processing file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private void loadChapters() {
            try {
                List<ChapterDTO> chapters = facade.getChaptersByBookId(book.getBookId());
                cardsContainer.removeAll();

                if (chapters.isEmpty()) {
                    // Create centered empty panel
                    JPanel mainEmptyPanel = new JPanel(new GridBagLayout());
                    mainEmptyPanel.setBackground(BG_COLOR);
                    mainEmptyPanel.setBorder(new EmptyBorder(50, 0, 50, 0));

                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                    emptyPanel.setOpaque(false);
                    emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                    JLabel emptyIcon = new JLabel();
                    emptyIcon.setIcon(createEmptyChaptersIcon());
                    emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyIcon.setBorder(new EmptyBorder(0, 0, 20, 0));

                    JLabel emptyLabel = new JLabel("No chapters found");
                    emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    emptyLabel.setForeground(TEXT_DARK);
                    emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JLabel emptySubLabel = new JLabel("Click 'Add Chapter' to start adding chapters");
                    emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    emptySubLabel.setForeground(TEXT_LIGHT);
                    emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptySubLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    emptyPanel.add(emptyIcon);
                    emptyPanel.add(emptyLabel);
                    emptyPanel.add(Box.createVerticalStrut(10));
                    emptyPanel.add(emptySubLabel);

                    mainEmptyPanel.add(emptyPanel);
                    cardsContainer.add(mainEmptyPanel);
                } else {
                    for (ChapterDTO chapter : chapters) {
                        int sentencesCount = 0;
                        try {
                            List<SentenceDTO> sentences = facade.getSentencesByChapterId(chapter.getChapterId());
                            sentencesCount = sentences != null ? sentences.size() : 0;
                        } catch (Exception e) {
                            System.err.println("Error loading chapter sentences: " + e.getMessage());
                        }
                        cardsContainer.add(new ChapterCard(chapter, sentencesCount));
                    }
                }
            } catch (SQLException ex) {
                showErrorDialog("Error loading chapters: " + ex.getMessage());
                ex.printStackTrace();
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        }

        // Create empty chapters icon
        private Icon createEmptyChaptersIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(TEXT_LIGHT);

                    int size = 80;

                    // Draw document stack
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.setStroke(new BasicStroke(2f));

                    for (int i = 0; i < 3; i++) {
                        g2d.drawRect(x + 5 + i*3, y + 5 + i*3, size - 10 - i*6, size - 10 - i*6);
                    }

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 80; }

                @Override
                public int getIconHeight() { return 80; }
            };
        }

        private void showSuccessMessage(String message) {
            JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        private void showErrorDialog(String msg) {
            JOptionPane.showMessageDialog(parentFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }

        // ChapterCard class with Edit/Delete buttons
        private class ChapterCard extends JPanel {
            private boolean isHovered = false;
            private final ChapterDTO chapter;
            private final int sentencesCount;

            public ChapterCard(ChapterDTO chapter, int sentencesCount) {
                this.chapter = chapter;
                this.sentencesCount = sentencesCount;

                this.setPreferredSize(new Dimension(220, 250));
                this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                this.setBackground(CARD_BG);
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                this.setBorder(new EmptyBorder(20, 15, 20, 15));
                this.setOpaque(false);

                JLabel nameLabel = new JLabel(chapter.getChapterName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                nameLabel.setForeground(TEXT_DARK);
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel orderLabel = new JLabel("Order: " + chapter.getChapterOrder());
                orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                orderLabel.setForeground(TEXT_LIGHT);
                orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                orderLabel.setHorizontalAlignment(SwingConstants.CENTER);

                String descText = (chapter.getDescription() != null && !chapter.getDescription().isEmpty())
                               ? (chapter.getDescription().length() > 60 ? chapter.getDescription().substring(0, 57) + "..." : chapter.getDescription())
                               : "No description";
                JLabel descLabel = new JLabel("<html><div style='text-align:center; width:180px;'>" + descText + "</div></html>");
                descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                descLabel.setForeground(TEXT_LIGHT);
                descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                descLabel.setHorizontalAlignment(SwingConstants.CENTER);

                JLabel statsLabel = new JLabel(sentencesCount + " Sentences");
                statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                statsLabel.setForeground(HEADER_START);
                statsLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(HEADER_START, 2, true),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)
                ));
                statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                statsLabel.setOpaque(true);
                statsLabel.setBackground(new Color(HEADER_START.getRed(), HEADER_START.getGreen(), HEADER_START.getBlue(), 10));
                statsLabel.setIcon(createSentencesIcon());
                statsLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
                statsLabel.setIconTextGap(4);

                // Action buttons panel
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
                actionPanel.setOpaque(false);
                actionPanel.setMaximumSize(new Dimension(180, 40));

                JButton editBtn = createIconButton(createSmallEditIcon(), INFO_COLOR, "Edit Chapter");
                JButton deleteBtn = createIconButton(createSmallDeleteIcon(), ERROR_COLOR, "Delete Chapter");

                editBtn.addActionListener(e -> {
                    openEditChapterDialog(chapter);
                });

                deleteBtn.addActionListener(e -> {
                    deleteChapter(chapter);
                });

                actionPanel.add(editBtn);
                actionPanel.add(deleteBtn);

                add(nameLabel);
                add(Box.createVerticalStrut(8));
                add(orderLabel);
                add(Box.createVerticalStrut(8));
                add(descLabel);
                add(Box.createVerticalGlue());
                add(statsLabel);
                add(Box.createVerticalStrut(10));
                add(actionPanel);

                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 1) {
                            showSentencesFullScreen(chapter);
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cornerRadius = 15;
                int shadowOffset = isHovered ? 6 : 3;
                Color shadowColor = isHovered ? CARD_HOVER_SHADOW : CARD_SHADOW;

                g2.setColor(shadowColor);
                g2.fill(new RoundRectangle2D.Double(shadowOffset, shadowOffset, getWidth() - shadowOffset * 2, getHeight() - shadowOffset * 2, cornerRadius, cornerRadius));

                g2.setColor(isHovered ? new Color(250, 250, 250) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

                g2.setColor(isHovered ? HEADER_START : new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(isHovered ? 2.0f : 1.2f));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            }
        }

        // Create sentences icon for stats label
        private Icon createSentencesIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(HEADER_START);

                    int size = 10;

                    // Draw writing/quill icon
                    g2d.setStroke(new BasicStroke(1.0f));
                    // Pen nib
                    g2d.drawLine(x + 2, y + size - 2, x + size - 2, y + 2);
                    // Pen body
                    g2d.drawLine(x + size - 2, y + 2, x + size, y);
                    // Ink lines
                    for (int i = 0; i < 2; i++) {
                        g2d.drawLine(x + 3 + i, y + size - 3, x + 3 + i, y + size - 1);
                    }

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 10; }

                @Override
                public int getIconHeight() { return 10; }
            };
        }

        // Method to open edit chapter dialog
        private void openEditChapterDialog(ChapterDTO chapter) {
            JDialog editChapterDialog = new JDialog(parentFrame, "Edit Chapter", true);
            editChapterDialog.setSize(500, 550);
            editChapterDialog.setLocationRelativeTo(parentFrame);
            editChapterDialog.setLayout(new BorderLayout());
            editChapterDialog.getContentPane().setBackground(BG_COLOR);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(new EmptyBorder(25, 25, 25, 25));
            content.setBackground(Color.WHITE);

            JLabel titleLabel = new JLabel("Edit Chapter");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(HEADER_START);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            titleLabel.setIcon(createSmallEditIcon());
            titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            titleLabel.setIconTextGap(10);

            JLabel nameLabel = new JLabel("Chapter Name:");
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JTextField nameField = new JTextField(chapter.getChapterName());
            styleField(nameField);

            JLabel orderLabel = new JLabel("Chapter Order:");
            orderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            orderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            orderLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

            JTextField orderField = new JTextField(String.valueOf(chapter.getChapterOrder()));
            styleField(orderField);

            JLabel descLabel = new JLabel("Description (Optional):");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            descLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

            JTextArea descArea = new JTextArea(3, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            descArea.setText(chapter.getDescription() != null ? chapter.getDescription() : "");

            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JButton saveBtn = new RoundedButton("Update Chapter", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
            saveBtn.setIcon(createSaveTickIcon());
            saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            saveBtn.setIconTextGap(8);
            saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            saveBtn.addActionListener(e -> {
                String chapterName = nameField.getText().trim();
                String orderText = orderField.getText().trim();
                String description = descArea.getText().trim();

                if (chapterName.isEmpty()) {
                    nameField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
                    JOptionPane.showMessageDialog(editChapterDialog, "Chapter name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int chapterOrder = Integer.parseInt(orderText);
                    if (chapterOrder <= 0) {
                        JOptionPane.showMessageDialog(editChapterDialog, "Chapter order must be a positive number!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Check if chapter order already exists (excluding current chapter)
                    List<ChapterDTO> existingChapters = facade.getChaptersByBookId(book.getBookId());
                    for (ChapterDTO existingChapter : existingChapters) {
                        if (existingChapter.getChapterId() != chapter.getChapterId() &&
                            existingChapter.getChapterOrder() == chapterOrder) {
                            JOptionPane.showMessageDialog(editChapterDialog, "Chapter order " + chapterOrder + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    chapter.setChapterName(chapterName);
                    chapter.setChapterOrder(chapterOrder);
                    chapter.setDescription(description.isEmpty() ? null : description);

                    facade.updateChapter(chapter);
                    refreshChapters(); // Auto-refresh after update
                    refreshBooks();
                    editChapterDialog.dispose();
                    showSuccessMessage("Chapter updated successfully!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editChapterDialog, "Please enter a valid number for chapter order!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(editChapterDialog, "Error updating chapter: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            content.add(titleLabel);
            content.add(nameLabel);
            content.add(nameField);
            content.add(orderLabel);
            content.add(orderField);
            content.add(descLabel);
            content.add(descScroll);
            content.add(Box.createVerticalStrut(20));
            content.add(saveBtn);

            editChapterDialog.add(content, BorderLayout.CENTER);
            editChapterDialog.setVisible(true);
        }

        // Method to delete chapter
        private void deleteChapter(ChapterDTO chapter) {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to delete '" + chapter.getChapterName() + "'?\nThis will also delete all sentences in this chapter.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    facade.deleteChapter(chapter.getChapterId());
                    showSuccessMessage("Chapter deleted successfully!");
                    refreshChapters(); // Auto-refresh after deletion
                    refreshBooks();
                } catch (SQLException ex) {
                    showErrorDialog("Error deleting chapter: " + ex.getMessage());
                }
            }
        }

        private void showSentencesFullScreen(ChapterDTO chapter) {
            // Create a new JFrame for full-screen sentences view
            JFrame sentencesFrame = new JFrame("Sentences: " + chapter.getChapterName());
            sentencesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            sentencesFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
            sentencesFrame.setLocationRelativeTo(parentFrame);
            
            // Create the sentences panel
            SentencesPanel sentencesPanel = new SentencesPanel(chapter, sentencesFrame);
            sentencesFrame.add(sentencesPanel);
            
            sentencesFrame.setVisible(true);
        }
    }

    // SentencesPanel class for full-screen sentences display
    private class SentencesPanel extends JPanel {
        private final ChapterDTO chapter;
        private final JFrame parentFrame;
        private List<SentenceDTO> sentences;
        private int currentIndex = 0;
        private JTextArea sentenceTextArea;
        private JLabel counterLabel;
        private JButton prevBtn, nextBtn, closeBtn;

        public SentencesPanel(ChapterDTO chapter, JFrame parentFrame) {
            this.chapter = chapter;
            this.parentFrame = parentFrame;
            initializePanel();
            loadSentences();
        }

        private void initializePanel() {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);

            // Header
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
                }
            };
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
            headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

            JLabel titleLabel = new JLabel("Sentences: " + chapter.getChapterName());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            closeBtn = new RoundedButton("Close", BTN_REFRESH_BG, TEXT_DARK, BTN_REFRESH_HOVER);
            closeBtn.setIcon(createAddBookIcon1());
            closeBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            closeBtn.setIconTextGap(8);
            closeBtn.addActionListener(e -> {
                parentFrame.dispose();
            });

            headerPanel.add(titleLabel, BorderLayout.CENTER);
            headerPanel.add(closeBtn, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // Main content area
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(BG_COLOR);
            contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

            // Sentence display area (larger for full screen)
            sentenceTextArea = new JTextArea();
            sentenceTextArea.setEditable(false);
            sentenceTextArea.setWrapStyleWord(true);
            sentenceTextArea.setLineWrap(true);
            sentenceTextArea.setBackground(Color.WHITE);
            sentenceTextArea.setFont(new Font("Traditional Arabic", Font.PLAIN, 24)); // Larger font
            sentenceTextArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            sentenceTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
            ));

            JScrollPane textScroll = new JScrollPane(sentenceTextArea);
            contentPanel.add(textScroll, BorderLayout.CENTER);

            // Navigation panel
            JPanel navPanel = new JPanel(new BorderLayout());
            navPanel.setBackground(BG_COLOR);
            navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            prevBtn = new RoundedButton("Previous", INFO_COLOR, Color.WHITE, new Color(37, 99, 235));
            prevBtn.setIcon(createPrevIcon());
            prevBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            prevBtn.setIconTextGap(8);

            nextBtn = new RoundedButton("Next", INFO_COLOR, Color.WHITE, new Color(37, 99, 235));
            nextBtn.setIcon(createNextIcon());
            nextBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            nextBtn.setIconTextGap(8);

            counterLabel = new JLabel("0 / 0");
            counterLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            counterLabel.setForeground(TEXT_DARK);
            counterLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
            buttonPanel.setBackground(BG_COLOR);
            buttonPanel.add(prevBtn);
            buttonPanel.add(counterLabel);
            buttonPanel.add(nextBtn);

            navPanel.add(buttonPanel, BorderLayout.CENTER);
            contentPanel.add(navPanel, BorderLayout.SOUTH);

            add(contentPanel, BorderLayout.CENTER);

            // Button listeners
            prevBtn.addActionListener(e -> showPreviousSentence());
            nextBtn.addActionListener(e -> showNextSentence());
        }

        private void loadSentences() {
            try {
                sentences = facade.getSentencesByChapterId(chapter.getChapterId());
                if (sentences != null && !sentences.isEmpty()) {
                    currentIndex = 0;
                    updateSentenceDisplay();
                } else {
                    sentenceTextArea.setText("No sentences found in this chapter.");
                    prevBtn.setEnabled(false);
                    nextBtn.setEnabled(false);
                }
            } catch (SQLException ex) {
                sentenceTextArea.setText("Error loading sentences: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        private void updateSentenceDisplay() {
            if (sentences == null || sentences.isEmpty()) return;

            SentenceDTO currentSentence = sentences.get(currentIndex);
            StringBuilder details = new StringBuilder();

            String arabicText = currentSentence.getTextDiacritized() != null && !currentSentence.getTextDiacritized().isEmpty()
                ? currentSentence.getTextDiacritized()
                : currentSentence.getText();
            
            details.append("Sentence ").append(currentIndex + 1).append(" of ").append(sentences.size()).append("\n\n");
            details.append("Text:\n").append(arabicText).append("\n\n");

            if (currentSentence.getTranslation() != null && !currentSentence.getTranslation().isEmpty()) {
                details.append("Translation:\n").append(currentSentence.getTranslation()).append("\n\n");
            }

            if (currentSentence.getNotes() != null && !currentSentence.getNotes().isEmpty()) {
                details.append("Notes:\n").append(currentSentence.getNotes());
            }

            sentenceTextArea.setText(details.toString());
            sentenceTextArea.setCaretPosition(0);

            // Update counter and button states
            counterLabel.setText((currentIndex + 1) + " / " + sentences.size());
            prevBtn.setEnabled(currentIndex > 0);
            nextBtn.setEnabled(currentIndex < sentences.size() - 1);
        }

        private void showPreviousSentence() {
            if (currentIndex > 0) {
                currentIndex--;
                updateSentenceDisplay();
            }
        }

        private void showNextSentence() {
            if (currentIndex < sentences.size() - 1) {
                currentIndex++;
                updateSentenceDisplay();
            }
        }

        // Create previous icon
        private Icon createPrevIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);

                    int size = 12;

                    // Draw left arrow
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(x + size - 2, y + 2, x + 2, y + size/2);
                    g2d.drawLine(x + 2, y + size/2, x + size - 2, y + size - 2);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 12; }

                @Override
                public int getIconHeight() { return 12; }
            };
        }

        // Create next icon
        private Icon createNextIcon() {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);

                    int size = 12;

                    // Draw right arrow
                    g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(x + 2, y + 2, x + size - 2, y + size/2);
                    g2d.drawLine(x + size - 2, y + size/2, x + 2, y + size - 2);

                    g2d.dispose();
                }

                @Override
                public int getIconWidth() { return 12; }

                @Override
                public int getIconHeight() { return 12; }
            };
        }
    }
}