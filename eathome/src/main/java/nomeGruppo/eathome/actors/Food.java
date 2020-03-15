package nomeGruppo.eathome.actors;

import java.util.HashSet;

public class Food {

    private String nameFood;
    private HashSet<String> ingredients;

    public Food(String nameFood){
        this.nameFood=nameFood;
        this.ingredients= new HashSet<>();
    }

    public boolean addIngredient(String ingredient){
        return this.ingredients.add(ingredient);
    }

    public boolean removeIngredient(String ingredient){
        return this.ingredients.remove(ingredient);
    }
}
