package nomeGruppo.eathome.actions;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import nomeGruppo.eathome.foods.Food;

public class Order implements Serializable {

    public String idOrder;
    public String addressOrder;
    public String nameClientOrder;
    public String phoneClientOrder;
    public String timeOrder;
    public ArrayList<String> foodsOrder;
    public String idClientOrder;
    public String idPlaceOrder;


    public Order() {

    }

    public void setFoodsOrder(ArrayList<String> foodsOrder) {
        this.foodsOrder = foodsOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public void setAddressOrder(String addressOrder) {
        this.addressOrder = addressOrder;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }


    public void setIdClientOrder(String idClientOrder) {
        this.idClientOrder = idClientOrder;
    }

    public void setIdPlaceOrder(String idPlaceOrder) {
        this.idPlaceOrder = idPlaceOrder;
    }

    public void setNameClientOrder(String nameClientOrder) {
        this.nameClientOrder = nameClientOrder;
    }

    public void setPhoneClientOrder(String phoneClientOrder) {
        this.phoneClientOrder = phoneClientOrder;
    }
}
