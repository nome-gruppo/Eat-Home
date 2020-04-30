package nomegruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import nomegruppo.eathome.R;
import nomegruppo.eathome.actions.Order;
import nomegruppo.eathome.actors.Place;

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
        final Order order = getItem(position);
        title.setText(order.nameClientOrder);
        total.setText(Float.toString(order.totalOrder)+" €");
        address.setText(order.addressOrder);
        date.setText(order.dateOrder+" "+order.timeOrder);
        phone.setText(order.phoneClientOrder);
        return convertView;
    }
}
