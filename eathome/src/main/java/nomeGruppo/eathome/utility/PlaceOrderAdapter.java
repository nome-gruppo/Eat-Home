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
import nomeGruppo.eathome.db.FirebaseConnection;

public class PlaceOrderAdapter extends ArrayAdapter<Order> {

    public PlaceOrderAdapter(@NonNull Context context, int resource, @NonNull List<Order> listOrder) {
        super(context, resource, listOrder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_order_info, null);
        final TextView title = (TextView) convertView.findViewById(R.id.txtNameOrderInfo);
        final TextView total = (TextView) convertView.findViewById(R.id.txtTotalOrderInfo);
        final TextView address = (TextView) convertView.findViewById(R.id.txtAddressOrderInfo);
        final TextView date = (TextView) convertView.findViewById(R.id.txtDateOrderInfo);
        final TextView phone = (TextView) convertView.findViewById(R.id.txtPhoneNumber);
        final CheckBox stateOrder = convertView.findViewById(R.id.checkBoxStateOrder);
        final Order order = getItem(position);
        title.setText(order.nameClientOrder);
        total.setText(order.totalOrder + " €");
        address.setText(order.addressOrder);
        date.setText(order.dateOrder + " " + order.timeOrder);
        phone.setText(order.phoneClientOrder);
        if (order.stateOrder) {
            stateOrder.setChecked(true);
            stateOrder.setText(getContext().getResources().getString(R.string.done));
        } else {
            stateOrder.setChecked(false);
            stateOrder.setText(getContext().getResources().getString(R.string.not_done));
        }

        stateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                // Check which checkbox was clicked
                if (view.getId() == R.id.checkBoxStateOrder) {
                    //aggiorno lo stato dell'ordine in firebase
                    if (checked) { //se place conferma l'avvenuta esecuzione dell'ordine
                        order.setStateOrder(true);//metto la checkBox su check
                        stateOrder.setText(getContext().getResources().getString(R.string.done));//cambio il testo in 'eseguito'
                    } else { //se place non conferma l'avvenuta esecuzione dell'ordine
                        order.setStateOrder(false);//metto la checkBox su uncheck
                        stateOrder.setText(getContext().getResources().getString(R.string.not_done));//cambio il testo in 'non eseguito'
                    }
                    updateStateOrder(order);//aggiorno lo stato dell'ordine in firebase
                }
            }


        });
        return convertView;
    }

    private void updateStateOrder(Order order) {
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).child(order.idOrder).child("stateOrder").setValue(order.stateOrder);
    }
}
