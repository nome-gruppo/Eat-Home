package nomeGruppo.eathome.utility;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.foods.Food;

public class MenuAdapterForClient extends ArrayAdapter<Food> {

    public MenuAdapterForClient(Context context, int textViewResourceId,
                         List<Food> food) {
        super(context, textViewResourceId, food);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_menu_client, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNameFoodPlaceInfo);
        TextView price = (TextView)convertView.findViewById(R.id.txtPriceFoodPlaceInfo);
        TextView ingredients=(TextView)convertView.findViewById(R.id.txtIngredientsFoodPlaceInfo);
        final TextView counter=(TextView)convertView.findViewById(R.id.txtCounterFoodPlaceInfo);
        ImageButton btnDelete=(ImageButton) convertView.findViewById(R.id.btnDeleteFoodPlaceInfo);
        ImageButton btnAdd=(ImageButton)convertView.findViewById(R.id.btnAddFoodPlaceInfo);
        final Food food = getItem(position);
        title.setText(food.nameFood);
        price.setText(Float.toString(food.priceFood));
        ingredients.setText(food.ingredientsFood);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int  i=Integer.parseInt(counter.getText().toString());
                i++;
                counter.setText(Integer.toString(i));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(counter.getText().toString());
                if(i>0) {
                    i--;
                }
                counter.setText(Integer.toString(i));
            }
        });

        return convertView;
    }

}
