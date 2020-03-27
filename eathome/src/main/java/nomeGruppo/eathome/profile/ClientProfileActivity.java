package nomeGruppo.eathome.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;

public class ClientProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Client client;

    private EditText nameEt;
    private EditText emailEt;
    private EditText oldPasswordEt;
    private EditText passwordEt;
    private EditText passwordConfirmEt;
    private EditText phoneEt;

    private ImageButton nameBtn;
    private ImageButton emailBtn;
    private ImageButton passwordBtn;
    private ImageButton phoneBtn;
    private Button logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        nameEt = findViewById(R.id.activity_client_et_name);
        emailEt = findViewById(R.id.activity_client_et_email);
        oldPasswordEt = findViewById(R.id.activity_client_et_oldPassword);
        passwordEt = findViewById(R.id.activity_client_et_password);
        passwordConfirmEt = findViewById(R.id.activity_client_et_password_confirm);
        phoneEt = findViewById(R.id.activity_client_et_phone);

        nameBtn = findViewById(R.id.activity_client_profile_imBtn_name);
        emailBtn = findViewById(R.id.activity_client_profile_imBtn_email);
        passwordBtn = findViewById(R.id.activity_client_profile_imBtn_password_confirm);
        phoneBtn = findViewById(R.id.activity_client_profile_imBtn_phone);

        logoutBtn = findViewById(R.id.activity_client_btn_logout);

        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        //imposto gli hint
        nameEt.setHint(client.nameClient);
        emailEt.setHint(client.emailClient);

        if(client.phoneClient == null){
            phoneEt.setHint("Telefono");
        }else{
            phoneEt.setHint(client.phoneClient);
        }

        //setto gli imageButton su non clickabili
        nameBtn.setClickable(false);
        emailBtn.setClickable(false);
        passwordBtn.setClickable(false);
        phoneBtn.setClickable(false);

        initEditTextsListeners();





    }//end onCreate

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initEditTextsListeners(){

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(nameEt.getText().toString().trim().length() == 0){
                    nameBtn.setClickable(false);
                }else{
                    nameBtn.setClickable(true);
                }
            }
        });

        emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(emailEt.getText().toString().trim().length() == 0){
                    emailBtn.setClickable(false);
                }else{
                    emailBtn.setClickable(true);
                }
            }
        });

        passwordConfirmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if((passwordConfirmEt.getText().length() == 0) && (oldPasswordEt.getText().length() == 0)){
                    passwordBtn.setClickable(false);
                }else{
                    passwordBtn.setClickable(true);
                }
            }
        });

        phoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(phoneEt.getText().toString().trim().length() == 0){
                    phoneBtn.setClickable(false);
                }else{
                    phoneBtn.setClickable(true);
                }
            }
        });
    }//fine metodo initEditTextListener()

    public void initButtonsListeners(){

        final FirebaseConnection connection = new FirebaseConnection();

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setNameClient(nameEt.getText().toString().trim());
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connection.reauthenticateUser(user, emailEt.getText().toString().trim(), oldPasswordEt.getText().toString());
                mAuth.fetchSignInMethodsForEmail()
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent homepageIntent = new Intent(ClientProfileActivity.this, HomepageActivity.class);
                homepageIntent.putExtra(FirebaseConnection.LOGGED_FLAG, false);
                startActivity(homepageIntent);
                finish();
            }
        });
    }//fine initButtonsListeners()


}

