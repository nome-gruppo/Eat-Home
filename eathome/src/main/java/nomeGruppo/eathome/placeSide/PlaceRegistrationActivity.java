package nomeGruppo.eathome.placeSide;

import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.clientSide.ClientRegistrationActivity;
import nomeGruppo.eathome.utility.City;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Locale;

import nomeGruppo.eathome.actors.PlaceCategories;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

/**
 * activity per la registrazione di Place
 */

public class PlaceRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "PlaceRegistration";
    private static final int DURATION = Toast.LENGTH_SHORT;

    private EditText namePlaceET;
    private AutoCompleteTextView cityPlaceET;
    private EditText phonePlaceET;
    private EditText addressPlaceET;
    private EditText emailPlaceET;
    private EditText passwordPlaceET;
    private EditText numberAddressPlaceET;
    private SeekBar deliveryCostSB;
    private TextView deliveryCostTV;
    private TextView statusTV;
    private Place place;
    private int currentDeliveryCost = 0;

    private UtilitiesAndControls control;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_registration);

        final Button signInBtn = findViewById(R.id.btnSignin);
        namePlaceET = findViewById(R.id.editNamePlace);
        namePlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT); //passa automaticamente nella EditText successiva appena l'utente preme invio sulla tastiera
        cityPlaceET = findViewById(R.id.editCityPlace);
        cityPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressPlaceET = findViewById(R.id.editAddressPlace);
        addressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numberAddressPlaceET = findViewById(R.id.editNumberAddressPlace);
        numberAddressPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phonePlaceET = findViewById(R.id.editPhonePlace);
        phonePlaceET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deliveryCostSB = findViewById(R.id.seekDeliveryCost);
        deliveryCostSB.setEnabled(false);
        deliveryCostSB.setOnSeekBarChangeListener(customSeekBarDelivery);
        emailPlaceET = findViewById(R.id.editMailPlace);
        emailPlaceET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordPlaceET = findViewById(R.id.editPasswordPlace);
        deliveryCostTV = findViewById(R.id.txtDeliveryCost);
        statusTV = findViewById(R.id.activity_place_registration_tw_status);

        mAuth = FirebaseAuth.getInstance();
        place = new Place();
        control = new UtilitiesAndControls();

        Toolbar toolbarPlaceRegistration = findViewById(R.id.tlbPlaceRegistration);
        setSupportActionBar(toolbarPlaceRegistration);
        toolbarPlaceRegistration.setTitle("@string/tw_signUp");
        toolbarPlaceRegistration.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        toolbarPlaceRegistration.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoginActivity = new Intent(PlaceRegistrationActivity.this, LoginActivity.class);
                intentLoginActivity.putExtra(FirebaseConnection.PLACE, place);
                startActivity(intentLoginActivity);
                finish();
            }
        });

        final City city = new City();//classe che contiene l'elenco delle città

        //creo un adapter che conterrà l'elenco delle città per l'autocompletetext
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, city.getListCity());
        cityPlaceET.setAdapter(adapterCity);//setto l'adapter in editCity

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTV.setVisibility(View.GONE);
                if (numberAddressPlaceET.getText().toString().trim().length() == 0 || addressPlaceET.getText().toString().trim().length() == 0 || cityPlaceET.getText().toString().trim().length() == 0 ||
                        namePlaceET.getText().toString().trim().length() == 0 || passwordPlaceET.getText().toString().trim().length() == 0 || emailPlaceET.getText().toString().trim().length() == 0 ||
                        phonePlaceET.getText().toString().trim().length() == 0 || place.categories.trim().length() == 0) {

                    Toast.makeText(PlaceRegistrationActivity.this, R.string.fill_all_fields, DURATION).show();

                } else {

                    String emailTemp = emailPlaceET.getText().toString().trim();
                    String passwordTemp = passwordPlaceET.getText().toString();

                    if (control.isEmailValid(emailTemp) && control.isPasswordValid(passwordTemp)) {
                        checkAccount(emailTemp, passwordTemp);
                    }
                }
            }
        });
    }// fine onCreate

    public void checkAccount(String email, final String password) {

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(this, new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && task.getResult().getSignInMethods() != null && task.getResult().getSignInMethods().isEmpty()) {

                        place.setAddressNumPlace(numberAddressPlaceET.getText().toString().trim());
                        place.setAddressPlace(addressPlaceET.getText().toString().trim());
                        place.setCityPlace(cityPlaceET.getText().toString().trim().substring(0, 1).toUpperCase());
                        place.setNamePlace(namePlaceET.getText().toString().trim().substring(0, 1).toUpperCase());
                        place.setPhonePlace(phonePlaceET.getText().toString().trim());
                        place.setEmailPlace(emailPlaceET.getText().toString().trim());

                        Intent placeOpeningTimeIntent = new Intent(PlaceRegistrationActivity.this, PlaceOpeningTimeActivity.class);
                        placeOpeningTimeIntent.putExtra(FirebaseConnection.PLACE, place);
                        placeOpeningTimeIntent.putExtra("password", password);
                        startActivity(placeOpeningTimeIntent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(PlaceRegistrationActivity.this, R.string.authenticationFailed,
                                Toast.LENGTH_SHORT).show();

                        statusTV.setText(R.string.emailAlreadyExists);
                        statusTV.setVisibility(View.VISIBLE);

                        emailPlaceET.setText("");
                        passwordPlaceET.setText("");
                    }
                }
            }
        });


    }

    private final SeekBar.OnSeekBarChangeListener customSeekBarDelivery =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    currentDeliveryCost = seekBar.getProgress();
                    deliveryCostTV.setText(String.format(Locale.getDefault(), "%d", currentDeliveryCost));
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
                if (checked)
                    place.setCategories(PlaceCategories.PIZZERIA_RISTORANTE.toString());
                break;
            case R.id.radioOther:
                if (checked)
                    place.setCategories(PlaceCategories.ALTRO.toString());
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






