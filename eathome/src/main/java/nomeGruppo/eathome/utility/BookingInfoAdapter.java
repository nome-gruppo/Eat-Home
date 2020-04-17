package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class BookingInfoAdapter extends ArrayAdapter<Booking> {

    public BookingInfoAdapter(@NonNull Context context, int resource, @NonNull List<Booking> listBooking) {
        super(context, resource, listBooking);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_booking_info, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNameBookingInfo);
        TextView address=(TextView)convertView.findViewById(R.id.txtAddressBookingInfo);
        TextView date=(TextView)convertView.findViewById(R.id.txtDateBookingInfo);
        TextView number=(TextView)convertView.findViewById(R.id.txtNumberPersonBookingInfo);
        final Booking booking = getItem(position);
        title.setText(booking.placeBooking.namePlace);
        address.setText(booking.placeBooking.cityPlace+" "+booking.placeBooking.addressPlace+" "+booking.placeBooking.addressNumPlace);
        date.setText(booking.dateBooking+" "+booking.timeBooking);
        number.setText(booking.personNumBooking);
        return convertView;
    }
}
