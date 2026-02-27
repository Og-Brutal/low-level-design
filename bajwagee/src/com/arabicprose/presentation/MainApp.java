package com.arabicprose.presentation;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.arabicprose.bll.*;
import com.arabicprose.dal.*;

public class MainApp {

    // --- Global Design Constants ---
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color HEADER_START = new Color(99, 102, 241); 
    private static final Color HEADER_END = new Color(139, 92, 246);   
    private static final Color TEXT_DARK = new Color(15, 23, 42);
    private static final Color TEXT_LIGHT = new Color(100, 116, 139);
    private static final Color ACCENT_COLOR = new Color(139, 92, 246);

    // Beautiful Color Palette for Menu Cards
    private static final Color AUTHORS_COLOR = new Color(16, 185, 129);    // Emerald Green - Growth & Creativity
    private static final Color BOOKS_COLOR = new Color(59, 130, 246);      // Royal Blue - Knowledge & Trust
    private static final Color SENTENCES_COLOR = new Color(245, 158, 11);  // Amber Gold - Writing & Wisdom
    private static final Color BROWSER_COLOR = new Color(139, 92, 246);    // Purple - Exploration & Mystery
    private static final Color SEARCH_COLOR = new Color(236, 72, 153);     // Pink - Attention & Discovery
    private static final Color FREQUENCY_COLOR = new Color(34, 197, 94);   // Green - Analysis & Growth

    private static CardLayout cardLayout;
    private static JPanel mainContainer;
    private static IBusinessLayerFacade facade;
    private static Timer loadingTimer;
    private static Timer floatingTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Arabic Prose Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.setLocationRelativeTo(null);
            
            // Initialize card layout and main container first
            cardLayout = new CardLayout();
            mainContainer = new JPanel(cardLayout);
            mainContainer.setBackground(BG_COLOR);

            // Show loading screen first
            showLoadingScreen(frame);
            
            // Initialize dependencies in background
            new Thread(() -> {
                initializeDependencies();
                
                // After loading, show instruction page
                SwingUtilities.invokeLater(() -> {
                    // Stop the loading animation
                    if (loadingTimer != null && loadingTimer.isRunning()) {
                        loadingTimer.stop();
                    }
                    showInstructionPage();
                });
            }).start();
            
