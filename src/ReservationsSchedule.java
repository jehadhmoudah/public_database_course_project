import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;

public class ReservationsSchedule extends WeekCalendar {
    public ReservationsSchedule(ArrayList<CalendarEvent> events) throws SQLException {
        super(events);
        setOnDoubleClickAction(e->new TicketEditor(((Reservation) e).getTicketId()).setVisible(true));
        getDataFromDB();
    }

    public void getDataFromDB() throws SQLException {
        ResultSet resultSet = Utils.executeQuery("SELECT * FROM public.tickets " +
                "WHERE creation_time >= CURRENT_DATE " +
                "AND creation_time < CURRENT_DATE + INTERVAL '1 day' AND schedule = true"
        );
        getEvents().clear();
        while (resultSet.next()) {
            var id = resultSet.getInt("id");
            var name = resultSet.getString("name");
            var room = resultSet.getString("room");
            var startsAt = Utils.convertStringToLocalDateTime(String.valueOf(resultSet.getString("starts_at")));
            var duration = resultSet.getInt("duration");
            var endsAt = startsAt.plusMinutes(duration);
            var reservation = new Reservation(
                    room,
                    name,
                    id,
                    LocalTime.of(startsAt.getHour(), startsAt.getMinute()),
                    LocalTime.of(endsAt.getHour(), endsAt.getMinute()));
            getEvents().add(reservation);
        }
        repaint();
    }

}
