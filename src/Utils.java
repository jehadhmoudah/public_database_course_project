import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Date;

public class Utils {
    public static Connection dbConnection;
    public static ImageIcon sizedImage(String filename, int size) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage(); // transform it
        Image newImg = image.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
    public static ImageIcon sizedImage(String filename, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage(); // transform it
        Image newImg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
    public static String convertLocalDateTimeToPostgresTimeWithTimezone(String inputTime) {
       return inputTime.replace("T", " ").substring(0, 16);
    }
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        Random rnd = new Random();
        while (count-- != 0) {
            int character = (int)(rnd.nextFloat() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    public static long convertDateStringToMillis(String dateString) {
        try {
            // Define the date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

            // Parse the date string to a Date object
            var date =  dateFormat.parse(dateString);

            // Get the time in milliseconds
            return date.getTime();
        } catch (ParseException e) {
//            e.printStackTrace();
            return -1; // Return a sentinel value or handle the error as appropriate
        }
    }

    public static String calculateTime(double moneyBalance, double hourlyRate) {
        // Calculate the time in hours
        double hours = moneyBalance / hourlyRate;

        // Convert hours to hours and minutes
        int totalMinutes = (int) (hours * 60);
        int calculatedHours = totalMinutes / 60;
        int calculatedMinutes = totalMinutes % 60;

        // Format the result as "hh:mm"
        return String.format("%02d:%02d", calculatedHours, calculatedMinutes);
    }
    public static String convertMillisToHoursMinutesString(long milliseconds) {
        // Calculate total minutes from milliseconds
        long totalMinutes = milliseconds / (60 * 1000);

        // Calculate hours and remaining minutes
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        // Format the result as "hh:mm"
        return String.format("%02d:%02d", hours, minutes);
    }
    public static Color changeAlpha(Color color, int alpha) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("Alpha value must be between 0 and 255");
        }
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    public static String convertMinutesToHoursMinutesString(long minutes) {

        // Calculate hours and remaining minutes
        long hours = minutes / 60;
        minutes = minutes% 60;
        // Format the result as "hh:mm"
        return String.format("%02d:%02d", hours, minutes);
    }

    public static Object[] getRowData(JTable table, int rowIndex) {
        int columnCount = table.getColumnCount();
        Object[] rowData = new Object[columnCount];

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            rowData[columnIndex] = table.getModel().getValueAt(rowIndex, columnIndex);
        }

        return rowData;
    }
    public static int convertTimeToMinutes(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid time format: " + timeStr);
            }

            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
                throw

                        new IllegalArgumentException("Invalid time value: " + timeStr);
            }

            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time format: " + timeStr);
        }
    }
    public static String convertMillisToTime(long milliseconds) {
        // Create a Date object using milliseconds
        Date date = new Date(milliseconds);

        // Define the time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        // Format the date to the desired time format
        return dateFormat.format(date);
    }
    public static String convertMillisToDateTimeString(long milliseconds) {
        // Create a Date object using milliseconds
        Date date = new Date(milliseconds);

        // Define the time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        // Format the date to the desired time format
        return dateFormat.format(date);
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTimeStr) {
        // Define the formatter with the expected pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssX");

        // Parse the string to an OffsetDateTime
        OffsetDateTime odt = OffsetDateTime.parse(dateTimeStr, formatter);

        // Convert OffsetDateTime to LocalDateTime
        return odt.toLocalDateTime();
    }
    public static String convertMinutesToTime(int minutes) {
        // Convert minutes to milliseconds
        long milliseconds = minutes * 60 * 1000;

        // Create a Date object using milliseconds
        Date date = new Date(milliseconds);

        // Define the time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        // Format the date to the desired time format
        return dateFormat.format(date);
    }

    public static ImageIcon sizedIcon(String filename, int size) {
        return Utils.colorizeIcon(Utils.sizedImage(filename, size), UIManager.getColor("Label.foreground"));
    }
    public static ImageIcon sizedIcon(String filename, int size, Color color) {
        return Utils.colorizeIcon(Utils.sizedImage(filename, size), color != null? color : UIManager.getColor("Label.foreground"));
    }


    public static ImageIcon colorizeIcon(ImageIcon originalIcon, Color color) {
        // Get the Image from the ImageIcon
        Image image = originalIcon.getImage();

        // Create a BufferedImage to manipulate pixel data
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );

        // Get the graphics context of the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // Draw the original image onto the buffered image
        g2d.drawImage(image, 0, 0, null);

        // Set the desired color
        g2d.setColor(color);

        // Change the color of the buffered image using a composite
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Dispose of the graphics context
        g2d.dispose();

        // Create a new ImageIcon from the modified buffered image
        return new ImageIcon(bufferedImage);
    }
    public static int[] extractHourAndMinutes(String dateTimeStr) {
        // Split the string by space to separate date and time
        String[] parts = dateTimeStr.split(" ");

        // Further split the time part by colon
        String[] timeParts = parts[1].split(":");

        // Extract hour and minute and convert them to integers
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Return them as an array
        return new int[]{hour, minute};
    }
    public static void slideInComponent(JComponent component, JFrame frame, int duration) {
        Point finalPosition = component.getLocation();
        Point startPosition = new Point(-component.getWidth(), finalPosition.y);

        component.setLocation(startPosition);

        new Timer(40, new ActionListener() {
            private long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                } else {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, elapsed / (float) duration);
                    int newX = (int) (startPosition.x + progress * (finalPosition.x - startPosition.x));

                    component.setLocation(newX, finalPosition.y);

                    if (progress >= 1.0f) {
                        ((Timer) e.getSource()).stop();
                    }
                }
                frame.repaint();
            }
        }) {
            { setInitialDelay(0); start(); }
        };
    }
    public static String convert24To12HourFormat(String time24) {
        try {
            // Create a new SimpleDateFormat instance for 24-hour format
            SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");

            // Parse the input time string
            Date date = format24.parse(time24);

            // Create a SimpleDateFormat instance for 12-hour format
            SimpleDateFormat format12 = new SimpleDateFormat("hh:mm a");

            // Return the formatted 12-hour format string
            return format12.format(date);
        } catch (ParseException e) {
            // Handle exception if the input string is not a valid time
            System.out.println("Invalid time format");
            return null;
        }
    }
    public static String addSpacesToCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        result.append(str.charAt(0)); // Add the first character as is

        // Iterate over the remaining characters
        for (int i = 1; i < str.length(); i++) {
            char currentChar = str.charAt(i);

            // Check if the current character is uppercase
            if (Character.isUpperCase(currentChar)) {
                result.append(" "); // Add a space before the uppercase character
            }

            result.append(currentChar); // Add the current character
        }

        return result.toString();
    }
    public static Color alphaBlend(Color color1, Color color2, float alpha) {
        float inverse_alpha = 1.0f - alpha;

        int red = (int) (inverse_alpha * color1.getRed() + alpha * color2.getRed());
        int green = (int) (inverse_alpha * color1.getGreen() + alpha * color2.getGreen());
        int blue = (int) (inverse_alpha * color1.getBlue() + alpha * color2.getBlue());

        return new Color(red, green, blue);
    }
    public static void fadeInComponent(JComponent component, int duration) {
        Color originalForeground = component.getForeground();
        Color originalBackground = component.getBackground();
        new Timer(40, new ActionListener() {
            private long startTime = -1;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                    component.setForeground(new Color(0, 0, 0, 0));
                    component.setBackground(new Color(0, 0, 0, 0));
                } else {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float progress = Math.min(1.0f, elapsed / (float) duration);

                    Color newForeground = new Color(
                            originalForeground.getRed(),
                            originalForeground.getGreen(),
                            originalForeground.getBlue(),
                            (int) (progress * originalForeground.getAlpha()));

                    Color newBackground = new Color(
                            originalBackground.getRed(),
                            originalBackground.getGreen(),
                            originalBackground.getBlue(),
                            (int) (progress * originalBackground.getAlpha()));

                    component.setForeground(newForeground);
                    component.setBackground(newBackground);

                    if (progress >= 1.0f) {
                        ((Timer) e.getSource()).stop();
                    }
                }
                component.repaint();
            }
        }) {
            { setInitialDelay(0); start(); }
        };
    }

    public static ResultSet executeQuery(String sql) {
        ResultSet resultSet = null;
        try {
            Statement statement = dbConnection.createStatement();
            resultSet = statement.executeQuery(sql);

        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return resultSet;
    }
    public static void initDBConnection() {
//        String url = "jdbc:postgresql://192.168.1.200:5432/SlashMainDB";
        String url = "jdbc:postgresql://localhost:5432/SlashMainDB";
        String user = "postgres";
        String password = "admin";

        try {
            // Load the JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish a connection
            dbConnection = DriverManager.getConnection(url, user, password);

            // Get DatabaseMetaData
            DatabaseMetaData metaData = dbConnection.getMetaData();

            // Retrieve the username
            String username = metaData.getUserName();

            // Close the connection
//            dbConnection.close();
        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
        }
    }

    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing
            byte[] encodedhash = digest.digest(password.getBytes());

            // Convert byte array into signum representation
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // Return the hashed password
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
