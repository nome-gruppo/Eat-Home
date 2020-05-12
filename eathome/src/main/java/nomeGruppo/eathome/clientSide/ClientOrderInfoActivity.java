package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.OrderInfoAdapter;

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

        //menu sottostante l'activity
        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return menuNavigationItemSelected.menuNavigation(item,client,ClientOrderInfoActivity.this);
            }
        });

        listViewOrderInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order order=(Order)adapterView.getItemAtPosition(i);
                opendDialogOrderSummary(order);
            }
        });

    }

    private void opendDialogOrderSummary(Order order){
        DialogListFoodOrder dialogListFoodOrder=new DialogListFoodOrder(order);
        dialogListFoodOrder.show(getSupportFragmentManager(),"Dialog list food");
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
                    Collections.reverse(listOrder);//inverto i valori nella lista così da averli in ordine di ordinazione più recente effettuata
                    orderInfoAdapter.notifyDataSetChanged();
                }else{//se non c'è nemmeno un ordine
                    Toast.makeText(ClientOrderInfoActivity.this,getResources().getString(R.string.no_order),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
