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

        mDB.child(FirebaseConnection.FEEDBACK_TABLE).orderByChild("idClientFeedback").equalTo(client.idClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        feedbackList.add(snapshot.getValue(Feedback.class));

                    }

                    listViewFeedbackClient.setAdapter(mAdapter);
                }
                if(feedbackList.isEmpty()){
                    imgNoFeedback.setVisibility(View.VISIBLE);
                    txtNoFeedback.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
