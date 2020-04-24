package nomeGruppo.eathome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;


public class DialogEnterPlaceReview extends AppCompatDialogFragment {
    private RatingBar ratingBar;
    private String idPlace,namePlace;
    private TextView txtNamePlaceReview,txtValuesRatingBar;
    private SQLiteDatabase mDB;
    private DBOpenHelper mDBHelper;


    public DialogEnterPlaceReview(String idPlace,String namePlace,SQLiteDatabase mDB,DBOpenHelper mDBHelper){
        this.idPlace=idPlace;
        this.namePlace=namePlace;
        this.mDB=mDB;
        this.mDBHelper=mDBHelper;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_enter_place_review, null);

        this.ratingBar = view.findViewById(R.id.ratingBar);
        this.txtNamePlaceReview = view.findViewById(R.id.txtNamePlaceReview);
        this.txtValuesRatingBar=view.findViewById(R.id.txtValuesRatingBar);
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
                   //sendReview();
                    builder.setView(view).setCancelable(true);
                }
            }
        });

        return builder.create();
    }

    private void sendReview(){
        FirebaseConnection firebaseConnection=new FirebaseConnection();

    }
}
