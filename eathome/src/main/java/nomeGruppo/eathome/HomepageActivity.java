package nomeGruppo.eathome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.db.StorageConnection;
import nomeGruppo.eathome.profile.ClientProfileActivity;
import nomeGruppo.eathome.utility.PlaceAdapter;

public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = "HomepageActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;

    private BottomNavigationView bottomMenuClient;
    private boolean logged;
    private Client client;
    private ListView listViewPlace;
    private List<nomeGruppo.eathome.actors.Place> listPlace;
    private PlaceAdapter mAdapter;
    private FirebaseUser user;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        logged = getIntent().getBooleanExtra(FirebaseConnection.LOGGED_FLAG, false);
        String apiKey = getString(R.string.api_key);
        bottomMenuClient = (BottomNavigationView) findViewById(R.id.bottom_navigationClient);

        listViewPlace=(ListView)findViewById(R.id.listViewPlace);
        listPlace=new LinkedList<>();
        mAdapter=new PlaceAdapter(this,R.layout.fragment_place_info_homepage_activity,listPlace);
        listViewPlace.setAdapter(mAdapter);

        listViewPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(HomepageActivity.this,"clicked"+i,Toast.LENGTH_SHORT).show();
                /*
                Place place=(Place)adapterView.getItemAtPosition(i);
                Intent placeInfoIntent=new Intent(HomepageActivity.this,PlaceInfoActivity.class);
                placeInfoIntent.putExtra(FirebaseConnection.PLACE, place);
                startActivity(placeInfoIntent);*/
            }
        });


            client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);



//        View placeBar = getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
//        mainLayout.addView(placeBar);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_homepage_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_orders:

                        break;
                    case R.id.action_bookings:

                        break;
                    case R.id.action_profile:
                        if(logged){
                            Intent intent = new Intent(HomepageActivity.this, ClientProfileActivity.class);
                            intent.putExtra(FirebaseConnection.CLIENT, client);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        break;
                }
                return true;
            }
        });
    }// end onCreate


    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseConnection firebaseConnection=new FirebaseConnection();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user != null){
            logged = true;
        }
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listPlace.add(snapshot.getValue(nomeGruppo.eathome.actors.Place.class));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
