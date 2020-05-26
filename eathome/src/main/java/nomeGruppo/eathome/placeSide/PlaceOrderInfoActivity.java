package nomeGruppo.eathome.placeSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.PlaceOrderAdapter;

public class PlaceOrderInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private BottomNavigationView bottomMenuPlace;
    private ListView listViewOrderInfo;
    private List<Order> listOrder;
    private PlaceOrderAdapter placeOrderAdapter;
    private TabLayout tabLayout;
    private ImageButton restore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_info);

        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        this.menuNavigationItemSelected=new MenuNavigationItemSelected();
        this.bottomMenuPlace=findViewById(R.id.bottom_navigationPlaceOrderInfo);
        this.listViewOrderInfo=findViewById(R.id.listViewPlaceOrderInfo);
        this.restore=findViewById(R.id.btnRestore);
        this.listOrder=new LinkedList<>();
        this.placeOrderAdapter=new PlaceOrderAdapter(this,R.layout.listitem_order_info,listOrder);



        this.tabLayout=findViewById(R.id.tabLayoutPlaceOrderInfo);

        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Switch to view for this tab
                switch (position){
                    case 0:
                        Intent orderInfoToday=new Intent(PlaceOrderInfoActivity.this,PlaceOrderInfoActivity.class);
                        orderInfoToday.putExtra(FirebaseConnection.PLACE,place);
                        startActivity(orderInfoToday);
                        break;
                    case 1:
                        Intent orderInfoPrevious=new Intent(PlaceOrderInfoActivity.this,PlaceOrderPreviousActivity.class);
                        orderInfoPrevious.putExtra(FirebaseConnection.PLACE,place);
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


        this.bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item,place,PlaceOrderInfoActivity.this);
            }
        });

        this.listViewOrderInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order=(Order)adapterView.getItemAtPosition(i);
                showDialogListFood(order);
            }
        });

        this.restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrder();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadOrder();

    }

    private void loadOrder(){
        listOrder.clear();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//imposto il formato della data
        final Date curDate=new Date();
        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        Date dateOrder = null;
                        try {
                            dateOrder = simpleDateFormat.parse(order.dateOrder);//faccio il cast della stringa dateOrder in formato Date
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (curDate.compareTo(dateOrder) < 1) {
                            listOrder.add(order);
                        }else{
                            Toast.makeText(PlaceOrderInfoActivity.this,getResources().getString(R.string.no_order),Toast.LENGTH_SHORT).show();
                        }
                        placeOrderAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(PlaceOrderInfoActivity.this,getResources().getString(R.string.no_order),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDialogListFood(Order order){
        DialogListFoodOrder dialogListFoodOrder=new DialogListFoodOrder(order);
        dialogListFoodOrder.show(getSupportFragmentManager(),"Dialog list food");
    }
}
