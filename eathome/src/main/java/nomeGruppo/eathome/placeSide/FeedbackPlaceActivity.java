package nomeGruppo.eathome.placeSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nomeGruppo.eathome.FeedbackAdapter;
import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

/*
activity per far visualizzare a Place le proprie recensioni
 */
public class FeedbackPlaceActivity extends AppCompatActivity {

    private Place mPlace;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback_place);

        final RatingBar ratingBar = findViewById(R.id.feedback_ratingBar);
        final TextView averageTW = findViewById(R.id.feedback_tw_average);
        final TextView numFeedbackTW = findViewById(R.id.feedback_tw_numReview);
        listView = findViewById(R.id.feedback_listview);

        mPlace = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        Toolbar toolBarPlaceFeedback=findViewById(R.id.tlbPlaceFeedback);
        setSupportActionBar(toolBarPlaceFeedback);
        toolBarPlaceFeedback.setTitle(getResources().getString(R.string.my_feedback));
        toolBarPlaceFeedback.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        toolBarPlaceFeedback.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivityIntent=new Intent(FeedbackPlaceActivity.this, OtherActivity.class);
                otherActivityIntent.putExtra(FirebaseConnection.PLACE,mPlace);
                startActivity(otherActivityIntent);
                finish();
            }
        });


        if(mPlace != null){

            ratingBar.setRating(mPlace.valuation);
            averageTW.setText(String.format("%.1f", mPlace.valuation));
            numFeedbackTW.setText(mPlace.numberReview + " recensioni");

            if(mPlace.numberReview == 0){

                TextView noReviews = findViewById(R.id.my_feedback_place_noFeedback);

                noReviews.setVisibility(View.VISIBLE);
            }else{
                loadFeedback();
            }

        }

    }

    private void loadFeedback(){

        final FirebaseConnection connection = new FirebaseConnection();
        final DatabaseReference mDB = connection.getmDatabase();

        final ArrayList<Feedback> feedbackList = new ArrayList<>();
        final FeedbackAdapter mAdapter = new FeedbackAdapter(this, R.layout.listitem_feedback, feedbackList);

        //leggo in firebase i feedback corrispondenti all'id diPlace
        mDB.child(FirebaseConnection.FEEDBACK_TABLE).orderByChild("idPlaceFeedback").equalTo(mPlace.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
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


