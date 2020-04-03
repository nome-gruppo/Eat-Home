package nomeGruppo.eathome.actions;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

public class Booking {

    public String idBooking;
    public String dateBooking;
    public String timeBooking;
    public int personNumBooking;
    public String nameBooking;
    public String idClientBooking;
    public String idPlaceBooking;

    public Booking(){

    }

    public void setIdBooking(String idBooking) {
        this.idBooking = idBooking;
    }

    public void setDateBooking(String dateBooking) {
        this.dateBooking = dateBooking;
    }

    public void setTimeBooking(String timeBooking) {
        this.timeBooking = timeBooking;
    }

    public void setPersonNumBooking(int personNumBooking) {
        this.personNumBooking = personNumBooking;
    }

    public void setNameBooking(String nameBooking) {
        this.nameBooking = nameBooking;
    }

    public void setIdClientBooking(String idClientBooking) {
        this.idClientBooking = idClientBooking;
    }

    public void setIdPlaceBooking(String idPlaceBooking) {
        this.idPlaceBooking = idPlaceBooking;
    }
}
