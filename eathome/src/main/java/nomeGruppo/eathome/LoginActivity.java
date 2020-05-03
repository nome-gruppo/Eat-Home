package nomeGruppo.eathome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.clientSide.ClientRegistrationActivity;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.placeSide.PlaceRegistrationActivity;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginFragment";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button loginBtn;
    private EditText emailET;
    private EditText passwordET;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.activity_login_et_email);
        passwordET = findViewById(R.id.activity_login_et_password);
        loginBtn = findViewById(R.id.activity_login_btn_login);
        progressBar = findViewById(R.id.activity_login_pb_loading);
        final TextView txtPlaceRegistration = findViewById(R.id.activity_login_tw_signInPlace);
        final TextView txtClientRegistration = findViewById(R.id.activity_login_tw_signIn);
        final TextView resetPasswordTW = findViewById(R.id.activity_login_tw_resetPassword);

        emailET.addTextChangedListener(afterTextChangedListener);
        passwordET.addTextChangedListener(afterTextChangedListener);

        //listener tasto login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String emailTemp = emailET.getText().toString().trim();
                String passwordTemp = passwordET.getText().toString().trim();

                signIn(emailTemp, passwordTemp);

            }
        });//fine listener tasto login

        txtPlaceRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent placeRegistration = new Intent(LoginActivity.this, PlaceRegistrationActivity.class);
                startActivity(placeRegistration);
            }
        });

        txtClientRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clientRegistration = new Intent(LoginActivity.this, ClientRegistrationActivity.class);
                startActivity(clientRegistration);
            }
        });

        resetPasswordTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseConnection connection = new FirebaseConnection();
                if(emailET.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Inserisci la tua mail per continuare", Toast.LENGTH_LONG).show();
                }else{
                    connection.resetPassword(mAuth, emailET.getText().toString().trim(), LoginActivity.this);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        this.user = mAuth.getCurrentUser();
    }

    private void signIn(final String email, final String password) {

        //ricerca in Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseConnection connection = new FirebaseConnection();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            connection.searchUserInDb(user.getUid(), FirebaseConnection.CLIENT_TABLE, progressBar, LoginActivity.this);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    TextWatcher afterTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (emailET.getText().toString().trim().length() == 0 || passwordET.getText().toString().trim().length() == 0) {
                loginBtn.setEnabled(false);
            } else {
                loginBtn.setEnabled(true);
            }
        }
    };

}
