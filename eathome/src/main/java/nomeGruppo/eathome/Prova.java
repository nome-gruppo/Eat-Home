package nomeGruppo.eathome;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.StorageConnection;


public class Prova extends AppCompatActivity {

    static final int PICK_IMAGE=100;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    private Button btnProva;
    private ImageView imgProva;
    private TextView txtProva;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prova);

        btnProva=(Button)findViewById(R.id.btnProva);
        txtProva=(TextView)findViewById(R.id.txtProva);
        imgProva=(ImageView)findViewById(R.id.imgProva);


        btnProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageConnection storage=new StorageConnection();
                storage.uploadImage(txtProva.getText().toString(),"id");
            }
        });
    }




}
