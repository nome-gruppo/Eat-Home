package nomegruppo.eathome;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomegruppo.eathome.actions.Order;
import nomegruppo.eathome.actors.Place;
import nomegruppo.eathome.db.FirebaseConnection;
import nomegruppo.eathome.utility.DialogListFoodOrder;
import nomegruppo.eathome.utility.MenuNavigationItemSelected;
import nomegruppo.eathome.utility.PlaceOrderAdapter;

public class PlaceOrderInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private BottomNavigationView bottomMenuPlace;
    private ListView listViewOrderInfo;
    private List<Order> listOrder;
    private PlaceOrderAdapter placeOrderAdapter;
    private TextView txtNoOrder;
    private ImageView imgNoOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_info);

        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.menuNavigationItemSelected=new MenuNavigationItemSelected();
        this.bottomMenuPlace=findViewById(R.id.bottom_navigationPlaceOrderInfo);
        this.listViewOrderInfo=findViewById(R.id.listViewPlaceOrderInfo);
        this.listOrder=new LinkedList<>();
        this.placeOrderAdapter=new PlaceOrderAdapter(this,R.layout.listitem_order_info,listOrder);
        this.listViewOrderInfo.setAdapter(placeOrderAdapter);
        this.txtNoOrder=findViewById(R.id.txtNoOrderPlace);
        this.imgNoOrder=findViewById(R.id.imgNoOrderPlace);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        listOrder.clear();
        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).orderByChild("idPlaceOrder").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        listOrder.add(order);
                    }
                }else{
                    txtNoOrder.setVisibility(View.VISIBLE);
                    imgNoOrder.setVisibility(View.VISIBLE);
                }
                placeOrderAdapter.notifyDataSetChanged();
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
