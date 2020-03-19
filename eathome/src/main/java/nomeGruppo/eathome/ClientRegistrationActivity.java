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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;

public class ClientRegistrationActivity extends AppCompatActivity {

    static final String NAME_TABLE="Clients";
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    static final int MY_PERMISSIONS_REQUEST_CAMERA=100;
    static final int HEIGHT_IMAGE=120;
    static final int WIDTH_IMAGE=120;

    private ImageView imgClient;
    private EditText nameClient;
    private EditText surnameClient;
    private EditText mailClient;
    private EditText passwordClient;
    private Button btnSignin;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_registration);

        imgClient.findViewById(R.id.imgClient);
        nameClient.findViewById(R.id.editNameClient);
        nameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        surnameClient.findViewById(R.id.editSurnameClient);
        surnameClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mailClient.findViewById(R.id.editMailClient);
        mailClient.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordClient.findViewById(R.id.editPasswordClient);
        passwordClient.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btnSignin.findViewById(R.id.btnSigninClient);

        imgClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(ClientRegistrationActivity.this);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                client.setNameClient(nameClient.getText().toString());
                client.setSurnameClient(surnameClient.getText().toString());
                client.setEmailClient(mailClient.getText().toString());
                client.setPasswordClient(passwordClient.getText().toString());

                FirebaseConnection db=new FirebaseConnection(); //apro la connessione al db
                db.writeObject(NAME_TABLE,client); //scrivo l'oggetto client nel db
            }
        });


    }


    //metodo per selezionare se caricare l'immagine tramite fotocamare, galleria o nulla
    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take a photo")) {
                    showCamera();
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);//intent per l'apertura della fotocamera
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    showGallery();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//intent per accedere alla galleria
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    //in base al requestCode ricevuto setta imgPlace dalla fotocamaera o dalla galleria
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imgClient.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap selectedImageGallery= BitmapFactory.decodeFile(picturePath);
                                selectedImageGallery=getResizedBitmap(selectedImageGallery,WIDTH_IMAGE,HEIGHT_IMAGE);
                                imgClient.setImageBitmap(selectedImageGallery);//si passa in decodeFile il path dell'immagine
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    //metodo per dimensionare l'immagine all'interno del riquadro
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void showCamera() {
        // permesso già concesso?
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            // Bisogna visualizzare dialog per richiedere il permesso?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                // possiamo richiedere il permesso
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.CAMERA }, MY_PERMISSIONS_REQUEST_CAMERA);
                // la richiesta del permesso viene gestito
            }
        } else {
            // utilizza permesso
        }
    }

    public void showGallery(){
        //vengono chiesti i permessi per utilizzare le immagini in galleria
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            // Permission has already been granted
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }case MY_PERMISSIONS_REQUEST_CAMERA: {
                // Se la richiesta è stata cancellata, l'array result è vuoto
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // utilizza permesso
                } else {
                    // gestisci permesso negato
                }
            }
            return;

        }
    }
}
