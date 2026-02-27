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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.arabicprose.bll.IBusinessLayerFacade;
import com.arabicprose.dto.AuthorDTO;
import com.arabicprose.dto.BookDTO;

public class AuthorPL extends JPanel implements IAuthorChangeObserver  {

    private IBusinessLayerFacade facade;
    private JPanel cardsContainer; 
    private JButton addBtn;
    private List<AuthorDTO> currentAuthors;
    private List<IAuthorChangeObserver> observers = new ArrayList<>();
    
    // --- Enhanced Color Palette ---
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
    
    // Accent Colors
    private final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private final Color ERROR_COLOR = new Color(239, 68, 68);
    private final Color INFO_COLOR = new Color(59, 130, 246);

    public AuthorPL(IBusinessLayerFacade facade) {
        this.facade = facade;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        
        initializeComponents();
        setupLayout();
        loadAuthors();
        setupEventListeners();
    }

    private void initializeComponents() {
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        cardsContainer.setBackground(BG_COLOR);
        cardsContainer.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        cardsContainer.setBorder(new EmptyBorder(30, 30, 30, 30));

        addBtn = new RoundedButton("Add New Author", BTN_ADD_BG, Color.WHITE, BTN_ADD_HOVER);
        addBtn.setIcon(createPlusIcon());
        addBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        addBtn.setIconTextGap(8);
    }

    // Plus icon method
    private Icon createPlusIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                
                int size = 16;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                int crossSize = 8;
                int thickness = 2;
                
