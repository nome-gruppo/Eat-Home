package nomeGruppo.eathome.actions;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class Booking {

    private int id;
    private Date date;
    private LocalTime time;
    private String personNum;
    private BookingState bookingState;
    private final int idClient;
    private final int idPlace;

    public Booking(Date date, LocalTime time, String personNumBooking,
                   int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.date = date;
        this.time = time;
        this.personNum = personNum;
        this.bookingState=BookingState.PENDING_CONFIRMATION;
        this.idClient=idClient;
        this.idPlace=idPlace;
    }

    public void setBookingState(BookingState bookingState){
        this.bookingState=bookingState;
    }
}
