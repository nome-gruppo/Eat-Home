package nomeGruppo.eathome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.actors.PlacesByName;
import nomeGruppo.eathome.actors.PlacesByValuation;
import nomeGruppo.eathome.actors.PlaceCategories;
import nomeGruppo.eathome.db.FirebaseConnection;

public class PlacesFilterActivity extends AppCompatActivity {

    //costanti usate per il bundle
    private static final String PIZZERIA_CB = "pizzeriaCB";
    private static final String RESTAURANT_CB  = "restaurantCB";
    private static final String SUSHI_CB = "sushiCB";
    private static final String RESTAURANT_PIZZERIA_CB = "restaurantPizzeriaCB";
    private static final String OTHER_CB  = "otherCB";
    private static final String DELIVERY_RB = "deliveryRB";
    private static final String BOOKING_RB = "bookingRB";


    private CheckBox pizzeriaCB;
    private CheckBox restaurantCB;
    private CheckBox sushiCB;
    private CheckBox restaurantPizzeriaCB;
    private CheckBox otherCB;

    private RadioButton deliveryRB;
    private RadioButton bookingRB;

    private RadioButton orderByValuationRB;

    private Switch freeDeliverySwitch;

    private SeekBar valuationSB;


    private boolean categoryChanged = false;
    private boolean typeChanged = false;
    private boolean freeDeliverySet = false;
    private boolean valuationChanged = false;
    private boolean orderChanged = false;

    private ArrayList<Place> places;
    private Button showBtn;

    private Bundle outState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_filter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        pizzeriaCB = findViewById(R.id.activity_places_filter_cb_pizzeria);
        restaurantCB = findViewById(R.id.activity_places_filter_cb_itaRestaurant);
        sushiCB = findViewById(R.id.activity_places_filter_cb_sushi);
        restaurantPizzeriaCB = findViewById(R.id.activity_places_filter_cb_restaurant_pizzeria);
        otherCB = findViewById(R.id.activity_places_filter_cb_other);

        deliveryRB = findViewById(R.id.activity_places_filter_rb_delivery);
        bookingRB = findViewById(R.id.activity_places_filter_rb_booking);

        orderByValuationRB = findViewById(R.id.activity_places_filter_rb_valuation_order);

        freeDeliverySwitch = findViewById(R.id.switch1activity_places_filter_switch_free_delivery);

        valuationSB = findViewById(R.id.activity_places_filter_sb_valuation);

        showBtn = findViewById(R.id.activity_places_filter_btn_show);

        outState = new Bundle();

        initCheckListener();

        places = new ArrayList<>();


        Bundle bundle = getIntent().getBundleExtra("outState");

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

            orderByValuationRB.setChecked(bundle.getBoolean("orderByValuationRB", false));

            freeDeliverySwitch.setChecked(bundle.getBoolean("freeDeliverySwitch", false));

            valuationSB.setProgress(bundle.getInt("valuationSB"));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final String userCity = getIntent().getStringExtra("userCity");

        showBtn.setClickable(false);
        if (userCity != null) {
            search(userCity);
        }

    }

    private void search(String userCity) {
        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //cerca nel database i locali nella città dell'utente
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).orderByChild("cityPlace").equalTo(userCity).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void initCheckListener() {

        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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

                freeDeliverySet = true;

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

                outState.putBoolean("orderByValuationRB", orderByValuationRB.isChecked());

                outState.putBoolean("freeDeliverySwitch", freeDeliverySwitch.isChecked());

                outState.putInt("valuationSB", valuationSB.getProgress());


                Intent resultIntent = new Intent();
                resultIntent.putExtra("listPlace", applyFilters());
                resultIntent.putExtra("outState", outState);
                setResult(RESULT_OK, resultIntent);
                finish();


            }
        });

    }

    private ArrayList<Place> applyFilters() {
        ArrayList<Place> result = null;

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

        if (orderChanged) {

            TreeSet<Place> treeSet;
            if (orderByValuationRB.isChecked()) {
                treeSet = new TreeSet<>(new PlacesByValuation());

            } else {
                treeSet = new TreeSet<>(new PlacesByName());

            }

            treeSet.addAll(places);
            result = new ArrayList<>(places);
        }

        return result;
    }// end applyFilters

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
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

//        // Check which radio button was clicked
//        if (view.getId() == R.id.activity_places_filter_rb_valuation_order) {
//            if (checked) {
//                orderChanged = true;
//            }
//        } else {
//            if (checked) {
//                orderChanged = false;
//            }
//        }
        orderChanged = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
