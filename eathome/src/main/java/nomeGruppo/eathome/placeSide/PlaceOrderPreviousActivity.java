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

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        tabLayout.selectTab(tabLayout.getTabAt(1));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Switch to view for this tab
                switch (position) {
                    case 0:
                        Intent orderInfoToday = new Intent(PlaceOrderPreviousActivity.this, PlaceOrderInfoActivity.class);
                        orderInfoToday.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(orderInfoToday);
                        break;
                    case 1:
                        Intent orderInfoPrevious = new Intent(PlaceOrderPreviousActivity.this, PlaceOrderPreviousActivity.class);
                        orderInfoPrevious.putExtra(FirebaseConnection.PLACE, place);
                        startActivity(orderInfoPrevious);
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
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceOrderPreviousActivity.this);
            }
        });

        listViewOrderPrevious.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order = (Order) adapterView.getItemAtPosition(i);
                showDialogListFood(order);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadOrder();

    }

    private void loadOrder() {
        listOrderPrevious.clear();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());//imposto il formato della data
        final Date curDate = new Date();
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        Date dateOrder = null;
                        try {
                            if(order != null) {
                                dateOrder = simpleDateFormat.parse(order.dateOrder);//faccio il cast della stringa dateOrder in formato Date

                                if (curDate.compareTo(dateOrder) >= 1) {
                                    listOrderPrevious.add(order);
                                } else {
                                    Toast.makeText(PlaceOrderPreviousActivity.this, getResources().getString(R.string.no_order), Toast.LENGTH_LONG).show();
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

    private void showDialogListFood(Order order) {
        DialogListFoodOrder dialogListFoodOrder = new DialogListFoodOrder(order);
        dialogListFoodOrder.show(getSupportFragmentManager(), "Dialog list food");
    }

}
