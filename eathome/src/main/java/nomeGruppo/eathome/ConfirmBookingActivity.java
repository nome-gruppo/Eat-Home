package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.libraries.places.api.model.LocalTime;


import java.sql.Time;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DatePickerFragment;
import nomeGruppo.eathome.utility.OpeningTime;
import nomeGruppo.eathome.utility.TimePickerFragment;

public class ConfirmBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Booking booking;
    private Place place;
    private EditText txtDateBooking;
    private EditText txtHourBooking;
    private TextView txtNumberPersonBooking;
    private ImageButton btnAddPersonBooking;
    private ImageButton btnDeletePersonBooking;
    private Button btnConfirmBooking;
    private EditText editNameBooking;
    private TextView txtDayOfWeek;
    private OpeningTime openingTimeUtility;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        this.booking=new Booking();
        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        this.txtDateBooking=findViewById(R.id.txtDateBooking);
        this.txtHourBooking=findViewById(R.id.txtHourBooking);
        this.txtNumberPersonBooking=findViewById(R.id.txtNumberPersonBooking);
        this.btnAddPersonBooking=findViewById(R.id.btnAddPersonBooking);
        this.btnDeletePersonBooking=findViewById(R.id.btnDeletePersonBooking);
        this.btnConfirmBooking=findViewById(R.id.btnConfirmBooking);
        this.editNameBooking=findViewById(R.id.editNameClientBooking);
        this.txtDayOfWeek=findViewById(R.id.txtDayOfWeek);
        this.openingTimeUtility=new OpeningTime();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getWritableDatabase();


        btnAddPersonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(txtNumberPersonBooking.getText().toString());
                i++;
                txtNumberPersonBooking.setText(Integer.toString(i));
            }
        });

        btnDeletePersonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(txtNumberPersonBooking.getText().toString());
                if(i>1){
                    i--;
                    txtNumberPersonBooking.setText(Integer.toString(i));
                }
            }
        });

        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editNameBooking.getText().toString().trim().length()==0||txtDateBooking.getText().toString().trim().length()==0||
                        txtHourBooking.getText().toString().trim().length()==0){
                    Toast.makeText(ConfirmBookingActivity.this,"Completa tutti i campi",Toast.LENGTH_SHORT).show();
                }else {
                    openDialogConfirm();
                }
            }
        });

        txtHourBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtDateBooking.toString().trim().length()==0){
                    Toast.makeText(ConfirmBookingActivity.this, "Devi selezionare prima una data", Toast.LENGTH_SHORT).show();
                }else {
                    openDialogChooseHour(txtDayOfWeek.getText().toString());
                }
            }
        });

        txtDateBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        openCalendar();
    }

    private void openCalendar(){
        DialogFragment datePicker=new DatePickerFragment();//apro il calendario
        datePicker.show(getSupportFragmentManager(),"Date picker");
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar dateBooking=Calendar.getInstance();
        dateBooking.set(year,month++,dayOfMonth);//moth++ perchè i mesi partono da 0 e non da 1

        //uso la funzione getDayOfWeek per convertire il valore numerico restituito da Calendra.DAY_OF_WEEk nella stringa corrispondente al giorn della settimana
        String dayOfWeek=openingTimeUtility.getDayOfWeek(dateBooking.get(Calendar.DAY_OF_WEEK)-1);
        if(place.openingTime.get(dayOfWeek).length()>8){
            txtDateBooking.setText(year+"/"+(month++)+"/"+dayOfMonth);//setto la data in base alla scelta dell'utente.
            openDialogChooseHour(dayOfWeek);//una volta selezionata la data apro il dialog per scegliere l'ora
        }else{
            Toast.makeText(ConfirmBookingActivity.this,"Il locale è chiuso nella data selezionata",Toast.LENGTH_SHORT).show();
            openCalendar();
        }
    }

    private void openDialogChooseHour(String dayOfWeek){
        //setto la TextView con il giorno della settiama corrispondente alla prenotazione che mi
        // servirà per confrontare se l'ora della prenotazione in quel giorno è valida o no
        txtDayOfWeek.setText(dayOfWeek);
        DialogFragment timePicker=new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),"Time picker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        String day=txtDayOfWeek.getText().toString();//prendo il giorno della settimana corrispondente alla data della prenotazione
        String openingTime=place.openingTime.get(day);
        Time hourOpening=openingTimeUtility.getTimeOpening(openingTime);
        Time hourClosed=openingTimeUtility.getTimeClosed(openingTime);
        Time hourBooking=Time.valueOf(hour+":"+minutes+":"+00);
        //se hourBooking è maggiore di hourOpening restituisce un valore positivo e se è minore di hourClosed restituisce un valore negativo
        if(hourBooking.after(hourOpening)&&hourBooking.before(hourClosed)){
            txtHourBooking.setText(hour+":"+minutes);//setto l'ora della prenotazione
        }else{
         Toast.makeText(ConfirmBookingActivity.this,"Ora non valida",Toast.LENGTH_SHORT).show();
         openDialogChooseHour(day);
        }
    }

    private void openDialogConfirm(){//dialog  di conferma
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.confirm_order));
        builder.setMessage(this.getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(addBookingFirebase()){
                    //una volta inserito l'oggetto nel Db torno alla homePage
                    Toast.makeText(ConfirmBookingActivity.this,"Prenotazione confermata",Toast.LENGTH_SHORT).show();
                    Intent homePage=new Intent(ConfirmBookingActivity.this,HomepageActivity.class);
                    startActivity(homePage);
                    finish();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean addBookingFirebase(){
        //assegno all'oogetto booking i valori
        booking.setDateBooking(txtDateBooking.getText().toString());
        booking.setTimeBooking(txtHourBooking.getText().toString());
        booking.setIdClientBooking(getIntent().getStringExtra("UserID"));
        booking.setPlaceBooking(place);
        booking.setPersonNumBooking(Integer.parseInt(txtNumberPersonBooking.getText().toString()));
        booking.setNameBooking(editNameBooking.getText().toString());

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.writeObject(FirebaseConnection.BOOKING_TABLE,booking);//inserisco booking all'interno del Db

        mDBHelper.addInfo(mDB,place.idPlace,place.namePlace,booking.dateBooking+" "+booking.timeBooking+":"+"00");
        return true;
    }

}
