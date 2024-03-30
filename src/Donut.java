// JEHAD HMOUDAH 12112410

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Donut extends JFrame {
    int centerX = 250;
    int centerY = 250;
    int originX = centerX - 20;
    int originY = centerY - 20;
    Graphics2D g2;
    int delay = 5;

    Donut() {
        this.setTitle("Java Emoji Homework - Jehad Hmoudah");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(centerX * 2, centerY * 2);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void paint(Graphics g) {
        g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        g2.setColor(new Color(94, 94, 94, 1));
        try {


            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX - 1 + (int) (70 * Math.cos(i + 0.5)),
                        originY - 15 + (int) (55 * Math.sin(i + 0.1)),
                        70,
                        90);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(103, 103, 103, 4));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX - 1 + (int) (70 * Math.cos(i + 0.2)),
                        originY - 15 + (int) (55 * Math.sin(i + 0.1)),
                        70,
                        90);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(183, 122, 79));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + (int) (70 * Math.cos(i + 0.08)),
                        originY + (int) (55 * Math.sin(i)),
                        50,
                        71);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(189, 134, 95, 210));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + (int) (70 * Math.cos(i + 0.08)),
                        originY + (int) (55 * Math.sin(i)),
                        50,
                        65);
                Thread.sleep(delay);
            }

            g2.setColor(new Color(255, 255, 255, 10));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + (int) (70 * Math.cos(i + 0.08)),
                        originY + (int) (55 * Math.sin(i)),
                        50,
                        60);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(183, 122, 79, 80));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + (int) (70 * Math.cos(i + 0.08)),
                        originY + (int) (55 * Math.sin(i)),
                        50,
                        55);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(112, 68, 39));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + (int) (70 * Math.cos(i + 0.08)),
                        originY + (int) (55 * Math.sin(i)),
                        50,
                        50);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(133, 84, 53, 60));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + 2 + (int) (70 * Math.cos(i + 0.08)),
                        originY + 6 + (int) (55 * Math.sin(i)),
                        40, 35);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(133, 84, 53, 50));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + 2 + (int) (70 * Math.cos(i + 0.1)),
                        originY + 6 + (int) (55 * Math.sin(i)),
                        40, 35);
                Thread.sleep(delay);
            }
            g2.setColor(new Color(255, 255, 255, 6));
            for (double i = 0; i < 2 * Math.PI; i += 0.1) {
                g2.fillOval(originX + 6 + (int) (70 * Math.cos(i + 0.01)),
                        originY + 12 + (int) (55 * Math.sin(i)),
                        30, 25);
                Thread.sleep(delay);
            }

            ArrayList<Color> toppingColors = new ArrayList<Color>(Arrays.asList(
                    Color.red,
                    Color.yellow,
                    Color.red,
                    Color.white,
                    Color.CYAN,
                    Color.MAGENTA,
                    Color.green,
                    Color.orange
            ));
            drawToppingCircle(65, toppingColors, 445641, 14414);
            drawToppingCircle(50, toppingColors, 1515151, 14151);
            drawToppingCircle(40, toppingColors, 5555, 74855);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void drawToppingCircle(int radius, ArrayList<Color> toppingColors, int angleSeed, int colorSeed) throws InterruptedException {
        Random angleRandom = new Random();
        Random colorRandom = new Random();
        angleRandom.setSeed(angleSeed);
        colorRandom.setSeed(colorSeed);
        double theta;
        for (double i = 0.28; i < 2 * Math.PI; i += (40 / (double) radius)) {
            theta = (angleRandom.nextInt(8) + 1) * Math.PI / (angleRandom.nextInt(6) + 1);
            g2.setColor(new Color(0, 0, 0, 11));
            drawTopping(originX + 24 + (int) ((radius + 15) * Math.cos(i)),
                    originY + 15 + (int) (radius * Math.sin(i)), theta);
            g2.setColor(toppingColors.get(colorRandom.nextInt(toppingColors.size())));
            drawTopping(originX + 22 + (int) ((radius + 15) * Math.cos(i)),
                    originY + 17 + (int) (radius * Math.sin(i)), theta
            );
            g2.setColor(new Color(0, 0, 0, 11));
            drawTopping(originX + 24 + (int) ((radius + 15) * Math.cos(i)),
                    originY + 15 + (int) (radius * Math.sin(i)), theta
            );
            g2.setColor(new Color(255, 255, 255, 15));
            drawTopping(originX + 21 + (int) ((radius + 15) * Math.cos(i)),
                    originY + 17 + (int) (radius * Math.sin(i)), theta);
            Thread.sleep(delay);

        }
    }

    private void drawTopping(int x, int y, double theta) {
        for (double i = 0; i < 1; i += 0.08)
            g2.fillOval(x + (int) (10 * i * Math.cos(theta)),
                    y + (int) (10 * i * Math.sin(theta)),
                    5,
                    5);
    }
}


// JEHAD HMOUDAH 12112410