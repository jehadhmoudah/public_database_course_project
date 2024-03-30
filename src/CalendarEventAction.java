import java.sql.SQLException;

@FunctionalInterface
public interface CalendarEventAction {
    void execute(CalendarEvent event) throws SQLException;
}
