package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class OrderInfoAdapter extends ArrayAdapter<Order> {

    public OrderInfoAdapter(@NonNull Context context, int resource, @NonNull List<Order> listOrder) {
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
        title.setText(order.placeOrder.namePlace);
        total.setText(Float.toString(order.totalOrder)+" €");
        address.setText(order.placeOrder.cityPlace+" "+order.placeOrder.addressPlace+" "+order.placeOrder.addressNumPlace);
        date.setText(order.dateOrder+" "+order.timeOrder);
        phone.setText(order.placeOrder.phonePlace);
        return convertView;
    }

}
