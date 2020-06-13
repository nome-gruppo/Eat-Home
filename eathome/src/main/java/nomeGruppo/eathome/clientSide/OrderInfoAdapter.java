package nomeGruppo.eathome.clientSide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;

public class OrderInfoAdapter extends ArrayAdapter<Order> {

    public OrderInfoAdapter(@NonNull Context context, int resource, @NonNull List<Order> listOrder) {
        super(context, resource, listOrder);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_order_info, parent, false);

            final TextView title = convertView.findViewById(R.id.txtNameOrderInfo);
            final TextView total = convertView.findViewById(R.id.txtTotalOrderInfo);
            final TextView address = convertView.findViewById(R.id.txtAddressOrderInfo);
            final TextView date = convertView.findViewById(R.id.txtDateOrderInfo);
            final TextView phone = convertView.findViewById(R.id.txtPhoneNumber);
            final CheckBox stateOrder = convertView.findViewById(R.id.checkBoxStateOrder);

            stateOrder.setEnabled(false);

            final Order order = getItem(position);
            if (order != null) {
                title.setText(order.namePlaceOrder);
                total.setText(order.totalOrder + " €");
                address.setText(order.addressPlaceOrder);
                Calendar calendar = Calendar.getInstance();//istanzio Calendar
                calendar.setTimeInMillis(order.timeOrder);//imposto la data in formato long
                //imposto la data in formato dd/mm/yyyy, hh:mm
                date.setText(new SimpleDateFormat(getContext().getResources().getString(R.string.dateFormat) + " - " +getContext().getResources().getString(R.string.hourFormat), Locale.getDefault()).format(calendar.getTime()));
                phone.setText(order.phonePlaceOrder);

                if (order.stateOrder) {
                    stateOrder.setChecked(true);
                    stateOrder.setText(getContext().getResources().getString(R.string.done));
                } else {
                    stateOrder.setChecked(false);
                    stateOrder.setText(getContext().getResources().getString(R.string.not_done));
                }
            }
        }
        return convertView;
    }


}
