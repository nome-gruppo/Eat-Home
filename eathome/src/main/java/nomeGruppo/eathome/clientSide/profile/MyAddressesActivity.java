package nomeGruppo.eathome.clientSide.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.clientSide.DialogAddAddress;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

public class MyAddressesActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener {

    private AddressAdapter mAdapter;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private Client client;
    private ArrayList<Address> addressList;

    private ListView addressesLW;
    private TextView noAddressesTW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        final FloatingActionButton addAddressBtn = findViewById(R.id.activity_my_address_btn_add);
        noAddressesTW = findViewById(R.id.activity_my_address_et_noAddresses);

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getWritableDatabase();

        addressList = new ArrayList<>();
        addressesLW = findViewById(R.id.activity_my_addresses_listView);
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        if (client != null) {
            mAdapter = new AddressAdapter(this, R.layout.listitem_my_address, addressList, client.idClient, MyAddressesActivity.this);

            Cursor c = mDB.query(DBOpenHelper.TABLE_ADDRESSES, DBOpenHelper.COLUMNS_ADDRESSES, DBOpenHelper.SELECTION_BY_USER_ID_ADDRESS, new String[]{client.idClient}, null, null, null);

            final int rows = c.getCount();

            //recupero indirizzi dal database
            if (rows == 0) {
                noAddressesTW.setVisibility(View.VISIBLE);
            } else {
                while (c.moveToNext()) {

                    int idAddress = c.getInt(c.getColumnIndexOrThrow(DBOpenHelper.ID_ADDRESS));
                    String address = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
                    String numAddress = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS));
                    String city = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY));

                    Address addressObj = new Address(idAddress, city, address, numAddress);
                    addressList.add(addressObj);
                }
                addressesLW.setAdapter(mAdapter);
                c.close();
            }

            addAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }
    }

    public void openDialog() {
        DialogAddAddress dialogAddAddress = new DialogAddAddress();
        dialogAddAddress.show(getSupportFragmentManager(), "Dialog add address");
    }

    public TextView getNoAddressTW() {
        return noAddressesTW;
    }

    @Override
    public void applyTexts(String city, String address, String numberAddress) {
        mDBHelper.addAddress(mDB, address, numberAddress, city, client.idClient);
        Address mAddress = new Address(mDBHelper.getLastIdAddresses(mDB), city, address, numberAddress);
        addressList.add(mAddress);
        mAdapter.notifyDataSetChanged();
        noAddressesTW.setVisibility(View.GONE);
        addressesLW.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDB.close();
    }
}
