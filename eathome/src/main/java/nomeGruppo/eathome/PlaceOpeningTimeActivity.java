package nomeGruppo.eathome;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.Days;
import nomeGruppo.eathome.utility.OpeningTime;

public class PlaceOpeningTimeActivity extends AppCompatActivity {

    private static final String TAG = "PlaceOpeningTime";
    private Place place;
    private TimePickerDialog picker;
    private OpeningTime openingTimeUtility;
    private EditText editMonday,editTuesday,editWednesday,editThursday,editFriday,editSaturday,editSunday;
    private EditText editMondayClosed,editTuesdayClosed,editWednesdayClosed,editThursdayClosed,editFridayClosed,editSaturdayClosed,editSundayClosed;
    private Switch switchMonday, switchTuesday,switchWednesday,switchThursday,switchFriday,switchSaturday,switchSunday;
    private Button btnSignin;
    private HashMap<String,String>openingTime;
    private static final String SPLIT=":";
    private static final int LENGTH=5;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_time);

        this.place=(Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        this.editMonday=findViewById(R.id.editMonday);
        this.editTuesday=findViewById(R.id.editTuesday);
        this.editWednesday=findViewById(R.id.editWednesday);
        this.editThursday=findViewById(R.id.editThursday);
        this.editFriday=findViewById(R.id.editFriday);
        this.editSaturday=findViewById(R.id.editSaturday);
        this.editSunday=findViewById(R.id.editSunday);
        this.editMondayClosed=findViewById(R.id.editMondayClosed);
        this.editTuesdayClosed=findViewById(R.id.editTuesdayClosed);
        this.editWednesdayClosed=findViewById(R.id.editWednesdayClosed);
        this.editThursdayClosed=findViewById(R.id.editThursdayClosed);
        this.editFridayClosed=findViewById(R.id.editFridayClosed);
        this.editSaturdayClosed=findViewById(R.id.editSaturdayClosed);
        this.editSundayClosed=findViewById(R.id.editSundayClosed);

        this.switchMonday=findViewById(R.id.switchMonday);
        this.switchTuesday=findViewById(R.id.switchTuesday);
        this.switchWednesday=findViewById(R.id.switchWednesday);
        this.switchThursday=findViewById(R.id.switchThursday);
        this.switchFriday=findViewById(R.id.switchFriday);
        this.switchSaturday=findViewById(R.id.switchSaturday);
        this.switchSunday=findViewById(R.id.switchSunday);

        this.btnSignin=findViewById(R.id.btnSigninPlace);
        this.openingTime=new HashMap<>(7);
        this.openingTimeUtility=new OpeningTime();

        this.mAuth = FirebaseAuth.getInstance();

        switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editMonday,editMondayClosed);
                }
            }
        });

        switchTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                   openingTimeUtility.setSwitch(editTuesday,editTuesday);
                }
            }
        });

        switchWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editWednesday,editWednesdayClosed);
                }
            }
        });

        switchThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editThursday,editThursdayClosed);
                }
            }
        });

        switchFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editFriday,editFridayClosed);
                }
            }
        });

        switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editSaturday,editSaturdayClosed);
                }
            }
        });

        switchSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    openingTimeUtility.setSwitch(editSunday,editSundayClosed);
                }
            }
        });


        editMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editMonday.setText(sHour + SPLIT + sMinute);
                                editWednesday.setText(sHour + SPLIT + sMinute);
                                editThursday.setText(sHour + SPLIT + sMinute);
                                editFriday.setText(sHour + SPLIT + sMinute);
                                editSaturday.setText(sHour + SPLIT + sMinute);
                                editSunday.setText(sHour + SPLIT + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editMondayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hourMonday=editMonday.getText().toString();
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(hourMonday.length()==LENGTH){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,2));
                    minutes=Integer.parseInt(hourMonday.substring(3));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editMondayClosed.setText(sHour + SPLIT + sMinute);
                                editTuesdayClosed.setText(sHour + SPLIT + sMinute);
                                editWednesdayClosed.setText(sHour + SPLIT + sMinute);
                                editThursdayClosed.setText(sHour + SPLIT + sMinute);
                                editFridayClosed.setText(sHour + SPLIT + sMinute);
                                editSaturdayClosed.setText(sHour + SPLIT + sMinute);
                                editSundayClosed.setText(sHour + SPLIT + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editTuesday.setText(sHour + SPLIT + sMinute);
                                editWednesday.setText(sHour + SPLIT + sMinute);
                                editThursday.setText(sHour + SPLIT + sMinute);
                                editFriday.setText(sHour + SPLIT + sMinute);
                                editSaturday.setText(sHour + SPLIT + sMinute);
                                editSunday.setText(sHour + SPLIT + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editTuesdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hourMonday=editTuesday.getText().toString();
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(hourMonday.length()==LENGTH){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,2));
                    minutes=Integer.parseInt(hourMonday.substring(3));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editTuesdayClosed.setText(sHour + SPLIT + sMinute);
                                editWednesdayClosed.setText(sHour + SPLIT + sMinute);
                                editThursdayClosed.setText(sHour + SPLIT + sMinute);
                                editFridayClosed.setText(sHour + SPLIT + sMinute);
                                editSaturdayClosed.setText(sHour + SPLIT + sMinute);
                                editSundayClosed.setText(sHour + SPLIT + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this,editWednesday);
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
                openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this,editThursday);
            }
        });

        editThursdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this,editThursday,editThursdayClosed);
            }
        });

      editFriday.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this,editFriday);
          }
      });

      editFridayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this,editFriday,editFridayClosed);
          }
      });

      editSaturday.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this,editSaturday);
          }
      });

      editSaturdayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this,editSaturday,editSaturdayClosed);
          }
      });

      editSunday.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTime(PlaceOpeningTimeActivity.this,editSunday);
          }
      });

      editSundayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTimeUtility.setOpeningTimeClose(PlaceOpeningTimeActivity.this,editSunday,editSundayClosed);
          }
      });

      btnSignin.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              openingTime.put(Days.MONDAY.toString(),editMonday.getText().toString()+"-"+editMondayClosed.getText().toString());
              openingTime.put(Days.TUESDAY.toString(),editTuesday.getText().toString()+"-"+editTuesdayClosed.getText().toString());
              openingTime.put(Days.WEDNESDAY.toString(),editWednesday.getText().toString()+"-"+editWednesdayClosed.getText().toString());
              openingTime.put(Days.THURSDAY.toString(),editThursday.getText().toString()+"-"+editThursdayClosed.getText().toString());
              openingTime.put(Days.FRIDAY.toString(),editFriday.getText().toString()+"-"+editFridayClosed.getText().toString());
              openingTime.put(Days.SATURDAY.toString(),editSaturday.getText().toString()+"-"+editSaturdayClosed.getText().toString());
              openingTime.put(Days.SUNDAY.toString(),editSunday.getText().toString()+"-"+editSundayClosed.getText().toString());

              place.setOpeningTime(openingTime);

              createAccount(place.emailPlace, getIntent().getStringExtra("password"));

          }
      });

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//
//
//        //assegno come chiave del db l'user id generato da Firebase Authentication
//
//    }

    //TODO controlla se si può mettere in onStop
    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        db.write(FirebaseConnection.PLACE_TABLE,place.idPlace, place);

    }

    private void createAccount(String email, String password){
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
                            Toast.makeText(PlaceOpeningTimeActivity.this, "Registrazione effettuata con successo", Toast.LENGTH_SHORT).show();
                            startActivity(placeOpeningTimeIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(PlaceOpeningTimeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}
