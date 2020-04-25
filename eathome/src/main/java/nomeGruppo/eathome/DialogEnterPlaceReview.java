package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;


public class DialogEnterPlaceReview extends AppCompatDialogFragment {
    private RatingBar ratingBar;
    private String idPlace,namePlace,idClient;
    private TextView txtNamePlaceReview,txtValuesRatingBar;
    private SQLiteDatabase mDB;
    private DBOpenHelper mDBHelper;
    private EditText editFeedback;
    private Calendar date;


    public DialogEnterPlaceReview(String idPlace, String namePlace, String idClient, Calendar date, SQLiteDatabase mDB, DBOpenHelper mDBHelper){
        this.idPlace=idPlace;
        this.namePlace=namePlace;
        this.idClient=idClient;
        this.mDB=mDB;
        this.mDBHelper=mDBHelper;
        this.date=date;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_enter_place_review, null);

        this.ratingBar = view.findViewById(R.id.ratingBar);
        this.txtNamePlaceReview = view.findViewById(R.id.txtNamePlaceReview);
        this.txtValuesRatingBar=view.findViewById(R.id.txtValuesRatingBar);
        this.editFeedback=view.findViewById(R.id.editTextFeedback);
        this.txtNamePlaceReview.setText(namePlace);


        this.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                txtValuesRatingBar.setText(String.valueOf(rating));//assegno alla TextView il valore della RatingBar
            }
        });

        builder.setView(view).setTitle(getActivity().getResources().getString(R.string.enterReview)).setNegativeButton(getActivity().getResources().getString(R.string.notNow), new DialogInterface.OnClickListener() {//se il cliente clicca 'non ora'
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDBHelper.deleteInfo(mDB,idPlace);//cancello la riga corrispondente all'interno del db
                builder.setView(view).setCancelable(true);
            }
        }).setPositiveButton(getActivity().getResources().getString(R.string.send), new DialogInterface.OnClickListener() { //se il cliente clicca su 'invia'
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(txtValuesRatingBar.getText().toString().trim().length()==0){ //se non è stata data alcuna valutazione
                    Toast.makeText(getContext(),getActivity().getResources().getString(R.string.assign_rating),Toast.LENGTH_SHORT).show();//stampo il Toast di avviso
                    builder.setView(view).setCancelable(false);
                }else {
                    sendReview();
                    builder.setView(view).setCancelable(true);
                }
            }
        });

        return builder.create();
    }

    private void sendReview(){
        FirebaseConnection firebaseConnection=new FirebaseConnection();
        Feedback feedback=new Feedback();
        feedback.setTextFeedback(editFeedback.getText().toString());
        feedback.setVoteFeedback(Integer.parseInt(txtValuesRatingBar.getText().toString()));
        feedback.setIdPlaceFeedback(idPlace);
        feedback.setIdClientFeedback(idClient);
        DateFormat formatoData = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
        String dateFeedback = formatoData.format(date.getTime());
        feedback.setDateFeedback(dateFeedback);
        String idFeedback = firebaseConnection.getmDatabase().child(FirebaseConnection.FEEDBACK_TABLE).push().getKey();
        feedback.setIdFeedback(idFeedback);

        firebaseConnection.writeObject(FirebaseConnection.FEEDBACK_TABLE,feedback);

        final Place[] place = {new Place()};

        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).child(idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    place[0] = dataSnapshot.getValue(Place.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
