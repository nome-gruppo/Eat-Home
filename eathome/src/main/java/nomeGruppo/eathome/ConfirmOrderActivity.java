package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.profile.DialogAddAddress;
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
    private TimePickerDialog timePickerDialog;


    private SQLiteDatabase mDB = null;
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
                    Toast.makeText(ConfirmOrderActivity.this, "Inserisci nome destinatario", Toast.LENGTH_SHORT).show();
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
        order.setIdPlaceOrder(place.idPlace);
        order.setPhoneClientOrder(editPhone.getText().toString());
        order.setTimeOrder(chooseTime.getText().toString());

        // TODO setIdClient in order

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).push().setValue(order);

        return true;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
        EditText editChooseTime=findViewById(R.id.editChooseTime);
        editChooseTime.setText(hourOfDay+" : "+minutes);
    }
}
