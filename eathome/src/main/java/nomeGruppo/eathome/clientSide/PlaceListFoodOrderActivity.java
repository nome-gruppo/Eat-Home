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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import nomeGruppo.eathome.actions.Address;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.utility.MenuAdapterForClient;

/*
activity per visualizzare e interagire con la lista dei cibi da ordinare
 */

public class PlaceListFoodOrderActivity extends AppCompatActivity implements DialogAddAddress.DialogAddAddressListener {

    private static final int TO_LOGIN_ACTIVITY = 51;

    private Place place;
    private List<Food> listFood;
    private MenuAdapterForClient menuAdapter;
    private Order order;
    private HashMap<Food, Integer> mapFoodOrder;
    private FirebaseUser user;
    private ArrayList<String> nameFood;
    private float finalTot;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;
    private PlaceListFoodOrderActivity.AddressAdapter addressAdapter;
    private List<Address> listAddress;

    private Client client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food_order);

        final ListView listViewFoodInfo = findViewById(R.id.listViewFoodInfo);
        final Button btnOrder = findViewById(R.id.btnOrderFood);

        place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getReadableDatabase();

        listAddress = new LinkedList<>();
        addressAdapter = new PlaceListFoodOrderActivity.AddressAdapter(PlaceListFoodOrderActivity.this, R.layout.listitem_address, listAddress);

        listFood = new LinkedList<>();
        mapFoodOrder = new HashMap<>();
        menuAdapter = new MenuAdapterForClient(PlaceListFoodOrderActivity.this, R.layout.listitem_menu_client, listFood, mapFoodOrder);
        listViewFoodInfo.setAdapter(menuAdapter);

        order = new Order();
        nameFood = new ArrayList<>();
        finalTot = 0;

        Toolbar toolbarListFoodOrder = findViewById(R.id.tlbListFoodOrder);
        setSupportActionBar(toolbarListFoodOrder);
        toolbarListFoodOrder.setTitle("@string/choose");
        toolbarListFoodOrder.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        toolbarListFoodOrder.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent placeInfoIntent = new Intent(PlaceListFoodOrderActivity.this, PlaceInfoActivity.class);
                        placeInfoIntent.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(placeInfoIntent);
                        finish();
                    }
                }
        );




        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {//se l'utente è loggato
                    if (mapFoodOrder.isEmpty()) {//se la lista degli ordini è vuota
                        Toast.makeText(PlaceListFoodOrderActivity.this, getResources().getString(R.string.invalid_order), Toast.LENGTH_SHORT).show();
                    } else {//se la lista ordine contiene almeno un ordine
                        openDialogOrder(mapFoodOrder, place);//apri dialog di riepilogo ordine
                    }
                } else {//se l'utente non è loggato
                    Intent loginIntent = new Intent(PlaceListFoodOrderActivity.this, LoginActivity.class);
                    loginIntent.putExtra(FirebaseConnection.FROM_ANOTHER_ACTIVITY, false);
                    loginIntent.putExtra(FirebaseConnection.PLACE, place);
                    loginIntent.putExtra(FirebaseConnection.ORDER, order);   //TODO serve?
                    startActivityForResult(loginIntent, TO_LOGIN_ACTIVITY);//apri login
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        loadFood();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TO_LOGIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {

                client = (Client) data.getSerializableExtra(FirebaseConnection.CLIENT);
                place = (Place) data.getSerializableExtra(FirebaseConnection.PLACE);
                order = (Order) data.getSerializableExtra(FirebaseConnection.ORDER);
            }
        }
    }

    /**
     *  dialog che visualizza l'elenco dei cibi ordinati con costo e quantità
     */

    public void openDialogOrder(final HashMap<Food, Integer> listFoodOrder, final Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.order_summary));
        float tot = 0;
        StringBuilder message = new StringBuilder();
        for (Map.Entry<Food, Integer> entry : listFoodOrder.entrySet()) {//scorro l'hashMap per prendere il nome dei cibi e la quantità
            Food key = entry.getKey();
            nameFood.add("X " + entry.getValue() + " " + key.nameFood);//aggiungo alla lista dei cibi il nome con la relativa quantità
            int number = entry.getValue();//prendo la quantità
            float totParz = key.priceFood * number;//moltiplico il prezzo per la quantità per avere il costo di un cibo ordinato
            tot += totParz;//sommo il costo del cibo con il totale finale
            message.append(number).append("X ").append(key.nameFood).append(" ").append(totParz).append(" €").append("\n");//imposto il messaggio con il riepilogo della quantità del nome e del costo parziale del cibo
        }
        message.append("Tot ").append(tot).append(" €");//imposto nel messaggio il totale finale
        builder.setMessage(message.toString());//mostro il messaggio
        finalTot = tot;
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openDialogChooseAddress(place);//se clicca su ok va avanti al successivo dialogo

            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //se clicca su no non succede nulla e l'alert di chiude
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void applyTexts(Address address) {
        addressAdapter.add(address);
        addressAdapter.notifyDataSetChanged();
        mDBHelper.addAddress(mDB, address, user.getUid());//aggiungo l'indirizzo appena scritto dall'utente al db interno

//        if(city.toLowerCase().equals(place.cityPlace.toLowerCase())) {
//            Intent orderActivity = new Intent(PlaceListFoodOrderActivity.this, ConfirmOrderActivity.class);
//            final Address mAddress = new Address(city, address, numberAddress);
//            order = setOrder(mAddress);//imposto l'indirizzo appena scritto dall'utente come indirizzo di consegna
//            orderActivity.putExtra(FirebaseConnection.ORDER, order);
//            orderActivity.putExtra(FirebaseConnection.PLACE, place);
//            orderActivity.putExtra(FirebaseConnection.CLIENT, client);
//            startActivity(orderActivity);//apro l'activity per confermare l'ordine
//        }else{
//
//        }
    }

    /**
     * metodo per visualizzare la lista dei cibi ordinabili presenti
     */
    private void loadFood() {
        listFood.clear();
        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //leggo i cibi presenti all'interno del ristorante e li assegno alla listFood collegata con l'adapter per poter stamparli sulla listView corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.FOOD_NODE).child(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listFood.add(snapshot.getValue(Food.class));
                    }
                }
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *    dialog per selezionare l'indirizzo di spedizione
     */
    private void openDialogChooseAddress(final Place place) {

        final LayoutInflater inflater = PlaceListFoodOrderActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_choose_address, (ViewGroup) getCurrentFocus(),false);

        final ListView listViewAddress = view.findViewById(R.id.listViewChooseAddress);
        final FloatingActionButton btnAddAddress = view.findViewById(R.id.btnAddAddress);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setTitle(this.getResources().getString(R.string.choose_address));

        listViewAddress.setAdapter(addressAdapter);
        listAddress.clear();

        //leggo in SQLite gli indirizzi presenti e li assegno alla listView
        final Cursor c = mDB.query(DBOpenHelper.TABLE_ADDRESSES, DBOpenHelper.COLUMNS_ADDRESSES, DBOpenHelper.SELECTION_BY_USER_ID_ADDRESS, new String[]{user.getUid()}, null, null, null);

        while (c.moveToNext()) {//se sono presenti indirizzi

            final String street = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ADDRESS));
            final String numAddress = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NUM_ADDRESS));
            final String city = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.CITY));


            listAddress.add(new Address(city, street, numAddress));//aggiungo l'indirizzo totale alla lista a cui è collegato l'adapter
        }
        c.close();
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

                Address addressOrder = (Address) adapterView.getItemAtPosition(i);

                if (addressOrder.getCity().toLowerCase().equals(place.cityPlace.toLowerCase())) {
                    Intent orderActivity = new Intent(PlaceListFoodOrderActivity.this, ConfirmOrderActivity.class);
                    order = setOrder(addressOrder);
                    orderActivity.putExtra(FirebaseConnection.ORDER, order);
                    orderActivity.putExtra(FirebaseConnection.PLACE, place);
                    orderActivity.putExtra(FirebaseConnection.CLIENT, client);
                    startActivity(orderActivity);
                } else {

                    Toast.makeText(PlaceListFoodOrderActivity.this, R.string.impossibleOrder, Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    /**
     * metodo per settare i valore di order
     * @param addressOrder indirizzo di spedizione
     * @return
     */
    private Order setOrder(Address addressOrder) {
        Address mAddress = new Address(place.cityPlace, place.addressPlace, place.addressNumPlace);
        order.setIdClientOrder(user.getUid());
        order.setIdPlaceOrder(place.idPlace);
        order.setNamePlaceOrder(place.namePlace);
        order.setAddressPlaceOrder(mAddress.getFullAddress());
        order.setPhonePlaceOrder(place.phonePlace);
        order.setStateOrder(false);
        order.setDeliveryCost(place.deliveryCost);
        order.setFoodsOrder(nameFood);
        order.setTotalOrder(finalTot);
        order.setAddressOrder(addressOrder.getFullAddress());
        return order;
    }

    /**
     * adapter per gli indirizzi
     */
    private static class AddressAdapter extends ArrayAdapter<Address> {

        private AddressAdapter(@NonNull Context context, int resource, List<Address> list) {
            super(context, resource, list);
        }

        @Nullable
        @Override
        public Address getItem(int position) {
            return super.getItem(position);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {
                convertView = inflater.inflate(R.layout.listitem_address, parent, false);

                final TextView txtAddress = convertView.findViewById(R.id.listitem_address_tw);

                Address address = getItem(position);
                if (address != null) {
                    txtAddress.setText(address.getFullAddress());
                }
            }

            return convertView;
        }
    }


}
