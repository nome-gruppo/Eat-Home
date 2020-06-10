package nomeGruppo.eathome.placeSide;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Objects;

import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.Days;
import nomeGruppo.eathome.utility.OpeningTime;

/*
activity per la gestione degli orari di apertura Place
 */

public class PlaceOpeningTimeActivity extends AppCompatActivity {

    private static final String TAG = "PlaceOpeningTime";
    private static final String CLOSED = "-";
    private Place place;
    private OpeningTime openingTimeUtility;
    private Button editMonday, editTuesday, editWednesday, editThursday, editFriday, editSaturday, editSunday;
    private Button editMondayClosed, editTuesdayClosed, editWednesdayClosed, editThursdayClosed, editFridayClosed, editSaturdayClosed, editSundayClosed;
    private HashMap<String, String> openingTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_time);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        this.editMonday = findViewById(R.id.editMonday);
        this.editTuesday = findViewById(R.id.editTuesday);
        this.editWednesday = findViewById(R.id.editWednesday);
        this.editThursday = findViewById(R.id.editThursday);
        this.editFriday = findViewById(R.id.editFriday);
        this.editSaturday = findViewById(R.id.editSaturday);
        this.editSunday = findViewById(R.id.editSunday);
        this.editMondayClosed = findViewById(R.id.editMondayClosed);
        this.editTuesdayClosed = findViewById(R.id.editTuesdayClosed);
        this.editWednesdayClosed = findViewById(R.id.editWednesdayClosed);
        this.editThursdayClosed = findViewById(R.id.editThursdayClosed);
        this.editFridayClosed = findViewById(R.id.editFridayClosed);
        this.editSaturdayClosed = findViewById(R.id.editSaturdayClosed);
        this.editSundayClosed = findViewById(R.id.editSundayClosed);

        final Switch switchMonday = findViewById(R.id.switchMonday);
        final Switch switchTuesday = findViewById(R.id.switchTuesday);
        final Switch switchWednesday = findViewById(R.id.switchWednesday);
        final Switch switchThursday = findViewById(R.id.switchThursday);
        final Switch switchFriday = findViewById(R.id.switchFriday);
        final Switch switchSaturday = findViewById(R.id.switchSaturday);
        final Switch switchSunday = findViewById(R.id.switchSunday);

        final Button btnSignIn = findViewById(R.id.btnSigninPlace);
        final Button btnEdit = findViewById(R.id.btnEditOpeningTime);
        this.openingTime = new HashMap<>(7);
        this.openingTimeUtility = new OpeningTime();

        if (place.openingTime != null) {//se sono stati impostati orari di apertura
            btnSignIn.setVisibility(View.INVISIBLE);//rendo invisibile il pulsante registrati
            btnEdit.setVisibility(View.VISIBLE);//rendo visibile il pulsante modifica

            if (Objects.equals(place.openingTime.get(Days.MONDAY.toString()), CLOSED)) {//se place è chiuso
                switchMonday.setChecked(false);//setto lo switch false
                openingTimeUtility.setSwitch(editMonday, editMondayClosed);//imposto non modificabili le edit corrispondenti al girno di chiusura
            } else {//se Place è aperto
                editMonday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.MONDAY.toString()))));//imposto orario di apertura
                editMondayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.MONDAY.toString()))));//imposto orario di chiusura
            }
            if (Objects.equals(place.openingTime.get(Days.TUESDAY.toString()), CLOSED)) {
                switchTuesday.setChecked(false);
                openingTimeUtility.setSwitch(editTuesday, editTuesdayClosed);
            } else {
                editTuesday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.TUESDAY.toString()))));
                editTuesdayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.TUESDAY.toString()))));
            }
            if (Objects.equals(place.openingTime.get(Days.WEDNESDAY.toString()), CLOSED)) {
                switchWednesday.setChecked(false);
                openingTimeUtility.setSwitch(editWednesday, editWednesdayClosed);
            } else {
                editWednesday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.WEDNESDAY.toString()))));
                editWednesdayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.WEDNESDAY.toString()))));
            }
            if (Objects.equals(place.openingTime.get(Days.THURSDAY.toString()), CLOSED)) {
                switchThursday.setChecked(false);
                openingTimeUtility.setSwitch(editThursday, editThursdayClosed);
            } else {
                editThursday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.THURSDAY.toString()))));
                editThursdayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.THURSDAY.toString()))));
            }
            if (Objects.equals(place.openingTime.get(Days.FRIDAY.toString()), CLOSED)) {
                switchFriday.setChecked(false);
                openingTimeUtility.setSwitch(editFriday, editFridayClosed);
            } else {
                editFriday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.FRIDAY.toString()))));
                editFridayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.FRIDAY.toString()))));
            }
            if (Objects.equals(place.openingTime.get(Days.SATURDAY.toString()), CLOSED)) {
                switchSaturday.setChecked(false);
                openingTimeUtility.setSwitch(editSaturday, editSaturdayClosed);
            } else {
                editSaturday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.SATURDAY.toString()))));
                editSaturdayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.SATURDAY.toString()))));
            }
            if (Objects.equals(place.openingTime.get(Days.SUNDAY.toString()), CLOSED)) {
                switchSunday.setChecked(false);
                openingTimeUtility.setSwitch(editSunday, editSundayClosed);
            } else {
                editSunday.setText(openingTimeUtility.getOpening(Objects.requireNonNull(place.openingTime.get(Days.SUNDAY.toString()))));
                editSundayClosed.setText(openingTimeUtility.getClosed(Objects.requireNonNull(place.openingTime.get(Days.SUNDAY.toString()))));
            }
        }

        switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {//se clicca sullo switch
                if (!isChecked) {//se switch è false
                    openingTimeUtility.setSwitch(editMonday, editMondayClosed);//rendo non modificabili le edit corrispondenti al giorno di chiusura
                } else {//se switch è true
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editMonday, editMondayClosed);//rendo modificabili le edit corrispondenti
                }
            }
        });

        switchTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editTuesday, editTuesdayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editTuesday, editTuesdayClosed);
                }
            }
        });

        switchWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editWednesday, editWednesdayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editWednesday, editWednesdayClosed);
                }
            }
        });

        switchThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editThursday, editThursdayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editThursday, editThursdayClosed);
                }
            }
        });

        switchFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editFriday, editFridayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editFriday, editFridayClosed);
                }
            }
        });

        switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editSaturday, editSaturdayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editSaturday, editSaturdayClosed);
                }
            }
        });

        switchSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editSunday, editSundayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editSunday, editSundayClosed);
                }
            }
        });

        editMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editMonday);
            }
        });

        editMondayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editMonday, editMondayClosed);
            }
        });

        editTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editTuesday);
            }
        });

        editTuesdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editTuesday, editTuesdayClosed);
            }
        });


        editWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editWednesday);
            }
        });

        editWednesdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editWednesday, editWednesdayClosed);
            }
        });

        editThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editThursday);
            }
        });

        editThursdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editThursday, editThursdayClosed);
            }
        });

        editFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editFriday);
            }
        });

        editFridayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editFriday, editFridayClosed);
            }
        });

        editSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editSaturday);
            }
        });

        editSaturdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editSaturday, editSaturdayClosed);
            }
        });

        editSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this, editSunday);
            }
        });

        editSundayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this, editSunday, editSundayClosed);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//se è Place è in fase di registrazione e quindi clicca su registra

                place.setOpeningTime(putOpeningTime(openingTime));//setto openingTime nell'oggetto Place con il metodo putOpeningTime

                createAccount(place.emailPlace, getIntent().getStringExtra("password"));//chiamo il metodo crea account

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//se clicca su modifica
                place.setOpeningTime(putOpeningTime(openingTime));//setto openingTime nell'oggetto Place con il metodo putOpeningTime
                Toast.makeText(PlaceOpeningTimeActivity.this, getResources().getString(R.string.success_save), Toast.LENGTH_SHORT).show();//messaggio di successo
                Intent otherActivity = new Intent(PlaceOpeningTimeActivity.this, OtherActivity.class);
                otherActivity.putExtra(FirebaseConnection.PLACE, place);
                startActivity(otherActivity);//avvio l'activity other
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        db.write(FirebaseConnection.PLACE_NODE, place.idPlace, place);
    }

    /**
     * metodo per impostare gli orari e i giorni di apertura nell'hashMap
     * @param openingTime HashMap
     * @return hashMap openingTime
     */
    private HashMap<String, String> putOpeningTime(HashMap<String, String> openingTime) {
        openingTime.put(Days.MONDAY.toString(), editMonday.getText().toString() + "-" + editMondayClosed.getText().toString());
        openingTime.put(Days.TUESDAY.toString(), editTuesday.getText().toString() + "-" + editTuesdayClosed.getText().toString());
        openingTime.put(Days.WEDNESDAY.toString(), editWednesday.getText().toString() + "-" + editWednesdayClosed.getText().toString());
        openingTime.put(Days.THURSDAY.toString(), editThursday.getText().toString() + "-" + editThursdayClosed.getText().toString());
        openingTime.put(Days.FRIDAY.toString(), editFriday.getText().toString() + "-" + editFridayClosed.getText().toString());
        openingTime.put(Days.SATURDAY.toString(), editSaturday.getText().toString() + "-" + editSaturdayClosed.getText().toString());
        openingTime.put(Days.SUNDAY.toString(), editSunday.getText().toString() + "-" + editSundayClosed.getText().toString());
        return openingTime;
    }

    /**
     * metodo per la creazione dell'account Place
     * @param email
     * @param password
     */

    private void createAccount(String email, String password) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser[] user = new FirebaseUser[1];
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user[0] = mAuth.getCurrentUser();

                            assert user[0] != null;
                            place.setIdPlace(user[0].getUid()); //assegno come id l'user id generato da Firebase Authentication

                            Intent placeOpeningTimeIntent = new Intent(PlaceOpeningTimeActivity.this, PlaceHomeActivity.class);
                            placeOpeningTimeIntent.putExtra(FirebaseConnection.PLACE, place);
                            Toast.makeText(PlaceOpeningTimeActivity.this, R.string.successfulRegistration, Toast.LENGTH_SHORT).show();
                            startActivity(placeOpeningTimeIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PlaceOpeningTimeActivity.this, R.string.authenticationFailed,
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}
