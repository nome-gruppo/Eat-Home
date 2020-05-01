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
        TextView title = (TextView)convertView.findViewById(R.id.txtNameOrderInfo);
        TextView total = (TextView)convertView.findViewById(R.id.txtTotalOrderInfo);
        TextView address=(TextView)convertView.findViewById(R.id.txtAddressOrderInfo);
        TextView date=(TextView)convertView.findViewById(R.id.txtDateOrderInfo);
        TextView phone=(TextView)convertView.findViewById(R.id.txtPhoneNumber);
        final CheckBox stateOrder=convertView.findViewById(R.id.checkBoxStateOrder);
        final Order order = getItem(position);
        title.setText(order.namePlaceOrder);
        total.setText(Float.toString(order.totalOrder)+" €");
        address.setText(order.addressPlaceOrder);
        date.setText(order.dateOrder+" "+order.timeOrder);
        phone.setText(order.phonePlaceOrder);

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
                            updateStateOrder(order);
                        }
                        else {
                            order.setStateOrder(false);
                            stateOrder.setText(getContext().getResources().getString(R.string.not_done));
                            updateStateOrder(order);
                        }break;
                }
            }


        });
        return convertView;
    }

    private void updateStateOrder(Order order){
        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).child(order.idOrder).child("stateOrder").setValue(order.stateOrder);
    }
}
