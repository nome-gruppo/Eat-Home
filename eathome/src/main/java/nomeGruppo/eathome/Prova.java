package nomeGruppo.eathome;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

import static android.content.ContentValues.TAG;

public class Prova extends AppCompatActivity {

    private Button btnProva;
    public Place place;
    private boolean exists;
    private List<Place>listPlace;
    private TextView txtProva;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prova);

        btnProva=(Button)findViewById(R.id.btnProva);
        txtProva=(TextView)findViewById(R.id.txtProva);

        this.place=new Place();
        this.exists=false;
        this.listPlace=new ArrayList<>();

        btnProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseConnection db=new FirebaseConnection();

                db.queryEqualTo("Places","emailPlace","in@gmail.com").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Place place1 = snapshot.getValue(Place.class);
                                place=place1;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
                
            }
        });

        txtProva.setText(place.namePlace);
    }
}
