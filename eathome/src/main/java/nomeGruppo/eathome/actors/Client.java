package nomeGruppo.eathome.actors;

import java.io.Serializable;


public class Client implements Serializable {

    //gli attributi sono public così che il DataSnapshot di ritorno dal firebase possa accedere a questi campi
    public String idClient;
    public String nameClient;
    public String phoneClient;
    public String emailClient;

    public Client(){

    }

}

