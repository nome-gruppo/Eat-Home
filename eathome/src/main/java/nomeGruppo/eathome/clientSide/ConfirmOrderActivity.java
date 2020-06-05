package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.OpeningTime;
import nomeGruppo.eathome.utility.TimePickerFragment;

/*
activity per completare e confermare un ordine
 */
public class ConfirmOrderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Order order;
    private Place place;
    private EditText editName;
    private EditText editPhone;
    private Button chooseTime;
    private OpeningTime openingTimeUtility;


    private SQLiteDatabase mDB;
    private DBOpenHelper mDBHelper;

    private FirebaseUser mUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        final TextView txtTotOrder = findViewById(R.id.txtTotOrder);
        final TextView txtTotDelivery = findViewById(R.id.txtDeliveryCostOrder);
        final TextView txtTotal = findViewById(R.id.txtTotal);
        final TextView txtAddressOrder = findViewById(R.id.txtAddressOrder);
        final Button btnAddNote = findViewById(R.id.btnAddNote);
        final Button btnConfirm = findViewById(R.id.btnConfirmOrder);
        this.editName = findViewById(R.id.editNameClientOrder);
        this.editPhone = findViewById(R.id.editPhoneClientOrder);
        this.chooseTime = findViewById(R.id.editChooseTime);

        this.order = (Order) getIntent().getSerializableExtra(FirebaseConnection.ORDER);
        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.openingTimeUtility = new OpeningTime();

        txtTotOrder.setText(String.format(Locale.getDefault(),"%.2f", order.totalOrder));
        txtTotOrder.setText(String.format(Locale.getDefault(),"%.2f",order.totalOrder));
        txtTotDelivery.setText(String.format(Locale.getDefault(),"%d",order.deliveryCost));
        txtTotal.setText(String.format(Locale.getDefault(),"%.2f",order.totalOrder + order.deliveryCost));
        txtAddressOrder.setText(order.addressOrder);

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();


        //se l'utenten clicca sulla editText per scegliere l'ora
        this.chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "Time picker");//apro il TimePicker
            }
        });

        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogAddNote();
            }
        });

        //se l'utente clicca su conferma
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se non sono stati completati tutti i campi
                if (editName.getText().toString().trim().length() == 0 || chooseTime.getText().toString().equals(getResources().getString(R.string.choose_time)) || editPhone.getText().toString().trim().length() == 0) {
                    //mostra messaggio
                    Toast.makeText(ConfirmOrderActivity.this, ConfirmOrderActivity.this.getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                } else {//se sono stati completati tutti i campi
                    openDialogConfirm();//apri il dialog di conferma
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }

    private void openDialogAddNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ConfirmOrderActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_reply_feedback, null);
        final EditText note=view.findViewById(R.id.editReplyFeedback);
        if(order.note==null){
            note.setHint(getResources().getString(R.string.add_note));
        }else{
            note.setText(order.note);
        }
        builder.setView(view).setTitle(getResources().getString(R.string.add_note));
        builder.setView(view).setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(note.getText().toString().trim().length()==0){
                    Toast.makeText(ConfirmOrderActivity.this,getResources().getString(R.string.no_note),Toast.LENGTH_SHORT).show();
                }else{
                    order.setNote(note.getText().toString());
                    Toast.makeText(ConfirmOrderActivity.this,getResources().getString(R.string.note_success),Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final Client client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
        builder.setTitle(this.getResources().getString(R.string.confirm));
        builder.setMessage(this.getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addOrderFirebase();//dopo che l'ordine è stato inserito correttamente all'interno del database
                Toast.makeText(ConfirmOrderActivity.this, ConfirmOrderActivity.this.getResources().getString(R.string.order_confirm), Toast.LENGTH_SHORT).show();//mostra messaggio
                Intent homePage = new Intent(ConfirmOrderActivity.this, HomepageActivity.class);
                homePage.putExtra(FirebaseConnection.CLIENT, client);
                homePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                homePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(homePage);//apri homepage
                finish();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void addOrderFirebase() {
        order.setNameClientOrder(editName.getText().toString());
        order.setPhoneClientOrder(editPhone.getText().toString());
        order.setTimeOrder(chooseTime.getText().toString());
        order.setDateOrder(new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(new Date()));
        order.timestampOrder = System.currentTimeMillis();   //momento in cui è stato realizzato l'ordine
        FirebaseConnection firebaseConnection = new FirebaseConnection();

        //prelevo la chiave assegnata in automatico da Firebase
        String idOrder = firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_NODE).push().getKey();
        order.setIdOrder(idOrder);
        firebaseConnection.write(FirebaseConnection.ORDER_NODE, idOrder, order);

        //inserisco l'informazione dell'ordinazione nel db interno
        mDBHelper.addInfo(mDB, order.idPlaceOrder, order.namePlaceOrder, new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(new Date()), mUser.getUid());
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
        SimpleDateFormat parser = new SimpleDateFormat(getString(R.string.hourFormat), Locale.getDefault());
        //leggo l'orario di apertura chiusura del locale
        String openingTime = place.openingTime.get(openingTimeUtility.getDayOfWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        Date timeOpening;
        Date timeClosed;
        Date timeOrder;
        try {
            if(openingTime != null) {
                timeOrder = parser.parse(hourOfDay + ":" + minutes);
                timeClosed = openingTimeUtility.getTimeClosed(getApplicationContext(), openingTime);//estrapolo l'orario di chiusura
                timeOpening = openingTimeUtility.getTimeOpening(getApplicationContext(), openingTime);//estrapolo l'orario di apertura

                //se l'ora dell'ordine è comprea tra ora di apertura e ora di chiusura
                if (timeOrder.after(timeOpening) && timeOrder.before(timeClosed)) {
                    Button editChooseTime = findViewById(R.id.editChooseTime);
                    editChooseTime.setText(parser.format(timeOrder));//imposta l'ora nella EditText
                } else {//se il locale è chiuso nell'ora selezionata
                    //mostra messaggio
                    Toast.makeText(ConfirmOrderActivity.this, ConfirmOrderActivity.this.getResources().getString(R.string.invalid_time), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
