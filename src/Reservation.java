import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation extends CalendarEvent {
    private int ticketId;
    public int getTicketId(){
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    private static LocalDate roomToDateConverter (String room) {
        return switch (room) {
            case Room.G1 -> LocalDate.of(1969, 12, 29);
            case Room.G2 -> LocalDate.of(1969, 12, 30);
            case Room.G3 -> LocalDate.of(1969, 12, 31);
            case Room.G4 -> LocalDate.of(1970, 1, 1);
            case Room.VIP -> LocalDate.of(1970, 1, 2);
            default -> LocalDate.of(2000, 1, 1);
        };
    }
    public Reservation(String room, String text, int ticketId, LocalTime start, LocalTime end) {
        super(roomToDateConverter(room), start, end, text);
        setTicketId(ticketId);
    }
}
