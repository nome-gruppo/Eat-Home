package nomeGruppo.eathome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.utility.MenuAdapterForClient;

public class PlaceInfoActivity extends AppCompatActivity {

    private Place place;
    private ImageView imgPlaceInfo;
    private ListView listViewFoodInfo;
    private TextView txtDeliveryPlaceInfo;
    private TextView txtBookingPlaceInfo;
    private TextView txtNamePlaceInfo;
    private TextView txtAddressPlaceInfo;
    private TextView txtCityPlaceInfo;
    private TextView txtDeliveryCostInfo;
    private List<Food> listFood;
    private MenuAdapterForClient mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        this.imgPlaceInfo=(ImageView) findViewById(R.id.imgPlaceInfo);
        this.listViewFoodInfo=(ListView)findViewById(R.id.listViewFoodInfo);
        this.txtDeliveryPlaceInfo=(TextView)findViewById(R.id.txtDeliveryPlaceInfo);
        this.txtBookingPlaceInfo=(TextView)findViewById(R.id.txtBookingPlaceInfo);
        this.txtNamePlaceInfo=(TextView)findViewById(R.id.txtNamePlaceInfo);
        this.txtAddressPlaceInfo=(TextView)findViewById(R.id.txtAddressPlaceInfo);
        this.txtCityPlaceInfo=(TextView)findViewById(R.id.txtCityPlaceInfo);
        this.txtDeliveryCostInfo=(TextView)findViewById(R.id.txtDeliveryCostInfo);

        this.txtNamePlaceInfo.setText(this.place.namePlace);
        this.txtAddressPlaceInfo.setText(this.place.addressPlace+" "+this.place.addressNumPlace);
        this.txtCityPlaceInfo.setText(this.place.cityPlace);

        listFood=new LinkedList<>();
        listFood.add(new Food());
        mAdapter=new MenuAdapterForClient(this,R.layout.listitem_menu_client,listFood);
        listViewFoodInfo.setAdapter(mAdapter);

        if(this.place.takesBookingPlace){
            this.txtBookingPlaceInfo.setVisibility(View.VISIBLE);
        }
        if(this.place.takesOrderPlace){
            this.txtDeliveryPlaceInfo.setVisibility(View.VISIBLE);
            this.txtDeliveryCostInfo.setText(this.place.deliveryCost);
            this.txtDeliveryCostInfo.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(3840*3840)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlaceInfo.setImageBitmap(bitmap);
                    }
                });

        final FirebaseConnection firebaseConnection=new FirebaseConnection();

        //leggo i cibi presenti all'interno del ristorante e li assegno alla listFood collegata con l'adapter per poter stamparli sulla listView corrispondente
        firebaseConnection.getmDatabase().child("Foods").child(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listFood.add(snapshot.getValue(Food.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
