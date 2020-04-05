package nomeGruppo.eathome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.profile.ClientProfileActivity;
import nomeGruppo.eathome.utility.PlaceAdapter;

public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = "HomepageActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_PERMISSION_LOCATION_CODE = 1000;

    private BottomNavigationView bottomMenuClient;

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

    private SharedPreferences mPreferences;

    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseUser user;

    private TextView noPlacesTw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //null se l'utente non ha effettuato il login
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        noPlacesTw = findViewById(R.id.activity_homepage_tw_no_places);
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

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        //se non è mai stata effettuata una ricerca prima
        if (userCity == null) {
            listViewPlace.setVisibility(View.GONE);
            findPlacesBtn.setVisibility(View.VISIBLE);

        } else {
            addressesBar.setText(mPreferences.getString("address", null));

        }

        //TODO controlla se serve
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        //inizializza tutti i listeners
        initListeners();

    }// end onCreate

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //se non è mai stata effettuata una ricerca prima
        if (userCity != null) {

            search(userCity);

        }//end else
    }//end onStart

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("address", addressesBar.getText().toString());
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
                    //TODO elimina i commenti sotto
////                mResult.append(" " + prediction.getFullText(null) + "\n");
////                    item.setText(prediction.getFullText(null));
//                    Log.i(TAG, prediction.getPlaceId());
//                    Log.i(TAG, prediction.getPrimaryText(null).toString());
////                    Toast.makeText(getApplicationContext(), prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
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
    }//end loadAddresses

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION_CODE) {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }
    }// end onRequestPermissionsResult

    private void search(String city) {

        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        //cerca nel database i locali nella città dell'utente
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).equalTo(city, "cityPlace").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noPlacesTw.setVisibility(View.GONE);
                    listViewPlace.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        listPlace.add(snapshot.getValue(nomeGruppo.eathome.actors.Place.class));
                    }
                    listViewPlace.setAdapter(placeAdapter);
//                        placeAdapter.notifyDataSetChanged();
                }else{
                    listViewPlace.setVisibility(View.GONE);
                    noPlacesTw.setVisibility(View.VISIBLE);

                }
                //placeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }// end onSearch

    private void initListeners() {

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(userCity);
            }
        });

        addressesBar.addTextChangedListener(new TextWatcher() {
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
        });

        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        addressesBar.setText(list.get(0).getAddressLine(0));
                        userCity = list.get(0).getLocality();
                        findPlacesBtn.setVisibility(View.GONE);
                        search(userCity);
                        listViewPlace.setVisibility(View.VISIBLE);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomepageActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });

        findPlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //richiedo permessi
                if (ActivityCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(HomepageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //permessi non concessi

                    ActivityCompat.requestPermissions(HomepageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_PERMISSION_LOCATION_CODE);

                } else {
                    //permessi concessi

                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                }
            }
        });

        bottomMenuClient.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_orders:

                        if (user != null) {
                            Intent intent = new Intent(HomepageActivity.this, ClientOrderInfoActivity.class);
                            intent.putExtra(FirebaseConnection.CLIENT, client);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case R.id.action_bookings:
                        if (user != null) {
                            Intent intent = new Intent(HomepageActivity.this, ClientBookingInfoActivity.class);
                            intent.putExtra(FirebaseConnection.CLIENT, client);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case R.id.action_profile:
                        if (user != null) {
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
        });

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
    }//end initListeners
}
