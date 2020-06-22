package nomeGruppo.eathome.clientSide;

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
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.actions.BookingComparator;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity per far visualizzare al cliente il riepilogo delle sue prenotazioni
 */
public class ClientBookingInfoActivity extends AppCompatActivity {

    private MenuNavigationItemSelected menuNavigationItemSelected;
    private Client client;
    private List<Booking> listBooking;
    private BookingInfoAdapter bookingInfoAdapter;
    private FirebaseConnection firebaseConnection;
    private BottomNavigationView bottomMenuClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_booking_info);

        final ListView listViewBookingInfo = findViewById(R.id.listViewBookingInfo);
        this.bottomMenuClient = findViewById(R.id.bottom_navigationClientBooking);

        this.firebaseConnection = new FirebaseConnection();
        this.client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
        this.menuNavigationItemSelected = new MenuNavigationItemSelected();
        this.listBooking = new LinkedList<>();
        this.bookingInfoAdapter = new BookingInfoAdapter(this, R.layout.listitem_booking_info, listBooking);
        listViewBookingInfo.setAdapter(bookingInfoAdapter);

        //menu sottostante l'activity
        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item, client, ClientBookingInfoActivity.this);
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final MenuItem mItem = bottomMenuClient.getMenu().findItem(R.id.action_bookings);
        mItem.setChecked(true);

        listBooking.clear();
        //leggo in firebase le prenotazioni del cliente in base al suo id
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_NODE).orderByChild(Booking.ID_CLIENT_FIELD).equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        listBooking.add(booking);//aggiungo la prenotazione alla lista collegata all'adapter
                    }
                } else {//se non ci sono prenotazioni
                    Toast.makeText(ClientBookingInfoActivity.this, getResources().getString(R.string.no_booking), Toast.LENGTH_LONG).show();
                }
                Collections.sort(listBooking, new BookingComparator());//ordino gli elementi in ordine di prenotazione più recente effettuata
                bookingInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
