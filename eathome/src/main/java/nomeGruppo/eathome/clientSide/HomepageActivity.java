package nomeGruppo.eathome.clientSide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Rating;
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
import android.widget.RatingBar;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.AddressesBarAdapter;
import nomeGruppo.eathome.DialogEnterPlaceReview;
import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.PlacesByName;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.PlaceAdapter;

/*
activity homepage dei clienti
 */
public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = "HomepageActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1000;
    private static final int SEARCH_FILTER_REQUEST_CODE = 51;


    private BottomNavigationView bottomMenuClient;

    private Client client;

    private ArrayList<nomeGruppo.eathome.actors.Place> listPlace;
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
    private FloatingActionButton filterFab;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private boolean setFilter;//variabile per controllare se l'utente ha filtrato la ricerca o no

    private Bundle filterBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //null se l'utente non ha effettuato il login
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        setFilter=false;

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getReadableDatabase();

        filterFab = findViewById(R.id.activity_homepage_fab_filter);
        noPlacesTw = findViewById(R.id.activity_homepage_tw_no_places);
        bottomMenuClient = findViewById(R.id.bottom_navigationClient);
        addressesBar = findViewById(R.id.activity_homepage_autoTV);
        listViewPlace = findViewById(R.id.listViewPlace);
        searchBtn = findViewById(R.id.search_button);
        findPlacesBtn = findViewById(R.id.activity_homepage_btn_find_places);
        addressesBarAdapter = new AddressesBarAdapter(getApplicationContext(), R.layout.dropdown_list_layout);


        //lista dei locali mostrati
        listPlace = new ArrayList<>();
        placeAdapter = new PlaceAdapter(this, R.layout.fragment_place_info_homepage_activity, listPlace);
        listViewPlace.setAdapter(placeAdapter);

        mPreferences = getPreferences(Context.MODE_PRIVATE);
        userCity = mPreferences.getString("city", null);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //se non è mai stata effettuata una ricerca prima
        if (userCity == null) {
            listViewPlace.setVisibility(View.GONE);
            findPlacesBtn.setVisibility(View.VISIBLE);
            filterFab.setVisibility(View.GONE);

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

        if(user != null) {

            //leggo la tabella myInfo per verificare se ci sono locali da recensire
            Cursor c = mDB.query(DBOpenHelper.TABLE_INFO, DBOpenHelper.COLUMNS_INFO, DBOpenHelper.SELECTION_BY_USER_ID_INFO, new String[]{user.getUid()}, null, null, null);
            final int rows = c.getCount();

            if (rows > 0) {//se ci sono locali per cui l'utente ha prenotato/ordinato
                while (c.moveToNext()) {
                    String idPlace = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ID_INFO));
                    String namePlace = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NAME_PLACE));
                    String dateInfo = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.DATE_TIME));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");//imposto il formato della data
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(dateInfo);//faccio il cast della stringa dateInfo in formato Date
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance();//accoglierà la data di prenotazione/ordinazione
                    Calendar curDate = Calendar.getInstance();//accoglierà la data odierna
                    calendar.setTime(date);//imposto la data in Calendar per poterla confronatare con la data odierna
                    curDate.getTime();//prendo la data odierna
                    if (curDate.after(calendar)) { //se la data odierna è successiva alla data di prenotazione/ordinazione
                        openDialogReview(idPlace, namePlace, client.idClient, client.nameClient, curDate, mDB, mDBHelper);//apre il dialog per la recensione
                    }
                }
                c.close();
            }

            //se non è mai stata effettuata una ricerca prima e l'utente non ha inserito nessun filtro
            if(!setFilter){
                search(userCity);
            }
        }

    }//end onStart

    private void openDialogReview(String idPlace,String namePlace,String idClient,String nameClient,Calendar curDate,SQLiteDatabase mDB,DBOpenHelper mDBHelper){
        DialogEnterPlaceReview dialogEnterPlaceReview=new DialogEnterPlaceReview(idPlace,namePlace,idClient,nameClient,curDate,mDB,mDBHelper);
        dialogEnterPlaceReview.show(getSupportFragmentManager(),"Enter review");
    }

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
        }else if(requestCode == SEARCH_FILTER_REQUEST_CODE){
            setFilter=true;//setto la variabile filtri a true
            listPlace.clear();//svuoto la listPlace
            //prendo l'arrayList restiuita da PlaceFilterActivity

            filterBundle = data.getBundleExtra("outState");


            ArrayList<nomeGruppo.eathome.actors.Place> listPlaceFilter =(ArrayList<nomeGruppo.eathome.actors.Place>) data.getSerializableExtra("listPlace");

            for(nomeGruppo.eathome.actors.Place place :listPlaceFilter){
                listPlace.add(place);
            }
            placeAdapter.notifyDataSetChanged();
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
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {

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
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_TABLE).orderByChild("cityPlace").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noPlacesTw.setVisibility(View.GONE);
                    listViewPlace.setVisibility(View.VISIBLE);
                    filterFab.setVisibility(View.VISIBLE);
                    listPlace.clear();
                    
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String  mPlaceId = snapshot.getValue(nomeGruppo.eathome.actors.Place.class).idPlace;
                        boolean mFound = false;

                        //controlla locale non sia già mostrato
                        for(nomeGruppo.eathome.actors.Place item: listPlace){
                            if (item.idPlace.equals(mPlaceId)) {
                                mFound = true;
                                break;
                            }
                        }
                        if(!mFound) {
                            listPlace.add(snapshot.getValue(nomeGruppo.eathome.actors.Place.class));
                        }
                    }

                    Collections.sort(listPlace, new PlacesByName());

                    noPlacesTw.setVisibility(View.GONE);
                    listViewPlace.setVisibility(View.VISIBLE);
                    findPlacesBtn.setVisibility(View.GONE);
                    filterFab.setClickable(true);

                    listViewPlace.setAdapter(placeAdapter);
