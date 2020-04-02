package nomeGruppo.eathome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.profile.DialogAddAddress;
import nomeGruppo.eathome.utility.MenuAdapterForClient;

public class PlaceInfoActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener {

    private Place place;
    private Order order;
    private HashMap<Food,Integer> listFoodOrder;
    private ImageView imgPlaceInfo;
    private ListView listViewFoodInfo;
    private TextView txtDeliveryPlaceInfo;
    private TextView txtBookingPlaceInfo;
    private TextView txtNamePlaceInfo;
    private TextView txtAddressPlaceInfo;
    private TextView txtCityPlaceInfo;
    private TextView txtDeliveryCostInfo;
    private Button btnOrder;
    private Button btnBook;
    private List<Food> listFood;
    private MenuAdapterForClient mAdapter;
    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.listFoodOrder=new HashMap<>();
        this.order=new Order();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();

        this.imgPlaceInfo=(ImageView) findViewById(R.id.imgPlaceInfo);
        this.listViewFoodInfo=(ListView)findViewById(R.id.listViewFoodInfo);
        this.txtDeliveryPlaceInfo=(TextView)findViewById(R.id.txtDeliveryPlaceInfo);
        this.txtBookingPlaceInfo=(TextView)findViewById(R.id.txtBookingPlaceInfo);
        this.txtNamePlaceInfo=(TextView)findViewById(R.id.txtNamePlaceInfo);
        this.txtAddressPlaceInfo=(TextView)findViewById(R.id.txtAddressPlaceInfo);
        this.txtCityPlaceInfo=(TextView)findViewById(R.id.txtCityPlaceInfo);
        this.txtDeliveryCostInfo=(TextView)findViewById(R.id.txtDeliveryCostInfo);
        this.btnBook=(Button)findViewById(R.id.btnBook);
        this.btnOrder=(Button)findViewById(R.id.btnOrder);

        this.txtNamePlaceInfo.setText(this.place.namePlace);
        this.txtAddressPlaceInfo.setText(this.place.addressPlace+" "+this.place.addressNumPlace);
        this.txtCityPlaceInfo.setText(this.place.cityPlace);

        listFood=new LinkedList<>();
        mAdapter=new MenuAdapterForClient(this,R.layout.listitem_menu_client,listFood,listFoodOrder);
        listViewFoodInfo.setAdapter(mAdapter);

        if(this.place.takesBookingPlace==true){
            this.txtBookingPlaceInfo.setVisibility(View.VISIBLE);
            this.btnBook.setEnabled(true);
        }

        if(this.place.takesOrderPlace==true){
            this.txtDeliveryPlaceInfo.setVisibility(View.VISIBLE);
            this.txtDeliveryCostInfo.setText(Integer.toString(this.place.deliveryCost));
            this.txtDeliveryCostInfo.setVisibility(View.VISIBLE);
            this.btnOrder.setEnabled(true);
        }

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogOrder(listFoodOrder,place);

            }
        });

    }

    public void openDialogOrder(final HashMap<Food,Integer>listFoodOrder, final Place place){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.order_summary));
        float tot=0;
        String message="";
        final ArrayList<String>nameFood=new ArrayList<>();
        for(Map.Entry<Food,Integer>entry:listFoodOrder.entrySet()){
            Food key=entry.getKey();
            nameFood.add(key.nameFood);
            int number=entry.getValue();
            float totParz=key.priceFood*number;
            tot+=totParz;
            message+=(number +"X "+key.nameFood+" "+ totParz+" €"+"\n");
        }
        message+="Tot" +tot+" €";
        builder.setMessage(message);
        final float finalTot = tot;
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                opendDialogChooseAddress(nameFood,finalTot,place);

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

    private void opendDialogChooseAddress(final ArrayList<String>nameFood, final float finalTot,final Place place){

        final List<String>listAddress=new LinkedList<>();
        final AddressAdapter addressAdapter=new AddressAdapter(this,R.layout.listitem_address,listAddress);
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=PlaceInfoActivity.this.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_choose_address,null);
        builder.setView(view).setTitle(this.getResources().getString(R.string.choose_address));

        ListView listViewAddress=(ListView)view.findViewById(R.id.listViewChooseAddress);
        listViewAddress.setAdapter(addressAdapter);
        ImageButton btnAddAddress=view.findViewById(R.id.btnAddAddress);

        final Cursor c = mDB.query(DBOpenHelper.TABLE_NAME,DBOpenHelper.COLUMNS, null, null, null, null, null);

        while(c.moveToNext()){
            String address=c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
            listAddress.add(address);
        }
        addressAdapter.notifyDataSetChanged();
        AlertDialog alert = builder.create();
        alert.show();

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddAddress dialogAddAddress = new DialogAddAddress();
                dialogAddAddress.show(getSupportFragmentManager(), "Dialog add address");
                if(dialogAddAddress.isRemoving()){
                    readDb(c,listAddress,addressAdapter);
                }
            }
        });

        listViewAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String addressOrder =(String)adapterView.getItemAtPosition(i);
                Intent orderActivity=new Intent(PlaceInfoActivity.this,ConfirmOrderActivity.class);
                orderActivity.putExtra(FirebaseConnection.PLACE,place);
                orderActivity.putExtra("NameFood", nameFood);
                orderActivity.putExtra("Tot", finalTot);
                orderActivity.putExtra("AddressOrder",addressOrder);
                startActivity(orderActivity);
            }
        });
    }

    private void readDb(Cursor c,List<String>listAddress, AddressAdapter addressAdapter){
        while(c.moveToNext()){
            String address=c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
            listAddress.add(address);
        }
        addressAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();

        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(3840*3840)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlaceInfo.setImageBitmap(bitmap);
                    }
                });

        final FirebaseConnection firebaseConnection=new FirebaseConnection();

        //leggo i cibi presenti all'interno del ristorante e li assegno alla listFood collegata con l'adapter per poter stamparli sulla listView corrispondente
        firebaseConnection.getmDatabase().child("Foods").child(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listFood.add(snapshot.getValue(Food.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void applyTexts(String city, String address, String numberAddress) {
        ContentValues values=new ContentValues();
        values.put(DBOpenHelper.ADDRESS,city+" - "+address+" - "+numberAddress);
        mDB.insert(DBOpenHelper.TABLE_NAME,null,values);
    }

    private class AddressAdapter extends ArrayAdapter<String> {
        private TextView txtAddress;

        public AddressAdapter(@NonNull Context context, int resource, List<String> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listitem_address, null);

            }
            txtAddress = convertView.findViewById(R.id.radioBtnAddress);

            String address = getItem(position);

            txtAddress.setText(address);

            return convertView;
        }
    }






}
