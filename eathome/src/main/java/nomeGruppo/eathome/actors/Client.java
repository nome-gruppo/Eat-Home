package nomeGruppo.eathome.actors;

import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Client {

    private int idClient;
    private String nameClient;
    private String surnameClient;
    private String phoneClient;
    private String emailClient;
    private List<Order> orderClient;
    private List<Booking> bookingClient;

    public Client(String name, String surname, String email){
        this.nameClient=name;
        this.surnameClient=surname;
        this.emailClient=email;
    }

    public void setPhoneClient(String phoneClient){
        this.phoneClient=phoneClient;
    }
}

