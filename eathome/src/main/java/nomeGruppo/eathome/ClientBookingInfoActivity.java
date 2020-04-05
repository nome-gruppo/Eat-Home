package nomeGruppo.eathome;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.BookingInfoAdapter;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

public class ClientBookingInfoActivity extends AppCompatActivity {

    private MenuNavigationItemSelected menuNavigationItemSelected;
    private Client client;
    private BottomNavigationView bottomMenuClient;
    private ListView listViewBookingInfo;
    private List<Booking> listBooking;
    private BookingInfoAdapter bookingInfoAdapter;
    private FirebaseConnection firebaseConnection;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_TABLE).orderByChild("idClientBooking").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        listBooking.add(booking);
                    }
                }
                bookingInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_booking_info);

        this.firebaseConnection=new FirebaseConnection();
        this.bottomMenuClient=findViewById(R.id.bottom_navigationClientBooking);
        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
        this.menuNavigationItemSelected=new MenuNavigationItemSelected();
        this.listViewBookingInfo=findViewById(R.id.listViewBookingInfo);
        this.listBooking=new LinkedList<>();
        this.bookingInfoAdapter=new BookingInfoAdapter(this,R.layout.listitem_booking_info,listBooking);
        this.listViewBookingInfo.setAdapter(bookingInfoAdapter);

        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item,client,ClientBookingInfoActivity.this);
            }
        });
    }

}
