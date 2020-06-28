package nomeGruppo.eathome.clientSide;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.BookingComparator;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.OrderComparator;

/*
activity per far visualizzare al cliente il riepilogo dei suoi ordini
 */

public class ClientOrderInfoActivity extends AppCompatActivity {
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private Client client;
    private OrderInfoAdapter orderInfoAdapter;
    private List<Order> listOrder;
    private FirebaseConnection firebaseConnection;
    private BottomNavigationView bottomMenuClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_info);


        final ListView listViewOrderInfo = findViewById(R.id.listViewOrderInfo);
        this.bottomMenuClient = findViewById(R.id.bottom_navigationClientOrder);

        this.firebaseConnection = new FirebaseConnection();

        this.client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
        this.menuNavigationItemSelected = new MenuNavigationItemSelected();

        this.listOrder = new LinkedList<>();
        this.orderInfoAdapter = new OrderInfoAdapter(this, R.layout.listitem_order_info, listOrder);
        listViewOrderInfo.setAdapter(orderInfoAdapter);

        //menu sottostante l'activity
        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item, client, ClientOrderInfoActivity.this);
            }
        });

        listViewOrderInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order = (Order) adapterView.getItemAtPosition(i);
                openDialogOrderSummary(order);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final MenuItem mItem = bottomMenuClient.getMenu().findItem(R.id.action_orders);
        mItem.setChecked(true);

        readOrder();
    }

    /**
     * metodo per aprire il dialog per visualizzare i dettagli dell'ordine
     */

    private void openDialogOrderSummary(Order order) {
        DialogListFoodOrder dialogListFoodOrder = new DialogListFoodOrder(order);
        dialogListFoodOrder.show(getSupportFragmentManager(), "Dialog list food");//mostro dialogListFoodOrder
    }

    /**
     * metodo per leggere gli ordini del cliente dal db firebase
     */

    private void readOrder() {
        listOrder.clear();//cancello la lista ordini

        //leggo in firebase gli ordini in base all'id del cliente
        firebaseConnection.getmDatabase().child(FirebaseConnection.ORDER_NODE).orderByChild("idClientOrder").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno un ordine
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        listOrder.add(order);//aggiungo l'ordine alla lista collegata all'adapter
                    }
                    Collections.sort(listOrder, new OrderComparator());//inverto i valori nella lista così da averli in ordine di ordinazione più recente effettuata
                    orderInfoAdapter.notifyDataSetChanged();
                } else {//se non c'è nemmeno un ordine
                    Toast.makeText(ClientOrderInfoActivity.this, R.string.no_order, Toast.LENGTH_SHORT).show();
                }
                Collections.sort(listOrder, new OrderComparator());//ordino gli elementi in base all'ordine effettuato più recenteente
                orderInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
