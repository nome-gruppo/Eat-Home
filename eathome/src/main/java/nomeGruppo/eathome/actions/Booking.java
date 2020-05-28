package nomeGruppo.eathome.actions;

import java.lang.reflect.Field;
import java.util.Calendar;

public class Booking {

    public Calendar dateBooking;
    public int personNumBooking;
    public String nameBooking;
    public String idClientBooking;
    public String idPlaceBooking;
    public String namePlaceBooking;
    public String addressPlaceBooking;
    public String phonePlaceBooking;

    public static final String ID_CLIENT_FIELD = "idClientBooking";

    public Booking(){

    }

    public void setDateBooking(Calendar dateBooking) {
        this.dateBooking = dateBooking;
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

    public void setNamePlaceBooking(String namePlaceBooking) {
        this.namePlaceBooking = namePlaceBooking;
    }

    public void setAddressPlaceBooking(String addressPlaceBooking) {
        this.addressPlaceBooking = addressPlaceBooking;
    }

    public void setPhonePlaceBooking(String phonePlaceBooking) {
        this.phonePlaceBooking = phonePlaceBooking;
    }

    public void setIdPlaceBooking(String idPlaceBooking) {
        this.idPlaceBooking = idPlaceBooking;
    }
}