                // Draw horizontal line of plus
                g2d.fillRect(centerX - crossSize/2, centerY - thickness/2, crossSize, thickness);
                // Draw vertical line of plus
                g2d.fillRect(centerX - thickness/2, centerY - crossSize/2, thickness, crossSize);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }
    @Override
    public void onAuthorDeleted(int authorId) {
        refreshAuthors();  // or whatever your update code is
    }

    @Override
    public void onAuthorAdded() {
        refreshAuthors();
    }

    @Override
    public void onAuthorUpdated() {
        refreshAuthors();
    }
    

    public void addObserver(IAuthorChangeObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(IAuthorChangeObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyAuthorDeleted(int authorId) {
        for (IAuthorChangeObserver observer : observers) {
            observer.onAuthorDeleted(authorId);
        }
    }
    
    private void notifyAuthorAdded() {
        for (IAuthorChangeObserver observer : observers) {
            observer.onAuthorAdded();
        }
    }
    
    private void notifyAuthorUpdated() {
        for (IAuthorChangeObserver observer : observers) {
            observer.onAuthorUpdated();
        }
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

        JLabel titleLabel = new JLabel("Authors Gallery");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel actionPanel = new JPanel(new BorderLayout(20, 0));
        actionPanel.setOpaque(false);
        
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

    private void loadAuthors() {
        cardsContainer.removeAll();
        
        // Create a separate centered panel for loading/empty states
        JPanel centeredPanel = new JPanel(new GridBagLayout());
        centeredPanel.setBackground(BG_COLOR);
        centeredPanel.setPreferredSize(cardsContainer.getSize());
        
        JLabel loadingLabel = new JLabel("Loading authors...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        loadingLabel.setForeground(TEXT_LIGHT);
        loadingLabel.setIcon(createLoadingIcon());
        loadingLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        loadingLabel.setIconTextGap(8);
        
        centeredPanel.add(loadingLabel);
        cardsContainer.add(centeredPanel);
        cardsContainer.revalidate();
        cardsContainer.repaint();
        
        SwingUtilities.invokeLater(() -> {
            try {
                currentAuthors = facade.getAllAuthors();
                cardsContainer.removeAll();
                
                if (currentAuthors.isEmpty()) {
                    // Use centered panel for empty state
                    JPanel emptyCenteredPanel = new JPanel(new GridBagLayout());
                    emptyCenteredPanel.setBackground(BG_COLOR);
                    emptyCenteredPanel.setPreferredSize(cardsContainer.getSize());
                    
                    JPanel emptyPanel = new JPanel();
                    emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                    emptyPanel.setOpaque(false);
                    
                    JLabel emptyIcon = new JLabel();
                    emptyIcon.setIcon(createEmptyAuthorsIcon());
                    emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
                    emptyIcon.setBorder(new EmptyBorder(0, 0, 20, 0));
                    
                    JLabel emptyLabel = new JLabel("No Authors Found");
                    emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    emptyLabel.setForeground(TEXT_DARK);
                    emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    JLabel emptySubLabel = new JLabel("Click 'Add New Author' To Begin Your Collection");
                    emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    emptySubLabel.setForeground(TEXT_LIGHT);
                    emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    emptyPanel.add(emptyIcon);
                    emptyPanel.add(emptyLabel);
                    emptyPanel.add(Box.createVerticalStrut(10));
                    emptyPanel.add(emptySubLabel);
                    
                    emptyCenteredPanel.add(emptyPanel);
                    cardsContainer.add(emptyCenteredPanel);
                } else {
                    // Restore flow layout for cards
                    for (AuthorDTO author : currentAuthors) {
                        int bookCount = 0;
                        try {
                            List<BookDTO> books = facade.getAllBooks();
                            bookCount = (int) books.stream()
                                .filter(b -> b.getAuthorId() == author.getAuthorId())
                                .count();
                        } catch (Exception e) {}
                        cardsContainer.add(new AuthorCard(author, bookCount));
                    }
                }
            } catch (SQLException ex) {
                cardsContainer.removeAll();
                
                JPanel errorCenteredPanel = new JPanel(new GridBagLayout());
                errorCenteredPanel.setBackground(BG_COLOR);
                errorCenteredPanel.setPreferredSize(cardsContainer.getSize());
                
                JLabel errorLabel = new JLabel("Error Loading Authors");
                errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                errorLabel.setForeground(ERROR_COLOR);
                
                errorCenteredPanel.add(errorLabel);
                cardsContainer.add(errorCenteredPanel);
                
                showErrorDialog("Error loading data: " + ex.getMessage());
            }
            cardsContainer.revalidate();
            cardsContainer.repaint();
        });
    }

    // Auto-refresh method
    private void refreshAuthors() {
        loadAuthors();
    }

    // Create empty authors icon
    private Icon createEmptyAuthorsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                
                // Draw book shelf with empty books
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(2f));
                
                // Shelf
                g2d.drawLine(x + 10, y + size - 10, x + size - 10, y + size - 10);
                
                // Empty book slots
                for (int i = 0; i < 3; i++) {
                    int bookX = x + 15 + (i * 20);
                    int bookY = y + size - 25;
                    
                    // Book spine
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.fillRect(bookX, bookY, 12, 15);
                    
                    // Book outline
                    g2d.setColor(new Color(180, 180, 180));
                    g2d.drawRect(bookX, bookY, 12, 15);
                    
                    // Empty lines (representing no content)
                    g2d.setColor(new Color(160, 160, 160));
                    for (int j = 0; j < 3; j++) {
                        g2d.drawLine(bookX + 2, bookY + 5 + j * 3, bookX + 10, bookY + 5 + j * 3);
                    }
                }
                
                // Plus sign in the center
                g2d.setColor(HEADER_START);
                g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int plusSize = 20;
                g2d.drawLine(centerX - plusSize/2, centerY, centerX + plusSize/2, centerY);
                g2d.drawLine(centerX, centerY - plusSize/2, centerX, centerY + plusSize/2);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 80; }

            @Override
            public int getIconHeight() { return 80; }
        };
    }

    // Create loading icon
    private Icon createLoadingIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_LIGHT);
                
                int size = 16;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                int radius = 6;
                
                // Draw loading spinner (circular arrow)
                g2d.setStroke(new BasicStroke(2f));
                
                // Draw circle
                g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                
                // Draw arrow head
                int arrowSize = 3;
                g2d.fillPolygon(new int[] {centerX + radius - 1, centerX + radius - 1, centerX + radius + arrowSize}, 
                               new int[] {centerY - arrowSize, centerY + arrowSize, centerY}, 
                               3);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

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

    private class AuthorCard extends JPanel {
        private boolean isHovered = false;
        private final Color[] AVATAR_COLORS = {
            new Color(239, 68, 68), new Color(245, 158, 11), new Color(34, 197, 94),
            new Color(59, 130, 246), new Color(139, 92, 246), new Color(236, 72, 153),
            new Color(14, 165, 233), new Color(168, 85, 247)
        };
        private final AuthorDTO author;
        
        public AuthorCard(AuthorDTO author, int bookCount) {
            this.author = author;
            this.setPreferredSize(new Dimension(240, 280));
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
                    Color avatarColor = AVATAR_COLORS[Math.abs(author.getName().hashCode()) % AVATAR_COLORS.length];
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 32, 2, 64, 64));
                    g2.setColor(avatarColor);
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 30, 0, 60, 60));
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fill(new Ellipse2D.Double(getWidth()/2 - 25, 5, 50, 25));
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    String initial = author.getName().isEmpty() ? "?" : author.getName().substring(0, 1).toUpperCase();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2, ((60 - fm.getHeight()) / 2) + fm.getAscent());
                }
            };
            avatarPanel.setPreferredSize(new Dimension(200, 75));
            avatarPanel.setMaximumSize(new Dimension(200, 75));
            avatarPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(author.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nameLabel.setForeground(TEXT_DARK);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Handle biography display
            JLabel bioLabel;
            if (author.getBiography() == null || author.getBiography().trim().isEmpty()) {
                bioLabel = new JLabel("<html><div style='text-align:center; width:200px; color: #94a3b8;'>No Biography Available</div></html>");
                bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                bioLabel.setForeground(new Color(148, 163, 184));
            } else {
                String bioText = author.getBiography().length() > 50 
                               ? author.getBiography().substring(0, 47) + "..." 
                               : author.getBiography();
                bioLabel = new JLabel("<html><div style='text-align:center; width:200px;'>" + bioText + "</div></html>");
                bioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                bioLabel.setForeground(TEXT_LIGHT);
            }
            bioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            bioLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel statsLabel = new JLabel(bookCount + " Books");
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

            add(avatarPanel);
            add(javax.swing.Box.createVerticalStrut(8));
            add(nameLabel);
            add(javax.swing.Box.createVerticalStrut(5));
            add(bioLabel);
            add(javax.swing.Box.createVerticalGlue());
            add(statsLabel);
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                public void mouseClicked(MouseEvent e) { showAuthorActionDialog(author); }
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

    private void showAuthorActionDialog(AuthorDTO author) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog menuDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null, "Options", true);
        menuDialog.setUndecorated(true);
        menuDialog.setSize(320, 260);
        menuDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panel.setLayout(new GridLayout(4, 1, 12, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Manage: " + author.getName());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setIcon(createSettingsIcon());
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        title.setIconTextGap(8);
        
        JButton editBtn = new RoundedButton("Edit Details", INFO_COLOR, Color.WHITE, new Color(37, 99, 235));
        editBtn.setIcon(createEditIcon());
        editBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        editBtn.setIconTextGap(8);
        
        JButton deleteBtn = new RoundedButton("Delete Author", ERROR_COLOR, Color.WHITE, new Color(220, 38, 38));
        deleteBtn.setIcon(createDeleteIcon());
        deleteBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        deleteBtn.setIconTextGap(8);
        
        JButton closeBtn = new RoundedButton("Close", BG_COLOR, TEXT_DARK, new Color(241, 245, 249));
        closeBtn.setIcon(createCloseIcon());
        closeBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        closeBtn.setIconTextGap(8);
        
        editBtn.addActionListener(e -> { menuDialog.dispose(); openAuthorInputForm(author); });
        deleteBtn.addActionListener(e -> { menuDialog.dispose(); performDelete(author); });
        closeBtn.addActionListener(e -> menuDialog.dispose());
        
        panel.add(title);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(closeBtn);
        
        menuDialog.add(panel);
        menuDialog.setVisible(true);
    }

    // Create settings icon for title
    private Icon createSettingsIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_DARK);
                
                int size = 16;
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                int radius = 5;
                
                // Draw gear icon
                g2d.setStroke(new BasicStroke(1.5f));
                // Outer circle
                g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                // Gear teeth
                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI * i / 4;
                    int x1 = centerX + (int)(radius * Math.cos(angle));
                    int y1 = centerY + (int)(radius * Math.sin(angle));
                    int x2 = centerX + (int)((radius + 3) * Math.cos(angle));
                    int y2 = centerY + (int)((radius + 3) * Math.sin(angle));
                    g2d.drawLine(x1, y1, x2, y2);
                }
                // Center dot
                g2d.fillOval(centerX - 1, centerY - 1, 2, 2);
                
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    // Create delete icon
    private Icon createDeleteIcon() {
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

    // Create close icon
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

    private void openAuthorInputForm(AuthorDTO authorToEdit) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog formDialog = new JDialog(parentWindow != null ? (Frame)parentWindow : null, 
            authorToEdit == null ? "New Author" : "Edit Author", true);
        formDialog.setSize(450, 400);
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
        JLabel titleLabel = new JLabel(authorToEdit == null ? "Add New Author" : "Edit Author");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_START);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        titleLabel.setIcon(authorToEdit == null ? createPlusIcon() : createEditIcon());
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(10);
        
        // Name field
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        namePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel("Author Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JTextField nameField = new JTextField(authorToEdit != null ? authorToEdit.getName() : "");
        styleField(nameField);
        
        namePanel.add(nameLabel);
        namePanel.add(nameField);
        
        // Biography field
        JPanel bioPanel = new JPanel();
        bioPanel.setLayout(new BoxLayout(bioPanel, BoxLayout.Y_AXIS));
        bioPanel.setOpaque(false);
        bioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        bioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JLabel bioLabel = new JLabel("Biography:");
        bioLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bioLabel.setForeground(TEXT_DARK);
        bioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bioLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        JTextField bioField = new JTextField(authorToEdit != null ? authorToEdit.getBiography() : "");
        styleField(bioField);
        
        bioPanel.add(bioLabel);
        bioPanel.add(bioField);
        
        // Save button with tick icon
        JButton saveBtn = new RoundedButton("Save Author", SUCCESS_COLOR, Color.WHITE, new Color(22, 163, 74));
        saveBtn.setIcon(createSaveTickIcon());
        saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveBtn.setIconTextGap(8);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
        
        saveBtn.addActionListener(e -> {
            if(nameField.getText().trim().isEmpty()) {
                nameField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 2));
                return;
            }
            try {
                AuthorDTO dto = (authorToEdit == null) ? new AuthorDTO() : authorToEdit;
                dto.setName(nameField.getText().trim());
                dto.setBiography(bioField.getText().trim());
                if (authorToEdit == null) { 
                    facade.addAuthor(dto); 
                    notifyAuthorAdded(); 
                    showSuccessMessage("Author added successfully!"); 
                } else { 
                    facade.updateAuthor(dto); 
                    notifyAuthorUpdated(); 
                    showSuccessMessage("Author updated successfully!"); 
                }
                refreshAuthors(); // Auto-refresh after save/update
                formDialog.dispose();
            } catch (Exception ex) { 
                showErrorDialog("Error saving author: " + ex.getMessage()); 
            }
        });
        
        // Add components to content
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(namePanel);
        content.add(bioPanel);
        content.add(Box.createVerticalStrut(25));
        content.add(saveBtn);
        
        formDialog.add(content);
        formDialog.setVisible(true);
    }

    // Create save tick icon
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

    // Create edit icon
    private Icon createEditIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(HEADER_START);
                
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

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        field.setBackground(new Color(250, 250, 250));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void performDelete(AuthorDTO author) {
        int opt = JOptionPane.showConfirmDialog(this, 
            "Delete " + author.getName() + "?\nThis will also delete all books, chapters, and sentences by this author.", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (opt == JOptionPane.YES_OPTION) {
            try {
                facade.deleteAuthor(author.getAuthorId());
                int authorId = author.getAuthorId();
                notifyAuthorDeleted(authorId);
                showSuccessMessage("Author deleted successfully!");
               
                refreshAuthors(); // Auto-refresh after deletion
            } catch (SQLException e) { 
                showErrorDialog("Error deleting author: " + e.getMessage()); 
            }
        }
    }
    

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "✅ Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "❌ Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setupEventListeners() {
        addBtn.addActionListener(e -> openAuthorInputForm(null));
    }
}