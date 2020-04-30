package nomegruppo.eathome.clientSide;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomegruppo.eathome.R;
import nomegruppo.eathome.actions.Booking;
import nomegruppo.eathome.actors.Client;
import nomegruppo.eathome.db.FirebaseConnection;
import nomegruppo.eathome.utility.BookingInfoAdapter;
import nomegruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity per far visualizzare al cliente il riepilogo delle sue prenotazioni
 */
public class ClientBookingInfoActivity extends AppCompatActivity {

    private MenuNavigationItemSelected menuNavigationItemSelected;
    private Client client;
    private BottomNavigationView bottomMenuClient;
    private ListView listViewBookingInfo;
    private List<Booking> listBooking;
    private BookingInfoAdapter bookingInfoAdapter;
    private FirebaseConnection firebaseConnection;
    private TextView txtNoBooking;
    private ImageView imgNoBooking;

    @Override
    protected void onStart() {
        super.onStart();

        listBooking.clear();
        //leggo in firebase le prenotazioni del cliente in base al suo id
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_TABLE).orderByChild("idClientBooking").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        listBooking.add(booking);//aggiungo la prenotazione alla lista collegata all'adapter
                    }
                }else{//se non ci sono prenotazioni
                    txtNoBooking.setVisibility(View.VISIBLE);//mostro messaggio 'siamo spiacenti'
                    imgNoBooking.setVisibility(View.VISIBLE);//mostro la smile triste
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
        this.txtNoBooking=findViewById(R.id.txtNoBookingClient);
        this.imgNoBooking=findViewById(R.id.imgNoBookingClient);

        //menu sottostante l'activity
        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item,client,ClientBookingInfoActivity.this);
            }
        });
    }

}
