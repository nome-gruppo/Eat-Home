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


import com.google.android.libraries.places.api.model.LocalTime;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.OpeningTime;
import nomeGruppo.eathome.utility.TimePickerFragment;

public class ConfirmOrderActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Order order;
    private Place place;
    private ArrayList<String> listFoodOrder;
    private float totOrder;
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
    private String addressOrder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        this.order=new Order();
        this.place=(Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.totOrder=(Float) getIntent().getExtras().getFloat("Tot");
        this.listFoodOrder= (ArrayList<String>)getIntent().getSerializableExtra("NameFood");
        this.addressOrder=(String) getIntent().getExtras().getString("AddressOrder");
        this.openingTimeUtility=new OpeningTime();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();

        this.txtTotOrder=findViewById(R.id.txtTotOrder);
        this.txtTotDelivery=findViewById(R.id.txtDeliveryCostOrder);
        this.txtTotal=findViewById(R.id.txtTotal);
        this.txtAddressOrder=findViewById(R.id.txtAddressOrder);
        this.txtTotOrder.setText(Float.toString(totOrder));
        this.txtTotDelivery.setText(Float.toString(place.deliveryCost));
        this.txtTotal.setText(Float.toString(totOrder+place.deliveryCost));
        this.txtAddressOrder.setText(addressOrder);
        this.btnConfirm=findViewById(R.id.btnConfirmOrder);
        this.editName=findViewById(R.id.editNameClientOrder);
        this.editPhone=findViewById(R.id.editPhoneClientOrder);
        this.chooseTime=findViewById(R.id.editChooseTime);

        this.chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker=new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"Time picker");
            }
        });

        this.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editName.getText().toString().trim().length()==0||chooseTime.getText().toString().trim().length()==0){
                    Toast.makeText(ConfirmOrderActivity.this, "Inserisci nome destinatario o orario di consegna", Toast.LENGTH_SHORT).show();
                }else{
                    openDialogConfirm();
                }

            }
        });

    }

    private void openDialogConfirm(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.confirm_order));
        builder.setMessage(this.getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(addOrderFirebase()){
                    Toast.makeText(ConfirmOrderActivity.this,"Ordine confermato",Toast.LENGTH_SHORT).show();
                    Intent homePage=new Intent(ConfirmOrderActivity.this,HomepageActivity.class);
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

    private boolean addOrderFirebase(){
        order.setAddressOrder(addressOrder);
        order.setNameClientOrder(editName.getText().toString());
        order.setFoodsOrder(listFoodOrder);
        order.setIdClientOrder(getIntent().getStringExtra("UserID"));
        order.setPlaceOrder(place);
        order.setPhoneClientOrder(editPhone.getText().toString());
        order.setTimeOrder(chooseTime.getText().toString());
        order.setDateOrder(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        order.setTotalOrder(totOrder);

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).push().setValue(order);

        //inserisco l'informazione dell'ordinazione nel db interno
        mDBHelper.addInfo(mDB,place.idPlace, place.namePlace,new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        return true;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
        Time timeOrder=Time.valueOf(hourOfDay+":"+minutes+":"+00);
        String openingTime=place.openingTime.get(openingTimeUtility.getDayOfWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)));
        Time timeOpening=openingTimeUtility.getTimeOpening(openingTime);
        Time timeClosed=openingTimeUtility.getTimeClosed(openingTime);

        if(timeOrder.after(timeOpening)&&timeOrder.before(timeClosed)){
            EditText editChooseTime=findViewById(R.id.editChooseTime);
            editChooseTime.setText(timeOrder.toString());
        }else{
            Toast.makeText(ConfirmOrderActivity.this,"Orario non valido",Toast.LENGTH_SHORT).show();
        }


    }
}
