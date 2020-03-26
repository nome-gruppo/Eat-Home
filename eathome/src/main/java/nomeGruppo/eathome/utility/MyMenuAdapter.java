package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.foods.Menu;

public class MyMenuAdapter extends ArrayAdapter<Food> {

    public MyMenuAdapter(Context context, int textViewResourceId,
                         List<Food> food) {
        super(context, textViewResourceId, food);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_menu, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNameFood);
        TextView price = (TextView)convertView.findViewById(R.id.txtPriceFood);
        TextView ingredients=(TextView)convertView.findViewById(R.id.txtIngredientsFood);
        Food food = getItem(position);
        title.setText(food.nameFood);
        price.setText(Double.toString(food.priceFood));
        ingredients.setText(food.ingredientsFood);
        return convertView;
    }

}