//                        placeAdapter.notifyDataSetChanged();
                } else {
                    listViewPlace.setVisibility(View.GONE);
                    noPlacesTw.setVisibility(View.VISIBLE);
                    filterFab.setVisibility(View.GONE);

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


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> list = null;

                try {
                    list = geocoder.getFromLocationName(addressesBar.getText().toString(),1);

                    userCity = list.get(0).getLocality();

                    search(userCity);
                } catch (IOException e) {

                    Toast.makeText(getApplicationContext(), "Località non trovata", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }


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

                loadAddresses(addressesBar.getText().toString());
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
//                Toast.makeText(HomepageActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
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
                            PERMISSION_LOCATION_REQUEST_CODE);

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
                            Intent intent = new Intent(HomepageActivity.this, OtherActivity.class);
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

//                String placeID = addressesBarAdapter.getItem(position).getPlaceId();
//                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeID + "key=" + getString(R.string.api_key);
//
//                try {
//                    JSONObject jsonObject = new JSONObject(url);
//                    JSONObject jsonCoordinates = jsonObject.getJSONObject("geometry").getJSONObject("location");
//                    latitude = jsonCoordinates.getDouble("lat");
//                    longitude = jsonCoordinates.getDouble("lng");
//
//                    Location mLocation = new Location("");
//                    mLocation.setLatitude(latitude);
//                    mLocation.setLongitude(longitude);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                String mAddress = addressesBarAdapter.getItem(position).getFullText(null).toString();
                addressesBar.setText(mAddress);

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    userCity = geocoder.getFromLocationName(mAddress,1).get(0).getLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent filtersIntent = new Intent(HomepageActivity.this, PlacesFilterActivity.class);
                filtersIntent.putExtra("listPlace", listPlace);
                filtersIntent.putExtra("outState", filterBundle);
                filtersIntent.putExtra("userCity", userCity);
                startActivityForResult(filtersIntent,SEARCH_FILTER_REQUEST_CODE);
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