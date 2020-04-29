package nomeGruppo.eathome;

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

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

/*
activity per la registrazione del cliente
 */

public class ClientRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClientRegistration";
    static final String NAME_TABLE = "Clients";

    private EditText nameClient;
    private EditText emailClient;
    private EditText passwordClient;
    private Button btnSignin;
    private TextView statusTV;
    private Client client;

    private int duration = Toast.LENGTH_SHORT;


    private UtilitiesAndControls control;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        mAuth = FirebaseAuth.getInstance();

        client = new Client();
        control = new UtilitiesAndControls();

        nameClient =  findViewById(R.id.editNameClient);
        nameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailClient =  findViewById(R.id.editMailClient);
        emailClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordClient =  findViewById(R.id.editPasswordClient);
        passwordClient.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btnSignin =  findViewById(R.id.btnSigninClient);
        statusTV =  findViewById(R.id.activity_client_registration_tw_status);

        //se l'utente clicca sul bottone registrati
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //se non sono stati compilati tutti i campi
                if (nameClient.getText().toString().trim().length() == 0 || emailClient.getText().toString().trim().length() == 0 || passwordClient.getText().toString().trim().length() == 0) {

                    //mostra 'compila tutti i campi'
                    Toast.makeText(ClientRegistrationActivity.this, ClientRegistrationActivity.this.getResources().getString(R.string.fill_all_fields), duration).show();

                } else {//se tutti i campi sono stati compilati

                    String emailTemp = emailClient.getText().toString().trim();//leggi mail
                    String passwordTemp = passwordClient.getText().toString();//leggi password

                    if (control.isEmailValid(emailTemp) && control.isPasswordValid(passwordTemp)) {//apri la fuznione di controllo se mail o password sono valide
                        createAccount(emailTemp, passwordTemp);
                    }
                }
            }
        });

    }//fine onCreate

    @Override
    protected void onStart() {
        super.onStart();


        user = mAuth.getCurrentUser();
    }

    //TODO in onStop
    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        client.setNameClient(nameClient.getText().toString().trim());
        client.setEmailClient(emailClient.getText().toString().trim());
        client.setPhoneClient(null);

        client.setIdClient(user.getUid()); //assegno come id l'user id generato da Firebase Authentication

        //assegno come chiave del db l'user id generato da Firebase Authentication
        db.write(NAME_TABLE, user.getUid(), client);
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();

                            Intent clientHomeIntent = new Intent(ClientRegistrationActivity.this, HomepageActivity.class);
                            clientHomeIntent.putExtra(FirebaseConnection.CLIENT, user);
                            Toast.makeText(ClientRegistrationActivity.this, "Registrazione effettuata con successo", duration).show();
                            startActivity(clientHomeIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ClientRegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            statusTV.setText("Email già presente");
                            statusTV.setVisibility(View.VISIBLE);

                            emailClient.setText("");
                            passwordClient.setText("");
                        }

                    }
                });
    }

}
