package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.HashMap;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.foods.Menu;

public class MyMenuAdapter extends ArrayAdapter<Food> {

    public MyMenuAdapter(Context context, int textViewResourceId,
                         List<Food> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_menu, null);
        TextView titolo = (TextView)convertView.findViewById(R.id.txtNameFood);
        TextView prezzo = (TextView)convertView.findViewById(R.id.txtPriceFood);
        Food c = getItem(position);
        titolo.setText(c.nameFood);
        prezzo.setText(Double.toString(c.priceFood));
        return convertView;
    }

}
