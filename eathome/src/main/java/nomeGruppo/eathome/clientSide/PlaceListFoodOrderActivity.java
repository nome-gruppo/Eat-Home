package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.utility.MenuAdapterForClient;

public class PlaceListFoodOrderActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener  {
    private static final String SPLIT = ", ";

    private Place place;
    private List<Food> listFood;
    private MenuAdapterForClient mAdapter;
    private Order order;
    private HashMap<Food,Integer> listFoodOrder;
    private ListView listViewFoodInfo;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ArrayList<String> nameFood;
    private float finalTot;
    private Button btnOrder;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PlaceListFoodOrderActivity.AddressAdapter addressAdapter;
    private List<String>listAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_food_order);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.order=new Order();
        this.listFoodOrder=new HashMap<>();
        this.nameFood=new ArrayList<>();
        this.finalTot=0;
        this.listViewFoodInfo=findViewById(R.id.listViewFoodInfo);
        this.btnOrder=findViewById(R.id.btnOrderFood);

        this.mDBHelper = new DBOpenHelper(this);
        this.mDB = mDBHelper.getReadableDatabase();
        this.listAddress=new LinkedList<>();
        this.addressAdapter=new PlaceListFoodOrderActivity.AddressAdapter(PlaceListFoodOrderActivity.this,R.layout.listitem_address,listAddress);

        listFood=new LinkedList<>();
        this.mAdapter=new MenuAdapterForClient(PlaceListFoodOrderActivity.this,R.layout.listitem_menu_client,listFood,listFoodOrder);
        listViewFoodInfo.setAdapter(mAdapter);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null) {//se l'utente è loggato
                    if(listFoodOrder.isEmpty()){//se la lista degli ordini è vuota
                        Toast.makeText(PlaceListFoodOrderActivity.this,getResources().getString(R.string.invalid_order),Toast.LENGTH_SHORT).show();
                    }else {//se la lista ordine contiene almeno un ordine
                        openDialogOrder(listFoodOrder, place);//apri dialog di riepilogo ordine
                    }
                }else{//se l'utente non è loggato
                    Intent loginIntent = new Intent(PlaceListFoodOrderActivity.this, LoginActivity.class);
                    loginIntent.putExtra(FirebaseConnection.LOGIN_FLAG, true);
                    startActivity(loginIntent);//apri login
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        loadFood();
    }

    private void loadFood(){
        listFood.clear();
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

    //dialog che visualizza l'elenco dei cibi ordinati con costo e quantità
    public void openDialogOrder(final HashMap<Food,Integer>listFoodOrder, final Place place){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.order_summary));
        float tot=0;
        String message="";
        for(Map.Entry<Food,Integer>entry:listFoodOrder.entrySet()){//scorro l'hashMap per prendere il nome dei cibi e la quantità
            Food key=entry.getKey();
            nameFood.add("X " +entry.getValue()+ " "+ key.nameFood);//aggiungo alla lista dei cibi il nome con la relativa quantità
            int number=entry.getValue();//prendo la quantità
            float totParz=key.priceFood*number;//moltiplico il prezzo per la quantità per avere il costo di un cibo ordinato
            tot+=totParz;//sommo il costo del cibo con il totale finale
            message+=(number +"X "+key.nameFood+" "+ totParz+" €"+"\n");//imposto il messaggio con il riepilogo della quantità del nome e del costo parziale del cibo
        }
        message+="Tot" +tot+" €";//imposto nel messaggio il totale finale
        builder.setMessage(message);//mostro il messaggio
        finalTot = tot;
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
        LayoutInflater inflater=PlaceListFoodOrderActivity.this.getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_choose_address,null);
        builder.setView(view).setTitle(this.getResources().getString(R.string.choose_address));

        ListView listViewAddress=(ListView)view.findViewById(R.id.listViewChooseAddress);
        listViewAddress.setAdapter(addressAdapter);
        FloatingActionButton btnAddAddress=view.findViewById(R.id.btnAddAddress);

        listAddress.clear();
        //leggo in SQLite gli indirizzi presenti e li assegno alla listView
        final Cursor c = mDB.query(DBOpenHelper.TABLE_ADDRESSES,DBOpenHelper.COLUMNS_ADDRESSES, DBOpenHelper.SELECTION_BY_USER_ID_ADDRESS, new String[]{user.getUid()}, null, null, null);

        while(c.moveToNext()){//se sono presenti indirizzi
            String address = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS)) + SPLIT;
            address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS)) + SPLIT);
            address = address.concat(c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY)));
            listAddress.add(address);//aggiungo l'indirizzo totale alla lista a cui è collegato l'adapter
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
                Intent orderActivity=new Intent(PlaceListFoodOrderActivity.this, ConfirmOrderActivity.class);
                order=setOrder(addressOrder);
                orderActivity.putExtra(FirebaseConnection.ORDER,order);
                orderActivity.putExtra(FirebaseConnection.PLACE,place);
                startActivity(orderActivity);
            }
        });
    }

    private Order setOrder(String addressOrder){//funzione per settare i valore di order
        order.setIdClientOrder(user.getUid());
        order.setIdPlaceOrder(place.idPlace);
        order.setNamePlaceOrder(place.namePlace);
        order.setAddressPlaceOrder(place.cityPlace+", "+place.addressPlace+", "+place.addressNumPlace);
        order.setPhonePlaceOrder(place.phonePlace);
        order.setStateOrder(false);
        order.setDeliveryCost(place.deliveryCost);
        order.setFoodsOrder(nameFood);
        order.setTotalOrder(finalTot);
        order.setAddressOrder(addressOrder);
        return order;
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

    @Override
    public void applyTexts(String city, String address, String numberAddress) {
        addressAdapter.notifyDataSetChanged();
        mDBHelper.addAddress(mDB, address, numberAddress, city,user.getUid());//aggiungo l'indirizzo appena scritto dall'utente al db interno

        Intent orderActivity=new Intent(PlaceListFoodOrderActivity.this,ConfirmOrderActivity.class);
        final String addressOrder=address+","+numberAddress+","+city;
        order=setOrder(addressOrder);//imposto l'indirizzo appena scritto dall'utente come indirizzo di consegna
        orderActivity.putExtra(FirebaseConnection.ORDER,order);
        startActivity(orderActivity);//apro l'activity per confermare l'ordine
    }

}
