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

public class BookingInfoAdapter extends ArrayAdapter<Booking> {

    private Resources res;

    public BookingInfoAdapter(@NonNull Context context, int resource, @NonNull List<Booking> listBooking) {
        super(context, resource, listBooking);
        this.res = context.getResources();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_booking_info, null);
        TextView title = convertView.findViewById(R.id.txtNameBookingInfo);
        TextView address=convertView.findViewById(R.id.txtAddressBookingInfo);
        TextView date=convertView.findViewById(R.id.txtDateBookingInfo);
        TextView number=convertView.findViewById(R.id.txtNumberPersonBookingInfo);
        final Booking booking = getItem(position);
        if(booking != null) {
            title.setText(booking.namePlaceBooking);
            address.setText(booking.addressPlaceBooking);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(booking.dateBooking);
            date.setText(new SimpleDateFormat(String.format("EEEE" + res.getString(R.string.dateFormat) +
                    ", " + res.getString(R.string.hourFormat)), Locale.getDefault()).format(calendar));
//            date.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy, hh:mm", Locale.getDefault()).format(calendar));
            number.setText(String.valueOf(booking.personNumBooking));
        }
        return convertView;
    }
}
