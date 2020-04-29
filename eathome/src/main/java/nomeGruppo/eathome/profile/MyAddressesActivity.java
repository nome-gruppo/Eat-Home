package nomeGruppo.eathome.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

public class MyAddressesActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener{

    private static final String SPLIT = ", ";
    private AddressAdapter mAdapter;
    private ArrayList<Address> addressList;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private Client client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

       final FloatingActionButton addAddressBtn = findViewById(R.id.activity_my_address_btn_add);
       final ListView addressesLW = findViewById(R.id.activity_my_addresses_listView);

        client = (Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        addressList = new ArrayList<>();
        mAdapter = new AddressAdapter(this, R.layout.listitem_my_address, addressList, client.idClient);

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getWritableDatabase();



        if(client != null) {

            Cursor c = mDB.query(DBOpenHelper.TABLE_ADDRESSES, DBOpenHelper.COLUMNS_ADDRESSES, DBOpenHelper.SELECTION_BY_USER_ID_ADDRESS, new String[]{client.idClient}, null, null, null);

            final int rows = c.getCount();

            //recupero indirizzi dal database
            if (rows == 0) {
                TextView noAddressET = findViewById(R.id.activity_my_address_et_noAddresses);
                noAddressET.setVisibility(View.VISIBLE);
            } else {
                while (c.moveToNext()) {

                    int idAddress=c.getInt(c.getColumnIndexOrThrow(DBOpenHelper.ID_ADDRESS));
                    String address = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
                    String numAddress = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS));
                    String city=c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY));
                    Address addressObj=new Address(idAddress,address,numAddress,city);
                    addressList.add(addressObj);
                }
                addressesLW.setAdapter(mAdapter);
            }

            addAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }
    }

    public void openDialog(){
        DialogAddAddress dialogAddAddress = new DialogAddAddress();
        dialogAddAddress.show(getSupportFragmentManager(),"Dialog add address");
    }

    @Override
    public void applyTexts(String address, String numberAddress, String city) {
        mDBHelper.addAddress(mDB, address, numberAddress, city, client.idClient);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDB.close();
    }
}
