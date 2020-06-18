package nomeGruppo.eathome.foods;

import java.util.List;

public class Menu {

    public String idPlace;
    public List<Food> listFood;

    public Menu() {

    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public void setListFood(List<Food> listFood) {
        this.listFood = listFood;
    }
}
