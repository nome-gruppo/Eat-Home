package nomeGruppo.eathome.actions;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class Booking {

    private int idBooking;
    private Date dateBooking;
    private LocalTime timeBooking;
    private String personNumBooking;
    private BookingState bookingStateBooking;
    private final int idClientBooking;
    private final int idPlaceBooking;

    public Booking(Date date, LocalTime time, String personNum,
                   int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.dateBooking = date;
        this.timeBooking = time;
        this.personNumBooking = personNum;
        this.bookingStateBooking=BookingState.PENDING_CONFIRMATION;
        this.idClientBooking=idClient;
        this.idPlaceBooking=idPlace;
    }

    public void setBookingState(BookingState bookingState){
        this.bookingStateBooking=bookingState;
    }
}
