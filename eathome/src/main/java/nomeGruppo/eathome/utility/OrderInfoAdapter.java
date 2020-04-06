package nomeGruppo.eathome.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class OrderInfoAdapter extends ArrayAdapter<Order> {

    public OrderInfoAdapter(@NonNull Context context, int resource, @NonNull List<Order> listOrder) {
        super(context, resource, listOrder);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_order_info, null);
        TextView title = (TextView)convertView.findViewById(R.id.txtNameOrderInfo);
        TextView total = (TextView)convertView.findViewById(R.id.txtTotalOrderInfo);
        TextView address=(TextView)convertView.findViewById(R.id.txtAddressOrderInfo);
        TextView date=(TextView)convertView.findViewById(R.id.txtDateOrderInfo);
        TextView phone=(TextView)convertView.findViewById(R.id.txtPhoneNumber);
        final Order order = getItem(position);
        Place place=readPlace(order.idPlaceOrder);
        title.setText(place.namePlace);
        total.setText(Float.toString(order.totalOrder));
        address.setText(place.cityPlace+" "+place.addressPlace+" "+place.addressNumPlace);
        date.setText(order.dateOrder+" "+order.timeOrder);
        phone.setText(place.phonePlace);
        return convertView;
    }

    public Place readPlace(String idPlace){
        final Place[] place = new Place[1];
        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.queryEqualTo(FirebaseConnection.PLACE_TABLE,idPlace,idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        place[0] =snapshot.getValue(Place.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return place[0];
    }
}
