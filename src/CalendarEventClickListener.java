import java.sql.SQLException;
import java.util.EventListener;

public interface CalendarEventClickListener extends EventListener {
    // Event dispatch methods
    void calendarEventClick(CalendarEventClickEvent e) throws SQLException;
}
