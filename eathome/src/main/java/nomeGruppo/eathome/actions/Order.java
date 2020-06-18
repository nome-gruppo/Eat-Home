package nomeGruppo.eathome.actions;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    public String idOrder;
    public String addressOrder;
    public String nameClientOrder;
    public String phoneClientOrder;
    public long timeOrder;
    public float timestampOrder;
    public ArrayList<String> foodsOrder;
    public float totalOrder;
    public String idClientOrder;
    public String idPlaceOrder;
    public String namePlaceOrder;
    public int deliveryCost;
    public String addressPlaceOrder;
    public String phonePlaceOrder;
    public String note;
    public boolean stateOrder;


    public Order() {

    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public void setFoodsOrder(ArrayList<String> foodsOrder) {
        this.foodsOrder = foodsOrder;
    }

    public void setAddressOrder(String addressOrder) {
        this.addressOrder = addressOrder;
    }

    public void setTimeOrder(long timeOrder) {
        this.timeOrder = timeOrder;
    }


    public void setIdClientOrder(String idClientOrder) {
        this.idClientOrder = idClientOrder;
    }

    public void setIdPlaceOrder(String idPlaceOrder) {
        this.idPlaceOrder = idPlaceOrder;
    }

    public void setDeliveryCost(int deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setNamePlaceOrder(String namePlaceOrder) {
        this.namePlaceOrder = namePlaceOrder;
    }

    public void setAddressPlaceOrder(String addressPlaceOrder) {
        this.addressPlaceOrder = addressPlaceOrder;
    }

    public void setPhonePlaceOrder(String phonePlaceOrder) {
        this.phonePlaceOrder = phonePlaceOrder;
    }

    public void setStateOrder(boolean stateOrder) {
        this.stateOrder = stateOrder;
    }

    public void setNameClientOrder(String nameClientOrder) {
        this.nameClientOrder = nameClientOrder;
    }

    public void setPhoneClientOrder(String phoneClientOrder) {
        this.phoneClientOrder = phoneClientOrder;
    }

    public void setTotalOrder(float totalOrder) {
        this.totalOrder = totalOrder;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
