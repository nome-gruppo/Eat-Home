package nomeGruppo.eathome.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.db.DBOpenHelper;

public class MyAddressesActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener {

    private TextView newAddressTV;
    private ImageButton addAddressBtn;
    private AddressAdapter mAdapter;
    private ArrayList<String> addressList;
    private SQLiteDatabase mDB = null;
    private DBOpenHelper mDBHelper;
    private ListView addressesLW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        newAddressTV = findViewById(R.id.activity_my_address_et_new_address);
        addAddressBtn = findViewById(R.id.activity_my_address_btn_add);
        addressesLW = findViewById(R.id.activity_my_addresses_listView);
//        LinearLayout addresses_list = (LinearLayout) findViewById(R.id.activity_my_addresses_linearLayout);

        addressList = new ArrayList<>();
        mAdapter = new AddressAdapter(this, R.layout.fragment_address, addressList);

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getWritableDatabase();

        addressesLW.setAdapter(mAdapter);
//        createadd();

        Cursor c = mDB.query(DBOpenHelper.TABLE_NAME,DBOpenHelper.COLUMNS, null, null, null, null, null);



        final int rows = c.getColumnCount();
        //View[] views = new View[rows];
        //LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        if(rows == 0){
            TextView noAddressET = findViewById(R.id.activity_my_address_et_noAddresses);
            noAddressET.setVisibility(View.VISIBLE);
        }else{
            //HashSet<AddressFragment> hashSetAddresses = new HashSet<>(rows);
          while(c.moveToNext()){
//                c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS))
                //hashSetAddresses.add(new AddressFragment());
                //views[i] = inflater.inflate(R.layout.fragment_address, null);
                String address=c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
                addressList.add(address);
//                addresses_list.addView(views[i]);
            }
            mAdapter.notifyDataSetChanged();
        }

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    public void openDialog(){
        DialogAddAddress dialogAddAddress=new DialogAddAddress();
        dialogAddAddress.show(getSupportFragmentManager(),"Dialog add address");
    }

    public void createadd(){
        ContentValues values = new ContentValues();

        values.put(DBOpenHelper.ADDRESS, "via roma");
        mDB.insert(DBOpenHelper.TABLE_NAME, null, values);
        values.clear();

        values.put(DBOpenHelper.ADDRESS, "via mignozzi");
        mDB.insert(DBOpenHelper.TABLE_NAME,null,values);
        values.clear();
    }

    @Override
    public void applyTexts(String city, String address, String numberAddress) {
        ContentValues values=new ContentValues();
        values.put(DBOpenHelper.ADDRESS,city+" - "+address+" - "+numberAddress);
        mDB.insert(DBOpenHelper.TABLE_NAME,null,values);
    }
}
