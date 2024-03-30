import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class CalendarEvent {

    private static final Color DEFAULT_COLOR = Color.PINK;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String text;
    private final Color backgroundColor;
    private final Color foregroundColor;

    public CalendarEvent(LocalDate date, LocalTime start, LocalTime end, String text) {
        Color[] randomColor = getRandomColor();
        this.date = date;
        this.start = start;
        this.end = end;
        this.text = text;
        backgroundColor = randomColor[0];
        foregroundColor = randomColor[1];
    }
    private static final ArrayList<Color[]> predefinedColors = new ArrayList<>();

    static {
        // Add your predefined colors to the list
        predefinedColors.add(new Color[]{
                new Color(177,214,216),
                new Color(84, 101, 102)
        });
        predefinedColors.add(new Color[]{
                new Color(167,175,252),
                new Color(79, 83, 120)
        });
        predefinedColors.add(new Color[]{
                new Color(220,184,203),
                new Color(97, 83, 91)
        });
        predefinedColors.add(new Color[]{
                new Color(197,166,253),
                new Color(100, 84, 128)
        });
        predefinedColors.add(new Color[]{
                new Color(168,232,251),
                new Color(74, 100, 107)
        });
        predefinedColors.add(new Color[]{
                new Color(254,247,207),
                new Color(87, 78, 71)
        });
    }

    // Function to get a random color from the predefined list
    public Color[] getRandomColor() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(predefinedColors.size());
        return predefinedColors.get(randomIndex);
    }
    public CalendarEvent(LocalDate date, LocalTime start, LocalTime end, String text, Color backgroundColor, Color foregroundColor) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return getDate() + " " + getStart() + "-" + getEnd() + ". " + getText();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent that = (CalendarEvent) o;

        if (!date.equals(that.date)) return false;
        if (!start.equals(that.start)) return false;
        return end.equals(that.end);

    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
