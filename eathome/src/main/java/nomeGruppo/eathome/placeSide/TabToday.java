package nomeGruppo.eathome.placeSide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class TabToday extends Fragment {
    private Place place;
    private ListView listView;
    private List<Booking>listBooking;
    private PlaceBookingAdapter placeBookingAdapter;

    public TabToday(Place place){
        this.place=place;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_today, (ViewGroup) requireActivity().getCurrentFocus(),false);
        this.listView=view.findViewById(R.id.listViewPlaceBookingInfoToday);
        this.listBooking=new LinkedList<>();
        this.placeBookingAdapter=new PlaceBookingAdapter(getContext(),R.layout.listitem_booking_info,listBooking);
        this.listView.setAdapter(placeBookingAdapter);

        return inflater.inflate(R.layout.tab_today,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();

        listBooking.clear();

        FirebaseConnection firebaseConnection = new FirebaseConnection();

        //leggo in firebase le prenotazioni con id place corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_NODE).orderByChild("idPlaceBooking").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    Date dateBooking=null;
                    Date curDate=new Date();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        if (booking != null) {
                            dateBooking.setTime(booking.dateBooking);
                            if (curDate.compareTo(dateBooking) < 1) {//se la prenotazione è alla data odierna
                                listBooking.add(booking);//aggiungo la prenotazione alla lista a cui è stato impostato l'adapter
                            } else {//se non ci sono prenotazioni odierne
                                Toast.makeText(getContext(), getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messaggio di avviso
                            }
                            placeBookingAdapter.notifyDataSetChanged();
                        }
                    }
                } else {//se non c'è alcuna prenotazione
                    Toast.makeText(getContext(),getResources().getString(R.string.no_booking),Toast.LENGTH_SHORT).show();
                }
                placeBookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
