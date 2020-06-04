package nomeGruppo.eathome.clientSide.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import nomeGruppo.eathome.OtherActivity;
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

    private boolean editFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);


        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        final Toolbar toolBarClientProfile = findViewById(R.id.tlbClientProfile);
        setSupportActionBar(toolBarClientProfile);
        toolBarClientProfile.setTitle(getResources().getString(R.string.my_account));
        toolBarClientProfile.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backspace_black_24dp));
        toolBarClientProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherActivityIntent=new Intent(ClientProfileActivity.this, OtherActivity.class);
                otherActivityIntent.putExtra(FirebaseConnection.CLIENT,client);
                startActivity(otherActivityIntent);
                finish();
            }
        });

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
        final Button btnSave = findViewById(R.id.activity_client_btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit){
                    editFinished = true;
                    Intent otherActivityIntent=new Intent(ClientProfileActivity.this, OtherActivity.class);
                    otherActivityIntent.putExtra(FirebaseConnection.CLIENT,client);
                    startActivity(otherActivityIntent);
                    finish();
                }else{
                    Toast.makeText(ClientProfileActivity.this,getResources().getString(R.string.no_change),Toast.LENGTH_SHORT).show();
                }
            }
        });

        controls = new UtilitiesAndControls();

        //imposto gli hint
        nameEt.setHint(client.nameClient);
        emailEt.setHint(client.emailClient);

        if (client.phoneClient == null) {
            phoneEt.setHint(R.string.enterPhone);
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

        if(editFinished){
            FirebaseConnection connection = new FirebaseConnection();
            connection.write(FirebaseConnection.CLIENT_TABLE, user.getUid(), client);

            Toast.makeText(ClientProfileActivity.this,getResources().getString(R.string.success_save),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ClientProfileActivity.this, getResources().getString(R.string.enterOldPasswordToChangeEmail), Toast.LENGTH_LONG).show();

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

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((passwordConfirmEt.getText().length() == 0) && (oldPasswordEt.getText().length() == 0)
                        && (passwordEt.getText().length() == 0)) {
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
                client.nameClient = nameEt.getText().toString().trim();
                edit = true;
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEt.getText().toString().trim();
                final String oldPassword = oldPasswordEt.getText().toString();

                connection.reauthenticateUser(user, email, oldPassword);

                //controlla riautentificazione utente
                if (connection.getOperationSuccess()) {
                    //controllo validità formato mail
                    if (controls.isEmailValid(email)) {

                        //cambia email in firebase authentication
                        connection.updateEmail(mAuth, user, email, ClientProfileActivity.this);

                        //controllo se l'email è stata cambiata, allora modifica le informazioni da inserire nel database
                        if (email.equals(user.getEmail())) {
                            client.emailClient = email;
                            edit = true;
                        }

                    } else {
                        Toast.makeText(ClientProfileActivity.this, getString(R.string.notValidEmail), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ClientProfileActivity.this, getString(R.string.incorrectPassword), Toast.LENGTH_LONG).show();
                }
            }
        });

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            final String oldPassword = oldPasswordEt.getText().toString();
            final String newPassword = passwordEt.getText().toString();
            final String confirmPassword = passwordConfirmEt.getText().toString();

            @Override
            public void onClick(View view) {
                connection.reauthenticateUser(user, user.getEmail(), oldPassword);

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
                            Toast.makeText(ClientProfileActivity.this, getString(R.string.incorrectPasswordFormat), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(ClientProfileActivity.this, getString(R.string.enteredDifferentPasswords), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ClientProfileActivity.this, getString(R.string.oldIncorrectPassword), Toast.LENGTH_LONG).show();
                }
            }
        });

        phoneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(controls.isPhoneValid(phoneEt.getText().toString().trim())){
                    client.phoneClient = phoneEt.getText().toString().trim();
                    edit = true;
                    Toast.makeText(ClientProfileActivity.this, getString(R.string.phoneNumberChangedCorrectly), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ClientProfileActivity.this, getString(R.string.notValidPhoneNumber), Toast.LENGTH_SHORT).show();
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

