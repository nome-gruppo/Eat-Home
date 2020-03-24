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
import nomeGruppo.eathome.ui.login.LoginViewModel;
import nomeGruppo.eathome.utility.Controls;

public class ClientRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClientRegistration";
    static final String NAME_TABLE = "Clients";

    private EditText nameClient;
    private EditText surnameClient;
    private EditText emailClient;
    private EditText passwordClient;
    private Button btnSignin;
    private TextView statusTV;
    private Client client;

    private int duration = Toast.LENGTH_SHORT;


    private Controls control;


    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        mAuth = FirebaseAuth.getInstance();

        client = new Client();
        control = new Controls();

        nameClient = (EditText) findViewById(R.id.editNameClient);
        nameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        surnameClient = (EditText) findViewById(R.id.editSurnameClient);
        surnameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailClient = (EditText) findViewById(R.id.editMailClient);
        emailClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordClient = (EditText) findViewById(R.id.editPasswordClient);
        passwordClient.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btnSignin = (Button) findViewById(R.id.btnSigninClient);
        statusTV = (TextView) findViewById(R.id.activity_client_registration_tw_status);


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameClient.getText().toString().trim().length() == 0 || surnameClient.getText().toString().trim().length() == 0 || emailClient.getText().toString().trim().length() == 0 || passwordClient.getText().toString().trim().length() == 0) {

                    Toast.makeText(ClientRegistrationActivity.this, "Compila tutti i campi", duration).show();

                } else {

                    String emailTemp = emailClient.getText().toString().trim();
                    String passwordTemp = passwordClient.getText().toString();

                    if (control.isEmailValid(emailTemp) && control.isPasswordValid(passwordTemp)) {
                        createAccount(emailTemp, passwordTemp);
                    }
                }
            }
        });

    }//fine onCreate

    @Override
    protected void onStart() {
        super.onStart();

        // TODO Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db

        client.setNameClient(nameClient.getText().toString().trim());
        client.setSurnameClient(surnameClient.getText().toString().trim());
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

                            Intent clientHomeIntent = new Intent(ClientRegistrationActivity.this, MainActivity.class);

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

    /*Metodo che controlla la validità della password
     */
    private boolean passwordControl(String password) {

        if (password.length() < 6) {
            statusTV.setText("La password deve essere almeno di 6 caratteri");
            statusTV.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

}
