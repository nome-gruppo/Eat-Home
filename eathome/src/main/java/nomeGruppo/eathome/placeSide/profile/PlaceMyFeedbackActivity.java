package nomeGruppo.eathome.placeSide.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Locale;
import nomeGruppo.eathome.FeedbackAdapter;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.ShowAnswerPlace;
import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;

/*
activity per far visualizzare a Place le proprie recensioni
 */

public class PlaceMyFeedbackActivity extends AppCompatActivity {

    private Place mPlace;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_my_feedback);

        final RatingBar ratingBar = findViewById(R.id.feedback_ratingBar);
        final TextView averageTW = findViewById(R.id.feedback_tw_average);
        final TextView numFeedbackTW = findViewById(R.id.feedback_tw_numReview);
        listView = findViewById(R.id.feedback_listview);

        mPlace = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);//recupero oggetto Place

        if(mPlace != null){//se esiste oggetto Place

            ratingBar.setRating(mPlace.valuation);
            averageTW.setText(String.format(Locale.getDefault(),"%.1f", mPlace.valuation));

            numFeedbackTW.setText(getResources().getQuantityString(R.plurals.numFeedback,mPlace.numberReview, mPlace.numberReview));

            if(mPlace.numberReview == 0){//se numero di recensioni=0

                TextView noReviews = findViewById(R.id.my_feedback_place_noFeedback);

                noReviews.setVisibility(View.VISIBLE);//rendo visibile il messaggio nessuna recensione
            }else{//se ci sono recensioni
                loadFeedback();//chiamo la funzione
            }

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//se clicca su una recensione
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Feedback feedback=(Feedback)adapterView.getItemAtPosition(i);
                if(feedback.replyPlace.trim().length()==0){//se non esite una risposta
                    Toast.makeText(PlaceMyFeedbackActivity.this,getResources().getString(R.string.no_reply),Toast.LENGTH_SHORT).show();//mostra messaggio di avviso
                }else {//se il ristorante ha risposto
                    ShowAnswerPlace showAnswerPlace = new ShowAnswerPlace(feedback.replyPlace);
                    showAnswerPlace.show(getSupportFragmentManager(), "Show answer");//mostra risposta chiamando showAnswerPlace
                }
            }
        });

    }

    /**
     * metodo per visualizzare le recensioni presenti
     */

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


