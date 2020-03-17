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

    private int id;
    private String address;
    private String addressNum; // TODO da eliminare se si può isolare il numero dall'indirizzo
    private Date date;
    private LocalTime time;
    private HashMap<Food, Integer> foods;
    private OrderState orderState;
    private final int idClient;
    private final int idPlace;

    public Order(String address, LocalTime time, int idClient, int idPlace){
        //TODO leggi l'ultimo id dal database e incrementa uno
        this.address = address;
        this.date = Calendar.getInstance().getTime(); //imposta in automatico la data corrente per l'ordine
        this.time =time;
        this.foods = new HashMap<>();
        this.orderState=OrderState.PENDING_CONFIRMATION;
        this.idClient = idClient;
        this.idPlace = idPlace;
    }

    public void addFood(Food food, int quantity){
        this.foods.put(food, quantity);
    }

    public void setOrderState(OrderState orderState){
        this.orderState=orderState;
    }
    public String getFormattedDateOrder(){

        if(date != null){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            return dateFormat.format(date);
        }else{
            NullPointerException e = new NullPointerException();
            e.printStackTrace();
            throw e;
        }

    }
}
