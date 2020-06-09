package nomeGruppo.eathome.clientSide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.TreeSet;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.actors.PlacesByDistance;
import nomeGruppo.eathome.actors.PlacesByName;
import nomeGruppo.eathome.actors.PlacesByValuation;
import nomeGruppo.eathome.actors.PlaceCategories;
import nomeGruppo.eathome.db.FirebaseConnection;

/**
 * Questa activity è chiamata quando l'utente clicca sul FAB che gli permette di filtrare i locali visulazzati
 */
public class PlacesFilterActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1000;

    //costanti usate per il bundle
    private static final String PIZZERIA_CB = "pizzeriaCB";
    private static final String RESTAURANT_CB = "restaurantCB";
    private static final String SUSHI_CB = "sushiCB";
    private static final String RESTAURANT_PIZZERIA_CB = "restaurantPizzeriaCB";
    private static final String OTHER_CB = "otherCB";
    private static final String DELIVERY_RB = "deliveryRB";
    private static final String BOOKING_RB = "bookingRB";
    private static final String ORDER_BY_VALUATION_RB = "orderByValuationRB";
    private static final String ORDER_BY_DISTANCE_RB = "orderByDistanceRB";
    private static final String FREE_DELIVERY_SWITCH = "freeDeliverySwitch";
    private static final String VALUATION_SB = "valuationSB";

    private CheckBox pizzeriaCB;
    private CheckBox restaurantCB;
    private CheckBox sushiCB;
    private CheckBox restaurantPizzeriaCB;
    private CheckBox otherCB;

    private RadioButton deliveryRB;
    private RadioButton bookingRB;

    private RadioButton orderByNameRB;
    private RadioButton orderByValuationRB;
    private RadioButton orderByDistanceRB;

    private Switch freeDeliverySwitch;

    private SeekBar valuationSB;

    private boolean categoryChanged = false;
    private boolean typeChanged = false;
    private boolean freeDeliverySet = false;
    private boolean valuationChanged = false;
    private boolean someChanged = false;

    private ArrayList<Place> places;
    private Button showBtn;

    private Bundle outState;

    private double userLatitude;
    private double userLongitude;

    private MyLocationListener myLocationListener;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_filter);

        pizzeriaCB = findViewById(R.id.activity_places_filter_cb_pizzeria);
        restaurantCB = findViewById(R.id.activity_places_filter_cb_itaRestaurant);
        sushiCB = findViewById(R.id.activity_places_filter_cb_sushi);
        restaurantPizzeriaCB = findViewById(R.id.activity_places_filter_cb_restaurant_pizzeria);
        otherCB = findViewById(R.id.activity_places_filter_cb_other);

        deliveryRB = findViewById(R.id.activity_places_filter_rb_delivery);
        bookingRB = findViewById(R.id.activity_places_filter_rb_booking);

        orderByNameRB = findViewById(R.id.activity_places_filter_rb_name_order);
        orderByValuationRB = findViewById(R.id.activity_places_filter_rb_valuation_order);
        orderByDistanceRB = findViewById(R.id.activity_places_filter_rb_distance_order);

        freeDeliverySwitch = findViewById(R.id.switch1activity_places_filter_switch_free_delivery);

        valuationSB = findViewById(R.id.activity_places_filter_sb_valuation);

        showBtn = findViewById(R.id.activity_places_filter_btn_show);

        myLocationListener = new MyLocationListener();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        outState = new Bundle();
        places = new ArrayList<>();

        userLatitude = getIntent().getDoubleExtra("userLatitude", 0);
        userLongitude = getIntent().getDoubleExtra("userLongitude", 0);

        //inizializza i listener delle varie view
        initCheckListener();

        final Bundle bundle = getIntent().getBundleExtra("outState");

        if (bundle != null) {
            pizzeriaCB.setChecked(bundle.getBoolean(PIZZERIA_CB, true));
            restaurantCB.setChecked(bundle.getBoolean(RESTAURANT_CB, true));
            sushiCB.setChecked(bundle.getBoolean(SUSHI_CB, true));
            restaurantPizzeriaCB.setChecked(bundle.getBoolean(RESTAURANT_PIZZERIA_CB, true));
            otherCB.setChecked(bundle.getBoolean(OTHER_CB, true));

            deliveryRB.setChecked(bundle.getBoolean(DELIVERY_RB, false));
            bookingRB.setChecked(bundle.getBoolean(BOOKING_RB, false));

//        if(!deliveryRB.isChecked() && !bookingRB.isChecked()){
//            allRB.isChecked();
//        }

            orderByValuationRB.setChecked(bundle.getBoolean(ORDER_BY_VALUATION_RB, false));
            orderByDistanceRB.setChecked(bundle.getBoolean(ORDER_BY_DISTANCE_RB, false));

            freeDeliverySwitch.setChecked(bundle.getBoolean(FREE_DELIVERY_SWITCH, false));

            valuationSB.setProgress(bundle.getInt(VALUATION_SB));

        }
    }// end onCreate

    @Override
    protected void onResume() {
        super.onResume();

        final String userCity = getIntent().getStringExtra("userCity");

        /*Il metodo ottiene la città ricercata nella barra degli indirizzi
        il button per visualizzare i locali non è cliccabile finché non sono caricati tutti i locali della città
        */
        showBtn.setClickable(false);
        if (userCity != null) {
            search(userCity);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //gps attivato
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000000000L, 0, myLocationListener);
            } else {
                //gps disabilitato

                orderByNameRB.setChecked(true);

            }
        }

    }

    public void initCheckListener() {

        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                someChanged = true;
                categoryChanged = true;

            }
        };

        pizzeriaCB.setOnCheckedChangeListener(changeListener);
        restaurantCB.setOnCheckedChangeListener(changeListener);
        sushiCB.setOnCheckedChangeListener(changeListener);
        restaurantPizzeriaCB.setOnCheckedChangeListener(changeListener);
        otherCB.setOnCheckedChangeListener(changeListener);

        freeDeliverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                someChanged = true;
                freeDeliverySet = b;
            }
        });


        valuationSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                someChanged = true;
                valuationChanged = true;
            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                outState.putBoolean(PIZZERIA_CB, pizzeriaCB.isChecked());
                outState.putBoolean(RESTAURANT_CB, restaurantCB.isChecked());
                outState.putBoolean(SUSHI_CB, sushiCB.isChecked());
                outState.putBoolean(RESTAURANT_PIZZERIA_CB, restaurantPizzeriaCB.isChecked());
                outState.putBoolean(OTHER_CB, otherCB.isChecked());

                outState.putBoolean(DELIVERY_RB, deliveryRB.isChecked());
                outState.putBoolean(BOOKING_RB, bookingRB.isChecked());

                outState.putBoolean(ORDER_BY_VALUATION_RB, orderByValuationRB.isChecked());
                outState.putBoolean(ORDER_BY_DISTANCE_RB, orderByDistanceRB.isChecked());

                outState.putBoolean(FREE_DELIVERY_SWITCH, freeDeliverySwitch.isChecked());

                outState.putInt(VALUATION_SB, valuationSB.getProgress());


                Intent resultIntent = new Intent();
                resultIntent.putExtra("listPlace", applyFilters());
                resultIntent.putExtra("outState", outState);
                setResult(RESULT_OK, resultIntent);
                finish();


            }
        });

    }

    private void search(String userCity) {
        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //cerca nel database i locali nella città dell'utente
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_NODE).orderByChild("cityPlace").equalTo(userCity).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        places.add(snapshot.getValue(Place.class));
                    }
                }
                showBtn.setClickable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Place> applyFilters() {

        ArrayList<Place> result;

        if (someChanged) {

            ArrayList<Place> toRemove = new ArrayList<>();
            //controllo cambio tipo
            if (typeChanged) {

                if (deliveryRB.isChecked()) {
                    for (Place item : places) {
                        if (!item.takesOrderPlace) {
                            toRemove.add(item);
                        }
                    }
                } else {
                    for (Place item : places) {
                        if (!item.takesBookingPlace) {
                            toRemove.add(item);
                        }
                    }
                }
                //se rimane su all non eliminare nulla
            }

            //controllo categoria
            if (categoryChanged) {
                if (!pizzeriaCB.isChecked()) {
                    for (Place item : places) {
                        if (item.categories.equals(PlaceCategories.PIZZERIA.toString())) {
                            toRemove.add(item);
                        }
                    }
                }

                if (!restaurantCB.isChecked()) {
                    for (Place item : places) {
                        if (item.categories.equals(PlaceCategories.RISTORANTE_ITALIANO.toString())) {
                            toRemove.add(item);
                        }
                    }
                }
                if (!sushiCB.isChecked()) {
                    for (Place item : places) {
                        if (item.categories.equals(PlaceCategories.SUSHI.toString())) {
                            toRemove.add(item);
                        }
                    }
                }

                if (!restaurantPizzeriaCB.isChecked()) {
                    for (Place item : places) {
                        if (item.categories.equals(PlaceCategories.PIZZERIA_RISTORANTE.toString())) {
                            toRemove.add(item);
                        }
                    }
                }
                if (!otherCB.isChecked()) {
                    for (Place item : places) {
                        if (item.categories.equals(PlaceCategories.ALTRO.toString())) {
                            toRemove.add(item);
                        }
                    }
                }
            }

            //controllo spedizione gratuita
            if (freeDeliverySet) {
                for (Place item : places) {
                    if (item.deliveryCost != 0) {
                        toRemove.add(item);
                    }
                }
            }

            //controllo valutazione minima
            if (valuationChanged) {
                int valuation = valuationSB.getProgress();

                for (Place item : places) {
                    if (item.valuation < valuation) {
                        toRemove.add(item);
                    }
                }
            }

            places.removeAll(toRemove);
        }

        //seleziona ordinamento
        TreeSet<Place> treeSet;
        if (orderByValuationRB.isChecked()) {
            treeSet = new TreeSet<>(new PlacesByValuation());

        } else if (orderByDistanceRB.isChecked()) {

            treeSet = new TreeSet<>(new PlacesByDistance(userLatitude, userLongitude, getApplicationContext()));

        } else {
            treeSet = new TreeSet<>(new PlacesByName());
        }

        treeSet.addAll(places);
        result = new ArrayList<>(treeSet);


        return result;

    }// end applyFilters

    private void locationPermissionRequest() {
        //richiedo permessi
        if (ActivityCompat.checkSelfPermission(PlacesFilterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(PlacesFilterActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permessi non concessi

            ActivityCompat.requestPermissions(PlacesFilterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);

        } else {
            //permessi concessi

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), PERMISSION_LOCATION_REQUEST_CODE);

            } else {
                orderByDistanceRB.setClickable(true);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000000000L, 0, myLocationListener);
            }
        }
    }

    public void filtersOnRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.activity_places_filter_rb_all:
            case R.id.activity_places_filter_rb_delivery:
                if (checked) {
                    freeDeliverySwitch.setClickable(true);

                }
                break;
            case R.id.activity_places_filter_rb_booking:
                if (checked)
                    freeDeliverySwitch.setClickable(false);
                freeDeliverySwitch.setChecked(false);
                freeDeliverySet = false;

                break;
        }
        typeChanged = true;
    }

    public void orderByOnRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        if (view.getId() == R.id.activity_places_filter_rb_distance_order) {
            if (checked) {
                locationPermissionRequest();
            }
        }
    }

    //TODO serve visto che non c'è la toolbar?
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            PlacesFilterActivity.this.userLatitude = location.getLatitude();
            PlacesFilterActivity.this.userLongitude = location.getLongitude();

        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
