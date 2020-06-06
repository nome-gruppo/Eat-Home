package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DatePickerFragment;
import nomeGruppo.eathome.utility.OpeningTime;
import nomeGruppo.eathome.utility.TimePickerFragment;

/*
activity per completare e confermare la prenotazione
 */
public class ConfirmBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String SLASH="/";

    private Booking booking;
    private Place place;
    private EditText txtDateBooking;
    private EditText txtHourBooking;
    private TextView txtNumberPersonBooking;
    private EditText editNameBooking;
    private OpeningTime openingTimeUtility;
    private Calendar dateBooking;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        final ImageButton btnAddPersonBooking = findViewById(R.id.btnAddPersonBooking);
        final ImageButton btnDeletePersonBooking = findViewById(R.id.btnDeletePersonBooking);
        final Button btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        this.txtDateBooking=findViewById(R.id.txtDateBooking);
        this.txtHourBooking=findViewById(R.id.txtHourBooking);
        this.txtNumberPersonBooking=findViewById(R.id.txtNumberPersonBooking);
        this.editNameBooking=findViewById(R.id.editNameClientBooking);

        this.booking=new Booking();
        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.openingTimeUtility=new OpeningTime();
        this.dateBooking=Calendar.getInstance();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getWritableDatabase();

        //se l'utente clicca sul bottone per aggiungere persone al tavolo della prenotazione
        btnAddPersonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(txtNumberPersonBooking.getText().toString());//trasformo in intero il numero letto dalla TextView
                i++;//aggiungo un posto
                txtNumberPersonBooking.setText(Integer.toString(i));//imposto il numero aggiornato nella TextView
            }
        });

        //se l'utente clicca sul bottone per sottrare persone al tavolo della prenotazione
        btnDeletePersonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i=Integer.parseInt(txtNumberPersonBooking.getText().toString());//trasformo in intero il numero letto dalla TextView
                if(i>1){//se il numero letto è maggiore di 1
                    i--;//sottraggo di un posto
                    txtNumberPersonBooking.setText(Integer.toString(i));//imposto il numero aggiornato nella TextView
                }
            }
        });

        //se l'utente clicca su conferma
        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //se non sono stati compilati tutti i campi
                if(editNameBooking.getText().toString().trim().length()==0||txtDateBooking.getText().toString().trim().length()==0||
                        txtHourBooking.getText().toString().trim().length()==0){
                    //mostra il messaggio completa tutti i campi
                    Toast.makeText(ConfirmBookingActivity.this,ConfirmBookingActivity.this.getResources().getString(R.string.fill_all_fields),Toast.LENGTH_SHORT).show();
                }else {//se tutti i campi sono stati compilati
                    openDialogConfirm();//apri il dialogo di conferma
                }
            }
        });

        //se l'utente clicca sul testo dell'orario della prenotazione
        txtHourBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //se non è stata ancora impostata una data
                if(txtDateBooking.toString().trim().length()==0){
                    //mostra messaggio
                    Toast.makeText(ConfirmBookingActivity.this, ConfirmBookingActivity.this.getResources().getString(R.string.need_data), Toast.LENGTH_SHORT).show();
                }else {//se è stata impostata una data
                    openDialogChooseHour();//apri il dialog per scegliere l'ora
                }
            }
        });

        //se l'utente clicca sul testo della data
        txtDateBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });//apre il dialog per impostare la data


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        openCalendar();
    }

    private void openCalendar(){
        DialogFragment datePicker=new DatePickerFragment();//apro il calendario
        datePicker.show(getSupportFragmentManager(),"Date picker");
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        dateBooking.set(year,month,dayOfMonth);//month++ perchè i mesi partono da 0 e non da 1

        //uso la funzione getDayOfWeek per convertire il valore numerico restituito da Calendar.DAY_OF_WEEk nella stringa corrispondente al giorno della settimana
        String dayOfWeek=openingTimeUtility.getDayOfWeek(dateBooking.get(Calendar.DAY_OF_WEEK));

        //se nel giorno selezioanto è stato impostato in orario quindi il locale non è chiuso
        if(place.openingTime.get(dayOfWeek) != null) {
            if (place.openingTime.get(dayOfWeek).length() > 8) {
                txtDateBooking.setText(String.format(Locale.getDefault(), String.format("%0" + 2 + "d", dayOfMonth).concat(SLASH).concat(String.format("%0" + 2 + "d", month++)).concat(SLASH).concat(Integer.toString(year))));//setto la data in base alla scelta dell'utente.
                openDialogChooseHour();//una volta selezionata la data apro il dialog per scegliere l'ora
            } else {//se il locale è chiuso nel giorno selezionato
                //mostra messaggio
                Toast.makeText(ConfirmBookingActivity.this, ConfirmBookingActivity.this.getResources().getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
                openCalendar();//riapri il dialog per scegliere la data
            }
        }
    }

    private void openDialogChooseHour(){
        DialogFragment timePicker=new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),"Time picker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        //uso la funzione getDayOfWeek per convertire il valore numerico restituito da Calendra.DAY_OF_WEEk nella stringa corrispondente al giorno della settimana
        String dayOfWeek=openingTimeUtility.getDayOfWeek(dateBooking.get(Calendar.DAY_OF_WEEK));
        String openingTime=place.openingTime.get(dayOfWeek);
        SimpleDateFormat parser = new SimpleDateFormat(getString(R.string.hourFormat), Locale.getDefault());


        try {
            Date hourOpening=null;
            Date hourClosed=null;
            if(openingTime!=null) {//se l'orario del locale è stato impostato
                hourOpening = openingTimeUtility.getTimeOpening(getApplicationContext(), openingTime);
                hourClosed = openingTimeUtility.getTimeClosed(getApplicationContext(), openingTime);
            }
            Date hourBooking = parser.parse(hour + ":" + minutes);
            //se l'ora della prenotazione è compresa tra l'ora di apertura e l'ora di chiusura

            assert hourBooking != null;
            if(hourBooking.after(hourOpening)&&hourBooking.before(hourClosed)){
                dateBooking.set(Calendar.HOUR_OF_DAY,hour);
                dateBooking.set(Calendar.MINUTE,minutes);
                txtHourBooking.setText(String.format("%0" + 2 + "d", hour)+":"+String.format("%0" + 2 + "d", minutes));//setto l'ora della prenotazione
            }else{//se il locale è chiuso nell'ora selezionata
                //mostra messaggio
                Toast.makeText(ConfirmBookingActivity.this,ConfirmBookingActivity.this.getResources().getString(R.string.invalid_time),Toast.LENGTH_SHORT).show();
                openDialogChooseHour();//riapri il dialog per scegliere l'ora
            }

        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void openDialogConfirm(){//dialog  di conferma
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.confirm));
        builder.setMessage(this.getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(addBookingFirebase()){//se la prenotazione è stata inserita correttamente nel database
                    //mostra messaggio
                    Toast.makeText(ConfirmBookingActivity.this,ConfirmBookingActivity.this.getResources().getString(R.string.reservation_confirmed),Toast.LENGTH_SHORT).show();
                    Intent homePage=new Intent(ConfirmBookingActivity.this, HomepageActivity.class);
                    startActivity(homePage);//apro la homepage
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
        //assegno all'oggetto booking i valori
        final Address mAddress = new Address(place.cityPlace, place.addressPlace, place.addressNumPlace);

        booking.setDateBooking(dateBooking.getTimeInMillis());//Firebase non accetta Calendar come tipo di dato in quanto non è un tipo JSON quindi lo trasformo in long
        booking.setIdClientBooking(getIntent().getStringExtra("UserID"));
        booking.setNamePlaceBooking(place.namePlace);
        booking.setAddressPlaceBooking(mAddress.getFullAddress());
        booking.setPhonePlaceBooking(place.phonePlace);
        booking.setIdPlaceBooking(place.idPlace);
        booking.setPersonNumBooking(Integer.parseInt(txtNumberPersonBooking.getText().toString()));
        booking.setNameBooking(editNameBooking.getText().toString());

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.writeObject(FirebaseConnection.BOOKING_NODE,booking);//inserisco booking all'interno del Db

        mDBHelper.addInfo(mDB,place.idPlace,place.namePlace,txtDateBooking.getText().toString(), mUser.getUid());//inserisco l'informazione della prenotazione del db interno
        return true;
    }

}
