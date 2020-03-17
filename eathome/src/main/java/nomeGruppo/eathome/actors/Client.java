package nomeGruppo.eathome.actors;

import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Client {

    private int id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private List<Order> orders;
    private List<Booking> bookings;

    public Client(String name, String surname, String email){
        this.name=name;
        this.surname=surname;
        this.email=email;
    }

    public void setPhoneClient(String phoneClient){
        this.phone=phoneClient;
    }
}

