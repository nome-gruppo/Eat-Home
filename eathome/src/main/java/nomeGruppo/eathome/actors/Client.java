package nomeGruppo.eathome.actors;

import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Client {
    String nameClient;
    String surnameClient;
    String phoneClient;
    List<Order> orderClient;
    List<Booking> bookingClient;

    public Client(String name, String surname, String phone){
        this.nameClient=name;
        this.surnameClient=surname;
        this.phoneClient=phone;
    }
}

