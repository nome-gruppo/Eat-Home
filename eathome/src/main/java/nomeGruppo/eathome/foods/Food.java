package nomeGruppo.eathome.foods;

import java.util.HashSet;

public class Food {

    private String name;
    private HashSet<String> ingredients;

    public Food(String name){
        this.name=name;
        this.ingredients= new HashSet<>();
    }

    public boolean addIngredient(String ingredient){
        return this.ingredients.add(ingredient);
    }

    public boolean removeIngredient(String ingredient){
        return this.ingredients.remove(ingredient);
    }
}
