
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;

class Clock extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND_COLOR = new Color(24, 116, 205);

    public Clock() {
        AnalogClockPanel container = new AnalogClockPanel();
        add(container, BorderLayout.CENTER);
    }

}

class AnalogClockPanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    Thread t = new Thread(this);

    /**
     * The coordinates used to paint the clock hands.
     */
    int xHandSec, yHandSec, xHandMin, yHandMin, xHandHour, yHandHour;

    /**
     * The size of the clock.
     */

    private final int HORIZONTAL_SIZE = 250;
    private final int VERTICAL_SIZE = 250;

    private final int CLOCK_X = (HORIZONTAL_SIZE / 2);
    private final int CLOCK_Y = (VERTICAL_SIZE / 2) - 17;

    private final int TIME_STR_X = (HORIZONTAL_SIZE / 2);
    private final int TIME_STR_Y = (VERTICAL_SIZE / 2) + 110;

    /**
     * The length of the clock hands relative to the clock size.
     */
    private final int secondHandLength = HORIZONTAL_SIZE / 2 - 50;
    private final int minuteHandLength = HORIZONTAL_SIZE / 2 - 70;
    private final int hourHandLength = HORIZONTAL_SIZE / 2 - 100;

    /**
     * The distance of the dots from the origin (center of the clock).
     */
    private final int DISTANCE_DOT_FROM_ORIGIN = CLOCK_X - 40;

    private final int DIAMETER_BIG_DOT = 8;
    private final int DIAMETER_SMALL_DOT = 4;

    private final static Color GREY_COLOR = new Color(160, 160, 160);

    public AnalogClockPanel() {
        setMinimumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        setMaximumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        setPreferredSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
        setLayout(null);
        t.start();
    }

    /**
     * At each iteration we recalculate the coordinates of the clock hands,
     * and repaint everything.
     */
    public void run() {
        while (true) {
            try {
                int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
                int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
                int currentHour = Calendar.getInstance().get(Calendar.HOUR);

                xHandSec = minToLocation(currentSecond, secondHandLength).x;
                yHandSec = minToLocation(currentSecond, secondHandLength).y;
                xHandMin = minToLocation(currentMinute, minuteHandLength).x;
                yHandMin = minToLocation(currentMinute, minuteHandLength).y;
                xHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).x;
                yHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).y;
                repaint();
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }


    /**
     * Returns how much the hour hand should be ahead
     * according to the minutes value.
     * 04:00, return 0.
     * 04:12, return 1, so that we move the hour handle ahead of one dot.
     *
     * @param min The current minute.
     * @return The relative offset to add to the hour hand.
     */
    private int getRelativeHour(int min) {
        return min / 12;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(UIManager.getColor("Panel.background"));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(UIManager.getColor("Component.borderColor"));

        g2.setStroke(new BasicStroke(1)); // Set border thickness
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 20);
        g2.setColor(UIManager.getColor("Table.background"));
        g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
        // Draw the dots
        g2.setColor(GREY_COLOR);
        for (int i = 0; i < 60; i++) {

            Point dotCoordinates = minToLocation(i, DISTANCE_DOT_FROM_ORIGIN);
            g2.setColor((i <= Calendar.getInstance().get(Calendar.SECOND)) ?  UIManager.getColor("Component.accentColor") :  Utils.changeAlpha(UIManager.getColor("Component.accentColor"), 50));

            if (i % 5 == 0) {
                // big dot
                g2.fillOval(dotCoordinates.x - (DIAMETER_BIG_DOT / 2),
                        dotCoordinates.y - (DIAMETER_BIG_DOT / 2),
                        DIAMETER_BIG_DOT,
                        DIAMETER_BIG_DOT);
            } else {
                // small dot
                g2.fillOval(dotCoordinates.x - (DIAMETER_SMALL_DOT / 2),
                        dotCoordinates.y - (DIAMETER_SMALL_DOT / 2),
                        DIAMETER_SMALL_DOT,
                        DIAMETER_SMALL_DOT);
            }
        }

        // Draw the clock hands
        g2.setStroke(new BasicStroke(4));
        g2.setColor(UIManager.getColor("Label.foreground"));
        g2.drawLine(CLOCK_X, CLOCK_Y, xHandHour, yHandHour);
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(CLOCK_X, CLOCK_Y, xHandMin, yHandMin);
//        g2.setColor(Color.gray);
//        g2.setStroke(new BasicStroke(2));
//        g2.drawLine(CLOCK_X, CLOCK_Y, xHandSec, yHandSec);
        g2.setFont(new Font("digital-7", Font.BOLD, 20));
        g2.setColor(UIManager.getColor("Label.foreground"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   h:mm a");
        String time = format.format(new Date());
        g2.drawString(time, (int) (TIME_STR_X - ((time.length() / 2) * 7.7)), (TIME_STR_Y));
    }


    private Point minToLocation(int timeStep, int radius) {
        double t = 2 * Math.PI * (timeStep - 15) / 60;
        int x = (int) (CLOCK_X + radius * Math.cos(t));
        int y = (int) (CLOCK_Y + radius * Math.sin(t));
        return new Point(x, y);
    }
}