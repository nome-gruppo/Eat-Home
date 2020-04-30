package nomegruppo.eathome.actions;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

import nomegruppo.eathome.actors.Place;

public class Booking {

    public String dateBooking;
    public String timeBooking;
    public int personNumBooking;
    public String nameBooking;
    public String idClientBooking;
    public Place placeBooking;

    public Booking(){

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

    public void setPlaceBooking(Place placeBooking) {
        this.placeBooking = placeBooking;
    }
}