            frame.add(mainContainer);
            frame.setVisible(true);
        });
    }

    /**
     * Initialize all dependencies
     */
    private static void initializeDependencies() {
        try {
            // Simulate loading time for better UX
            Thread.sleep(2000);
            
            AuthorDAO authorDAO = new AuthorDAO();
            BookDAO bookDAO = new BookDAO();
            SentenceDAO sentenceDAO = new SentenceDAO();
            ChapterDAO chapterDAO = new ChapterDAO();
            AnalysisResultDAO analysisResultDAO = new AnalysisResultDAO();
            TokenizationDAO tokenizationDAO = new TokenizationDAO();
            LemmatizationDAO lemmatizationDAO = new LemmatizationDAO();
            SegmentationDAO segmentationDAO = new SegmentationDAO();
            RootDAO rootDAO = new RootDAO();
            
            DataAccessLayerFacade DALF = new DataAccessLayerFacade(
                authorDAO, bookDAO, sentenceDAO, chapterDAO,
                analysisResultDAO, tokenizationDAO, lemmatizationDAO,
                segmentationDAO, rootDAO
            );
            
            AuthorBO authorBO = new AuthorBO(DALF);
            BookBO bookBO = new BookBO(DALF);
            SentenceBO sentenceBO = new SentenceBO(DALF);
            MorphologicalAnalysisBO morphBO = new MorphologicalAnalysisBO(
                analysisResultDAO, tokenizationDAO, lemmatizationDAO,
                segmentationDAO, rootDAO
            );
            ChapterBO chapterBO = new ChapterBO(DALF, new TextFileDAO(), morphBO);

            facade = new BusinessLayerFacade(
                authorBO, bookBO, sentenceBO, chapterBO, morphBO
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows beautiful loading screen with animation
     */
    private static void showLoadingScreen(JFrame frame) {
        LoadingPanel loadingPanel = new LoadingPanel();
        mainContainer.add(loadingPanel, "LOADING");
        cardLayout.show(mainContainer, "LOADING");
        
        loadingPanel.startAnimation();
    }

    /**
     * Custom Loading Panel with Animation
     */
    private static class LoadingPanel extends JPanel {
        private float rotation = 0f;
        
        public LoadingPanel() {
            setLayout(new BorderLayout());
            setBackground(BG_COLOR);
            
            // Center content
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 30, 0);
            
            // Animated logo container
            JPanel logoContainer = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Background circle
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillOval(10, 10, 80, 80);
                    
                    // Book icon
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawRoundRect(30, 25, 40, 50, 5, 5);
                    g2d.fillRect(30, 25, 8, 50);
                    
                    // Pages
                    g2d.setColor(new Color(255, 255, 255, 150));
                    for (int i = 0; i < 3; i++) {
                        g2d.drawLine(40 + i, 30, 40 + i, 70);
                    }
                }
            };
            logoContainer.setPreferredSize(new Dimension(100, 100));
            logoContainer.setOpaque(false);
            centerPanel.add(logoContainer, gbc);
            
            gbc.gridy = 1;
            gbc.insets = new Insets(20, 0, 10, 0);
            JLabel title = new JLabel("Arabic Prose System");
            title.setFont(new Font("Segoe UI", Font.BOLD, 36));
            title.setForeground(Color.WHITE);
            centerPanel.add(title, gbc);
            
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 40, 0);
            JLabel subtitle = new JLabel("Loading your literary journey...");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            subtitle.setForeground(new Color(224, 231, 255));
            centerPanel.add(subtitle, gbc);
            
            add(centerPanel, BorderLayout.CENTER);
        }
        
        public void startAnimation() {
            loadingTimer = new Timer(16, e -> {
                rotation += 0.1f;
                repaint();
            });
            loadingTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Animated gradient background
            float cycle = (float) (Math.sin(rotation * 0.5) * 0.1f + 0.9f);
            Color animatedStart = new Color(
                (int)(HEADER_START.getRed() * cycle),
                (int)(HEADER_START.getGreen() * cycle),
                (int)(HEADER_START.getBlue() * cycle)
            );
            
            GradientPaint gp = new GradientPaint(0, 0, animatedStart, getWidth(), getHeight(), HEADER_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Floating particles
            g2d.setColor(new Color(255, 255, 255, 30));
            for (int i = 0; i < 20; i++) {
                float x = (float) (Math.sin(rotation + i * 0.5) * 100 + getWidth() / 2);
                float y = (float) (Math.cos(rotation + i * 0.3) * 50 + getHeight() / 2);
                float size = (float) (Math.sin(rotation + i) * 3 + 5);
                g2d.fillOval((int)x, (int)y, (int)size, (int)size);
            }
            
            // Animated spinner
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2 + 50;
            int spinnerSize = 60;
            
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawOval(centerX - spinnerSize/2, centerY - spinnerSize/2, spinnerSize, spinnerSize);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int arcStart = (int) (rotation * 180 / Math.PI);
            g2d.drawArc(centerX - spinnerSize/2, centerY - spinnerSize/2, spinnerSize, spinnerSize, arcStart, 120);
        }
    }

    /**
     * Shows beautiful instruction/guidance page
     */
    private static void showInstructionPage() {
        InstructionPanel instructionPanel = new InstructionPanel();
        mainContainer.add(instructionPanel, "INSTRUCTIONS");
        cardLayout.show(mainContainer, "INSTRUCTIONS");
        instructionPanel.startFloatingAnimation();
    }

    /**
     * Stunning Instruction Panel with Elegant Design
     */
    private static class InstructionPanel extends JPanel {
        private float floatOffset = 0f;
        private List<FloatingShape> floatingShapes = new ArrayList<>();
        
        public InstructionPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));
            
            // Create floating shapes
            for (int i = 0; i < 12; i++) {
                floatingShapes.add(new FloatingShape());
            }
            
            // Main content panel with elegant layout
            JPanel mainContent = new JPanel(new BorderLayout());
            mainContent.setOpaque(false);
            
            // Header Section
            JPanel headerPanel = createElegantHeader();
            
            // Center Content - Simple & Beautiful
            JPanel centerPanel = createBeautifulCenter();
            
            mainContent.add(headerPanel, BorderLayout.NORTH);
            mainContent.add(centerPanel, BorderLayout.CENTER);
            
            add(mainContent, BorderLayout.CENTER);
        }
        
        private JPanel createElegantHeader() {
            JPanel header = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Elegant gradient background
                    GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241), 
                                                       getWidth(), getHeight(), new Color(139, 92, 246));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Subtle pattern overlay
                    g2d.setColor(new Color(255, 255, 255, 10));
                    for (int i = 0; i < getWidth(); i += 60) {
                        for (int j = 0; j < getHeight(); j += 60) {
                            if ((i + j) % 120 == 0) {
                                g2d.fillOval(i, j, 4, 4);
                            }
                        }
                    }
                }
            };
            header.setPreferredSize(new Dimension(1200, 300));
            header.setLayout(new GridBagLayout());
            
            // Main content in header
            JPanel headerContent = new JPanel();
            headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
            headerContent.setOpaque(false);
            headerContent.setAlignmentX(CENTER_ALIGNMENT);
            
            // Beautiful icon
            JLabel welcomeIcon = new JLabel("📖") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Background glow
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillOval(10, 10, 80, 80);
                    
                    super.paintComponent(g);
                }
            };
            welcomeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            welcomeIcon.setAlignmentX(CENTER_ALIGNMENT);
            welcomeIcon.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            // Main title
            JLabel title = new JLabel("Welcome to Arabic Prose");
            title.setFont(new Font("Segoe UI", Font.BOLD, 48));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(CENTER_ALIGNMENT);
            
            // Subtitle
            JLabel subtitle = new JLabel("Your Gateway to Arabic Literature Analysis");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            subtitle.setForeground(new Color(226, 232, 240));
            subtitle.setAlignmentX(CENTER_ALIGNMENT);
            subtitle.setBorder(new EmptyBorder(10, 0, 0, 0));
            
            headerContent.add(welcomeIcon);
            headerContent.add(title);
            headerContent.add(subtitle);
            
            header.add(headerContent);
            return header;
        }
        
        private JPanel createBeautifulCenter() {
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setOpaque(false);
            centerPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
            
            // Simple feature showcase
            JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 30, 30));
            featuresPanel.setOpaque(false);
            featuresPanel.setMaximumSize(new Dimension(1000, 200));
            
            // Feature 1
            featuresPanel.add(createSimpleFeature("👨‍🎨", "Authors", "Manage literary contributors"));
            featuresPanel.add(createSimpleFeature("📚", "Books", "Organize your library"));
            featuresPanel.add(createSimpleFeature("🔍", "Analysis", "Advanced text processing"));
            
            // Main call to action
            JPanel ctaPanel = new JPanel();
            ctaPanel.setLayout(new BoxLayout(ctaPanel, BoxLayout.Y_AXIS));
            ctaPanel.setOpaque(false);
            ctaPanel.setAlignmentX(CENTER_ALIGNMENT);
            ctaPanel.setBorder(new EmptyBorder(60, 0, 0, 0));
            
            JLabel readyText = new JLabel("Ready to explore Arabic literature?");
            readyText.setFont(new Font("Segoe UI", Font.BOLD, 24));
            readyText.setForeground(new Color(30, 41, 59));
            readyText.setAlignmentX(CENTER_ALIGNMENT);
            
            JLabel startText = new JLabel("Begin your journey with a single click");
            startText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            startText.setForeground(new Color(100, 116, 139));
            startText.setAlignmentX(CENTER_ALIGNMENT);
            startText.setBorder(new EmptyBorder(10, 0, 30, 0));
            
            // Beautiful start button
            JButton startButton = new BeautifulButton("Start Your Journey");
            startButton.setAlignmentX(CENTER_ALIGNMENT);
            startButton.setMaximumSize(new Dimension(250, 60));
            startButton.addActionListener(e -> showMainDashboard());
            
            ctaPanel.add(readyText);
            ctaPanel.add(startText);
            ctaPanel.add(startButton);
            
            centerPanel.add(featuresPanel);
            centerPanel.add(ctaPanel);
            
            return centerPanel;
        }
        
        private JPanel createSimpleFeature(String icon, String title, String description) {
            JPanel feature = new JPanel();
            feature.setLayout(new BoxLayout(feature, BoxLayout.Y_AXIS));
            feature.setOpaque(false);
            feature.setAlignmentX(CENTER_ALIGNMENT);
            
            // Icon with elegant background
            JLabel iconLabel = new JLabel(icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Circular background
                    g2d.setColor(new Color(255, 255, 255, 20));
                    g2d.fillOval(0, 0, 80, 80);
                    
                    // Border
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawOval(0, 0, 80, 80);
                    
                    super.paintComponent(g);
                }
            };
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            iconLabel.setPreferredSize(new Dimension(80, 80));
            iconLabel.setAlignmentX(CENTER_ALIGNMENT);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Title
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titleLabel.setForeground(new Color(30, 41, 59));
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            titleLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
            
            // Description
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descLabel.setForeground(new Color(100, 116, 139));
            descLabel.setAlignmentX(CENTER_ALIGNMENT);
            
            feature.add(iconLabel);
            feature.add(titleLabel);
            feature.add(descLabel);
            
            return feature;
        }
        
        public void startFloatingAnimation() {
            floatingTimer = new Timer(50, e -> {
                floatOffset += 0.03f;
                for (FloatingShape shape : floatingShapes) {
                    shape.update();
                }
                repaint();
            });
            floatingTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw floating shapes
            for (FloatingShape shape : floatingShapes) {
                shape.draw(g2d);
            }
        }
        
        private class FloatingShape {
            private float x, y;
            private float size;
            private float speed;
            private Color color;
            private float rotation;
            
            public FloatingShape() {
                Random rand = new Random();
                this.x = rand.nextFloat() * getWidth();
                this.y = rand.nextFloat() * getHeight();
                this.size = 20 + rand.nextFloat() * 60;
                this.speed = 0.2f + rand.nextFloat() * 0.3f;
                this.color = new Color(255, 255, 255, 10 + rand.nextInt(20));
                this.rotation = rand.nextFloat() * 360;
            }
            
            public void update() {
                y -= speed;
                rotation += 0.5f;
                
                if (y < -size) {
                    y = getHeight() + size;
                    x = (float) (Math.random() * getWidth());
                }
            }
            
            public void draw(Graphics2D g2d) {
                g2d.setColor(color);
                
                AffineTransform oldTransform = g2d.getTransform();
                g2d.rotate(Math.toRadians(rotation), x + size/2, y + size/2);
                
                // Draw different shapes for variety
                if (size > 40) {
                    // Hexagon
                    Polygon hexagon = new Polygon();
                    for (int i = 0; i < 6; i++) {
                        double angle = 2 * Math.PI / 6 * i;
                        int px = (int)(x + size/2 + size/2 * Math.cos(angle));
                        int py = (int)(y + size/2 + size/2 * Math.sin(angle));
                        hexagon.addPoint(px, py);
                    }
                    g2d.fill(hexagon);
                } else {
                    // Circle
                    g2d.fillOval((int)x, (int)y, (int)size, (int)size);
                }
                
                g2d.setTransform(oldTransform);
            }
        }
    }

    /**
     * Beautiful Animated Button
     */
    private static class BeautifulButton extends JButton {
        private boolean isHovered = false;
        private float glowIntensity = 0f;
        
        public BeautifulButton(String text) {
            super(text);
            
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(20, 40, 20, 40));
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    startGlowAnimation();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                }
            });
        }
        
        private void startGlowAnimation() {
            Timer timer = new Timer(30, e -> {
                if (isHovered && glowIntensity < 1f) {
                    glowIntensity += 0.1f;
                    repaint();
                } else if (!isHovered && glowIntensity > 0f) {
                    glowIntensity -= 0.1f;
                    repaint();
                }
            });
            timer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Glow effect
            if (glowIntensity > 0) {
                int glowAlpha = (int)(80 * glowIntensity);
                g2d.setColor(new Color(139, 92, 246, glowAlpha));
                for (int i = 1; i <= 5; i++) {
                    g2d.fillRoundRect(-i, -i, width + i*2, height + i*2, 40, 40);
                }
            }
            
            // Main gradient
            Color startColor = new Color(99, 102, 241);
            Color endColor = new Color(139, 92, 246);
            if (isHovered) {
                startColor = startColor.brighter();
                endColor = endColor.brighter();
            }
            
            GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, width, height, 40, 40);
            
            // Text shadow for better readability
            g2d.setColor(new Color(0, 0, 0, 30));
            FontMetrics fm = g2d.getFontMetrics();
            String text = getText();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int x = (width - textWidth) / 2;
            int y = (height - textHeight) / 2 + fm.getAscent();
            g2d.drawString(text, x + 1, y + 1);
            
            // Main text
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);
            
            // Add a subtle arrow on hover
            if (isHovered) {
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int arrowX = x + textWidth + 10;
                g2d.drawLine(arrowX, y - 5, arrowX + 5, y);
                g2d.drawLine(arrowX, y + 5, arrowX + 5, y);
            }
        }
    }

    /**
     * Shows the main dashboard
     */
    private static void showMainDashboard() {
        // Stop floating animation when leaving instruction page
        if (floatingTimer != null && floatingTimer.isRunning()) {
            floatingTimer.stop();
        }
        
        // Initialize Pages with the facade
        AuthorPL authorPL = new AuthorPL(facade);
        BookPL bookPL = new BookPL(facade);
        SentencePL sentencePL = new SentencePL(facade);
        BrowserPL browserPL = new BrowserPL(facade);
        SearchPL searchPL = new SearchPL(facade);
        FrequencyAnalysisPL frequencyAnalysisPL = new FrequencyAnalysisPL(facade);
        
        // Set up observer pattern for auto-refresh
        authorPL.addObserver(bookPL);
        bookPL.setAuthorChangeObserver(authorPL);

        // Create the Main Menu Dashboard
        JPanel dashboardPanel = createDashboard();

        // Add Pages to CardLayout (Wrapped with Navigation Headers)
        mainContainer.add(dashboardPanel, "MENU");
        mainContainer.add(createPageWrapper(authorPL, ""), "AUTHORS");
        mainContainer.add(createPageWrapper(bookPL, ""), "BOOKS");
        mainContainer.add(createPageWrapper(sentencePL, ""), "SENTENCES");
        mainContainer.add(createPageWrapper(browserPL, ""), "BROWSER");
        mainContainer.add(createPageWrapper(searchPL, ""), "SEARCH");
        mainContainer.add(createPageWrapper(frequencyAnalysisPL, ""), "FREQUENCY");

        cardLayout.show(mainContainer, "MENU");
    }

    /**
     * Creates the Main Menu Dashboard with a Grid of Cards
     */
    private static JPanel createDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG_COLOR);

        // 1. Large Hero Header with Beautiful Exit Icon
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Beautiful gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241), 
                                                   getWidth(), getHeight(), new Color(139, 92, 246));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle pattern
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        if ((i + j) % 80 == 0) {
                            g2d.fillOval(i, j, 3, 3);
                        }
                    }
                }
            }
        };
        header.setPreferredSize(new Dimension(1200, 220));
        
        // Title in center
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Arabic Prose System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("Manage Authors, Books, And Morphological Analysis");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(224, 231, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        titlePanel.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
        titlePanel.add(subtitle, gbc);
        
        // Beautiful exit icon in top-right corner (ONLY IN MAIN MENU)
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitPanel.setOpaque(false);
        exitPanel.setBorder(new EmptyBorder(25, 0, 0, 35));
        
        BeautifulExitIcon exitButton = new BeautifulExitIcon();
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                mainContainer, 
                "Are you sure you want to exit Arabic Prose System?", 
                "Confirm Exit", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        exitPanel.add(exitButton);
        
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(exitPanel, BorderLayout.EAST);

        // 2. Menu Grid (The Cards) with Beautiful Colors and Icons
        JPanel menuGrid = new JPanel(new GridLayout(2, 3, 25, 25));
        menuGrid.setBackground(BG_COLOR);
        menuGrid.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Add Menu Options with Beautiful Related Colors and Icons
        menuGrid.add(new MenuCard("Authors", "Manage contributors", "AUTHORS", AUTHORS_COLOR, "👨‍🎨"));
        menuGrid.add(new MenuCard("Books", "Library & Chapters", "BOOKS", BOOKS_COLOR, "📚"));
        menuGrid.add(new MenuCard("Sentences", "View Text Data", "SENTENCES", SENTENCES_COLOR, "📝"));
        menuGrid.add(new MenuCard("Browser", "Tokens & Roots", "BROWSER", BROWSER_COLOR, "🔍"));
        menuGrid.add(new MenuCard("Search", "Deep Query", "SEARCH", SEARCH_COLOR, "🔎"));
        menuGrid.add(new MenuCard("Frequency", "Token Analysis", "FREQUENCY", FREQUENCY_COLOR, "📊"));

        dashboard.add(header, BorderLayout.NORTH);
        dashboard.add(menuGrid, BorderLayout.CENTER);

        return dashboard;
    }

    /**
     * Beautiful Exit Icon for Main Menu Only
     */
    private static class BeautifulExitIcon extends JButton {
        private boolean isHovered = false;
        private float pulse = 0f;
        
        public BeautifulExitIcon() {
            setPreferredSize(new Dimension(50, 50));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText("Exit Application");
            
            // Pulse animation timer
            Timer pulseTimer = new Timer(50, e -> {
                pulse += 0.1f;
                if (pulse > 2 * Math.PI) pulse = 0;
                repaint();
            });
            pulseTimer.start();
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            
            // Pulsing glow effect
            if (isHovered) {
                float glowIntensity = (float) (0.3f + 0.2f * Math.sin(pulse));
                g2d.setColor(new Color(239, 68, 68, (int)(80 * glowIntensity)));
                for (int i = 1; i <= 3; i++) {
                    g2d.fillOval(x - i, y - i, size + i*2, size + i*2);
                }
            }
            
            // Background circle with gradient
            Color startColor = isHovered ? new Color(220, 38, 38) : new Color(239, 68, 68);
            Color endColor = isHovered ? new Color(185, 28, 28) : new Color(220, 38, 38);
            GradientPaint gradient = new GradientPaint(0, y, startColor, 0, y + size, endColor);
            g2d.setPaint(gradient);
            g2d.fillOval(x, y, size, size);
            
            // Outer ring
            g2d.setColor(isHovered ? new Color(255, 255, 255, 150) : new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(x, y, size, size);
            
            // Beautiful door exit icon
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int centerX = x + size/2;
            int centerY = y + size/2;
            int iconSize = size - 20;
            
            // Draw door frame
            g2d.drawRect(centerX - iconSize/2, centerY - iconSize/2, iconSize, iconSize);
            
            // Draw door
            int doorWidth = iconSize - 8;
            g2d.fillRect(centerX - doorWidth/2, centerY - iconSize/2 + 4, doorWidth, iconSize - 8);
            
            // Draw door handle
            g2d.setColor(new Color(253, 230, 138)); // Gold color for handle
            g2d.fillOval(centerX + doorWidth/2 - 8, centerY, 6, 6);
            
            // Draw exit arrow
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int arrowStartX = centerX - iconSize/2 - 5;
            g2d.drawLine(arrowStartX, centerY, arrowStartX - 8, centerY);
            g2d.drawLine(arrowStartX - 8, centerY, arrowStartX - 4, centerY - 4);
            g2d.drawLine(arrowStartX - 8, centerY, arrowStartX - 4, centerY + 4);
        }
    }

    /**
     * Wraps a functional PL panel with a Navigation Header
     */
    /**
     * Wraps a functional PL panel with a Navigation Header
     */
    private static JPanel createPageWrapper(JPanel contentPL, String pageTitle) {
        JPanel wrapper = new JPanel(new BorderLayout());
        
        // 1. Navigation Bar (NO EXIT BUTTON HERE - only in main menu)
        JPanel navBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_START, getWidth(), 0, HEADER_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        // Reduced height from 70 to 50
        navBar.setPreferredSize(new Dimension(1200, 50));
        navBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Home Button
        JButton homeBtn = new JButton(" \u2190 Dashboard ");
        homeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Slightly smaller font
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setBackground(new Color(255, 255, 255, 40));
        homeBtn.setFocusPainted(false);
        homeBtn.setBorderPainted(false);
        homeBtn.setContentAreaFilled(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        // Page Title
        JLabel titleLbl = new JLabel(pageTitle);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Slightly smaller font
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setHorizontalAlignment(JLabel.CENTER);

        // Dummy panel to balance layout (NO EXIT BUTTON)
        JPanel dummy = new JPanel(); 
        dummy.setOpaque(false);
        dummy.setPreferredSize(new Dimension(50, 10));

        navBar.add(homeBtn, BorderLayout.WEST);
        navBar.add(titleLbl, BorderLayout.CENTER);
        navBar.add(dummy, BorderLayout.EAST);

        wrapper.add(navBar, BorderLayout.NORTH);
        wrapper.add(contentPL, BorderLayout.CENTER);
        
        return wrapper;
    }
    /**
     * Custom "Card" Button for the Menu with Beautiful Colors and Icons
     */
    private static class MenuCard extends JButton {
        private Color accentColor;
        private boolean isHovered = false;
        private String icon;

        public MenuCard(String title, String subtitle, String targetCard, Color accent, String icon) {
            this.accentColor = accent;
            this.icon = icon;
            
            setLayout(new GridBagLayout());
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(280, 160));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            
            // Beautiful Icon with circular background
            JLabel iconLabel = new JLabel(icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Circular background with accent color
                    Color bgColor = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 20);
                    g2d.setColor(bgColor);
                    g2d.fillOval(5, 5, 50, 50);
                    
                    // Border
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawOval(5, 5, 50, 50);
                    
                    super.paintComponent(g);
                }
            };
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            iconLabel.setPreferredSize(new Dimension(60, 60));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, gbc);

            // Title
            gbc.gridy = 1;
            gbc.insets = new Insets(10, 0, 0, 0);
            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleLbl.setForeground(TEXT_DARK);
            add(titleLbl, gbc);

            // Subtitle
            gbc.gridy = 2;
            gbc.insets = new Insets(5, 0, 0, 0);
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subLbl.setForeground(TEXT_LIGHT);
            add(subLbl, gbc);

            // Events
            addActionListener(e -> {
                if (!targetCard.equals("EXIT")) {
                    cardLayout.show(mainContainer, targetCard);
                }
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Shadow and Background
            if (isHovered) {
                g2.setColor(new Color(210, 210, 210));
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
                
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 20, 20);
                
                // Beautiful accent border on hover
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 20, 20);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
                
                // Subtle accent border
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-10, getHeight()-10, 20, 20);
            }
            
            super.paintComponent(g);
        }
    }
}