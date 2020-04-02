package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.profile.DialogAddAddress;

public class ConfirmOrderActivity extends AppCompatActivity {

    private Order order;
    private Place place;
    private ArrayList<String> listFoodOrder;
    private float totOrder;
    private TextView txtTotOrder;
    private TextView txtTotDelivery;
    private TextView txtTotal;
    private TextView txtAddressOrder;


    private SQLiteDatabase mDB = null;
    private DBOpenHelper mDBHelper;
    private String addressOrder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);


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








    }

}
