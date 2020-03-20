package nomeGruppo.eathome;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.ui.home.HomeFragment;

public class ClientRegistrationActivity extends AppCompatActivity {

    static final String NAME_TABLE="Clients";

    private EditText nameClient;
    private EditText surnameClient;
    private EditText mailClient;
    private EditText passwordClient;
    private Button btnSignin;
    private Client client;

    private int duration = Toast.LENGTH_SHORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        this.client=new Client();

        this.nameClient=(EditText)findViewById(R.id.editNameClient);
        this.nameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.surnameClient=(EditText)findViewById(R.id.editSurnameClient);
        this.surnameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.mailClient=(EditText)findViewById(R.id.editMailClient);
        this.mailClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        this.passwordClient=(EditText)findViewById(R.id.editPasswordClient);
        this.passwordClient.setImeOptions(EditorInfo.IME_ACTION_DONE);
        this.btnSignin=(Button)findViewById(R.id.btnSigninClient);

        final Intent clientHomeIntent = new Intent(this, MainActivity.class);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameClient.getText().toString().trim().length() == 0||surnameClient.getText().toString().trim().length() ==0||mailClient.getText().toString().trim().length() ==0||passwordClient.getText().toString().trim().length() ==0) {

                    Toast.makeText(ClientRegistrationActivity.this, "Compila tutti i campi", duration).show();

                }else{
                    client.setNameClient(nameClient.getText().toString());
                    client.setSurnameClient(surnameClient.getText().toString());
                    client.setEmailClient(mailClient.getText().toString());
                    client.setPasswordClient(passwordClient.getText().toString());
                    client.setPhoneClient(null);

                    FirebaseConnection db = new FirebaseConnection(); //apro la connessione al db
                    db.writeObject(NAME_TABLE, client); //scrivo l'oggetto client nel db

                    startActivity(clientHomeIntent);

                }
            }
        });

    }


}
