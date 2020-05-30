package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
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

        final TextView title =  convertView.findViewById(R.id.txtNameOrderInfo);
        final TextView total = convertView.findViewById(R.id.txtTotalOrderInfo);
        final TextView address = convertView.findViewById(R.id.txtAddressOrderInfo);
        final TextView date = convertView.findViewById(R.id.txtDateOrderInfo);
        final TextView phone =  convertView.findViewById(R.id.txtPhoneNumber);
        final CheckBox stateOrder = convertView.findViewById(R.id.checkBoxStateOrder);

        stateOrder.setEnabled(false);

        final Order order = getItem(position);
        if(order != null) {
            title.setText(order.namePlaceOrder);
            total.setText(order.totalOrder + " €");
            address.setText(order.addressPlaceOrder);
            date.setText(order.dateOrder + " " + order.timeOrder);
            phone.setText(order.phonePlaceOrder);

            if (order.stateOrder) {
                stateOrder.setChecked(true);
                stateOrder.setText(getContext().getResources().getString(R.string.done));
            } else {
                stateOrder.setChecked(false);
                stateOrder.setText(getContext().getResources().getString(R.string.not_done));
            }
        }
        return convertView;
    }


}
