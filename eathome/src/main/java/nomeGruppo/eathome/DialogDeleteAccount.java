package nomeGruppo.eathome;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

public class DialogDeleteAccount extends AppCompatDialogFragment {

    private String userId;
    private FirebaseUser mUser;
    private boolean deleted;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_delete_account, (ViewGroup) getActivity().getCurrentFocus(),false);

        final EditText emailEt = dialogView.findViewById(R.id.dialog_delete_account_et_email);
        final EditText passwordEt = dialogView.findViewById(R.id.dialog_delete_account_et_password);

        final FirebaseConnection connection = new FirebaseConnection();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();

        builder.setView(dialogView).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //onClick return back
            }
        }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String email = emailEt.getText().toString().trim();
                final String password = passwordEt.getText().toString();

                connection.reauthenticateUser(mUser, email, password);

                if (connection.getOperationSuccess()) {
                    deleted = true;

                }

            }
        });


        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(deleted) {

            Client mClient = (Client)getActivity().getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
            Place mPlace = (Place)getActivity().getIntent().getSerializableExtra(FirebaseConnection.PLACE);
            //elimina ordinazioni e prenotazioni
            if(mClient != null){

                try(DBOpenHelper helper = new DBOpenHelper(getContext())) {

                    helper.deleteDatabase();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

                SharedPreferences.Editor mEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                mEditor.clear();
                mEditor.apply();


                final FirebaseConnection.DeleteAccount deleteAccount = new FirebaseConnection.DeleteAccount(mUser, userId, FirebaseConnection.CLIENT_NODE, getActivity());

                Thread accountThread = new Thread(deleteAccount);
                accountThread.start();


            }else if(mPlace != null){

                final FirebaseConnection.DeleteAccount deleteAccount = new FirebaseConnection.DeleteAccount(mUser, userId,FirebaseConnection.PLACE_NODE, getActivity());
                final FirebaseConnection.DeleteFeedback deleteFeedback = new FirebaseConnection.DeleteFeedback(userId);

                Thread accountThread = new Thread(deleteAccount);
                Thread feedbackThread = new Thread(deleteFeedback);

                accountThread.start();
                feedbackThread.start();
            }
        }
    }
}
