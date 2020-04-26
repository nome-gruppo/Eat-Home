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

import java.util.ArrayList;
import java.util.TreeSet;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.actors.PlacesByValuation;
import nomeGruppo.eathome.actors.PlaceCategories;

public class PlacesFilterActivity extends AppCompatActivity {

    private CheckBox pizzeriaCB;
    private CheckBox restaurantCB;
    private CheckBox sushiCB;
    private CheckBox restaurantPizzeriaCB;
    private CheckBox otherCB;

    private RadioButton deliveryRB;
    private RadioButton bookingRB;
    private RadioButton allRB;

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
        allRB = findViewById(R.id.activity_places_filter_rb_all);

        orderByValuationRB = findViewById(R.id.activity_places_filter_rb_valuation_order);

        freeDeliverySwitch = findViewById(R.id.switch1activity_places_filter_switch_free_delivery);

        valuationSB = findViewById(R.id.activity_places_filter_sb_valuation);

        showBtn = findViewById(R.id.activity_places_filter_btn_show);

        places = (ArrayList<Place>) getIntent().getSerializableExtra("listPlace");

        initCheckListener();

    }

    public void initCheckListener() {

        CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    categoryChanged = true;
                } else {
                    allCheckTrue();
                }
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
                if(b){
                    freeDeliverySet = true;
                }else{
                    freeDeliverySet = false;
                }
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
                Intent resultIntent = new Intent();
                resultIntent.putExtra("listPlace", applyFilters());
                setResult(RESULT_OK,resultIntent);
                finish();


            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("pizzeriaCB", pizzeriaCB.isChecked());
        outState.putBoolean("restaurantCB", restaurantCB.isChecked());
        outState.putBoolean("sushiCB", sushiCB.isChecked());
        outState.putBoolean("restaurantPizzeriaCB", restaurantPizzeriaCB.isChecked());
        outState.putBoolean("otherCB", otherCB.isChecked());

        outState.putBoolean("deliveryRB", deliveryRB.isChecked());
        outState.putBoolean("bookingRB", bookingRB.isChecked());

        outState.putBoolean("orderByValuationRB", orderByValuationRB.isChecked());

        outState.putBoolean("freeDeliverySwitch", freeDeliverySwitch.isChecked());

        outState.putInt("valuationSB", valuationSB.getProgress());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        pizzeriaCB.setChecked(savedInstanceState.getBoolean("pizzeriaCB", true));
        restaurantCB.setChecked(savedInstanceState.getBoolean("restaurantCB", true));
        sushiCB.setChecked(savedInstanceState.getBoolean("sushiCB", true));
        restaurantPizzeriaCB.setChecked(savedInstanceState.getBoolean("restaurantPizzeriaCB", true));
        otherCB.setChecked(savedInstanceState.getBoolean("otherCB", true));

        deliveryRB.setChecked(savedInstanceState.getBoolean("deliveryRB", false));
        bookingRB.setChecked(savedInstanceState.getBoolean("bookingRB", false));

//        if(!deliveryRB.isChecked() && !bookingRB.isChecked()){
//            allRB.isChecked();
//        }

        orderByValuationRB.setChecked(savedInstanceState.getBoolean("orderByValuationRB", false));

        freeDeliverySwitch.setChecked(savedInstanceState.getBoolean("freeDeliverySwitch", false));

        valuationSB.setProgress(savedInstanceState.getInt("valuationSB"));


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
        if(freeDeliverySet){
            for(Place item: places){
                if(item.deliveryCost != 0){
                    toRemove.add(item);
                }
            }
        }

        //controllo valutazione minima
        if(valuationChanged){
            int valuation = valuationSB.getProgress();

            for(Place item: places){
                if(item.valuation < valuation){
                    toRemove.add(item);
                }
            }
        }

        places.removeAll(toRemove);

        if(orderChanged){
            TreeSet<Place> orderedPlaces = new TreeSet<>(new PlacesByValuation());

            orderedPlaces.addAll(places);

            result = new ArrayList<>(orderedPlaces);
        }else{
            result = new ArrayList<>(places);
        }

        return result;
    }// end applyFilters

    private void allCheckTrue() {
        if (pizzeriaCB.isChecked() && restaurantCB.isChecked() &&
                sushiCB.isChecked() && restaurantPizzeriaCB.isChecked() && otherCB.isChecked()) {
            categoryChanged = false;
        } else {
            categoryChanged = true;
        }
    }

    public void filtersOnRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.activity_places_filter_rb_all:
                if(checked){
                    freeDeliverySwitch.setClickable(true);

                    typeChanged = false;
                }
            case R.id.activity_places_filter_rb_delivery:
                if (checked) {
                    freeDeliverySwitch.setClickable(true);

                    typeChanged = true;
                }
                break;
            case R.id.activity_places_filter_rb_booking:
                if (checked)
                    freeDeliverySwitch.setClickable(false);
                freeDeliverySwitch.setChecked(false);
                freeDeliverySet = false;

                typeChanged = true;
                break;
        }
    }

    public void orderByOnRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        if (view.getId() == R.id.activity_places_filter_rb_valuation_order) {
            if (checked) {
                orderChanged = true;
            }
        } else {
            if (checked) {
                orderChanged = false;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
