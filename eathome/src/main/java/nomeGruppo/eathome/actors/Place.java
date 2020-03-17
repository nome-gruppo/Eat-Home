package nomeGruppo.eathome.actors;

import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;

public class Place {
    String namePlace;
    String cityPalce;
    String phonePlace;
    String adressPlace;
    String mailPlace;
    String passwordPlace;
    List<Order> orderPlace;
    List<Booking> bookingPlace;

    public Place(){

    }

    public Place(String name, String city, String phone,String adress,String mail,String password){
        this.namePlace=name;
        this.cityPalce=city;
        this.phonePlace=phone;
        this.adressPlace=adress;
        this.mailPlace=mail;
        this.passwordPlace=password;
        this.orderPlace=null;
        this.bookingPlace=null;
    }

    public String getNamePlace() {
        return namePlace;
    }

    public String getCityPalce() {
        return cityPalce;
    }

    public String getPhonePlace() {
        return phonePlace;
    }

    public String getAdressPlace() {
        return adressPlace;
    }

    public String getMailPlace() {
        return mailPlace;
    }

    public String getPasswordPlace() {
        return passwordPlace;
    }

    public List<Order> getOrderPlace() {
        return orderPlace;
    }

    public List<Booking> getBookingPlace() {
        return bookingPlace;
    }
}
