package nomeGruppo.eathome.actors;

import java.io.Serializable;

import nomeGruppo.eathome.foods.Menu;
import nomeGruppo.eathome.utility.Categories;

public class Place implements Serializable {
    //gli attributi sono public così che il DataSnapshot di ritorno dal firebase possa accedere a questi campi
    public String idPlace;
    public String addressNumPlace;
    public String addressPlace;
    public String categories;
    public String cityPlace;
    public int deliveryCost;
    public String emailPlace;
    public String namePlace;
    public int passwordPlace;
    public String phonePlace;
    public boolean takesBookingPlace;
    public boolean takesOrderPlace;

    public Place(){

    }

    public Place(String addressNumPlace, String addressPlace, String cityPlace, int deliveryCost, String namePlace, int passwordPlace, String phonePlace) {
        this.addressNumPlace = addressNumPlace;
        this.addressPlace = addressPlace;
        this.categories = null;
        this.cityPlace = cityPlace;
        this.deliveryCost = deliveryCost;
        this.namePlace = namePlace;
        this.passwordPlace = passwordPlace;
        this.phonePlace = phonePlace;
        this.takesBookingPlace = false;
        this.takesOrderPlace = false;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setTakesBookingPlace(boolean takesBookingPlace) {
        this.takesBookingPlace = takesBookingPlace;
    }

    public void setTakesOrderPlace(boolean takesOrderPlace) {
        this.takesOrderPlace = takesOrderPlace;
    }

    public void setAddressNumPlace(String addressNumPlace) {
        this.addressNumPlace = addressNumPlace;
    }

    public void setAddressPlace(String addressPlace) {
        this.addressPlace = addressPlace;
    }

    public void setCityPlace(String cityPlace) {
        this.cityPlace = cityPlace;
    }

    public void setDeliveryCost(int deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setEmailPlace(String emailPlace) {
        this.emailPlace = emailPlace;
    }

    public void setNamePlace(String namePlace) {
        this.namePlace = namePlace;
    }

    public void setPasswordPlace(int passwordPlace) {
        this.passwordPlace = passwordPlace;
    }

    public void setPhonePlace(String phonePlace) {
        this.phonePlace = phonePlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }
}
