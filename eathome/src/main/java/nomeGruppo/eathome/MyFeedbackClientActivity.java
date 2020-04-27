package nomeGruppo.eathome;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;

/*
activity per far visualizzare al cliente le recensioni scritte
 */

public class MyFeedbackClientActivity extends AppCompatActivity {

    private ListView listViewFeedbackClient;
    private Client client;
    private ImageView imgNoFeedback;
    private TextView txtNoFeedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_feedback_client);

        this.client=(Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        this.listViewFeedbackClient=findViewById(R.id.listViewFeedbackClient);
        this.imgNoFeedback=findViewById(R.id.imgNoFeedback);
        this.txtNoFeedback=findViewById(R.id.txtNoFeedback);

        loadFeedback();

    }

    private void loadFeedback(){

        final FirebaseConnection connection = new FirebaseConnection();
        final DatabaseReference mDB = connection.getmDatabase();

        final ArrayList<Feedback> feedbackList = new ArrayList<>();
        final FeedbackAdapter mAdapter = new FeedbackAdapter(this, R.layout.listitem_feedback, feedbackList);

        //leggo in firebase i feedback con idCliente corrispondente
        mDB.child(FirebaseConnection.FEEDBACK_TABLE).orderByChild("idClientFeedback").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){//se esiste almeno un feedback
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        feedbackList.add(snapshot.getValue(Feedback.class));//aggiungo il feedback trovato alla lista a cui ho impostato l'adapter

                    }

                    listViewFeedbackClient.setAdapter(mAdapter);
                }
                if(feedbackList.isEmpty()){//se non è stato trovato alcun feedback
                    imgNoFeedback.setVisibility(View.VISIBLE);//mostro la smile triste
                    txtNoFeedback.setVisibility(View.VISIBLE);//mostro il messaggio 'siamo spiacenti'
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
