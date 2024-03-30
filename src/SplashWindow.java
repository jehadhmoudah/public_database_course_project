import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class SplashWindow extends JWindow implements ActionListener {
    private BufferedImage splashImage;
    private Timer fadeTimer, dotTimer;
    private final float activeDotAlpha = 1.0f; // Alpha for active dot
    private final float inactiveDotAlpha = 0.5f; // Alpha for inactive dots
    private float alpha = 0.0f; // Start fully transparent
    private final float deltaAlpha = 0.05f; // Increment of alpha per step
    private final int padding = 20; // Padding around the image
    private final int cornerRadius = 30; // The radius of the rounded corners
    private final int numberOfDots = 3;
    private final float[] dotAlphas = new float[numberOfDots]; // Initialize the array here
    private int dotIndex = 0;
    private final Color dotColor = new Color(48,110,181); // Color of the dots
    private JPanel panel;

    public SplashWindow() {
        // Initialize dotAlphas
        for (int i = 0; i < numberOfDots; i++) {
            dotAlphas[i] = 0.5f; // Initial alpha value for each dot
        }

        // Load the image
        try {
            splashImage = ImageIO.read(getClass().getResource("/SlashLogo.png"));
        } catch (IOException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image could not be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

//        setUndecorated(true);
        initComponents();
        setupTimers();

        pack();
        setLocationRelativeTo(null);
        Timer closeTimer = new Timer(5000, e -> {
            dispose(); // Close the splash screen
            try {
                openMainFrame(); // Open the main frame
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        closeTimer.setRepeats(false); // Ensure the timer only fires once
        closeTimer.start();
    }
    private void openMainFrame() throws SQLException {
        Utils.initDBConnection();
        MainPage.setGlobalFont(new Font("IBM Plex Sans Arabic", Font.BOLD, 17));
        FlatMacLightLaf.setup();

        new MainPage();
    }
    private void initComponents() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Draw the image with fade effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(splashImage, padding, padding, this);

                // Draw loading dots
                drawLoadingDots(g2d);

                g2d.dispose();
            }

            private void drawLoadingDots(Graphics2D g2d) {
                int dotRadius = 5;
                int dotDiameter = dotRadius * 2;
                int spaceBetweenDots = 10;
                int totalWidth = (3 * dotDiameter) + (2 * spaceBetweenDots);
                int startX = (getWidth() - totalWidth) / 2;
                int y = splashImage.getHeight() + padding + 30;

                // Enable anti-aliasing for smoother dots
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(dotColor);

                for (int i = 0; i < numberOfDots; i++) {
                    int x = startX + i * (dotDiameter + spaceBetweenDots);
                    Ellipse2D dot = new Ellipse2D.Float(x, y, dotDiameter, dotDiameter);
                    float alpha = i == dotIndex ? activeDotAlpha : inactiveDotAlpha;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.fill(dot);
                }
            }

        };

        panel.setPreferredSize(new Dimension(
                splashImage.getWidth() + 2 * padding,
                splashImage.getHeight() + 2 * padding + 50));
        add(panel);

        // Set the window shape to be rounded
        setShape(new RoundRectangle2D.Double(
                0, 0, panel.getPreferredSize().width, panel.getPreferredSize().height, cornerRadius, cornerRadius));
    }

    private void setupTimers() {
        fadeTimer = new Timer(50, this);
        fadeTimer.start();

        dotTimer = new Timer(300, e -> {
            updateDotAlphas();
            panel.repaint();
        });
        dotTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (alpha < 1.0f) {
            alpha += deltaAlpha;
            if (alpha > 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop(); // Stop the fadeTimer after the fade-in completes
            }
            panel.repaint();
        }
    }

    private void updateDotAlphas() {
        float alphaDelta = 0.1f;
        for (int i = 0; i < numberOfDots; i++) {
            dotAlphas[i] += (i == dotIndex) ? alphaDelta : -alphaDelta;
            if (dotAlphas[i] > 1.0f) dotAlphas[i] = 1.0f;
            if (dotAlphas[i] < 0.5f) dotAlphas[i] = 0.5f;
        }
        dotIndex = (dotIndex + 1) % numberOfDots;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SplashWindow().setVisible(true));
    }
}
