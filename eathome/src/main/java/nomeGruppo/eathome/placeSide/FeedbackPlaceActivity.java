package nomeGruppo.eathome.placeSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nomeGruppo.eathome.FeedbackAdapter;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.ShowAnswerPlace;
import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

/*
activity per far visualizzare a Place le proprie recensioni
 */
public class FeedbackPlaceActivity extends AppCompatActivity {

    private Place mPlace;

    private ListView listView;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback_place);

        final RatingBar ratingBar = findViewById(R.id.feedback_ratingBar);
        final TextView averageTW = findViewById(R.id.feedback_tw_average);
        final TextView numFeedbackTW = findViewById(R.id.feedback_tw_numReview);
        listView = findViewById(R.id.feedback_listview);

        mPlace = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        if(mPlace != null){

            ratingBar.setRating(mPlace.valuation);
            averageTW.setText(String.format("%.1f", mPlace.valuation));

            numFeedbackTW.setText(mPlace.numberReview + getResources().getQuantityString(R.plurals.numFeedback,mPlace.numberReview));

            if(mPlace.numberReview == 0){

                TextView noReviews = findViewById(R.id.my_feedback_place_noFeedback);

                noReviews.setVisibility(View.VISIBLE);
            }else{
                loadFeedback();
            }

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Feedback feedback=(Feedback)adapterView.getItemAtPosition(i);
                if(feedback.replyPlace.trim().length()==0){
                    Toast.makeText(FeedbackPlaceActivity.this,getResources().getString(R.string.no_reply),Toast.LENGTH_SHORT).show();
                }else {
                    ShowAnswerPlace showAnswerPlace = new ShowAnswerPlace(feedback.replyPlace);
                    showAnswerPlace.show(getSupportFragmentManager(), "Show answer");
                }
            }
        });

    }

    private void loadFeedback(){

        final FirebaseConnection connection = new FirebaseConnection();
        final DatabaseReference mDB = connection.getmDatabase();

        final ArrayList<Feedback> feedbackList = new ArrayList<>();
        final FeedbackAdapter mAdapter = new FeedbackAdapter(this, R.layout.listitem_feedback, feedbackList);

        //leggo in firebase i feedback corrispondenti all'id diPlace
        mDB.child(FirebaseConnection.FEEDBACK_NODE).orderByChild("idPlaceFeedback").equalTo(mPlace.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){//se esiste almeno un feedback
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        feedbackList.add(snapshot.getValue(Feedback.class));//aggiungo il feedback trovato alla lista collegata all'adapter

                    }

                    listView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}


