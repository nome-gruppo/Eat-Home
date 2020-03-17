package nomeGruppo.eathome.actors;

import java.util.ArrayList;
import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Place {

    private String name;
    private String phone;
    private String address;
    private String addressNumber; // TODO da eliminare se si può isolare il numero dall'indirizzo
    private ArrayList<Order> order;
    private ArrayList<Booking> booking;
    private boolean takesOrder;
    private boolean takesBooking;

    public Place(String name, String phone, String address, String addressNumber){
        this.name=name;
        this.phone=phone;
        this.address = address;
        this.address = addressNumber;
        this.order = new ArrayList<>();
        this.booking = new ArrayList<>();
    }
}
