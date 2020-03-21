package nomeGruppo.eathome;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import nomeGruppo.eathome.utility.Categories;


public class PlaceRegistrationActivity extends AppCompatActivity {

    static final String NAME_TABLE="Places";

    private EditText namePlace;
    private EditText cityPlace;
    private EditText phonePlace;
    private EditText addressPlace;
    private EditText mailPlace;
    private EditText passwordPlace;
    private EditText numberAddressPlace;
    private SeekBar deliveryCost;
    private Button btnSignin;
    private TextView txtDeliveryCost;
    private Place place;
    private int currentDeliveryCost=0;
    private int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_registration);

        this.place=new Place();

        namePlace=(EditText)findViewById(R.id.editNamePlace);
        namePlace.setImeOptions(EditorInfo.IME_ACTION_NEXT); //passa automaticamente nella EditText successiva appena l'utente preme invio sulla tastiera
        cityPlace=(EditText)findViewById(R.id.editCityPlace);
        cityPlace.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressPlace =(EditText)findViewById(R.id.editAdressPlace);
        addressPlace.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numberAddressPlace=(EditText)findViewById(R.id.editNumberAdressPlace);
        numberAddressPlace.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phonePlace=(EditText)findViewById(R.id.editPhonePlace);
        phonePlace.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deliveryCost=(SeekBar) findViewById(R.id.seekDeliveryCost);
        deliveryCost.setEnabled(false);
        deliveryCost.setOnSeekBarChangeListener(customSeekBarDelivery);
        mailPlace=(EditText)findViewById(R.id.editMailPlace);
        mailPlace.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordPlace=(EditText)findViewById(R.id.editPasswordPlace);
        btnSignin=(Button)findViewById(R.id.btnSignin);
        txtDeliveryCost=(TextView)findViewById(R.id.txtDeliveryCost);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberAddressPlace.getText().toString().trim().length() == 0 || addressPlace.getText().toString().trim().length() == 0 || cityPlace.getText().toString().trim().length() == 0 ||
                        namePlace.getText().toString().trim().length() == 0 || passwordPlace.getText().toString().trim().length() == 0 || mailPlace.getText().toString().trim().length() == 0 ||
                        phonePlace.getText().toString().trim().length() == 0 || place.categories.trim().length() == 0) {

                    Toast.makeText(PlaceRegistrationActivity.this, "Compila tutti i campi", duration).show();

                } else {

                    place.setAddressNumPlace(numberAddressPlace.getText().toString());
                    place.setAddressPlace(addressPlace.getText().toString());
                    place.setCityPlace(cityPlace.getText().toString());
                    place.setNamePlace(namePlace.getText().toString());
                    place.setPasswordPlace(passwordPlace.getText().toString());
                    place.setPhonePlace(phonePlace.getText().toString());
                    place.setEmailPlace(mailPlace.getText().toString());

                    FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db
                    place.setIdPlace(db.getKey(NAME_TABLE));//assegno la chiave random data da firebase al campo idPlace
                    db.writeObject(NAME_TABLE, place); //scrivo l'oggetto place nel db


                    Intent placeHomeIntent = new Intent(PlaceRegistrationActivity.this, PlaceHomepageActivity.class);
                    placeHomeIntent.putExtra("PLACE",place);
                    Toast.makeText(PlaceRegistrationActivity.this, "Registrazione effettuata con successo", duration).show();
                    startActivity(placeHomeIntent);
                }
            }
        });

    }// fine onCreate

    private SeekBar.OnSeekBarChangeListener customSeekBarDelivery=
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentDeliveryCost=seekBar.getProgress();
                    txtDeliveryCost.setText(Integer.toString(currentDeliveryCost));
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
                    deliveryCost.setEnabled(true);
                    break;
            case R.id.radioBooking:
                if (checked)
                    place.setTakesBookingPlace(true);
                    deliveryCost.setEnabled(false);
                    break;
            case R.id.radioEither:
                if (checked)
                    place.setTakesOrderPlace(true);
                    place.setTakesBookingPlace(true);
                    deliveryCost.setEnabled(true);
                    break;
        }
    }



}






