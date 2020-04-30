package nomegruppo.eathome.actions;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import nomegruppo.eathome.actors.Place;
import nomegruppo.eathome.foods.Food;

public class Order implements Serializable {

    public String addressOrder;
    public String nameClientOrder;
    public String phoneClientOrder;
    public String timeOrder;
    public String dateOrder;
    public ArrayList<String> foodsOrder;
    public float totalOrder;
    public String idClientOrder;
    public Place placeOrder;


    public Order() {

    }

    public void setFoodsOrder(ArrayList<String> foodsOrder) {
        this.foodsOrder = foodsOrder;
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

    public void setPlaceOrder(Place placeOrder) {
        this.placeOrder = placeOrder;
    }

    public void setNameClientOrder(String nameClientOrder) {
        this.nameClientOrder = nameClientOrder;
    }

    public void setPhoneClientOrder(String phoneClientOrder) {
        this.phoneClientOrder = phoneClientOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public void setTotalOrder(float totalOrder) {
        this.totalOrder = totalOrder;
    }
}
