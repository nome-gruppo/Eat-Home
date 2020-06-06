package nomeGruppo.eathome.clientSide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

/*
activity per la registrazione del cliente
 */

public class ClientRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClientRegistration";
    private static final int DURATION = Toast.LENGTH_SHORT;

    private EditText nameClientET;
    private EditText emailClientET;
    private EditText passwordClientET;
    private TextView statusTV;
    private Client client;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private boolean accountCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        mAuth = FirebaseAuth.getInstance();

        client = new Client();
        final UtilitiesAndControls control = new UtilitiesAndControls();

        nameClientET =  findViewById(R.id.editNameClient);
        nameClientET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailClientET =  findViewById(R.id.editMailClient);
        emailClientET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordClientET =  findViewById(R.id.editPasswordClient);
        passwordClientET.setImeOptions(EditorInfo.IME_ACTION_DONE);
        final Button signInBtn = findViewById(R.id.btnSignInClient);
        statusTV =  findViewById(R.id.activity_client_registration_tw_status);

        //se l'utente clicca sul bottone registrati
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //se non sono stati compilati tutti i campi
                if (nameClientET.getText().toString().trim().length() == 0 || emailClientET.getText().toString().trim().length() == 0 || passwordClientET.getText().toString().trim().length() == 0) {

                    //mostra 'compila tutti i campi'
                    Toast.makeText(ClientRegistrationActivity.this, ClientRegistrationActivity.this.getResources().getString(R.string.fill_all_fields), DURATION).show();

                } else {//se tutti i campi sono stati compilati

                    String emailTemp = emailClientET.getText().toString().trim();//leggi mail
                    String passwordTemp = passwordClientET.getText().toString();//leggi password

                    if (control.isEmailValid(emailTemp) && control.isPasswordValid(passwordTemp)) {//apri la fuznione di controllo se mail o password sono valide
                        createAccount(emailTemp, passwordTemp);

                    }
                }
            }
        });

    }//fine onCreate

    @Override
    protected void onPause() {
        super.onPause();

        if(accountCreated) {
            FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

            //assegno come chiave del db l'user id generato da Firebase Authentication
            db.write(FirebaseConnection.CLIENT_NODE, user.getUid(), client);
        }
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            accountCreated = true;

                            user = mAuth.getCurrentUser();

                            client.nameClient = nameClientET.getText().toString().trim();
                            client.emailClient = emailClientET.getText().toString().trim();
                            client.idClient = user.getUid(); //assegno come id l'user id generato da Firebase Authentication

                            Intent clientHomeIntent = new Intent(ClientRegistrationActivity.this, HomepageActivity.class);
                            clientHomeIntent.putExtra(FirebaseConnection.CLIENT, client);
                            Toast.makeText(ClientRegistrationActivity.this, R.string.successfulRegistration, DURATION).show();
                            startActivity(clientHomeIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ClientRegistrationActivity.this, R.string.authenticationFailed,
                                    Toast.LENGTH_SHORT).show();

                            statusTV.setText(R.string.emailAlreadyExists);
                            statusTV.setVisibility(View.VISIBLE);

                            emailClientET.setText("");
                            passwordClientET.setText("");
                        }

                    }
                });
    }

}
