package nomeGruppo.eathome.profile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

public class DialogDeleteAccount extends AppCompatDialogFragment {

    private String userId;
    private FirebaseUser mUser;
    private boolean deleted;
    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_delete_account, null);

        final FirebaseConnection connection = new FirebaseConnection();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();

        final EditText emailEt = dialogView.findViewById(R.id.dialog_delete_account_et_email);
        final EditText passwordEt = dialogView.findViewById(R.id.dialog_delete_account_et_password);

        builder.setView(dialogView).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final String email = emailEt.getText().toString().trim();
                final String password = passwordEt.getText().toString();

                connection.reauthenticateUser(mUser, email, password);

                deleted = true;

                if (connection.getOperationSuccess()) {
                    mAuth.signOut();
                    Intent homeIntent = new Intent(getActivity(), HomepageActivity.class);
                    homeIntent.putExtra(FirebaseConnection.LOGIN_FLAG, false);
                    startActivity(homeIntent);
                    getActivity().finish();
                }

            }
        });


        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(deleted) {
            mAuth.signOut();
            Client mClient = (Client)getActivity().getIntent().getSerializableExtra(FirebaseConnection.CLIENT);
            Place mPlace = (Place)getActivity().getIntent().getSerializableExtra(FirebaseConnection.PLACE);
            //elimina ordinazioni e prenotazioni
            if(mClient != null){

                DBOpenHelper helper = new DBOpenHelper(getContext());
                helper.deleteDatabase();

                SharedPreferences.Editor mEditor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                mEditor.clear();
                mEditor.apply();

                final FirebaseConnection.DeleteAccount deleteAccount = new FirebaseConnection.DeleteAccount(mUser, userId, FirebaseConnection.CLIENT_TABLE);
                final FirebaseConnection.DeleteClientInfo deleteBooking = new FirebaseConnection.DeleteClientInfo(userId,FirebaseConnection.BOOKING_TABLE);
                final FirebaseConnection.DeleteClientInfo deleteOrder = new FirebaseConnection.DeleteClientInfo(userId, FirebaseConnection.ORDER_TABLE);

                Thread accountThread = new Thread(deleteAccount);
                Thread bookingThread = new Thread(deleteBooking);
                Thread orderThread = new Thread(deleteOrder);

                accountThread.start();
                bookingThread.start();
                orderThread.start();

            }else if(mPlace != null){

                final FirebaseConnection.DeleteAccount deleteAccount = new FirebaseConnection.DeleteAccount(mUser, userId,FirebaseConnection.PLACE_TABLE);
                final FirebaseConnection.DeleteFeedbacks deleteFeedbacks = new FirebaseConnection.DeleteFeedbacks(userId);
                //TODO elimina le recensioni

                Thread accountThread = new Thread(deleteAccount);
                Thread feedbackThread = new Thread(deleteFeedbacks);

                accountThread.start();
                feedbackThread.start();
            }
        }
    }
}
