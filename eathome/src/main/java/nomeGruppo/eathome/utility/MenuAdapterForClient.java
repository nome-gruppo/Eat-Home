package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.foods.Food;

public class MenuAdapterForClient extends ArrayAdapter<Food> {
    private final HashMap<Food, Integer> order;

    public MenuAdapterForClient(Context context, int textViewResourceId,
                                List<Food> food, HashMap<Food, Integer> order) {
        super(context, textViewResourceId, food);
        this.order = order;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_menu_client, parent, false);
            final TextView title = convertView.findViewById(R.id.txtNameFoodPlaceInfo);
            final TextView counter = convertView.findViewById(R.id.txtCounterFoodPlaceInfo);
            final TextView price = convertView.findViewById(R.id.txtPriceFoodPlaceInfo);
            final TextView ingredients = convertView.findViewById(R.id.txtIngredientsFoodPlaceInfo);
            final ImageButton btnDelete = convertView.findViewById(R.id.btnDeleteFoodPlaceInfo);
            final ImageButton btnAdd = convertView.findViewById(R.id.btnAddFoodPlaceInfo);

            final Food food = getItem(position);

            if (food != null) {
                title.setText(food.nameFood);
                price.setText(String.format(Locale.getDefault(), "%.2f", food.priceFood));
                ingredients.setText(food.ingredientsFood);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i = Integer.parseInt(counter.getText().toString());
                        i++;
                        Toast.makeText(getContext(), "+ 1 " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                        counter.setText(String.format(Locale.getDefault(), "%d", i));
                        order.put(food, i);
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int i = Integer.parseInt(counter.getText().toString());
                        if (i > 0) {
                            i--;
                            Toast.makeText(getContext(), "- 1 " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                            counter.setText(String.format(Locale.getDefault(), "%d", i));
                            if (i == 0) {
                                order.remove(food);
                            } else {
                                order.put(food, i);
                            }
                        }
                    }
                });
            }
        }
        return convertView;
    }

}
