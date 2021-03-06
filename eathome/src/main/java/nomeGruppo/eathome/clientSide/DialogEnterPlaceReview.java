package nomeGruppo.eathome.clientSide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

/**
 * dialog per l'inserimento delle recensioni da parte del cliente
 */

public class DialogEnterPlaceReview extends AppCompatDialogFragment {
    private RatingBar ratingBar;
    private final String idPlace;
    private final String namePlace;
    private final String idClient;
    private final String nameClient;
    private final SQLiteDatabase mDB;
    private final DBOpenHelper mDBHelper;
    private EditText editFeedback;
    private final Calendar date;
    private final FirebaseConnection firebaseConnection;


    DialogEnterPlaceReview(String idPlace, String namePlace, String idClient, String nameClient, SQLiteDatabase mDB, DBOpenHelper mDBHelper) {
        this.idPlace = idPlace;
        this.namePlace = namePlace;
        this.idClient = idClient;
        this.nameClient = nameClient;
        this.mDB = mDB;
        this.mDBHelper = mDBHelper;
        this.date = Calendar.getInstance();
        this.firebaseConnection = new FirebaseConnection();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.dialog_enter_place_review, (ViewGroup) getActivity().getCurrentFocus(), false);

            final TextView txtNamePlaceReview = view.findViewById(R.id.txtNamePlaceReview);
            this.ratingBar = view.findViewById(R.id.ratingBar);
            this.editFeedback = view.findViewById(R.id.editTextFeedback);

            txtNamePlaceReview.setText(namePlace);


            builder.setView(view).setTitle(getActivity().getResources().getString(R.string.enterReview)).setNegativeButton(getActivity().getResources().getString(R.string.notNow), new DialogInterface.OnClickListener() {//se il cliente clicca 'non ora'
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mDBHelper.deleteInfo(mDB, idPlace);//cancello la riga corrispondente all'interno del db
                }
            }).setPositiveButton(getActivity().getResources().getString(R.string.send), new DialogInterface.OnClickListener() { //se il cliente clicca su 'invia'
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (ratingBar.getRating() > 0.0) { //se è stata data una valutazione 
                        sendReview();//inserisco la recensione in Firebase
                        updateValuationPlace();//aggiorno la valutazione media all'interno di Place corrispondente
                    }
                    mDBHelper.deleteInfo(mDB, idPlace);
                }
            });
        }
        return builder.create();
    }

    /**
     * metodo per inserire la recensione in db firebase
     */
    private void sendReview() {
        Feedback feedback = new Feedback();
        if (editFeedback.getText().toString().trim().length() == 0) {
            editFeedback.setText(" ");
        }
        feedback.setTextFeedback(editFeedback.getText().toString());//assegno il testo della recensione
        feedback.setVoteFeedback(ratingBar.getRating());//assegno la valutazione numerica del cliente
        feedback.setIdPlaceFeedback(idPlace);
        feedback.setIdClientFeedback(idClient);
        feedback.setClientNameFeedback(nameClient);
        DateFormat formatData = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);//creo il formato data 'gg/mm'yyyy'
        String dateFeedback = formatData.format(date.getTime());//setto la data nel formato corretto
        feedback.setDateFeedback(dateFeedback);
        //prelevo la chiave assegnata in automatico da Firebase
        String idFeedback = firebaseConnection.getmDatabase().child(FirebaseConnection.FEEDBACK_NODE).push().getKey();
        feedback.setIdFeedback(idFeedback);
        firebaseConnection.write(FirebaseConnection.FEEDBACK_NODE, idFeedback, feedback);//inserisco Feedback all'interno di Firebase
    }

    /**
     * metodo per aggiornare la media della valutazione del locale
     */
    private void updateValuationPlace() {

        //leggo il Place corrispondente all'id all'interno di firebase
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_NODE).orderByKey().equalTo(idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {//se è stato trovato Place
                    //ritorna un iterable con un solo elemento
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Place place = snapshot.getValue(Place.class);
                        if (place != null) {
                            place.newValuation(ratingBar.getRating());//assegno la valutazione data dal cliente al Place corrispondente

                            firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_NODE).child(idPlace).setValue(place);//aggiorno il valore in firebase
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
