package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;

public class PlaceOrderAdapter extends ArrayAdapter <Order>{

    public PlaceOrderAdapter(@NonNull Context context, int resource, @NonNull List<Order> listOrder) {
        super(context, resource, listOrder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_order_info, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNameOrderInfo);
        TextView total = (TextView)convertView.findViewById(R.id.txtTotalOrderInfo);
        TextView address=(TextView)convertView.findViewById(R.id.txtAddressOrderInfo);
        TextView date=(TextView)convertView.findViewById(R.id.txtDateOrderInfo);
        TextView phone=(TextView)convertView.findViewById(R.id.txtPhoneNumber);
        final CheckBox stateOrder=convertView.findViewById(R.id.checkBoxStateOrder);
        final Order order = getItem(position);
        title.setText(order.nameClientOrder);
        total.setText(order.totalOrder+" €");
        address.setText(order.addressOrder);
        date.setText(order.dateOrder+" "+order.timeOrder);
        phone.setText(order.phoneClientOrder);
        if(order.stateOrder){
            stateOrder.setChecked(true);
            stateOrder.setText(getContext().getResources().getString(R.string.done));
        }else{
            stateOrder.setChecked(false);
            stateOrder.setText(getContext().getResources().getString(R.string.not_done));
        }

        stateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                // Check which checkbox was clicked
                switch(view.getId()) {
                    case R.id.checkBoxStateOrder:
                        if (checked){
                            order.setStateOrder(true);
                            stateOrder.setText(getContext().getResources().getString(R.string.done));
                        }
                        else {
                            order.setStateOrder(false);
                            stateOrder.setText(getContext().getResources().getString(R.string.not_done));
                        }break;
                }
            }


        });
        return convertView;
    }
}
