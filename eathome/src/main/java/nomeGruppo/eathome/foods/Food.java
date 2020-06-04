package nomeGruppo.eathome.foods;

public class Food {
    public String idFood;
    public String nameFood;
    public float priceFood;
    public String ingredientsFood;

    public Food(){

    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
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
