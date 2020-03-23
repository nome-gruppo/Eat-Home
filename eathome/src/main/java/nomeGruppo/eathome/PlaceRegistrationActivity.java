package nomeGruppo.eathome;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.actors.PlaceCategories;
import nomeGruppo.eathome.ui.login.LoginViewModel;
import nomeGruppo.eathome.utility.Controls;


public class PlaceRegistrationActivity extends AppCompatActivity {

    static final String NAME_TABLE = "Places";
    private static final String TAG = "PlaceRegistration";


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
    private int currentDeliveryCost = 0;
    private int duration = Toast.LENGTH_SHORT;

    private Controls control;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_registration);

        mAuth = FirebaseAuth.getInstance();

        place = new Place();
        control = new Controls();

        namePlaceET = (EditText) findViewById(R.id.editNamePlace);
        namePlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT); //passa automaticamente nella EditText successiva appena l'utente preme invio sulla tastiera
        cityPlaceET = (EditText) findViewById(R.id.editCityPlace);
        cityPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressPlaceET = (EditText) findViewById(R.id.editAddressPlace);
        addressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numberAddressPlaceET = (EditText) findViewById(R.id.editNumberAddressPlace);
        numberAddressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phonePlaceET = (EditText) findViewById(R.id.editPhonePlace);
        phonePlaceET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deliveryCostSB = (SeekBar) findViewById(R.id.seekDeliveryCost);
        deliveryCostSB.setEnabled(false);
        deliveryCostSB.setOnSeekBarChangeListener(customSeekBarDelivery);
        emailPlaceET = (EditText) findViewById(R.id.editMailPlace);
        emailPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordPlaceET = (EditText) findViewById(R.id.editPasswordPlace);
        signinBtn = (Button) findViewById(R.id.btnSignin);
        deliveryCostTV = (TextView) findViewById(R.id.txtDeliveryCost);
        statusTV = (TextView) findViewById(R.id.activity_place_registration_tw_status);



        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTV.setVisibility(View.GONE);
                if (numberAddressPlaceET.getText().toString().trim().length() == 0 || addressPlaceET.getText().toString().trim().length() == 0 || cityPlaceET.getText().toString().trim().length() == 0 ||
                        namePlaceET.getText().toString().trim().length() == 0 || passwordPlaceET.getText().toString().trim().length() == 0 || emailPlaceET.getText().toString().trim().length() == 0 ||
                        phonePlaceET.getText().toString().trim().length() == 0 || place.categories.trim().length() == 0) {

                    Toast.makeText(PlaceRegistrationActivity.this, "Compila tutti i campi", duration).show();

                } else {

                    String emailTemp = emailPlaceET.getText().toString().trim();
                    String passwordTemp = passwordPlaceET.getText().toString();

                    if (control.isEmailValid(emailTemp) && control.isPasswordValid(passwordTemp)) {
                        createAccount(emailTemp, passwordTemp);
                    }
                }
            }
        });
    }// fine onCreate

    @Override
    protected void onStart() {
        super.onStart();

        // TODO Check if user is signed in
        user = mAuth.getCurrentUser();
    }

    /**
     * I dati vengono salvati nel database in onPause
     */
    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        place.setAddressNumPlace(numberAddressPlaceET.getText().toString().trim());
        place.setAddressPlace(addressPlaceET.getText().toString().trim());
        place.setCityPlace(cityPlaceET.getText().toString().trim());
        place.setNamePlace(namePlaceET.getText().toString().trim());
        place.setPhonePlace(phonePlaceET.getText().toString().trim());
        place.setEmailPlace(emailPlaceET.getText().toString().trim());

        place.setIdPlace(user.getUid()); //assegno come id l'user id generato da Firebase Authentication

        //assegno come chiave del db l'user id generato da Firebase Authentication
        db.write(NAME_TABLE,user.getUid(), place);


    }


    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();

                            Intent placeHomeIntent = new Intent(PlaceRegistrationActivity.this, PlaceHomepageActivity.class);
                            placeHomeIntent.putExtra("PLACE", place);
                            Toast.makeText(PlaceRegistrationActivity.this, "Registrazione effettuata con successo", duration).show();
                            startActivity(placeHomeIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PlaceRegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            statusTV.setText("Email già presente");
                            statusTV.setVisibility(View.VISIBLE);

                            emailPlaceET.setText("");
                            passwordPlaceET.setText("");
                        }

                    }
                });
    }

    private SeekBar.OnSeekBarChangeListener customSeekBarDelivery =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentDeliveryCost = seekBar.getProgress();
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
        switch (view.getId()) {
            case R.id.radioPizzeria:
                if (checked)
                    place.setCategories(PlaceCategories.PIZZERIA.toString());
                break;
            case R.id.radioItalianRestaurant:
                if (checked)
                    place.setCategories(PlaceCategories.RISTORANTE_ITALIANO.toString());
                break;
            case R.id.radioSushi:
                if (checked)
                    place.setCategories(PlaceCategories.SUSHI.toString());
                break;
            case R.id.radioPizzeriaRestaurant:
                if(checked)
                    place.setCategories(PlaceCategories.PIZZERIA_RISTORANTE.toString());
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



}






