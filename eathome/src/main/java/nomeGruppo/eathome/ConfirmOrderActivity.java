package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.OpeningTime;
import nomeGruppo.eathome.utility.TimePickerFragment;

/*
activity per completare e confermare un ordine
 */
public class ConfirmOrderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Order order;
    private TextView txtTotOrder;
    private TextView txtTotDelivery;
    private TextView txtTotal;
    private TextView txtAddressOrder;
    private EditText editName;
    private EditText editPhone;
    private Button btnConfirm;
    private EditText chooseTime;
    private OpeningTime openingTimeUtility;


    private SQLiteDatabase mDB;
    private DBOpenHelper mDBHelper;

    private FirebaseUser mUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        this.order=(Order)getIntent().getSerializableExtra(FirebaseConnection.ORDER);
        this.openingTimeUtility=new OpeningTime();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();

        this.txtTotOrder=findViewById(R.id.txtTotOrder);
        this.txtTotDelivery=findViewById(R.id.txtDeliveryCostOrder);
        this.txtTotal=findViewById(R.id.txtTotal);
        this.txtAddressOrder=findViewById(R.id.txtAddressOrder);
        this.txtTotOrder.setText(Float.toString(order.totalOrder));
        this.txtTotDelivery.setText(Float.toString(order.placeOrder.deliveryCost));
        this.txtTotal.setText(Float.toString(order.totalOrder+order.placeOrder.deliveryCost));
        this.txtAddressOrder.setText(order.addressOrder);
        this.btnConfirm=findViewById(R.id.btnConfirmOrder);
        this.editName=findViewById(R.id.editNameClientOrder);
        this.editPhone=findViewById(R.id.editPhoneClientOrder);
        this.chooseTime=findViewById(R.id.editChooseTime);


        //se l'utenten clicca sulla editText per scegliere l'ora
        this.chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker=new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"Time picker");//apro il TimePicker
            }
        });

        //se l'utente clicca su conferma
        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se non sono stati completati tutti i campi
                if(editName.getText().toString().trim().length()==0||chooseTime.getText().toString().trim().length()==0||editPhone.getText().toString().trim().length()==0){
                    //mostra messaggio
                    Toast.makeText(ConfirmOrderActivity.this, ConfirmOrderActivity.this.getResources().getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                }else{//se sono stati completati tutti i campi
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

    private void openDialogConfirm(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.confirm_order));
        builder.setMessage(this.getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(addOrderFirebase()){//se l'ordine è stato inserito correttamente all'interno del database
                    //mostra messaggio
                    Toast.makeText(ConfirmOrderActivity.this,ConfirmOrderActivity.this.getResources().getString(R.string.order_confirm),Toast.LENGTH_SHORT).show();
                    Intent homePage=new Intent(ConfirmOrderActivity.this, HomepageActivity.class);
                    startActivity(homePage);//apri homepage
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

    private boolean addOrderFirebase(){
        order.setNameClientOrder(editName.getText().toString());
        order.setPhoneClientOrder(editPhone.getText().toString());
        order.setTimeOrder(chooseTime.getText().toString());
        order.setDateOrder(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).push().setValue(order);

        //inserisco l'informazione dell'ordinazione nel db interno
        mDBHelper.addInfo(mDB,order.placeOrder.idPlace, order.placeOrder.namePlace,new SimpleDateFormat("yyyy/MM/dd").format(new Date()), mUser.getUid());
        return true;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        //leggo l'orario di apertura chiusura del locale
        String openingTime=order.placeOrder.openingTime.get(openingTimeUtility.getDayOfWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        Date  timeOpening=null;
        Date timeClosed= null;
        Date timeOrder=null;
        try {
            timeOrder=parser.parse(hourOfDay+":"+minutes);
            timeClosed = openingTimeUtility.getTimeClosed(openingTime);//estrapolo l'orario di chiusura
            timeOpening=openingTimeUtility.getTimeOpening(openingTime);//estrapolo l'orario di apertura
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //se l'ora dell'ordine è comprea tra ora di apertura e ora di chiusura
        if(timeOrder.after(timeOpening)&&timeOrder.before(timeClosed)){
            EditText editChooseTime=findViewById(R.id.editChooseTime);
            editChooseTime.setText(parser.format(timeOrder));//imposta l'ora nella EditText
        }else{//se il locale è chiuso nell'ora selezionata
            //mostra messaggio
            Toast.makeText(ConfirmOrderActivity.this,ConfirmOrderActivity.this.getResources().getString(R.string.invalid_time),Toast.LENGTH_SHORT).show();
        }


    }
}
