package nomeGruppo.eathome;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.Days;

public class PlaceOpeningTimeActivity extends AppCompatActivity {
    private Place place;
    private TimePickerDialog picker;
    private EditText editMonday,editTuesday,editWednesday,editThursday,editFriday,editSaturday,editSunday;
    private EditText editMondayClosed,editTuesdayClosed,editWednesdayClosed,editThursdayClosed,editFridayClosed,editSaturdayClosed,editSundayClosed;
    private Switch switchMonday, switchTuesday,switchWednesday,switchThursday,switchFriday,switchSaturday,switchSunday;
    private Button btnSignin;
    private HashMap<String,String>openingTime;

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

        switchMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editMonday.setEnabled(false);
                    editMondayClosed.setEnabled(false);
                    editMonday.setText("");
                    editMondayClosed.setText("");
                }
            }
        });

        switchTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editTuesday.setEnabled(false);
                    editTuesdayClosed.setEnabled(false);
                    editTuesday.setText("");
                    editTuesdayClosed.setText("");
                }
            }
        });

        switchWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editWednesday.setEnabled(false);
                    editWednesdayClosed.setEnabled(false);
                    editWednesday.setText("");
                    editWednesdayClosed.setText("");
                }
            }
        });

        switchThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editThursday.setEnabled(false);
                    editThursdayClosed.setEnabled(false);
                    editThursday.setText("");
                    editThursdayClosed.setText("");
                }
            }
        });

        switchFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editFriday.setEnabled(false);
                    editFridayClosed.setEnabled(false);
                    editFriday.setText("");
                    editFridayClosed.setText("");
                }
            }
        });

        switchSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editSaturday.setEnabled(false);
                    editSaturdayClosed.setEnabled(false);
                    editSaturday.setText("");
                    editSaturdayClosed.setText("");
                }
            }
        });

        switchSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    editSunday.setEnabled(false);
                    editSundayClosed.setEnabled(false);
                    editSunday.setText("");
                    editSundayClosed.setText("");
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
                                editMonday.setText(sHour + " : " + sMinute);
                                editTuesday.setText(sHour + " : " + sMinute);
                                editWednesday.setText(sHour + " : " + sMinute);
                                editThursday.setText(sHour + " : " + sMinute);
                                editFriday.setText(sHour + " : " + sMinute);
                                editSaturday.setText(sHour + " : " + sMinute);
                                editSunday.setText(sHour + " : " + sMinute);
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
                if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,1));
                    minutes=Integer.parseInt(hourMonday.substring(5));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editMondayClosed.setText(sHour + " : " + sMinute);
                                editTuesdayClosed.setText(sHour + " : " + sMinute);
                                editWednesdayClosed.setText(sHour + " : " + sMinute);
                                editThursdayClosed.setText(sHour + " : " + sMinute);
                                editFridayClosed.setText(sHour + " : " + sMinute);
                                editSaturdayClosed.setText(sHour + " : " + sMinute);
                                editSundayClosed.setText(sHour + " : " + sMinute);
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
                                editTuesday.setText(sHour + " : " + sMinute);
                                editWednesday.setText(sHour + " : " + sMinute);
                                editThursday.setText(sHour + " : " + sMinute);
                                editFriday.setText(sHour + " : " + sMinute);
                                editSaturday.setText(sHour + " : " + sMinute);
                                editSunday.setText(sHour + " : " + sMinute);
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
                if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,1));
                    minutes=Integer.parseInt(hourMonday.substring(5));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editTuesdayClosed.setText(sHour + " : " + sMinute);
                                editWednesdayClosed.setText(sHour + " : " + sMinute);
                                editThursdayClosed.setText(sHour + " : " + sMinute);
                                editFridayClosed.setText(sHour + " : " + sMinute);
                                editSaturdayClosed.setText(sHour + " : " + sMinute);
                                editSundayClosed.setText(sHour + " : " + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editWednesday.setOnClickListener(new View.OnClickListener() {
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
                                editWednesday.setText(sHour + " : " + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editWednesdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hourMonday=editWednesday.getText().toString();
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,1));
                    minutes=Integer.parseInt(hourMonday.substring(5));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editWednesdayClosed.setText(sHour + " : " + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editThursday.setOnClickListener(new View.OnClickListener() {
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
                                editThursday.setText(sHour + " : " + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        editThursdayClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hourMonday=editThursday.getText().toString();
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                    hour=Integer.parseInt(hourMonday.substring(0,1));
                    minutes=Integer.parseInt(hourMonday.substring(5));
                }
                // time picker dialog
                picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                editThursdayClosed.setText(sHour + " : " + sMinute);
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

      editFriday.setOnClickListener(new View.OnClickListener() {
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
                              editFriday.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
          }
      });

      editFridayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String hourMonday=editFriday.getText().toString();
              final Calendar cldr = Calendar.getInstance();
              int hour = cldr.get(Calendar.HOUR_OF_DAY);
              int minutes = cldr.get(Calendar.MINUTE);
              if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                  hour=Integer.parseInt(hourMonday.substring(0,1));
                  minutes=Integer.parseInt(hourMonday.substring(5));
              }
              // time picker dialog
              picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                      new TimePickerDialog.OnTimeSetListener() {
                          @Override
                          public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                              editFridayClosed.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
          }
      });

      editSaturday.setOnClickListener(new View.OnClickListener() {
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
                              editSaturday.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
          }
      });

      editSaturdayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String hourMonday=editSaturday.getText().toString();
              final Calendar cldr = Calendar.getInstance();
              int hour = cldr.get(Calendar.HOUR_OF_DAY);
              int minutes = cldr.get(Calendar.MINUTE);
              if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                  hour=Integer.parseInt(hourMonday.substring(0,1));
                  minutes=Integer.parseInt(hourMonday.substring(5));
              }
              // time picker dialog
              picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                      new TimePickerDialog.OnTimeSetListener() {
                          @Override
                          public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                              editSaturdayClosed.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
          }
      });

      editSunday.setOnClickListener(new View.OnClickListener() {
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
                              editSunday.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
          }
      });

      editSundayClosed.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String hourMonday=editSunday.getText().toString();
              final Calendar cldr = Calendar.getInstance();
              int hour = cldr.get(Calendar.HOUR_OF_DAY);
              int minutes = cldr.get(Calendar.MINUTE);
              if(hourMonday.length()==7){//se l'orario di apertura è stato settato allora faccio partire l'orologio da questo orario
                  hour=Integer.parseInt(hourMonday.substring(0,1));
                  minutes=Integer.parseInt(hourMonday.substring(5));
              }
              // time picker dialog
              picker = new TimePickerDialog(PlaceOpeningTimeActivity.this,
                      new TimePickerDialog.OnTimeSetListener() {
                          @Override
                          public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                              editSundayClosed.setText(sHour + " : " + sMinute);
                          }
                      }, hour, minutes, true);
              picker.show();
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

              Intent homePlaceIntent=new Intent(PlaceOpeningTimeActivity.this,PlaceHomeActivity.class);
              homePlaceIntent.putExtra(FirebaseConnection.PLACE,place);
              Toast.makeText(PlaceOpeningTimeActivity.this, "Registrazione effettuata con successo", Toast.LENGTH_SHORT).show();
              startActivity(homePlaceIntent);
              finish();
          }
      });

    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        //assegno come chiave del db l'user id generato da Firebase Authentication
        db.write(FirebaseConnection.PLACE_TABLE,place.idPlace, place);
    }
}
