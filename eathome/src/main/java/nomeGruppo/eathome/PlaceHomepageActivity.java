package nomeGruppo.eathome;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.foods.Food;
import nomeGruppo.eathome.foods.Menu;
import nomeGruppo.eathome.utility.MyMenuAdapter;

import static android.content.ContentValues.TAG;

public class PlaceHomepageActivity extends AppCompatActivity {

    static final int PICK_IMAGE=100;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    static final int HEIGHT_IMAGE=100;
    static final int WIDTH_IMAGE=200;

    private ImageView imgPlace;
    private Place place;
    private TextView txtNamePlace;
    private ListView listViewMenu;
    private List<Food> listFood;
    private MyMenuAdapter mAdapter;
    private ImageButton btnAddMenu;
    private TextView txtPath;
    private BottomNavigationView bottomMenuPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_homepage);

        place = (Place) getIntent().getSerializableExtra("PLACE");

        txtNamePlace=(TextView)findViewById(R.id.txtNamePlace);
        txtNamePlace.setText(place.namePlace);
        btnAddMenu=(ImageButton)findViewById(R.id.btnAddMenu);
        txtPath=(TextView)findViewById(R.id.txtPath);
        txtPath.setVisibility(View.INVISIBLE);
        listViewMenu=(ListView)findViewById(R.id.listMenu);
        bottomMenuPlace=(BottomNavigationView) findViewById(R.id.bottom_navigationPlace);

        //FirebaseConnection db=new FirebaseConnection();
        //db.queryEqualTo("Menu","idPlace",place.idPlace).addListenerForSingleValueEvent(valueEventListener);
        //StorageConnection storageConnection=new StorageConnection();
        //storageConnection.downloadImage(place.idPlace);


        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_orders:

                        break;
                    case R.id.action_bookings:

                        break;
                    case R.id.action_profile:

                        break;
                }
                return true;
            }
        });

        listFood=new LinkedList<Food>();
        mAdapter=new MyMenuAdapter(this,R.layout.listitem_menu,listFood);
        listViewMenu.setAdapter(mAdapter);

        imgPlace= (ImageView)findViewById(R.id.placeImg);

        imgPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(PlaceHomepageActivity.this);
                dialog.setContentView(R.layout.dialog_insert_food);
                EditText editNameFood=(EditText)dialog.findViewById(R.id.editNameFood);
                EditText editIngredientsFood=(EditText)dialog.findViewById(R.id.editIngredientsFood);
            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Menu menu = snapshot.getValue(Menu.class);
                    listFood=menu.listFood;
                }
                mAdapter.notifyDataSetChanged();
            }
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

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
            imgPlace.setImageURI(imageUri);
            BitmapDrawable drawable = (BitmapDrawable) imgPlace.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            getResizedBitmap(bitmap,WIDTH_IMAGE,HEIGHT_IMAGE);
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
    protected void onPause() {
        super.onPause();

        StorageConnection storage=new StorageConnection();//apro la connessione allo Storage di Firebase
        storage.uploadImage(txtPath.getText().toString(),place.idPlace);//carico l'immagine nello Storage con nome corrispondente all'idPlace
    }

}
