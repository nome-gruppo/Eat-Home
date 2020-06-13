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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);//recupero l'oggetto Place

        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceOrderInfo);
        this.listView=findViewById(R.id.listViewPlaceOrderInfo);

        this.menuNavigationItemSelected = new MenuNavigationItemSelected();

        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//navigazione nel bottom menu
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceOrderInfoActivity.this);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {//se clicca su un ordine nella lista
                Order order = (Order) adapterView.getItemAtPosition(i);//recupero l'ordine cliccato
                showDialogListFood(order);//mostro dialog chiamando showDialogListFood
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        showListOrder();
    }

    private void showListOrder(){

        final List<Order>listOrder = new LinkedList<>();
        final PlaceOrderAdapter placeOrderAdapter = new PlaceOrderAdapter(this, R.layout.listitem_order_info, listOrder);
        this.listView.setAdapter(placeOrderAdapter);

        listOrder.clear();

        FirebaseConnection firebaseConnection = new FirebaseConnection();
        //leggo gli ordini corrispondenti a idPlace dal db
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_NODE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);//recupero l'Order letto
                        listOrder.add(order);
                    }
                    Collections.reverse(listOrder);//inverto i valori nella lista così da averli in ordine di ordinazione più recente effettuata
                    placeOrderAdapter.notifyDataSetChanged();
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
