package nomeGruppo.eathome.placeSide;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

import static nomeGruppo.eathome.utility.UtilitiesAndControls.PICT_SIZE_MAX;

/*
activity homepage per Place
 */
public class PlaceHomeActivity extends AppCompatActivity implements DialogAddMenu.DialogAddMenuListener {

    private static final int GET_FROM_GALLERY = 3;

    private ImageView imgPlace;
    private Place place;
    private List<Food> listFood;
    private MyMenuAdapter mAdapter;
    private boolean changeImg = false;
    private Food food;
    private MenuNavigationItemSelected menuNavigationItemSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_homepage);


        place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        food = new Food();

        final TextView txtNamePlace = findViewById(R.id.place_homepage_txtNamePlace);
        final FloatingActionButton btnAddMenu = findViewById(R.id.place_homepage_btnAddMenu);
        final ListView listViewMenu = findViewById(R.id.place_homepage_listMenu);
        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlace);
        imgPlace = findViewById(R.id.place_homepage_placeImg);

        menuNavigationItemSelected = new MenuNavigationItemSelected();
        listFood = new LinkedList<>();
        mAdapter = new MyMenuAdapter(this, listFood, place);

        txtNamePlace.setText(place.namePlace);
        listViewMenu.setAdapter(mAdapter);
        //mostro il menu sottostante
        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceHomeActivity.this);
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        listFood.clear();

        if (!changeImg) {//se l'immagine non è stata appena cambiata
            StorageConnection storageConnection = new StorageConnection();//apro la connessione allo Storage di Firebase
            StorageReference storageReference = storageConnection.storageReference(place.idPlace);//l'immagine nello Storage ha lo stesso nome del codice del ristorante

            //metodo di lettura immagine tramite byte
            storageReference.getBytes(PICT_SIZE_MAX * PICT_SIZE_MAX)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgPlace.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imgPlace.setVisibility(View.VISIBLE);
                }
            });
        }

        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //leggo i cibi presenti all'interno del ristorante e li assegno alla listFood collegata con l'adapter per poter stamparli sulla listView corrispondente
        firebaseConnection.getmDatabase().child(FirebaseConnection.FOOD_NODE).child(place.idPlace).addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * metodo per aprire il dialog per aggiungere voci al menu
     */

    private void openDialog() {
        DialogAddMenu dialogAddMenu = new DialogAddMenu();
        dialogAddMenu.show(getSupportFragmentManager(), "Dialog add menu");
    }

    /**
     * metodo per aprire la galleria del dispositivo
     */
    private void openGallery() {
        //intent per accedere alla galleria
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    /**
     * metodo per prendere l'immagine dalla galleria dell'utente e caricarla sull'app
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();//restituisce l'uri dell'immagine
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);//converto l'uri in Bitmap

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);//comprimo l'immagine

                imgPlace.setImageBitmap(bitmap);//assegno l'immagine come copertina della home
                changeImg = true;//imposto che l'immagine è stata appena cambiata

                StorageConnection storage = new StorageConnection();//apro la connessione allo Storage di Firebase
                storage.uploadImageBitmap(stream, place.idPlace);//inserisco l'immagine nello storage

                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * metodo per inserire la nuova voce del menu al db firebase
     *
     * @param nameFood        nome del cibo da aggiungere
     * @param ingredientsFood ingredienti del cibo da aggiungere
     * @param priceFood       prezzo del cibo da aggiungere
     */

    @Override
    public void applyTexts(String nameFood, String ingredientsFood, float priceFood) {
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        food.setName(nameFood);
        food.setIngredients(ingredientsFood);
        food.setPrice(priceFood);
        food.setIdFood(firebaseConnection.getmDatabase().push().getKey());
        firebaseConnection.getmDatabase().child(FirebaseConnection.FOOD_NODE).child(place.idPlace).child(food.idFood).setValue(food);//aggiungo il nuovo 'cibo' al database

        listFood.add(0, food);//aggiungo food in testa alla lista
        mAdapter.notifyDataSetChanged();//aggiorno l'adapter così da aggiornare la listView con l'elenco dei cibi
    }
}
