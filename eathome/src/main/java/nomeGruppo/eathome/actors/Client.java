package nomeGruppo.eathome.actors;

import java.io.Serializable;
import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Client implements Serializable {

    //gli attributi sono public così che il DataSnapshot di ritorno dal firebase possa accedere a questi campi
    public String idClient;
    public String nameClient;
    public String surnameClient;
    public String phoneClient;
    public String emailClient;

    public Client(){

    }

    public void setNameClient(String nameClient) {
        this.nameClient = nameClient;
    }

    public void setSurnameClient(String surnameClient) {
        this.surnameClient = surnameClient;
    }

    public void setPhoneClient(String phoneClient) {
        this.phoneClient = phoneClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }


    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }
}

