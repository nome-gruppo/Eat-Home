package nomegruppo.eathome.placeSide.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomegruppo.eathome.placeSide.PlaceHomeActivity;
import nomegruppo.eathome.PlaceOpeningTimeActivity;
import nomegruppo.eathome.R;
import nomegruppo.eathome.actors.Place;
import nomegruppo.eathome.actors.PlaceCategories;
import nomegruppo.eathome.db.FirebaseConnection;
import nomegruppo.eathome.DialogDeleteAccount;
import nomegruppo.eathome.utility.UtilitiesAndControls;

public class PlaceProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private EditText nameEt;
    private EditText emailEt;
    private EditText oldPasswordEt;
    private EditText newPasswordEt;
    private EditText confirmPasswordEt;
    private EditText addressEt;
    private EditText addressNumEt;
    private EditText cityEt;
    private EditText phoneEt;

    private ImageButton nameBtn;
    private ImageButton emailBtn;
    private ImageButton passwordBtn;
    private ImageButton addressBtn;
    private ImageButton cityBtn;
    private ImageButton phoneBtn;

    private SeekBar deliveryCostSb;
    private CheckBox deliveryCb;
    private CheckBox bookingCb;

    private RadioButton categoryRb;

    private Place mPlace;

    private Button timesBtn;
    private Button saveBtn;
    private Button deleteAccountBtn;

    private DialogDeleteAccount dialog;

    private boolean edit = false; //flag per controllare se qualche campo è stato modificato

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_profile);

        mPlace = (Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);

        mAuth = FirebaseAuth.getInstance();

        nameEt = findViewById(R.id.activity_place_profile_et_name);
        emailEt = findViewById(R.id.activity_place_profile_et_email);
        oldPasswordEt = findViewById(R.id.activity_place_profile_et_oldPassword);
        newPasswordEt = findViewById(R.id.activity_place_profile_et_newPassword);
        confirmPasswordEt = findViewById(R.id.activity_place_profile_et_confirmPassword);
        addressEt = findViewById(R.id.activity_place_profile_et_address);
        addressNumEt = findViewById(R.id.activity_place_profile_et_numAddress);
        cityEt = findViewById(R.id.activity_place_profile_et_city);
        phoneEt = findViewById(R.id.activity_place_profile_et_phone);

        nameBtn = findViewById(R.id.activity_place_profile_imBtn_name);
        emailBtn = findViewById(R.id.activity_place_profile_imBtn_email);
        passwordBtn = findViewById(R.id.activity_place_profile_imBtn_password);
        addressBtn = findViewById(R.id.activity_place_profile_imBtn_address);
        cityBtn = findViewById(R.id.activity_place_profile_imBtn_city);
        phoneBtn = findViewById(R.id.activity_place_profile_imBtn_phone);

        deliveryCostSb = findViewById(R.id.activity_place_profile_sb_deliveryCost);
        deliveryCb = findViewById(R.id.activity_place_profile_cb_delivery);
        bookingCb = findViewById(R.id.activity_place_profile_cb_booking);

        timesBtn = findViewById(R.id.activity_place_profile_btn_time);
        saveBtn = findViewById(R.id.activity_place_profile_btn_save);
        deleteAccountBtn = findViewById(R.id.activity_client_btn_deleteAccount);

        if(mPlace != null) {
            nameEt.setHint(mPlace.namePlace);
            emailEt.setHint(mPlace.emailPlace);
            addressEt.setHint(mPlace.addressPlace);
            addressNumEt.setHint(mPlace.addressNumPlace);
            cityEt.setHint(mPlace.cityPlace);

            if(mPlace.phonePlace != null) {
                phoneEt.setHint(mPlace.phonePlace);
            }else{
                phoneEt.setHint("Inserisci un recapito telefonico");
            }

            deliveryCostSb.setProgress(mPlace.deliveryCost);
            deliveryCb.setChecked(mPlace.takesOrderPlace);
            deliveryCb.setChecked(mPlace.takesBookingPlace);

            String category = mPlace.categories;

            //match categoria locale
            if(category.equals(PlaceCategories.SUSHI.toString())){
                categoryRb = findViewById(R.id.activity_place_profile_rb_sushi);

            }else if(category.equals(PlaceCategories.PIZZERIA.toString())){
                categoryRb = findViewById(R.id.activity_place_profile_rb_pizzeria);

            }else if(category.equals(PlaceCategories.PIZZERIA_RISTORANTE.toString())){
                categoryRb = findViewById(R.id.activity_place_profile_rb_restaurant);

            }else if(category.equals(PlaceCategories.RISTORANTE_ITALIANO.toString())){
                categoryRb = findViewById(R.id.activity_place_profile_rb_italianRestaurant);

            }else if(category.equals(PlaceCategories.ALTRO.toString())){
                categoryRb = findViewById(R.id.activity_place_profile_rb_other);

            }
            categoryRb.setChecked(true);
        }

        //setto gli imageButton su non clickabili
        nameBtn.setClickable(false);
        emailBtn.setClickable(false);
        passwordBtn.setClickable(false);
        cityBtn.setClickable(false);
        phoneBtn.setClickable(false);

        initEditTextsListeners();
        initButtonsListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //modifica database solo se qualche campo è stato editato
        if(edit){
            FirebaseConnection connection = new FirebaseConnection();

            connection.write(FirebaseConnection.PLACE_TABLE, user.getUid(), mPlace);
        }
    }

    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        //assegna la categoria
        switch (view.getId()) {
            case R.id.radioPizzeria:
                if (checked)
                    mPlace.setCategories(PlaceCategories.PIZZERIA.toString());
                break;
            case R.id.radioItalianRestaurant:
                if (checked)
                    mPlace.setCategories(PlaceCategories.RISTORANTE_ITALIANO.toString());
                break;
            case R.id.radioSushi:
                if (checked)
                    mPlace.setCategories(PlaceCategories.SUSHI.toString());
                break;
            case R.id.radioPizzeriaRestaurant:
                if (checked)
                    mPlace.setCategories(PlaceCategories.PIZZERIA_RISTORANTE.toString());
                break;
            case R.id.radioOther:
                if (checked)
                    mPlace.setCategories(PlaceCategories.ALTRO.toString());
                break;
        }

        edit = true;
    }

    //inizializza i listener delle EditText
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
                Toast.makeText(PlaceProfileActivity.this, "Inserisci anche il campo vecchia password per cambiare la mail", Toast.LENGTH_LONG).show();

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

        confirmPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Toast.makeText(PlaceProfileActivity.this, "Inserisci anche il campo mail per cambiare la password", Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((confirmPasswordEt.getText().length() == 0) && (oldPasswordEt.getText().length() == 0)
                        && (newPasswordEt.getText().length() == 0) && (emailEt.getText().toString().trim().length() == 0)) {
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

        TextWatcher addressTW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(addressEt.getText().toString().trim().length() == 0 || addressNumEt.getText().toString().trim().length() == 0){
                    addressBtn.setClickable(false);
                }else {
                    addressBtn.setClickable(true);
                }
            }
        };

        addressEt.addTextChangedListener(addressTW);

        addressNumEt.addTextChangedListener(addressTW);

        cityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cityEt.getText().toString().trim().length() == 0){
                    cityBtn.setClickable(false);
                }else{
                    cityBtn.setClickable(true);
                }
            }
        });

    }

    //inizializza listener buttons, seekbar e checkboxes
    public void initButtonsListeners(){

        final FirebaseConnection connection = new FirebaseConnection();
        final UtilitiesAndControls controls = new UtilitiesAndControls();

        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlace.namePlace = nameEt.getText().toString().trim();
                edit = true;
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String oldPassword = oldPasswordEt.getText().toString();

                connection.reauthenticateUser(user, email, oldPassword);

                //controlla riautenticazione utente
                if (connection.getOperationSuccess()) {
                    //controllo validità formato mail
                    if (controls.isEmailValid(email)) {

                        connection.updateEmail(mAuth, user, email, PlaceProfileActivity.this);

                        //controllo se l'email è stata cambiata, allora modifica le informazioni da inserire nel database
                        if (email.equals(user.getEmail())) {
                            mPlace.emailPlace = email;
                            edit = true;
                            Toast.makeText(PlaceProfileActivity.this, "Email cambiata correttamente", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(PlaceProfileActivity.this, "Email non valida", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(PlaceProfileActivity.this, "Password non corretta", Toast.LENGTH_LONG).show();
                }
            }
        });

        passwordBtn.setOnClickListener(new View.OnClickListener() {
            String email = emailEt.getText().toString().trim();
            String oldPassword = oldPasswordEt.getText().toString();
            String newPassword = newPasswordEt.getText().toString();
            String confirmPassword = confirmPasswordEt.getText().toString();

            @Override
            public void onClick(View view) {
                connection.reauthenticateUser(user, email, oldPassword);

                //controllo riautenticazione utente
                if (connection.getOperationSuccess()){

                    //controllo uguaglianza password
                    if(oldPassword.equals(newPassword) && newPassword.equals(confirmPassword)){

                        if(controls.isPasswordValid(confirmPassword)){

                            connection.updatePassword(user, newPassword, PlaceProfileActivity.this);
                            if(connection.getOperationSuccess()){
                                edit = true;
                            }
                        }else{
                            Toast.makeText(PlaceProfileActivity.this, "Formato password non corretto", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(PlaceProfileActivity.this, "Le password non sono corrette", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(PlaceProfileActivity.this, "Email non corretta", Toast.LENGTH_LONG).show();
                }
            }
        });


        phoneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(controls.isPhoneValid(phoneEt.getText().toString().trim())){
                    mPlace.setPhonePlace(phoneEt.getText().toString().trim());
                    edit = true;
                    Toast.makeText(PlaceProfileActivity.this, "Telefono cambiato correttamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PlaceProfileActivity.this, "Telefono non valido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(addressEt.getText() != null){
                    mPlace.addressPlace = addressEt.getText().toString().trim();
                    edit = true;
                }

                if(addressNumEt.getText() != null){
                    mPlace.addressNumPlace = addressNumEt.getText().toString().trim();
                    edit = true;
                }
            }
        });

        cityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlace.cityPlace = cityEt.getText().toString().trim();
                edit = true;
            }
        });

        deliveryCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPlace.takesOrderPlace = b;
                edit = true;
            }
        });

        bookingCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPlace.takesBookingPlace = b;
                edit = true;
            }
        });

        deliveryCostSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mPlace.deliveryCost = i;
                edit = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        timesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent timesIntent = new Intent(PlaceProfileActivity.this, PlaceOpeningTimeActivity.class);
                timesIntent.putExtra(FirebaseConnection.PLACE,mPlace);
                startActivity(timesIntent);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit) {
                    Intent intent = new Intent(PlaceProfileActivity.this, PlaceHomeActivity.class);
                    intent.putExtra(FirebaseConnection.PLACE, mPlace);
                    startActivity(intent);
                }
                finish();
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new DialogDeleteAccount();
                dialog.show(getSupportFragmentManager(), "Dialog delete account");
            }
        });

    }
}

