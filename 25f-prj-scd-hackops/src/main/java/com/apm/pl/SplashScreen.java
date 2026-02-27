package main.java.com.apm.pl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Custom JWindow for a professional Splash Screen with fade-in/out effects,
 * modern design, and optimized transition timing, tailored for a library app aesthetic.
 */
public class SplashScreen extends JWindow {

    // Duration the splash screen STAYS visible (in MS)
    private static final int DISPLAY_DURATION_MS = 2500;
    
    // Professional Library Color Palette
    private static final Color LIBRARY_BLUE_DARK = new Color(28, 40, 60);       // Darkest Blue/Navy for gradient end
    private static final Color LIBRARY_BLUE_LIGHT = new Color(50, 70, 100);    // Lighter shade for gradient start
    // Removed SHADOW_COLOR since the shadow effect is now removed per user request
    private static final Color CREAM_TEXT = new Color(240, 240, 240);          // Soft white for main text
    private static final Color SUBTLE_GREY_TEXT = new Color(180, 180, 180);    // For credits/loading

    // Animation control
    private Timer fadeTimer;
    private float opacity = 0.0f;
    private boolean isFadingIn = true;

    public SplashScreen() {
      
        // INCREASED WIDTH to ensure the long title text fits perfectly
        setSize(900, 500); 
        setLocationRelativeTo(null);
        setOpacity(opacity);
        
        JPanel panel = createContentPanel();
        
        // --- Custom Drawing Panel (Used only for Gradient Background) ---
        JPanel gradientPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw the rounded background shape (full window size)
                g2d.setColor(LIBRARY_BLUE_DARK);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); 

                // Apply Inner Gradient
                GradientPaint gp = new GradientPaint(0, 0, LIBRARY_BLUE_LIGHT, 0, getHeight(), LIBRARY_BLUE_DARK);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2d.dispose();
            }
        };
        gradientPanel.setOpaque(false);
        gradientPanel.add(panel, BorderLayout.CENTER); // Add content panel on top of the gradient
        
        // The main window adds the panel with the gradient background
        add(gradientPanel);
    }

	/**
     * Builds the main content panel with app name, icon, and credits.
     */
    private JPanel createContentPanel() {
        // Content panel is transparent so the gradient panel behind it is visible
        JPanel content = new JPanel(new BorderLayout(0, 40)); 
        content.setOpaque(false); 
        // Adjusted padding for the 900px wide window
        content.setBorder(new EmptyBorder(80, 50, 50, 50)); 

        // App Icon 
        JLabel iconLabel;
        try {
            ImageIcon icon = new ImageIcon("bin/Openbook.png");
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH); 
            iconLabel = new JLabel(new ImageIcon(img), SwingConstants.CENTER);
            iconLabel.setBorder(new EmptyBorder(0,0,20,0)); 
        } catch (Exception e) {
            iconLabel = new JLabel("📚", SwingConstants.CENTER); 
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
            iconLabel.setForeground(CREAM_TEXT); 
            iconLabel.setBorder(new EmptyBorder(0,0,20,0)); 
        }
        
        // App Name Label - Ensures text fits in 900px wide screen
        JLabel titleLabel = new JLabel("Arabic Prose Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 46)); 
        titleLabel.setForeground(CREAM_TEXT); 
        
        // Version/Credits Label
        JLabel creditLabel = new JLabel("Developed by HackOps Team | Initializing Digital Archives...", SwingConstants.CENTER);
        creditLabel.setFont(new Font("SansSerif", Font.PLAIN, 18)); 
        creditLabel.setForeground(SUBTLE_GREY_TEXT); 
        
        // Combine Title and Icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(iconLabel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        content.add(headerPanel, BorderLayout.CENTER);
        content.add(creditLabel, BorderLayout.SOUTH);

        return content;
    }

    /**
     * Starts the fade-in, waits for duration, and starts fade-out.
     * @param callback The Runnable to execute after the fade-out is complete (launches main app).
     */
    public void startSplash(Runnable callback) {
        setVisible(true);

        fadeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFadingIn) {
                    opacity += 0.08f;
                    if (opacity >= 1.0f) {
                        opacity = 1.0f;
                        isFadingIn = false;
                        fadeTimer.stop();
                        
                        new Timer(DISPLAY_DURATION_MS, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((Timer) e.getSource()).stop();
                                startFadeOut(callback);
                            }
                        }).start();
                    }
                } else {
                    opacity -= 0.08f;
                    if (opacity <= 0.0f) {
                        opacity = 0.0f;
                        fadeTimer.stop();
                        dispose(); 
                        callback.run(); 
                        return;
                    }
                }
                setOpacity(opacity);
            }
        });
        fadeTimer.start();
    }
    
    /**
     * Starts the fade-out sequence.
     */
    private void startFadeOut(Runnable callback) {
        isFadingIn = false;
        fadeTimer.setDelay(30); 
        fadeTimer.start();
    }
}