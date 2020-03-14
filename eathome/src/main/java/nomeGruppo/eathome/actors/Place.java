package nomeGruppo.eathome.actors;

import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Place {
    String namePlace;
    String phonePlace;
    List<Order> orderPlace;
    List<Booking> bookingPlace;

    Place(String name, String phone){
        this.namePlace=name;
        this.phonePlace=phone;
        this.orderPlace=null;
        this.bookingPlace=null;
    }
}
