package nomeGruppo.eathome.clientSide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.placeSide.profile.PlaceMyFeedbackActivity;
import nomeGruppo.eathome.utility.OpeningTime;

import static nomeGruppo.eathome.utility.UtilitiesAndControls.PICT_SIZE_MAX;

/*
activity che visualizza le informazioni per il locale selezionato dall'utente
 */
public class PlaceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Client client;
    private Place place;
    private ImageView imgPlaceInfo;
    private TextView txtOpeningTime;
    private Button btnOrder;

    private OpeningTime openingTimeUtility;

    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        final TextView txtDeliveryPlaceInfo = findViewById(R.id.txtDeliveryPlaceInfo);
        final TextView txtBookingPlaceInfo = findViewById(R.id.txtBookingPlaceInfo);
        final TextView txtAddressPlaceInfo = findViewById(R.id.txtAddressPlaceInfo);
        final TextView txtNamePlaceInfo = findViewById(R.id.txtNamePlaceInfo);
        final Button btnBook = findViewById(R.id.btnBook);
        final RatingBar ratingBar = findViewById(R.id.activity_place_info_ratingBar);
        final TextView numFeedbackTW = findViewById(R.id.activity_place_info_numFeedback);

        imgPlaceInfo = findViewById(R.id.imgPlaceInfo);
        txtOpeningTime = findViewById(R.id.txtOpeningTime);
        btnOrder = findViewById(R.id.btnOrder);

        openingTimeUtility = new OpeningTime();

        place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        Toolbar toolbarPlaceInfo = findViewById(R.id.tlbPlaceInfo);
        setSupportActionBar(toolbarPlaceInfo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarPlaceInfo.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));



        if (place != null) {

            final MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);

            txtNamePlaceInfo.setText(place.namePlace);
            txtAddressPlaceInfo.setText(getString(R.string.addressPrinted, this.place.addressPlace,
                    this.place.addressNumPlace, this.place.cityPlace));
            ratingBar.setRating(place.valuation);

            numFeedbackTW.setText(getResources().getQuantityString(R.plurals.numFeedback, place.numberReview, place.numberReview));


            final Client client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

            numFeedbackTW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean FLAG_CLIENT = true;//flag per avvisare che l'activity PlaceMyFeedbackActivity è stata raggiunta da un client
                    Intent intent = new Intent(PlaceInfoActivity.this, PlaceMyFeedbackActivity.class);
                    intent.putExtra(FirebaseConnection.PLACE, place);
                    intent.putExtra("flag_client", FLAG_CLIENT);
                    startActivity(intent);
                }
            });


            //se il locale accetta prenotazioni
            if (place.takesBookingPlace) {
                txtBookingPlaceInfo.setVisibility(View.VISIBLE);//mostro messaggio 'il locale accetta prenotazioni'
                btnBook.setEnabled(true);//rendo il bottone prenota cliccabile
            }

            //se il locale accetta ordinazioni
            if (place.takesOrderPlace) {
                txtDeliveryPlaceInfo.setText(getResources().getString(R.string.delivery_expected) + " " + this.place.deliveryCost + " €");
                txtDeliveryPlaceInfo.setVisibility(View.VISIBLE);//mostro messaggio 'il locale accetta ordinazioni'
                this.btnOrder.setEnabled(true);//rendo cliccabile il bottone ordina
            }

            //se clicca su ordina
            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orderActivity = new Intent(PlaceInfoActivity.this, PlaceListFoodOrderActivity.class);
                    orderActivity.putExtra(FirebaseConnection.CLIENT, client);
                    orderActivity.putExtra(FirebaseConnection.PLACE, place);
                    startActivity(orderActivity);
                }
            });

            //se clicca su ordina
            btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user != null) {//se l'utente è loggato
                        Intent bookingActivity = new Intent(PlaceInfoActivity.this, ConfirmBookingActivity.class);
                        bookingActivity.putExtra(FirebaseConnection.PLACE, place);
                        bookingActivity.putExtra(FirebaseConnection.CLIENT, client);
                        bookingActivity.putExtra("UserID", user.getUid());
                        startActivity(bookingActivity);//apri activity di conferma ordinazione
                    } else {//se l'utente non è loggato
                        Intent loginIntent = new Intent(PlaceInfoActivity.this, LoginActivity.class);
                        startActivity(loginIntent);//apri login
                    }
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();

        StorageConnection storageConnection = new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference = storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(PICT_SIZE_MAX * PICT_SIZE_MAX)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgPlaceInfo.setImageBitmap(bitmap);
                    }
                });

        try {
            openingTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * metodo per leggere e visualizzare gli orari di apertura di place
     *
     * @throws ParseException
     */
    public void openingTime() throws ParseException {
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat parser = new SimpleDateFormat(getString(R.string.hourFormat), Locale.getDefault());
        String day = openingTimeUtility.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        String openingTime = place.openingTime.get(day);

        Date localTime = parser.parse(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        assert openingTime != null;
        if (openingTime.length() > 8) {//se è stato impostato un orario di apertura e chiusura
            Date timeOpening = openingTimeUtility.getTimeOpening(getApplicationContext(), openingTime);//estrapolo l'ora di apertura
            Date timeClosed = openingTimeUtility.getTimeClosed(getApplicationContext(), openingTime);//estrapolo l'ora di chiusura

            //se timeClosed.before(timeOpening) allora il locale chiude dopo la mezzanotte
            if (timeClosed.before(timeOpening)) {
                calendar.setTime(timeClosed);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                timeClosed = calendar.getTime();
            }

            //se localTime si trova tra timeOpening e timeClosed
            assert localTime != null;
            if (localTime.after(timeOpening) && localTime.before(timeClosed)) {
                txtOpeningTime.setText(getResources().getString(R.string.opening_time) + " " + parser.format(timeClosed));
                txtOpeningTime.setTextColor(getResources().getColor(R.color.quantum_vanillagreenA400));
            } else {//se l'ora corrente non è tra l'ora di apertura e l'ora di chiusura
                txtOpeningTime.setText(getResources().getString(R.string.closed_time) + " " + parser.format(timeOpening));
                txtOpeningTime.setTextColor(getResources().getColor(R.color.quantum_vanillaredA700));
                btnOrder.setEnabled(false);//non è possibile ordinare
            }
        } else {//se non è stato impostato alcun orario per il giorno corrente
            txtOpeningTime.setText(getResources().getString(R.string.closed_place));
            txtOpeningTime.setTextColor(getResources().getColor(R.color.quantum_vanillaredA700));
            btnOrder.setEnabled(false);//non è possibile ordinare
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
        final double OFFSET = 0.001;

        try {
            List<Address> mList = geocoder.getFromLocationName(place.addressPlace + ", " + place.addressNumPlace + ", " + place.cityPlace, 1);

            LatLng placeLatLng = new LatLng(mList.get(0).getLatitude(), mList.get(0).getLongitude());
            LatLngBounds bounds = new LatLngBounds(new LatLng(placeLatLng.latitude - OFFSET, placeLatLng.longitude - OFFSET),
                    new LatLng(placeLatLng.latitude + OFFSET, placeLatLng.longitude + OFFSET));
            googleMap.addMarker(new MarkerOptions().position(placeLatLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
