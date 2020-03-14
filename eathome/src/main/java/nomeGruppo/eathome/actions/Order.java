package nomeGruppo.eathome.actions;

import java.util.List;

import nomeGruppo.eathome.actors.Food;

public class Order {
    String adressOrder;
    String dataOrder;
    String timeOrder;
    List<Food> foods;

    public Order(String adressOrder, String dataOrder, String timeOrder){
        this.adressOrder=adressOrder;
        this.dataOrder=dataOrder;
        this.timeOrder=timeOrder;
    }
}
