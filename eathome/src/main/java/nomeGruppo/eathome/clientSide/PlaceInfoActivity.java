package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.profile.DialogAddAddress;
import nomeGruppo.eathome.utility.MenuAdapterForClient;
import nomeGruppo.eathome.utility.OpeningTime;

/*
activity che visualizza le informazioni per il locale selezionato dall'utente
 */
public class PlaceInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String SPLIT = ", ";
    private Place place;
    private ImageView imgPlaceInfo;
    private TextView txtDeliveryPlaceInfo;
    private TextView txtBookingPlaceInfo;
    private TextView txtNamePlaceInfo;
    private TextView txtAddressPlaceInfo;
    private TextView txtCityPlaceInfo;
    private TextView txtDeliveryCostInfo;
    private TextView txtOpeningTime;
    private Button btnOrder;
    private Button btnBook;

    private OpeningTime openingTimeUtility;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);



        this.openingTimeUtility=new OpeningTime();



        this.imgPlaceInfo=(ImageView) findViewById(R.id.imgPlaceInfo);
        this.txtDeliveryPlaceInfo=(TextView)findViewById(R.id.txtDeliveryPlaceInfo);
        this.txtBookingPlaceInfo=(TextView)findViewById(R.id.txtBookingPlaceInfo);
        this.txtNamePlaceInfo=(TextView)findViewById(R.id.txtNamePlaceInfo);
        this.txtAddressPlaceInfo=(TextView)findViewById(R.id.txtAddressPlaceInfo);
        this.txtCityPlaceInfo=(TextView)findViewById(R.id.txtCityPlaceInfo);
        this.txtDeliveryCostInfo=(TextView)findViewById(R.id.txtDeliveryCostInfo);
        this.txtOpeningTime=findViewById(R.id.txtOpeningTime);
        this.btnBook=(Button)findViewById(R.id.btnBook);
        this.btnOrder=(Button)findViewById(R.id.btnOrder);

        this.txtNamePlaceInfo.setText(this.place.namePlace);
        this.txtAddressPlaceInfo.setText(this.place.addressPlace+" "+this.place.addressNumPlace);
        this.txtCityPlaceInfo.setText(this.place.cityPlace);


        final RatingBar ratingBar = findViewById(R.id.activity_place_info_ratingBar);
        final TextView numFeedbackTW = findViewById(R.id.activity_place_info_numFeedback);

        ratingBar.setRating(place.valuation);

        numFeedbackTW.setText(place.numberReview + " recensioni");

        numFeedbackTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceInfoActivity.this, FeedbackPlaceActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                startActivity(intent);
            }
        });

//        MapFragment mMapFragment = MapFragment.newInstance();
//        FragmentTransaction fragmentTransaction =
//                getFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.activity_place_info, mMapFragment);
//        fragmentTransaction.commit();

        MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);



        //se il locale accetta prenotazioni
        if(this.place.takesBookingPlace){
            this.txtBookingPlaceInfo.setVisibility(View.VISIBLE);//mostro messaggio 'il locale accetta prenotazioni'
            this.btnBook.setEnabled(true);//rendo il bottone prenota cliccabile
        }

        //se il locale accetta ordinazioni
        if(this.place.takesOrderPlace){
            this.txtDeliveryPlaceInfo.setVisibility(View.VISIBLE);//mostro messaggio 'il locale accetta ordinazioni'
            this.txtDeliveryCostInfo.setText(Integer.toString(this.place.deliveryCost));//imposto la TExtView con il costo della spedizione
            this.txtDeliveryCostInfo.setVisibility(View.VISIBLE);//mostro il costo della spedizione
            this.btnOrder.setEnabled(true);//rendo cliccabile il bottone ordina
        }

        //se clicca su ordina
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivity=new Intent(PlaceInfoActivity.this,PlaceListFoodOrderActivity.class);
                orderActivity.putExtra(FirebaseConnection.PLACE,place);
                startActivity(orderActivity);
            }
        });

        //se clicca su ordina
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {//se l'utente è loggato
                    Intent bookingActivity = new Intent(PlaceInfoActivity.this,ConfirmBookingActivity.class);
                    bookingActivity.putExtra(FirebaseConnection.PLACE,place);
                    bookingActivity.putExtra("UserID",user.getUid());
                    startActivity(bookingActivity);//apri activity di conferma ordinazione
                }else{//se l'utente non è loggato
                    Intent loginIntent = new Intent(PlaceInfoActivity.this, LoginActivity.class);
                    loginIntent.putExtra(FirebaseConnection.LOGIN_FLAG, true);
                    startActivity(loginIntent);//apri login
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(3840*3840)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlaceInfo.setImageBitmap(bitmap);
                    }
                });

        try {
            openingTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void openingTime() throws ParseException {
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        String day = openingTimeUtility.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        String openingTime = place.openingTime.get(day);
        Date localTime=parser.parse(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
        if (openingTime.length()>8) {//se è stato impostato un orario di apertura e chiusura
            Date timeOpening=openingTimeUtility.getTimeOpening(openingTime);//estrapolo l'ora di apertura
            Date timeClosed=openingTimeUtility.getTimeClosed(openingTime);//estrapolo l'ora di chiusura
            //se localTime si trova tra timeOpening e timeClosed
            if (localTime.after(timeOpening)&&localTime.before(timeClosed)){
                txtOpeningTime.setText(getResources().getString(R.string.opening_time) + " " + parser.format(timeClosed));
                return;
            } else{//se l'ora corrente non è tra l'ora di apertura e l'ora di chiusura
                txtOpeningTime.setText(getResources().getString(R.string.closed_time) + " " + parser.format(timeOpening));
                btnOrder.setEnabled(false);//non è possibile ordinare
                return;
            }
        } else {//se non è stato impostato alcun orario per il giorno corrente
            txtOpeningTime.setText(getResources().getString(R.string.closed_place));
            btnOrder.setEnabled(false);//non è possibile ordinare
            return;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> mList = geocoder.getFromLocationName(place.addressPlace + " "+ place.addressNumPlace + ", " + place.cityPlace, 1);

            LatLng placeLatLng = new LatLng(mList.get(0).getLatitude(), mList.get(0).getLongitude());
            LatLngBounds bounds = new LatLngBounds(new LatLng(placeLatLng.latitude-0.001, placeLatLng.longitude -0.001),
                    new LatLng(placeLatLng.latitude+0.001, placeLatLng.longitude+0.001));
            googleMap.addMarker(new MarkerOptions().position(placeLatLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mAddress.setAddressLine();

        // Add a marker in Sydney and move the camera
    }
}
