package nomeGruppo.eathome.placeSide;

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

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity per far visualizzare a Place il riepilogo delle sue prenotazioni
 */
public class PlaceBookingInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private BottomNavigationView bottomMenuPlace;
    private ListView listViewBooking;
    private List<Booking> listBooking;
    private PlaceBookingAdapter placeBookingAdapter;
    private TextView txtNoBooking;
    private ImageView imgNoBookin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_booking_info);

        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.menuNavigationItemSelected=new MenuNavigationItemSelected();
        this.bottomMenuPlace=findViewById(R.id.bottom_navigationPlaceBookingInfo);
        this.listViewBooking=findViewById(R.id.listViewPlaceBookingInfo);
        this.listBooking=new LinkedList<>();
        this.txtNoBooking=findViewById(R.id.txtNoBookingPlace);
        this.imgNoBookin=findViewById(R.id.imgNoBookingPlace);
        this.placeBookingAdapter=new PlaceBookingAdapter(this,R.layout.listitem_booking_info,listBooking);

        //mostro il menu sottostante
        this.bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item,place,PlaceBookingInfoActivity.this);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        listBooking.clear();

        FirebaseConnection firebaseConnection=new FirebaseConnection();

        //leggo in firebase le prenotazioni con id place corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_TABLE).orderByChild("idPlaceBooking").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        listBooking.add(booking);//aggiungo la prenotazione alla lista a cui è stato impostato l'adapter
                    }
                }else{//se non c'è alcuna prenotazione
                    txtNoBooking.setVisibility(View.VISIBLE);//mostro il messaggio 'siamo spiacenti'
                    imgNoBookin.setVisibility(View.VISIBLE);//mostro la smile triste
                }
                placeBookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
