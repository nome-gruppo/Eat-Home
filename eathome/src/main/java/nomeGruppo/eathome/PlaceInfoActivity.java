package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.profile.DialogAddAddress;
import nomeGruppo.eathome.utility.MenuAdapterForClient;
import nomeGruppo.eathome.utility.OpeningTime;

public class PlaceInfoActivity extends FragmentActivity implements DialogAddAddress.DialogAddAddressListener, OnMapReadyCallback {

    private static final String SPLIT = ", ";
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
    private TextView txtOpeningTime;
    private Button btnOrder;
    private Button btnBook;
    private List<Food> listFood;
    private MenuAdapterForClient mAdapter;
    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;
    private AddressAdapter addressAdapter;
    private List<String>listAddress;
    private OpeningTime openingTimeUtility;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private boolean firstTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.listFoodOrder=new HashMap<>();
        this.order=new Order();
        this.openingTimeUtility=new OpeningTime();

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();
        this.listAddress=new LinkedList<>();
        this.addressAdapter=new AddressAdapter(PlaceInfoActivity.this,R.layout.listitem_address,listAddress);

        this.imgPlaceInfo=(ImageView) findViewById(R.id.imgPlaceInfo);
        this.listViewFoodInfo=(ListView)findViewById(R.id.listViewFoodInfo);
        this.txtDeliveryPlaceInfo=(TextView)findViewById(R.id.txtDeliveryPlaceInfo);
        this.txtBookingPlaceInfo=(TextView)findViewById(R.id.txtBookingPlaceInfo);
        this.txtNamePlaceInfo=(TextView)findViewById(R.id.txtNamePlaceInfo);
        this.txtAddressPlaceInfo=(TextView)findViewById(R.id.txtAddressPlaceInfo);
        this.txtCityPlaceInfo=(TextView)findViewById(R.id.txtCityPlaceInfo);
        this.txtDeliveryCostInfo=(TextView)findViewById(R.id.txtDeliveryCostInfo);
        this.txtOpeningTime=findViewById(R.id.txtOpeningTime);
        this.btnBook=(Button)findViewById(R.id.btnBook);
        this.btnOrder=(Button)findViewById(R.id.btnOrder);

        this.txtNamePlaceInfo.setText(this.place.namePlace);
        this.txtAddressPlaceInfo.setText(this.place.addressPlace+" "+this.place.addressNumPlace);
        this.txtCityPlaceInfo.setText(this.place.cityPlace);

        this.firstTime = true;

        final RatingBar ratingBar = findViewById(R.id.activity_place_info_ratingBar);
        final TextView numFeedbackTW = findViewById(R.id.activity_place_info_numFeedback);

        ratingBar.setRating(place.valuation);

        numFeedbackTW.setText(place.numberReview + " recensioni");

        numFeedbackTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceInfoActivity.this, FeedbackPlaceActivity.class);
                intent.putExtra(FirebaseConnection.PLACE, place);
                startActivity(intent);
            }
        });

