package nomeGruppo.eathome.actions;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import nomeGruppo.eathome.foods.Food;

public class Order implements Serializable {

    public int idOrder;
    public String addressOrder;
    public String addressNumOrder; // TODO da eliminare se si può isolare il numero dall'indirizzo
    public Date dateOrder;
    public LocalTime timeOrder;
    public HashMap<Food, Integer> foodsOrder;
    public OrderState orderStateOrder;
    public int idClientOrder;
    public int idPlaceOrder;


    public Order() {

    }

    public Order(String address, LocalTime time, int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.addressOrder = address;
        this.dateOrder = Calendar.getInstance().getTime(); //imposta in automatico la data corrente per l'ordine
        this.timeOrder =time;
        this.foodsOrder = new HashMap<>();
        this.orderStateOrder=OrderState.PENDING_CONFIRMATION;
        this.idClientOrder = idClient;
        this.idPlaceOrder = idPlace;
    }

    public void addFood(Food food, int quantity){
        this.foodsOrder.put(food, quantity);
    }

    public void setOrderState(OrderState orderState){
        this.orderStateOrder=orderState;
    }
    public String getFormattedDateOrder(){

        if(dateOrder != null){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            return dateFormat.format(dateOrder);
        }else{
            NullPointerException e = new NullPointerException();
            e.printStackTrace();
            throw e;
        }

    }

    public void setFoodsOrder(HashMap<Food, Integer> foodsOrder) {
        this.foodsOrder = foodsOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public void setAddressOrder(String addressOrder) {
        this.addressOrder = addressOrder;
    }

    public void setAddressNumOrder(String addressNumOrder) {
        this.addressNumOrder = addressNumOrder;
    }

    public void setDateOrder(Date dateOrder) {
        this.dateOrder = dateOrder;
    }

    public void setTimeOrder(LocalTime timeOrder) {
        this.timeOrder = timeOrder;
    }

    public void setOrderStateOrder(OrderState orderStateOrder) {
        this.orderStateOrder = orderStateOrder;
    }

    public void setIdClientOrder(int idClientOrder) {
        this.idClientOrder = idClientOrder;
    }

    public void setIdPlaceOrder(int idPlaceOrder) {
        this.idPlaceOrder = idPlaceOrder;
    }
}
