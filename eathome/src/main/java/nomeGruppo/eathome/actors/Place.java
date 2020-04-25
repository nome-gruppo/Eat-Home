package nomeGruppo.eathome.actors;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

import nomeGruppo.eathome.utility.Days;

public class Place implements Serializable{
    //gli attributi sono public così che il DataSnapshot di ritorno dal firebase possa accedere a questi campi
    public String idPlace;
    public String addressNumPlace;
    public String addressPlace;
    public String categories;
    public String cityPlace;
    public int deliveryCost;
    public String emailPlace;
    public String namePlace;
    public String phonePlace;
    public float valuation;
    public int numberReview;
    public boolean takesBookingPlace;
    public boolean takesOrderPlace;
    public HashMap<String,String>openingTime;

    public Place(){

    }

    public Place(String addressNumPlace, String addressPlace, String cityPlace, int deliveryCost, String namePlace,String phonePlace) {
        this.addressNumPlace = addressNumPlace;
        this.addressPlace = addressPlace;
        this.categories = null;
        this.cityPlace = cityPlace;
        this.deliveryCost = deliveryCost;
        this.namePlace = namePlace;
        this.phonePlace = phonePlace;
        this.valuation = 0;
        this.numberReview = 0;
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

    public void setPhonePlace(String phonePlace) {
        this.phonePlace = phonePlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public void setOpeningTime(HashMap<String, String> openingTime) {
        this.openingTime = openingTime;
    }

    public void setValuation(float valuation) {
        this.valuation = valuation;
    }
}
