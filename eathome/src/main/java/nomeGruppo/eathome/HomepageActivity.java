package nomeGruppo.eathome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.profile.ClientProfileActivity;
import nomeGruppo.eathome.utility.PlaceAdapter;

public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = "HomepageActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_LOCATION_CODE = 1;
//    private GoogleMap mMap;

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private BottomNavigationView bottomMenuClient;

    private boolean logged;
    private Client client;

    private List<nomeGruppo.eathome.actors.Place> listPlace;
    private PlaceAdapter placeAdapter;

    private AutoCompleteTextView addressesBar;
    private ListView listViewPlace;

    private ImageButton searchBtn;

    private Button findPlacesBtn;

    private AddressesBarAdapter addressesBarAdapter;

    private String userCity;

    private LocationManager mLocationManager;
    private Location mLocation;

    private SharedPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //flag per controllare se c'è un utente che ha effettuato il login
        logged = getIntent().getBooleanExtra(FirebaseConnection.LOGGED_FLAG, false);

        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        bottomMenuClient = findViewById(R.id.bottom_navigationClient);
        addressesBar = findViewById(R.id.activity_homepage_autoTV);
        listViewPlace = findViewById(R.id.listViewPlace);
        searchBtn = findViewById(R.id.search_button);
        findPlacesBtn = findViewById(R.id.activity_homepage_btn_find_places);
        addressesBarAdapter = new AddressesBarAdapter(getApplicationContext(), R.layout.dropdown_list_layout);

        //lista dei locali mostrati
        listPlace = new LinkedList<>();
        placeAdapter = new PlaceAdapter(this, R.layout.fragment_place_info_homepage_activity, listPlace);

        mPreferences = getSharedPreferences("AddressesPref", Context.MODE_PRIVATE);
        userCity = mPreferences.getString("city", null);

        //se non è mai stata effettuata una ricerca prima
        if (userCity == null) {
            listViewPlace.setVisibility(View.GONE);
            findPlacesBtn.setVisibility(View.VISIBLE);

        } else {
            addressesBar.setText(mPreferences.getString("address", null));

        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        //inizializza tutti i listeners
        initListeners();


//        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//        // and once again when the user makes a selection (for example when calling fetchPlace()).
//        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//
//        // Create a RectangularBounds object.
//        RectangularBounds bounds = RectangularBounds.newInstance(
//                new LatLng(-33.880490, 151.184363),
//                new LatLng(-33.858754, 151.229596));
//        // Use the builder to create a FindAutocompletePredictionsRequest.
////        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
////                // Call either setLocationBias() OR setLocationRestriction().
////                .setLocationBias(bounds)
////                //.setLocationRestriction(bounds)
////                .setOrigin(new LatLng(-33.8749937,151.2041382))
////                .setCountries("AU", "NZ")
////                .setTypeFilter(TypeFilter.ADDRESS)
////                .setSessionToken(token)
////                .setQuery(query)
////                .build();
//
//        Places.initialize(getApplicationContext(), apiKey);
//        placesClient = Places.createClient(this);
//
//        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                .setLocationBias(bounds)
//                .setQuery(autoTV.getText().toString())
//                .build();
//
//
//        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
//            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                Log.i(TAG, prediction.getPlaceId());
//                Log.i(TAG, prediction.getPrimaryText(null).toString());
//            }
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//            }
//        });
////        View placeBar = getLayoutInflater().inflate(R.layout.fragment_autocomplete, null);
////        mainLayout.addView(placeBar);
//
//        /**
//         * Initialize Places. For simplicity, the API key is hard-coded. In a production
//         * environment we recommend using a secure mechanism to manage API keys.
//         */
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), apiKey);
//        }
//
//        // Create a new Places client instance.
//        PlacesClient placesClient = Places.createClient(this);

// Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.activity_homepage_autocomplete_fragment);
//
//
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });


    }// end onCreate

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (user != null) {
            logged = true;

        }

        //se non è mai stata effettuata una ricerca prima
        if (userCity != null) {

            search(userCity);

        }//end else
    }//end onStart

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
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("addresses", addressesBar.getText().toString());
        editor.putString("city", userCity);

        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public void loadAddresses(String query) {

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        PlacesClient placesClient = Places.createClient(getApplicationContext());

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setSessionToken(token)
                .setCountries("IT")
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

                    addressesBarAdapter.add(prediction);
//                mResult.append(" " + prediction.getFullText(null) + "\n");
//                    item.setText(prediction.getFullText(null));
                    Log.i(TAG, prediction.getPlaceId());
                    Log.i(TAG, prediction.getPrimaryText(null).toString());
//                    Toast.makeText(getApplicationContext(), prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
                }
            }
        });

        placesClient.findAutocompletePredictions(request).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

    private void initListeners() {

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(userCity);
            }
        });

        addressesBar.addTextChangedListener(addressesBarTextWatcher);
        findPlacesBtn.setOnClickListener(findPlacesBtnListener);
        bottomMenuClient.setOnNavigationItemSelectedListener(bottomMenuListener);

        addressesBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String[] splits = addressesBarAdapter.getItem(position).getSecondaryText(null).toString().split(",");
                //la città è situata nel penultimo elemento dell'array
                userCity = splits[splits.length - 1].trim();

            }
        });

        listViewPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nomeGruppo.eathome.actors.Place place = (nomeGruppo.eathome.actors.Place) adapterView.getItemAtPosition(i);
                Intent placeInfoIntent = new Intent(HomepageActivity.this, PlaceInfoActivity.class);
                placeInfoIntent.putExtra(FirebaseConnection.PLACE, place);
                startActivity(placeInfoIntent);
            }
        });
    }

    TextWatcher addressesBarTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            loadAddresses(addressesBar.getText().toString());
            addressesBarAdapter.notifyDataSetChanged(); //TODO controlla quale notifyDataSetChanged si può eliminare
            addressesBar.setAdapter(addressesBarAdapter);
            addressesBarAdapter.notifyDataSetChanged(); //TODO controlla quale notifyDataSetChanged si può eliminare

        }

        @Override
        public void afterTextChanged(Editable editable) {

            addressesBarAdapter.notifyDataSetChanged();
            addressesBar.setAdapter(addressesBarAdapter);

        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener bottomMenuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_orders:

                    break;
                case R.id.action_bookings:

                    break;
                case R.id.action_profile:
                    if (logged) {
                        Intent intent = new Intent(HomepageActivity.this, ClientProfileActivity.class);
                        intent.putExtra(FirebaseConnection.CLIENT, client);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    break;
            }
            return true;

        }
    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    View.OnClickListener findPlacesBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(HomepageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(HomepageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    //TODO mostrare una spiegazione del perchè sono richiesti i permessi
                } else {
                    ActivityCompat.requestPermissions(HomepageActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION_CODE);

                }
                return;
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    };

    private void search(String city) {

        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //cerca nel database i locali nella città dell'utente
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).equalTo(city, "cityPlace").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listPlace.add(snapshot.getValue(nomeGruppo.eathome.actors.Place.class));
                    }
                    listViewPlace.setAdapter(placeAdapter);
//                        placeAdapter.notifyDataSetChanged();
                }
                //placeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
