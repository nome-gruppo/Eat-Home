package nomegruppo.eathome.foods;

import java.util.HashSet;

public class Food {

    public String nameFood;
    public float priceFood;
    public String ingredientsFood;

    public Food(){

    }

    public void setName(String name) {
        this.nameFood = name;
    }

    public void setPrice(float price) {
        this.priceFood = price;
    }

    public void setIngredients(String ingredients) {
        this.ingredientsFood = ingredients;
    }
}
