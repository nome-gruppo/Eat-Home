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

import nomeGruppo.eathome.utility.Categories;


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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_registration);

        mAuth = FirebaseAuth.getInstance();

        this.place = new Place();

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

                    if (passwordControl(passwordTemp)) {
                        createAccount(emailTemp, passwordTemp);
                    }
                }
            }
        });

    }// fine onCreate


    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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

        place.setIdPlace(db.getKey(NAME_TABLE));//assegno la chiave random data da firebase al campo idPlace

        db.writeObject(NAME_TABLE, place); //scrivo l'oggetto place nel db

    }


    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                            Intent placeHomeIntent = new Intent(PlaceRegistrationActivity.this, PlaceHomepageActivity.class);
                            placeHomeIntent.putExtra("PLACE", place);
                            Toast.makeText(PlaceRegistrationActivity.this, "Registrazione effettuata con successo", duration).show();
                            startActivity(placeHomeIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PlaceRegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            statusTV.setText("Email già presente");
                            statusTV.setVisibility(View.VISIBLE);

                            emailPlaceET.setText("");
                            passwordPlaceET.setText("");
                        }

                        // ...
                    }
                });
    }

    /*Metodo che controlla la validità della password
     */
    private boolean passwordControl(String password) {

        if (password.length() < 6) {
            statusTV.setText("La password deve essere almeno di 6 caratteri");
            statusTV.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
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
            case R.id.radioPizzeriaRestaurant:
                if(checked)
                    place.setCategories(Categories.PIZZERIA_RISTORANTE.toString());
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






