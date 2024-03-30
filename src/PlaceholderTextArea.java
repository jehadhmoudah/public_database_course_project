import javax.swing.*;
import java.awt.*;

public class PlaceholderTextArea extends JTextArea {

    private final String placeholder;

    public PlaceholderTextArea(String placeholder, int rows, int columns) {
        super(rows, columns);
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getDisabledTextColor());
        g2d.setFont(new Font("IBM Plex Sans Arabic", Font.PLAIN, 17));
        g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Placeholder TextArea Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.setSize(400, 200);

            PlaceholderTextArea textArea = new PlaceholderTextArea("Enter your text here...", 5, 20);
            frame.add(textArea);

            frame.setVisible(true);
        });
    }
}
