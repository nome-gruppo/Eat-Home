package nomeGruppo.eathome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

public class MyFeedbackPlaceActivity extends AppCompatActivity {

    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback_place);

        final RatingBar ratingBar = findViewById(R.id.feedback_ratingBar);
        final TextView averageTW = findViewById(R.id.feedback_tw_average);
        final TextView numFeedbackTW = findViewById(R.id.feedback_tw_numReview);
        final ListView listView = findViewById(R.id.feedback_listview);

        mPlace = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);

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

        mDB.child(FirebaseConnection.FEEDBACK_TABLE).orderByChild("idPlaceFeedback").equalTo(mPlace.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        Feedback mFeedback = snapshot.getValue(Feedback.class);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}


