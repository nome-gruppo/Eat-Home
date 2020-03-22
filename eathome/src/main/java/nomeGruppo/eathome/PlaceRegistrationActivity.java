package nomeGruppo.eathome;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import nomeGruppo.eathome.utility.Categories;


public class PlaceRegistrationActivity extends AppCompatActivity {

    static final String NAME_TABLE="Places";

    private EditText namePlaceET;
    private EditText cityPlaceET;
    private EditText phonePlaceET;
    private EditText addressPlaceET;
    private EditText emailPlaceET;
    private EditText passwordPlaceET;
    private EditText numberAddressPlaceET;
    private SeekBar deliveryCostSB;
    private Button signinBtn;
    private TextView deliveryCostTV;
    private TextView statusTV;
    private Place place;
    private int currentDeliveryCost=0;
    private int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_registration);

        this.place=new Place();

        namePlaceET =(EditText)findViewById(R.id.editNamePlace);
        namePlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT); //passa automaticamente nella EditText successiva appena l'utente preme invio sulla tastiera
        cityPlaceET =(EditText)findViewById(R.id.editCityPlace);
        cityPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressPlaceET =(EditText)findViewById(R.id.editAdressPlace);
        addressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numberAddressPlaceET =(EditText)findViewById(R.id.editNumberAdressPlace);
        numberAddressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phonePlaceET =(EditText)findViewById(R.id.editPhonePlace);
        phonePlaceET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deliveryCostSB =(SeekBar) findViewById(R.id.seekDeliveryCost);
        deliveryCostSB.setEnabled(false);
        deliveryCostSB.setOnSeekBarChangeListener(customSeekBarDelivery);
        emailPlaceET =(EditText)findViewById(R.id.editMailPlace);
        emailPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordPlaceET =(EditText)findViewById(R.id.editPasswordPlace);
        signinBtn =(Button)findViewById(R.id.btnSignin);
        deliveryCostTV =(TextView)findViewById(R.id.txtDeliveryCost);
        statusTV = (TextView)findViewById(R.id.activity_place_registration_tw_status);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTV.setVisibility(View.INVISIBLE);
                if (numberAddressPlaceET.getText().toString().trim().length() == 0 || addressPlaceET.getText().toString().trim().length() == 0 || cityPlaceET.getText().toString().trim().length() == 0 ||
                        namePlaceET.getText().toString().trim().length() == 0 || passwordPlaceET.getText().toString().trim().length() == 0 || emailPlaceET.getText().toString().trim().length() == 0 ||
                        phonePlaceET.getText().toString().trim().length() == 0 || place.categories.trim().length() == 0) {

                    Toast.makeText(PlaceRegistrationActivity.this, "Compila tutti i campi", duration).show();

                } else {

                    FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

                    //controllo che la mail non sia già presente nel database
                    if(1==1){
                        db.queryEqualTo(NAME_TABLE, "emailPlace", emailPlaceET.getText().toString().trim()).addListenerForSingleValueEvent(valueEventListener);
                        statusTV.setVisibility(View.VISIBLE);
                        emailPlaceET.setText("");
                        passwordPlaceET.setText("");
                    }else {

                        place.setAddressNumPlace(numberAddressPlaceET.getText().toString());
                        place.setAddressPlace(addressPlaceET.getText().toString());
                        place.setCityPlace(cityPlaceET.getText().toString());
                        place.setNamePlace(namePlaceET.getText().toString());
                        place.setPasswordPlace(passwordPlaceET.getText().toString().hashCode());
                        place.setPhonePlace(phonePlaceET.getText().toString());
                        place.setEmailPlace(emailPlaceET.getText().toString());

                        place.setIdPlace(db.getKey(NAME_TABLE));//assegno la chiave random data da firebase al campo idPlace

                        db.writeObject(NAME_TABLE, place); //scrivo l'oggetto place nel db

                        Intent placeHomeIntent = new Intent(PlaceRegistrationActivity.this, PlaceHomepageActivity.class);
                        placeHomeIntent.putExtra("PLACE", place);
                        Toast.makeText(PlaceRegistrationActivity.this, "Registrazione effettuata con successo", duration).show();
                        startActivity(placeHomeIntent);
                    }
                }
            }
        });

    }// fine onCreate

    ValueEventListener valueEventListener = new ValueEventListener(){
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Place place1 = snapshot.getValue(Place.class);
                    Place place=place1;
                    if(place1.emailPlace!=null){

                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private SeekBar.OnSeekBarChangeListener customSeekBarDelivery=
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentDeliveryCost=seekBar.getProgress();
                    deliveryCostTV.setText(Integer.toString(currentDeliveryCost));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    place.setDeliveryCost(currentDeliveryCost);
                } // end method onStopTrackingTouch
            };// end OnSeekBarChangeListener;

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioPizzeria:
                if (checked)
                    place.setCategories(Categories.PIZZERIA.toString());
                    break;
            case R.id.radioItalianRestaurant:
                if (checked)
                    place.setCategories(Categories.RISTORANTE_ITALIANO.toString());
                    break;
            case R.id.radioSushi:
                if (checked)
                    place.setCategories(Categories.SUSHI.toString());
                    break;
            case R.id.radioOrders:
                if (checked)
                    place.setTakesOrderPlace(true);
                    deliveryCostSB.setEnabled(true);
                    break;
            case R.id.radioBooking:
                if (checked)
                    place.setTakesBookingPlace(true);
                    deliveryCostSB.setEnabled(false);
                    break;
            case R.id.radioEither:
                if (checked)
                    place.setTakesOrderPlace(true);
                    place.setTakesBookingPlace(true);
                    deliveryCostSB.setEnabled(true);
                    break;
        }
    }

    private boolean emailControl(FirebaseConnection db, String tableName, String column, String value){
        final boolean[] result = {false};

        return result[0];
    }




}






