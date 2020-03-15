package nomeGruppo.eathome.actions;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import nomeGruppo.eathome.actors.Food;

public class Order {

    private String addressOrder;
    private String addressNumOrder;
    private Date dateOrder;
    private LocalTime timeOrder;
    private HashMap<Food, Integer> foods;
    private OrderState orderState;

    public Order(String addressOrder, LocalTime timeOrder){
        this.addressOrder=addressOrder;
        this.dateOrder= Calendar.getInstance().getTime(); //imposta in automatico la data corrente per l'ordine
        this.timeOrder=timeOrder;
        this.foods = new HashMap<>();
        this.orderState=OrderState.PENDING_CONFIRMATION;
    }

    public void addFood(Food food, int quantity){
        this.foods.put(food, quantity);
    }

    public void setOrderState(OrderState orderState){
        this.orderState=orderState;
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
