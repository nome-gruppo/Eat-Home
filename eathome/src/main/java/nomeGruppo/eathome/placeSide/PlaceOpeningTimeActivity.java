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

import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.Days;
import nomeGruppo.eathome.utility.OpeningTime;

public class PlaceOpeningTimeActivity extends AppCompatActivity {

    private static final String TAG = "PlaceOpeningTime";
    private static final String CLOSED = "-";
    private Place place;
    private TimePickerDialog picker;
    private OpeningTime openingTimeUtility;
    private Button editMonday, editTuesday, editWednesday, editThursday, editFriday, editSaturday, editSunday;
    private Button editMondayClosed, editTuesdayClosed, editWednesdayClosed, editThursdayClosed, editFridayClosed, editSaturdayClosed, editSundayClosed;
    private HashMap<String, String> openingTime;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

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

        this.mAuth = FirebaseAuth.getInstance();

        if (place.openingTime != null) {
            btnSignIn.setVisibility(View.INVISIBLE);
            btnEdit.setVisibility(View.VISIBLE);

            if (place.openingTime.get(Days.MONDAY.toString()).equals(CLOSED)) {
                switchMonday.setChecked(false);
                openingTimeUtility.setSwitch(editMonday, editMondayClosed);
            } else {
                editMonday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.MONDAY.toString())));
                editMondayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.MONDAY.toString())));
            }
            if (place.openingTime.get(Days.TUESDAY.toString()).equals(CLOSED)) {
                switchTuesday.setChecked(false);
                openingTimeUtility.setSwitch(editTuesday, editTuesdayClosed);
            } else {
                editTuesday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.TUESDAY.toString())));
                editTuesdayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.TUESDAY.toString())));
            }
            if (place.openingTime.get(Days.WEDNESDAY.toString()).equals(CLOSED)) {
                switchWednesday.setChecked(false);
                openingTimeUtility.setSwitch(editWednesday, editWednesdayClosed);
            } else {
                editWednesday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.WEDNESDAY.toString())));
                editWednesdayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.WEDNESDAY.toString())));
            }
            if (place.openingTime.get(Days.THURSDAY.toString()).equals(CLOSED)) {
                switchThursday.setChecked(false);
                openingTimeUtility.setSwitch(editThursday, editThursdayClosed);
            } else {
                editThursday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.THURSDAY.toString())));
                editThursdayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.THURSDAY.toString())));
            }
            if (place.openingTime.get(Days.FRIDAY.toString()).equals(CLOSED)) {
                switchFriday.setChecked(false);
                openingTimeUtility.setSwitch(editFriday, editFridayClosed);
            } else {
                editFriday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.FRIDAY.toString())));
                editFridayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.FRIDAY.toString())));
            }
            if (place.openingTime.get(Days.SATURDAY.toString()).equals(CLOSED)) {
                switchSaturday.setChecked(false);
                openingTimeUtility.setSwitch(editSaturday, editSaturdayClosed);
            } else {
                editSaturday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.SATURDAY.toString())));
                editSaturdayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.SATURDAY.toString())));
            }
            if (place.openingTime.get(Days.SUNDAY.toString()).equals(CLOSED)) {
                switchSunday.setChecked(false);
                openingTimeUtility.setSwitch(editSunday, editSundayClosed);
            } else {
                editSunday.setText(openingTimeUtility.getOpening(place.openingTime.get(Days.SUNDAY.toString())));
                editSundayClosed.setText(openingTimeUtility.getClosed(place.openingTime.get(Days.SUNDAY.toString())));
            }
        }

        switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    openingTimeUtility.setSwitch(editMonday, editMondayClosed);
                } else {
                    openingTimeUtility.setSwitchChecked(getApplicationContext(), editMonday, editMondayClosed);
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
            public void onClick(View view) {

                place.setOpeningTime(putOpeningTime(openingTime));

                createAccount(place.emailPlace, getIntent().getStringExtra("password"));

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place.setOpeningTime(putOpeningTime(openingTime));
                Toast.makeText(PlaceOpeningTimeActivity.this, getResources().getString(R.string.success_save), Toast.LENGTH_SHORT).show();
                Intent otherActivity = new Intent(PlaceOpeningTimeActivity.this, OtherActivity.class);
                otherActivity.putExtra(FirebaseConnection.PLACE, place);
                startActivity(otherActivity);
                finish();
            }
        });

    }

    //TODO controlla se si può mettere in onStop
    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        db.write(FirebaseConnection.PLACE_TABLE, place.idPlace, place);
    }

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

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();

                            place.setIdPlace(user.getUid()); //assegno come id l'user id generato da Firebase Authentication

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
