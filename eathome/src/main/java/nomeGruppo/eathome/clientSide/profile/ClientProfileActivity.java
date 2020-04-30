package nomeGruppo.eathome.clientSide.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.DialogDeleteAccount;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

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
    private Button myAddressesBtn;
    private Button deleteAccountBtn;

    private UtilitiesAndControls controls;

    private DialogDeleteAccount dialog;

    private boolean edit = false; //flag per controllare se qualche campo è stato modificato

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);


        nameEt = findViewById(R.id.activity_client_et_name);
        emailEt = findViewById(R.id.activity_client_et_email);
        oldPasswordEt = findViewById(R.id.activity_client_et_oldPassword);
        passwordEt = findViewById(R.id.activity_client_et_newPassword);
        passwordConfirmEt = findViewById(R.id.activity_client_et_password_confirm);
        phoneEt = findViewById(R.id.activity_client_et_phone);

        nameBtn = findViewById(R.id.activity_client_profile_imBtn_name);
        emailBtn = findViewById(R.id.activity_client_profile_imBtn_email);
        passwordBtn = findViewById(R.id.activity_client_profile_imBtn_password_confirm);
        phoneBtn = findViewById(R.id.activity_client_profile_imBtn_phone);
        myAddressesBtn = findViewById(R.id.activity_client_btn_myAddresses);
        deleteAccountBtn = findViewById(R.id.activity_client_btn_deleteAccount);

        controls = new UtilitiesAndControls();

        //imposto gli hint
        nameEt.setHint(client.nameClient);
        emailEt.setHint(client.emailClient);

        if (client.phoneClient == null) {
            phoneEt.setHint("Telefono");
        } else {
            phoneEt.setHint(client.phoneClient);
        }

        //setto gli imageButton su non clickabili
        nameBtn.setClickable(false);
        emailBtn.setClickable(false);
        passwordBtn.setClickable(false);
        phoneBtn.setClickable(false);

        initEditTextsListeners();
        initButtonsListeners();

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

        if(edit){
            FirebaseConnection connection = new FirebaseConnection();

            connection.write(FirebaseConnection.CLIENT_TABLE, user.getUid(), client);
        }
    }

    public void initEditTextsListeners() {

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nameEt.getText().toString().trim().length() == 0) {
                    nameBtn.setClickable(false);
                } else {
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
                Toast.makeText(ClientProfileActivity.this, "Inserisci anche il campo vecchia password per cambiare la mail", Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((emailEt.getText().toString().trim().length() == 0) && (oldPasswordEt.getText().length() == 0)) {
                    emailBtn.setClickable(false);
                } else {
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
                Toast.makeText(ClientProfileActivity.this, "Inserisci anche il campo mail per cambiare la password", Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((passwordConfirmEt.getText().length() == 0) && (oldPasswordEt.getText().length() == 0)
                        && (passwordEt.getText().length() == 0) && (emailEt.getText().toString().trim().length() == 0)) {
                    passwordBtn.setClickable(false);
                } else {
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
                if (phoneEt.getText().toString().trim().length() == 0) {
                    phoneBtn.setClickable(false);
                } else {
                    phoneBtn.setClickable(true);
                }
            }
        });
    }//fine metodo initEditTextListener()

    public void initButtonsListeners() {

        final FirebaseConnection connection = new FirebaseConnection();


        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.setNameClient(nameEt.getText().toString().trim());
                edit = true;
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String oldPassword = oldPasswordEt.getText().toString();

                connection.reauthenticateUser(user, email, oldPassword);

                //controlla riautentificazione utente
                if (connection.getOperationSuccess()) {
                    //controllo validità formato mail
                    if (controls.isEmailValid(email)) {

                        connection.updateEmail(mAuth, user, email, ClientProfileActivity.this);

                        //controllo se l'email è stata cambiata, allora modifica le informazioni da inserire nel database database
                        if (email.equals(user.getEmail())) {
                            client.emailClient = email;
                            edit = true;
                            Toast.makeText(ClientProfileActivity.this, "Email cambiata correttamente", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(ClientProfileActivity.this, "Email non valida", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ClientProfileActivity.this, "Password non corretta", Toast.LENGTH_LONG).show();
                }
            }
        });

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            String email = emailEt.getText().toString().trim();
            String oldPassword = oldPasswordEt.getText().toString();
            String newPassword = passwordEt.getText().toString();
            String confirmPassword = passwordConfirmEt.getText().toString();

            @Override
            public void onClick(View view) {
                connection.reauthenticateUser(user, email, oldPassword);

                //controllo riautenticazione utente
                if (connection.getOperationSuccess()){

                    //controllo uguaglianza password
                    if(oldPassword.equals(newPassword) && newPassword.equals(confirmPassword)){

                        if(controls.isPasswordValid(confirmPassword)){

                            connection.updatePassword(user, newPassword, ClientProfileActivity.this);
                            if(connection.getOperationSuccess()){
                                edit = true;
                            }
                        }else{
                            Toast.makeText(ClientProfileActivity.this, "Formato password non corrette", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(ClientProfileActivity.this, "Le password non sono corrette", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ClientProfileActivity.this, "Email non corretta", Toast.LENGTH_LONG).show();
                }
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(controls.isPhoneValid(phoneEt.getText().toString().trim())){
                    client.setPhoneClient(phoneEt.getText().toString().trim());
                    edit = true;
                    Toast.makeText(ClientProfileActivity.this, "Telefono cambiato correttamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ClientProfileActivity.this, "Telefono non valido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myAddressesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesIntent = new Intent(ClientProfileActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra(FirebaseConnection.CLIENT, client);
                startActivity(myAddressesIntent);
            }
        });




        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new DialogDeleteAccount();
                dialog.show(getSupportFragmentManager(), "Dialog delete account");
            }
        });
    }//fine initButtonsListeners()
}

