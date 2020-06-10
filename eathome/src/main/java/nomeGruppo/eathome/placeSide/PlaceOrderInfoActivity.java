package nomeGruppo.eathome.placeSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.PlaceOrderAdapter;

/**
 * activity per visualizzare le informazioni sulle ordinazioni
 */
public class PlaceOrderInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private List<Order> listOrder;
    private PlaceOrderAdapter placeOrderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_info);

        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceOrderInfo);
        final ListView listViewOrderInfo = findViewById(R.id.listViewPlaceOrderInfo);
        final ImageButton restore = findViewById(R.id.btnRestore);
        final TabLayout tabLayout = findViewById(R.id.tabLayoutPlaceOrderInfo);

        this.menuNavigationItemSelected = new MenuNavigationItemSelected();
        this.listOrder = new LinkedList<>();
        this.placeOrderAdapter = new PlaceOrderAdapter(this, R.layout.listitem_order_info, listOrder);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);//recupero l'oggetto Place

        //tabLoyout per switchare tra gli ordini odierni e precedenti
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Switch to view for this tab
                switch (position) {
                    case 0://se in posizione ordiniOdierni
                        Intent orderInfoToday = new Intent(PlaceOrderInfoActivity.this, PlaceOrderInfoActivity.class);
                        orderInfoToday.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(orderInfoToday);//avvio PlaceOrderInfoActivity
                        break;
                    case 1://se in posizione ordini precedenti
                        Intent orderInfoPrevious = new Intent(PlaceOrderInfoActivity.this, PlaceOrderPreviousActivity.class);
                        orderInfoPrevious.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(orderInfoPrevious);//avvio PlaceOrderPreviousActivity
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//navigazione nel bottom menu
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceOrderInfoActivity.this);
            }
        });

        listViewOrderInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//se clicca su un ordine nella lista
                Order order = (Order) adapterView.getItemAtPosition(i);//recupero l'ordine cliccato
                showDialogListFood(order);//mostro dialog chiamando showDialogListFood
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {//se clicco sul bottone ricarica
            @Override
            public void onClick(View view) {
                loadOrder();//chiamo loadOrder
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadOrder();

    }

    /**
     * metodo per visualizzare gli ordini
     */

    private void loadOrder() {
        listOrder.clear();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());//imposto il formato della data
        final Date curDate = new Date();
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        //leggo gli ordini corrispondenti a idPlace dal db
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_NODE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);//recupero l'Order letto
                        Date dateOrder;

                        try {
                            if (order != null) {
                                dateOrder = simpleDateFormat.parse(order.dateOrder);//faccio il cast della stringa dateOrder in formato Date
                                if (curDate.compareTo(dateOrder) < 1) {//se l'ordine è odierno
                                    listOrder.add(order);//aggiungo l'ordine alla lista
                                } else {//se non ci sono ordini odierni
                                    Toast.makeText(PlaceOrderInfoActivity.this, getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messaggio di avviso
                                }

                                placeOrderAdapter.notifyDataSetChanged();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {//se non ci sono ordini per il Place
                    Toast.makeText(PlaceOrderInfoActivity.this, getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messagio di avviso
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * metodo per aprire dialogListFoodOrder per visualizzare i dettagli dell'ordine
     * @param order
     */
    private void showDialogListFood(Order order) {
        DialogListFoodOrder dialogListFoodOrder = new DialogListFoodOrder(order);
        dialogListFoodOrder.show(getSupportFragmentManager(), "Dialog list food");
    }
}
