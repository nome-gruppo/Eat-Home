package nomeGruppo.eathome.profile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class DialogDeleteAccount extends DialogFragment {


    private String userId;
    private FirebaseUser mUser;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final FirebaseConnection connection = new FirebaseConnection();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_delete_account, null);

        final EditText emailEt = view.findViewById(R.id.dialog_delete_account_et_email);
        final EditText passwordEt = view.findViewById(R.id.dialog_delete_account_et_password);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();


        builder.setView(inflater.inflate(R.layout.layout_dialog_delete_account, null))
                .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String email = emailEt.getText().toString().trim();
                        final String password = passwordEt.getText().toString();

                        connection.reauthenticateUser(mUser, email, password);

                        if(connection.getOperationSuccess()){
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

        FirebaseConnection connection = new FirebaseConnection();
        connection.deleteAccount(mUser, userId);

    }
}
