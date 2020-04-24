package nomeGruppo.eathome.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.R;
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
            FirebaseConnection connection = new FirebaseConnection();
            connection.deleteAccount(mUser, userId);
        }
    }
}
