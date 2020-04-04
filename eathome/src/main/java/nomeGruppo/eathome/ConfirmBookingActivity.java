package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DatePickerFragment;
import nomeGruppo.eathome.utility.TimePickerFragment;

public class ConfirmBookingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Booking booking;
    private Place place;
    private TextView txtDateBooking;
    private TextView txtHourBooking;
    private TextView txtNumberPersonBooking;
    private ImageButton btnAddPersonBooking;
    private ImageButton btnDeletePersonBooking;
    private Button btnConfirmBooking;
    private EditText editNameBooking;

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
                if(editNameBooking.getText().toString().trim().length()==0){
                    Toast.makeText(ConfirmBookingActivity.this,"Completa tutti i campi",Toast.LENGTH_SHORT).show();
                }else {
                    openDialogConfirm();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        DialogFragment datePicker=new DatePickerFragment();//apro il calendario
        datePicker.show(getSupportFragmentManager(),"Date picker");
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        txtDateBooking.setText(dayOfMonth+"/"+(month++)+"/"+year);//setto la data in base alla scelta dell'utente. N.B. moth++ perchè partono da 0 e non da 1
        openDialogChooseHour();//una volta selezionata la data apro il dialog per scegliere l'ora
    }

    private void openDialogChooseHour(){
        DialogFragment timePicker=new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),"Time picker");
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        txtHourBooking.setText(hour+" : "+minutes);//setto l'ora della prenotazione
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
        booking.setIdPlaceBooking(place.idPlace);
        booking.setPersonNumBooking(Integer.parseInt(txtNumberPersonBooking.getText().toString()));
        booking.setNameBooking(editNameBooking.getText().toString());

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.writeObject(FirebaseConnection.BOOKING_TABLE,booking);//inserisco booking all'interno del Db
        return true;
    }

}