//        MapFragment mMapFragment = MapFragment.newInstance();
//        FragmentTransaction fragmentTransaction =
//                getFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.activity_place_info, mMapFragment);
//        fragmentTransaction.commit();

        MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        listFood=new LinkedList<>();
        mAdapter=new MenuAdapterForClient(this,R.layout.listitem_menu_client,listFood,listFoodOrder);
        listViewFoodInfo.setAdapter(mAdapter);

        if(this.place.takesBookingPlace){
            this.txtBookingPlaceInfo.setVisibility(View.VISIBLE);
            this.btnBook.setEnabled(true);
        }

        if(this.place.takesOrderPlace){
            this.txtDeliveryPlaceInfo.setVisibility(View.VISIBLE);
            this.txtDeliveryCostInfo.setText(Integer.toString(this.place.deliveryCost));
            this.txtDeliveryCostInfo.setVisibility(View.VISIBLE);
            this.btnOrder.setEnabled(true);
        }


        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    openDialogOrder(listFoodOrder, place);
                }else{
                    Intent loginIntent = new Intent(PlaceInfoActivity.this, LoginActivity.class);
                    loginIntent.putExtra(FirebaseConnection.LOGIN_FLAG, true);
                    startActivity(loginIntent);
                }
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {
                    Intent bookingActivity = new Intent(PlaceInfoActivity.this,ConfirmBookingActivity.class);
                    bookingActivity.putExtra(FirebaseConnection.PLACE,place);
                    bookingActivity.putExtra("UserID",user.getUid());
                    startActivity(bookingActivity);
                }else{
                    Intent loginIntent = new Intent(PlaceInfoActivity.this, LoginActivity.class);
                    loginIntent.putExtra(FirebaseConnection.LOGIN_FLAG, true);
                    startActivity(loginIntent);
                }
            }
        });

    }

    //dialog che visualizza l'elenco dei cibi ordinati con costo e quantità
    public void openDialogOrder(final HashMap<Food,Integer>listFoodOrder, final Place place){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.order_summary));
        float tot=0;
        String message="";
        final ArrayList<String>nameFood=new ArrayList<>();
        for(Map.Entry<Food,Integer>entry:listFoodOrder.entrySet()){//scorro l'hashMap per prendere il nome dei cibi e la quantità
            Food key=entry.getKey();
            nameFood.add("X " +entry.getValue()+ " "+ key.nameFood);
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
                openDialogChooseAddress(nameFood,finalTot,place);//se clicca su ok va avanti al successivo dialogo

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

    //dialog per selezionare l'indirizzo di spedizione
    private void openDialogChooseAddress(final ArrayList<String>nameFood, final float finalTot, final Place place){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=PlaceInfoActivity.this.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_choose_address,null);
        builder.setView(view).setTitle(this.getResources().getString(R.string.choose_address));

        ListView listViewAddress=(ListView)view.findViewById(R.id.listViewChooseAddress);
        listViewAddress.setAdapter(addressAdapter);
        ImageButton btnAddAddress=view.findViewById(R.id.btnAddAddress);

        //leggo in SQLite gli indirizzi presenti e li assegno alla listView
        final Cursor c = mDB.query(DBOpenHelper.TABLE_ADDRESSES,DBOpenHelper.COLUMNS_ADDRESSES, DBOpenHelper.SELECTION_BY_USER_ID, new String[]{user.getUid()}, null, null, null);

        while(c.moveToNext()){
            String address = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS)) + SPLIT;
            address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS)) + SPLIT);
            address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY)));
            listAddress.add(address);
        }
        addressAdapter.notifyDataSetChanged();
        AlertDialog alert = builder.create();
        alert.show();

        //bottone che mi permette l'aggiunta di un nuovo indirizzo
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogAddAddress dialogAddAddress = new DialogAddAddress();
                dialogAddAddress.show(getSupportFragmentManager(), "Dialog add address");
            }
        });

        //una volta che l'utente seleziona l'indirizzo si passa alla successiva Activity per la conferma dell'ordine
        listViewAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String addressOrder =(String)adapterView.getItemAtPosition(i);
                Intent orderActivity=new Intent(PlaceInfoActivity.this,ConfirmOrderActivity.class);
                orderActivity.putExtra("UserID", user.getUid());
                orderActivity.putExtra(FirebaseConnection.PLACE,place);
                orderActivity.putExtra("NameFood", nameFood);
                orderActivity.putExtra("Tot", finalTot);
                orderActivity.putExtra("AddressOrder",addressOrder);
                startActivity(orderActivity);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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

        openingTime();

        if(firstTime) {
            loadFood();
        }
        firstTime = false;


    }

    public void openingTime() {
        final Calendar calendar = Calendar.getInstance();
        String day = openingTimeUtility.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        String openingTime = place.openingTime.get(day);
        Time localTime=new Time(System.currentTimeMillis());
        if (openingTime.length()>8) {//se è stato impostato un orario di apertura e chiusura
            Time timeOpening=openingTimeUtility.getTimeOpening(openingTime);
            Time timeClosed=openingTimeUtility.getTimeClosed(openingTime);
            //se localTime si trova tra timeOpening e timeClosed
            if (localTime.after(timeOpening)&&localTime.before(timeClosed)) {
                txtOpeningTime.setText(getResources().getString(R.string.opening_time) + " " + timeClosed.toString());
                return;
            } else{
                txtOpeningTime.setText(getResources().getString(R.string.closed_time) + " " + timeOpening.toString());
                return;
            }
        } else {
            txtOpeningTime.setText(getResources().getString(R.string.closed_place));
            btnOrder.setEnabled(false);
            return;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> mList = geocoder.getFromLocationName(place.addressPlace + " "+ place.addressNumPlace + ", " + place.cityPlace, 1);

            LatLng placeLatLng = new LatLng(mList.get(0).getLatitude(), mList.get(0).getLongitude());
            LatLngBounds bounds = new LatLngBounds(new LatLng(placeLatLng.latitude-0.001, placeLatLng.longitude -0.001),
                    new LatLng(placeLatLng.latitude+0.001, placeLatLng.longitude+0.001));
            googleMap.addMarker(new MarkerOptions().position(placeLatLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mAddress.setAddressLine();

        // Add a marker in Sydney and move the camera
    }

    private void loadFood(){
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
        addressAdapter.notifyDataSetChanged();
        mDBHelper.addAddress(mDB, address, numberAddress, city,user.getUid());
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
