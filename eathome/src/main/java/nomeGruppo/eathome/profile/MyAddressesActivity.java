package nomeGruppo.eathome.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.db.DBOpenHelper;

public class MyAddressesActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener{

    private static final String SPLIT = ", ";
    private AddressAdapter mAdapter;
    private ArrayList<String> addressList;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private DialogAddAddress dialogAddAddress;

    private ArrayList<String> newAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        ImageButton addAddressBtn = findViewById(R.id.activity_my_address_btn_add);
        ListView addressesLW = findViewById(R.id.activity_my_addresses_listView);

        addressList = new ArrayList<>();
        newAddresses = new ArrayList<>();
        mAdapter = new AddressAdapter(this, R.layout.fragment_my_place_autocomplete, addressList);

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getWritableDatabase();



        Cursor c = mDB.query(DBOpenHelper.TABLE_NAME,DBOpenHelper.COLUMNS, null, null, null, null, null);

        final int rows = c.getColumnCount();

        //recupero indirizzi dal database
        if(rows == 0){
            TextView noAddressET = findViewById(R.id.activity_my_address_et_noAddresses);
            noAddressET.setVisibility(View.VISIBLE);
        }else{
          while(c.moveToNext()){

                String address = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS)) + SPLIT;
                address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS)) + SPLIT);
                address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY)));
                addressList.add(address);
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

    public void openDialog(){
        dialogAddAddress=new DialogAddAddress();
        dialogAddAddress.show(getSupportFragmentManager(),"Dialog add address");
    }

    @Override
    public void applyTexts(String address, String numberAddress, String city) {

        mAdapter.notifyDataSetChanged();
        mDBHelper.addAddress(mDB, address, numberAddress, city);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDB.close();
    }
}
