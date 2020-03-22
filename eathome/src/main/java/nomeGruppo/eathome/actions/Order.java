package nomeGruppo.eathome.actions;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import nomeGruppo.eathome.foods.Food;

public class Order {

    private int idOrder;
    private String addressOrder;
    private String addressNumOrder; // TODO da eliminare se si può isolare il numero dall'indirizzo
    private Date dateOrder;
    private LocalTime timeOrder;
    private HashMap<Food, Integer> foodsOrder;
    private OrderState orderStateOrder;
    private final int idClientOrder;
    private final int idPlaceOrder;

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
}
