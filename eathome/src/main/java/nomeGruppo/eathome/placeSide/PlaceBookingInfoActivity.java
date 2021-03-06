package nomeGruppo.eathome.placeSide;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.actions.BookingComparator;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity per far visualizzare a Place il riepilogo delle sue prenotazioni
 */

public class PlaceBookingInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private ListView listView;
    private BottomNavigationView bottomMenuPlace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_booking_info);

        this.bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceBookingInfo);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.menuNavigationItemSelected = new MenuNavigationItemSelected();
        this.listView = findViewById(R.id.listViewPlaceBookingInfo);

        //mostro il menu sottostante
        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceBookingInfoActivity.this);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        final MenuItem mItem = bottomMenuPlace.getMenu().findItem(R.id.action_bookings);
        mItem.setChecked(true);

        showListBooking();
    }

    private void showListBooking() {

        final List<Booking> listBooking = new LinkedList<>();
        final PlaceBookingAdapter placeBookingAdapter = new PlaceBookingAdapter(this, R.layout.listitem_booking_info, listBooking);
        this.listView.setAdapter(placeBookingAdapter);

        listBooking.clear();

        FirebaseConnection firebaseConnection = new FirebaseConnection();

        //leggo in firebase le prenotazioni con id place corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_NODE).orderByChild("idPlaceBooking").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        listBooking.add(booking);
                    }
                    Collections.sort(listBooking, new BookingComparator());//ordino gli elementi in ordine di prenotazione più recente effettuata
                    placeBookingAdapter.notifyDataSetChanged();
                } else {//se non c'è alcuna prenotazione
                    Toast.makeText(PlaceBookingInfoActivity.this, getResources().getString(R.string.no_booking), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
