package nomeGruppo.eathome.clientSide;

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

/**
 * adapter per il riepilogo delle prenotazioni
 */
public class BookingInfoAdapter extends ArrayAdapter<Booking> {

    private final Resources res;

    public BookingInfoAdapter(@NonNull Context context, int resource, @NonNull List<Booking> listBooking) {
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
            TextView title = convertView.findViewById(R.id.txtNameBookingInfo);//
            TextView address = convertView.findViewById(R.id.txtAddressBookingInfo);
            TextView date = convertView.findViewById(R.id.txtDateBookingInfo);
            TextView number = convertView.findViewById(R.id.txtNumberPersonBookingInfo);
            TextView phone = convertView.findViewById(R.id.txtPhonePlace);

            final Booking booking = getItem(position);//recupero oggetto booking in posizione position
            if (booking != null) {//se esiste un oggetto booking
                title.setText(booking.namePlaceBooking);//imposto il titolo con il nome del locale
                address.setText(booking.addressPlaceBooking);//imposto indirizzo del locale
                Calendar calendar = Calendar.getInstance();//istanzio Calendar
                calendar.setTimeInMillis(booking.dateBooking);//setto la data in formato long in calendar
                date.setText(new SimpleDateFormat(res.getString(R.string.dateFormat) +
                        " - " + res.getString(R.string.hourFormat), Locale.getDefault()).format(calendar.getTime()));//converto la data dal formato long in dd//mm/yyyy
                number.setText(String.valueOf(booking.personNumBooking));//imposto il numero di persone della prenotazione
                phone.setText(booking.phonePlaceBooking);//imposto il numero di telefono del locale
            }
        }
        return convertView;
    }
}
