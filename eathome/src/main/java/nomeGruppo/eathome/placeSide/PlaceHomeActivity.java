package nomeGruppo.eathome.placeSide;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.utility.DialogAddMenu;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.MyMenuAdapter;

/*
activity homepage per Place
 */
public class PlaceHomeActivity extends AppCompatActivity implements DialogAddMenu.DialogAddMenuListener {

    static final String NAME_TABLE_FOODS="Foods";
    static final int PICK_IMAGE=100;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;

    private ImageView imgPlace;
    private Place place;
    private TextView txtNamePlace;
    private ListView listViewMenu;
    private List<Food> listFood;
    private MyMenuAdapter mAdapter;
    private ImageButton btnAddMenu;
    private TextView txtPath;
    private BottomNavigationView bottomMenuPlace;
    private Food food;
    private boolean logged;
    private ImageButton btnDeleteFood;
    private MenuNavigationItemSelected menuNavigationItemSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_homepage);

        logged = getIntent().getBooleanExtra(FirebaseConnection.LOGGED_FLAG, false);

        place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        food=new Food();

        txtNamePlace=(TextView)findViewById(R.id.txtNamePlace);
        txtNamePlace.setText(place.namePlace);
        btnAddMenu=(ImageButton)findViewById(R.id.btnAddMenu);
        txtPath=(TextView)findViewById(R.id.txtPath);
        txtPath.setVisibility(View.INVISIBLE);
        listViewMenu=(ListView)findViewById(R.id.listMenu);
        bottomMenuPlace=(BottomNavigationView) findViewById(R.id.bottom_navigationPlace);
        imgPlace= (ImageView)findViewById(R.id.placeImg);
        btnAddMenu=(ImageButton)findViewById(R.id.btnAddMenu);
        listFood=new LinkedList<>();
        mAdapter=new MyMenuAdapter(this,R.layout.listitem_menu,listFood,place);
        listViewMenu.setAdapter(mAdapter);
        btnDeleteFood=(ImageButton)findViewById(R.id.btnDeleteFood);
        this.menuNavigationItemSelected=new MenuNavigationItemSelected();

        //mostro il menu sottostante
        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item,place,PlaceHomeActivity.this);
            }
        });

        //se place clicca sull'immagine
        imgPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });//apre la galleria per far scegliere l'immagine del ristorante

        //se clicca sul pulsante per aggiungere una nuova voce al menu
        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();//apro una finestra di dialogo per permettere all'utente inserire una nuova voce nel menu in maniera interattiva

            }
        });

        //se clicca su una voce nella lista del menu
        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Food food=(Food)adapterView.getItemAtPosition(i);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        listFood.clear();

        StorageConnection storageConnection=new StorageConnection();//apro la connessione allo Storage di Firebase
        StorageReference storageReference=storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

        //metodo di lettura immagine tramite byte
        storageReference.getBytes(3840*3840)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imgPlace.setImageBitmap(bitmap);
                    }
                });

        final FirebaseConnection firebaseConnection=new FirebaseConnection();

        //leggo i cibi presenti all'interno del ristorante e li assegno alla listFood collegata con l'adapter per poter stamparli sulla listView corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.FOOD_TABLE).child(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listFood.add(snapshot.getValue(Food.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        StorageConnection storage=new StorageConnection();//apro la connessione allo Storage di Firebase
        storage.uploadImage(txtPath.getText().toString(),place.idPlace);//carico l'immagine nello Storage con nome corrispondente all'idPlace
    }

    private void openDialog(){
        DialogAddMenu dialogAddMenu=new DialogAddMenu();
        dialogAddMenu.show(getSupportFragmentManager(),"Dialog add menu");
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);//intent per accedere alla galleria
        startActivityForResult(gallery,PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==PICK_IMAGE) {
            Uri imageUri = data.getData();//restituisce l'uri dell'immagine
            //trasforma l'Uri in Path
            Context context = getBaseContext();
            Cursor cursor = getContentResolver().query(imageUri,null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String absoluteFilePath = cursor.getString(idx);
            txtPath.setText(absoluteFilePath);//assegno il valore del path dell'immagine a txtPath
            imgPlace.setImageURI(imageUri);//assegno l'immagine come copertina della home
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
            }
            return;
        }
    }

    @Override
    public void applyTexts(String nameFood, String ingredientsFood,float priceFood) {
        food.setName(nameFood);
        food.setIngredients(ingredientsFood);
        food.setPrice(priceFood);

        FirebaseConnection firebaseConnection=new FirebaseConnection();
        firebaseConnection.getmDatabase().child(NAME_TABLE_FOODS).child(place.idPlace).push().setValue(food);//aggiungo il nuovo 'cibo' al databse

        listFood.add(food);//aggiungo food alla lista
        mAdapter.notifyDataSetChanged();//aggiorno l'adapter così da aggiornare la listView con l'elenco dei cibi
    }
}
