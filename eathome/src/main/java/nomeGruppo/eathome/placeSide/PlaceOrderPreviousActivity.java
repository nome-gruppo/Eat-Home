package nomeGruppo.eathome.placeSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
 * activity per visualizzare gli ordini precedenti alla data odierna
 */

public class PlaceOrderPreviousActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private List<Order> listOrderPrevious;
    private PlaceOrderAdapter placeOrderAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_previous);

        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceOrderPrevious);
        final ListView listViewOrderPrevious = findViewById(R.id.listViewPlaceOrderPrevious);
        final TabLayout tabLayout = findViewById(R.id.tabLayoutPlaceOrderPrevious);

        this.menuNavigationItemSelected = new MenuNavigationItemSelected();
        this.listOrderPrevious = new LinkedList<>();
        this.placeOrderAdapter = new PlaceOrderAdapter(this, R.layout.listitem_order_info, listOrderPrevious);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);//recupero l'oggetto Place

        tabLayout.selectTab(tabLayout.getTabAt(1));//imposto il tabLayout in posizione 1

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Switch to view for this tab
                switch (position) {
                    case 0://se in posizione ordiniOdierni
                        Intent orderInfoToday = new Intent(PlaceOrderPreviousActivity.this, PlaceOrderInfoActivity.class);
                        orderInfoToday.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(orderInfoToday);//avvio PlaceOrderInfoActivity
                        break;
                    case 1://se in posizione ordini precedenti
                        Intent orderInfoPrevious = new Intent(PlaceOrderPreviousActivity.this, PlaceOrderPreviousActivity.class);
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

        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {//navigazione nel bottom menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceOrderPreviousActivity.this);
            }
        });

        listViewOrderPrevious.setOnItemClickListener(new AdapterView.OnItemClickListener() {//se clicca su un ordine mella lista
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order = (Order) adapterView.getItemAtPosition(i);//recupero l'oggetto Order nella posizione corrispondente
                showDialogListFood(order);
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
        listOrderPrevious.clear();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());//imposto il formato della data
        final Date curDate = new Date();
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_NODE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        Date dateOrder;
                        try {
                            if(order != null) {
                                dateOrder = simpleDateFormat.parse(order.dateOrder);//faccio il cast della stringa dateOrder in formato Date

                                //se la data odierna è antecedente alla data dell'ordine
                                if (curDate.compareTo(dateOrder) >= 1) {
                                    listOrderPrevious.add(order);//aggiungo l'ordine alla lista
                                } else {//se non è antecedente
                                    Toast.makeText(PlaceOrderPreviousActivity.this, getResources().getString(R.string.no_order), Toast.LENGTH_LONG).show();//messaggio di avviso
                                }
                                placeOrderAdapter.notifyDataSetChanged();

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(PlaceOrderPreviousActivity.this, getResources().getString(R.string.no_order), Toast.LENGTH_LONG).show();
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
