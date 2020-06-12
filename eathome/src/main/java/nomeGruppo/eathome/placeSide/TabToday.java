package nomeGruppo.eathome.placeSide;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.PlaceOrderAdapter;

public class TabToday extends Fragment {
    private Place place;
    private ListView listView;


    public TabToday(Place place){
        this.place=place;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_today, (ViewGroup) requireActivity().getCurrentFocus(),false);
        this.listView=view.findViewById(R.id.listViewPlaceBookingInfoToday);

        return inflater.inflate(R.layout.tab_today,container,false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        PlaceBookingInfoActivity placeBookingInfoActivity=new PlaceBookingInfoActivity();
        if(context==placeBookingInfoActivity.getApplicationContext()){
            showListBooking();
        }else{
            showListOrder();
        }

    }

    private void showListBooking(){

        final List<Booking> listBooking=new LinkedList<>();
        final PlaceBookingAdapter placeBookingAdapter=new PlaceBookingAdapter(getContext(),R.layout.listitem_booking_info,listBooking);
        this.listView.setAdapter(placeBookingAdapter);

        listBooking.clear();

        FirebaseConnection firebaseConnection = new FirebaseConnection();

        //leggo in firebase le prenotazioni con id place corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.BOOKING_NODE).orderByChild("idPlaceBooking").equalTo(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se esiste almeno una prenotazione
                    Date dateBooking=null;
                    Date curDate=new Date();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Booking booking = snapshot.getValue(Booking.class);
                        if (booking != null) {
                            dateBooking.setTime(booking.dateBooking);
                            if (curDate.compareTo(dateBooking) < 1) {//se la prenotazione è alla data odierna
                                listBooking.add(booking);//aggiungo la prenotazione alla lista a cui è stato impostato l'adapter
                            } else {//se non ci sono prenotazioni odierne
                                Toast.makeText(getContext(), getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messaggio di avviso
                            }
                            placeBookingAdapter.notifyDataSetChanged();
                        }
                    }
                } else {//se non c'è alcuna prenotazione
                    Toast.makeText(getContext(),getResources().getString(R.string.no_booking),Toast.LENGTH_SHORT).show();
                }
                placeBookingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showListOrder(){

        final List<Order>listOrder = new LinkedList<>();
        final PlaceOrderAdapter placeOrderAdapter = new PlaceOrderAdapter(getContext(), R.layout.listitem_order_info, listOrder);
        this.listView.setAdapter(placeOrderAdapter);

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
                                    Toast.makeText(getContext(), getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messaggio di avviso
                                }

                                placeOrderAdapter.notifyDataSetChanged();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {//se non ci sono ordini per il Place
                    Toast.makeText(getContext(), getResources().getString(R.string.no_order), Toast.LENGTH_SHORT).show();//messagio di avviso
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
        dialogListFoodOrder.show(getChildFragmentManager(), "Dialog list food");
    }

}
