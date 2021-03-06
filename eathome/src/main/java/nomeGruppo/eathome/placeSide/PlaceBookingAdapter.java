package nomeGruppo.eathome.placeSide;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;

/*
adapter per visualizzare le prenotazioni
 */

public class PlaceBookingAdapter extends ArrayAdapter<Booking> {

    private final Resources res;

    public PlaceBookingAdapter(@NonNull Context context, int resource, @NonNull List<Booking> listBooking) {
        super(context, resource, listBooking);
        this.res = context.getResources();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.listitem_booking_info, parent, false);
            final TextView title = convertView.findViewById(R.id.txtNameBookingInfo);
            final TextView address = convertView.findViewById(R.id.txtAddressBookingInfo);
            final TextView date = convertView.findViewById(R.id.txtDateBookingInfo);
            final TextView number = convertView.findViewById(R.id.txtNumberPersonBookingInfo);
            final TextView phone = convertView.findViewById(R.id.txtPhonePlace);
            final Booking booking = getItem(position);

            if (booking != null) {
                title.setText(booking.nameBooking);//imposto come titolo il nome del cliente che ha prenotato
                address.setVisibility(View.INVISIBLE);//rendo invisibile la textView per l'indirizzo
                phone.setVisibility(View.INVISIBLE);//rendo invisibile la textView per il numero di telefono del locale
                Calendar calendar = Calendar.getInstance();//istanzio Calendar
                calendar.setTimeInMillis(booking.dateBooking);//imposto la data in formato long
                //imposto la data in formato dd/mm/yyyy, hh:mm
                date.setText(new SimpleDateFormat(res.getString(R.string.dateFormat) + " - " + res.getString(R.string.hourFormat), Locale.getDefault()).format(calendar.getTime()));
                number.setText(String.valueOf(booking.personNumBooking));
            }
        }
        return convertView;
    }

}
