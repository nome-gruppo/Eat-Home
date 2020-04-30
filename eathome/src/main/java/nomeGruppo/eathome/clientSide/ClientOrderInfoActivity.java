package nomegruppo.eathome.clientSide;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomegruppo.eathome.R;
import nomegruppo.eathome.actions.Order;
import nomegruppo.eathome.actors.Client;
import nomegruppo.eathome.db.FirebaseConnection;
import nomegruppo.eathome.utility.MenuNavigationItemSelected;
import nomegruppo.eathome.utility.OrderInfoAdapter;

/*
activity per far visualizzare al cliente il riepilogo dei suoi ordini
 */

public class ClientOrderInfoActivity extends AppCompatActivity {
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private Client client;
    private BottomNavigationView bottomMenuClient;
    private ListView listViewOrderInfo;
    private OrderInfoAdapter orderInfoAdapter;
    private List<Order>listOrder;
    private FirebaseConnection firebaseConnection;
    private TextView txtNoOrder;
    private ImageView imgNoOrder;

    @Override
    protected void onStart() {
        super.onStart();

        readOrder();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_info);

        this.firebaseConnection=new FirebaseConnection();
        this.bottomMenuClient=findViewById(R.id.bottom_navigationClientOrder);
        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
        this.menuNavigationItemSelected=new MenuNavigationItemSelected();
        this.listViewOrderInfo=findViewById(R.id.listViewOrderInfo);
        this.listOrder=new LinkedList<>();
        this.orderInfoAdapter=new OrderInfoAdapter(this,R.layout.listitem_order_info,listOrder);
        this.listViewOrderInfo.setAdapter(orderInfoAdapter);
        this.txtNoOrder=findViewById(R.id.txtNoOrderClient);
        this.imgNoOrder=findViewById(R.id.imgNoOrderClient);

        //menu sottostante l'activity
        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item,client,ClientOrderInfoActivity.this);
            }
        });

    }

    private void readOrder(){
        listOrder.clear();

        //leggo in firebase gli ordini in base all'id del cliente
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_TABLE).orderByChild("idClientOrder").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {//se esiste almeno un ordine
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        listOrder.add(order);//aggiungo l'ordine alla lista collegata all'adapter
                    }
                }else{//se non c'è nemmeno un ordine
                    txtNoOrder.setVisibility(View.VISIBLE);//mostro messaggio 'siamo spiacenti'
                    imgNoOrder.setVisibility(View.VISIBLE);//mostro la smile triste
                }
                orderInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
