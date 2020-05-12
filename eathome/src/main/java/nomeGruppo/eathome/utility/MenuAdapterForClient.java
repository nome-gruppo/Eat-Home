package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.foods.Food;

public class MenuAdapterForClient extends ArrayAdapter<Food> {
    private HashMap<Food,Integer>order;

    public MenuAdapterForClient(Context context, int textViewResourceId,
                                List<Food> food, HashMap<Food,Integer>order) {
        super(context, textViewResourceId, food);
        this.order=order;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_menu_client, null);
        final TextView title = (TextView)convertView.findViewById(R.id.txtNameFoodPlaceInfo);
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
                int i=Integer.parseInt(counter.getText().toString());
                i++;
                Toast.makeText(getContext(),"+ 1 "+title.getText().toString(),Toast.LENGTH_SHORT).show();
                counter.setText(Integer.toString(i));
                order.put(food,i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(counter.getText().toString());
                if(i>0) {
                    i--;
                    Toast.makeText(getContext(),"- 1 "+title.getText().toString(),Toast.LENGTH_SHORT).show();
                    counter.setText(Integer.toString(i));
                    if(i==0){
                        order.remove(food);
                    }else{
                        order.put(food,i);
                    }
                }
            }
        });

        return convertView;
    }

}
