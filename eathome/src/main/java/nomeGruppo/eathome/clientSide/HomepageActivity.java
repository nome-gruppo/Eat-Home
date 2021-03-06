package nomeGruppo.eathome.clientSide;

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
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nomeGruppo.eathome.LoginActivity;
import nomeGruppo.eathome.OtherActivity;
import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.PlacesByName;
import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.MyExceptions;
import nomeGruppo.eathome.utility.MyLocationListenerAbstract;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

/**
 * Homepage dell'app
 */
public class HomepageActivity extends AppCompatActivity {

    private static final String TAG = HomepageActivity.class.getName();
    private static final String LIST = "listPlace";     //nome usato per passare la lista dei locali quando si cambia orientazione dello schermo

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1000;
    private static final int SEARCH_FILTER_REQUEST_CODE = 51;
    private static final int PLACE_INFO_REQUEST_CODE  = 46;

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

    private FirebaseUser user;

    private TextView noPlacesTw;
    private FloatingActionButton filterFab;

    private DBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    private boolean setFilter;//variabile per controllare se l'utente ha filtrato la ricerca o no

    private Bundle filterBundle;
    private static boolean firstLogin = true;

    private ProgressBar progressBar;

    private MyLocationListener myLocationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //null se l'utente non ha effettuato il login
        client = (Client) getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        setFilter = false;

        mDBHelper = new DBOpenHelper(this);
        mDB = mDBHelper.getReadableDatabase();

        filterFab = findViewById(R.id.activity_homepage_fab_filter);
        noPlacesTw = findViewById(R.id.activity_homepage_tw_no_places);
        bottomMenuClient = findViewById(R.id.bottom_navigationClient);
        addressesBar = findViewById(R.id.activity_homepage_autoTV);
        listViewPlace = findViewById(R.id.listViewPlace);
        searchBtn = findViewById(R.id.search_button);
        findPlacesBtn = findViewById(R.id.activity_homepage_btn_find_places);
        progressBar = findViewById(R.id.homepage_progressBar);

        myLocationListener = new MyLocationListener(UtilitiesAndControls.LOCATION_TIMEOUT);

        addressesBarAdapter = new AddressesBarAdapter(getApplicationContext(), R.layout.dropdown_list_layout);

        //lista dei locali mostrati
        listPlace = new ArrayList<>();

        if (savedInstanceState != null) {
            listPlace = (ArrayList<nomeGruppo.eathome.actors.Place>) savedInstanceState.getSerializable(LIST);
        }

        placeAdapter = new PlaceAdapter(this, R.layout.listitem_place_homepage, listPlace);
        listViewPlace.setAdapter(placeAdapter);

        mPreferences = getPreferences(Context.MODE_PRIVATE);
        userCity = mPreferences.getString("city", null);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        addressesBar.dismissDropDown();


        //se non è mai stata effettuata una ricerca prima
        if (userCity == null) {
            listViewPlace.setVisibility(View.GONE);
            findPlacesBtn.setVisibility(View.VISIBLE);
            filterFab.setVisibility(View.GONE);

        } else {
            addressesBar.setText(mPreferences.getString("address", null));

        }


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }

        //inizializza tutti i listeners
        initListeners();

    }// end onCreate

    @Override
    protected void onStart() {
        super.onStart();

        final MenuItem mItem = bottomMenuClient.getMenu().findItem(R.id.action_home);
        mItem.setChecked(true);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (listPlace.isEmpty()) {
            if (firstLogin && user != null) {

                //leggo la tabella myInfo per verificare se ci sono locali da recensire
                Cursor c = mDB.query(DBOpenHelper.TABLE_INFO, DBOpenHelper.COLUMNS_INFO, DBOpenHelper.SELECTION_BY_USER_ID_INFO, new String[]{user.getUid()}, null, null, null);
                final int rows = c.getCount();

                if (rows > 0) {//se ci sono locali per cui l'utente ha prenotato/ordinato
                    while (c.moveToNext()) {
                        final String idPlace = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.ID_INFO));
                        final String namePlace = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.NAME_PLACE));
                        final String dateInfo = c.getString(c.getColumnIndexOrThrow(DBOpenHelper.DATE_TIME));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());//imposto il formato della data
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(dateInfo);//faccio il cast della stringa dateInfo in formato Date
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        final Calendar calendar = Calendar.getInstance();//accoglierà la data di prenotazione/ordinazione
                        final Calendar curDate = Calendar.getInstance();//accoglierà la data odierna

                        if (date != null) {
                            calendar.setTime(date);//imposto la data in Calendar per poterla confronatare con la data odierna
                            curDate.getTime();//prendo la data odierna
                            if (curDate.compareTo(calendar) >= 1) { //se la data odierna è successiva alla data di prenotazione/ordinazione
                                openDialogReview(idPlace, namePlace, client.idClient, client.nameClient, mDB, mDBHelper);//apre il dialog per la recensione
                            }
                        }
                    }
                    c.close();

                }
            }

            //se non è mai stata effettuata una ricerca prima e l'utente non ha inserito nessun filtro
            if (!setFilter && userCity != null) {
                search(userCity);
            }

        }
    }//end onStart

    @Override
    protected void onResume() {
        super.onResume();

        if (firstLogin) {
            FirebaseConnection connection = new FirebaseConnection();
            final long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS);
            Query query = connection.getmDatabase().child(FirebaseConnection.ORDER_NODE).orderByChild("timestampOrder").endAt(cutoff);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //cc
                }
            });

            firstLogin = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

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
            }  // The user canceled the operation.

        } else if (requestCode == SEARCH_FILTER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                setFilter = true;//setto la variabile filtri a true
                listPlace.clear();//svuoto la listPlace
                //prendo l'arrayList restiuita da PlaceFilterActivity

                filterBundle = data.getBundleExtra("outState");

                ArrayList<nomeGruppo.eathome.actors.Place> listPlaceFilter = (ArrayList<nomeGruppo.eathome.actors.Place>) data.getSerializableExtra("listPlace");


                if (listPlaceFilter != null) {
                    listPlace.addAll(listPlaceFilter);
                    placeAdapter.notifyDataSetChanged();
                    if (listPlaceFilter.isEmpty()) {
                        noPlacesTw.setVisibility(View.VISIBLE);
                        findPlacesBtn.setVisibility(View.VISIBLE);
                    } else {
                        noPlacesTw.setVisibility(View.GONE);
                        findPlacesBtn.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LIST, listPlace);
    }

    public void loadAddresses(String query) {

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        final PlacesClient placesClient = Places.createClient(getApplicationContext());


        // Call either setLocationBias() OR setLocationRestriction().
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

    private void openDialogReview(String idPlace, String namePlace, String idClient, String nameClient, SQLiteDatabase mDB, DBOpenHelper mDBHelper) {
        DialogEnterPlaceReview dialogEnterPlaceReview = new DialogEnterPlaceReview(idPlace, namePlace, idClient, nameClient, mDB, mDBHelper);
        dialogEnterPlaceReview.show(getSupportFragmentManager(), "Enter review");
    }

    private void search(String city) {

        final FirebaseConnection firebaseConnection = new FirebaseConnection();

        progressBar.setVisibility(View.VISIBLE);

        //cerca nel database i locali nella città dell'utente
        firebaseConnection.getmDatabase().child(FirebaseConnection.PLACE_NODE).orderByChild("cityPlace").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    noPlacesTw.setVisibility(View.GONE);
                    listViewPlace.setVisibility(View.VISIBLE);
                    filterFab.setVisibility(View.VISIBLE);
                    listPlace.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.exists()) {
                            final nomeGruppo.eathome.actors.Place mPlace = snapshot.getValue(nomeGruppo.eathome.actors.Place.class);

                            if (mPlace != null) {
                                String mPlaceId = mPlace.idPlace;
                                boolean mFound = false;

                                //controlla locale non sia già mostrato
                                for (nomeGruppo.eathome.actors.Place item : listPlace) {
                                    if (item.idPlace.equals(mPlaceId)) {
                                        mFound = true;
                                        break;
                                    }
                                }
                                if (!mFound) {
                                    listPlace.add(snapshot.getValue(nomeGruppo.eathome.actors.Place.class));
                                }
                            }
                        }
                    }

                    Collections.sort(listPlace, new PlacesByName());

                    listViewPlace.setVisibility(View.VISIBLE);
                    noPlacesTw.setVisibility(View.GONE);
                    findPlacesBtn.setVisibility(View.GONE);
                    filterFab.setClickable(true);

                    listViewPlace.setAdapter(placeAdapter);
                } else {

                    noPlacesTw.setVisibility(View.VISIBLE);
                    findPlacesBtn.setVisibility(View.VISIBLE);
                    listViewPlace.setVisibility(View.GONE);
                    filterFab.setVisibility(View.GONE);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //something
            }
        });

    }// end onSearch

    private void initListeners() {

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UtilitiesAndControls.isNetworkAvailable(getApplicationContext())) {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> list;

                    try {
                        list = geocoder.getFromLocationName(addressesBar.getText().toString(), 1);

                        userCity = list.get(0).getLocality();

                        search(userCity);
                    } catch (IndexOutOfBoundsException | IOException e) {

                        Toast.makeText(getApplicationContext(), getString(R.string.locationNotFound), Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    }
                }

                UtilitiesAndControls.hideKeyboard(getApplicationContext());
            }
        });

        findPlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitiesAndControls.locationPermissionRequest(HomepageActivity.this, mLocationManager, myLocationListener, PERMISSION_LOCATION_REQUEST_CODE, progressBar, null);
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
                            finish();

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
                            finish();
                        } else {
                            Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case R.id.action_other:
                        if (user != null) {
                            Intent intent = new Intent(HomepageActivity.this, OtherActivity.class);
                            intent.putExtra(FirebaseConnection.CLIENT, client);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        break;
                }
                return true;

            }
        });

        addressesBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                loadAddresses(addressesBar.getText().toString());
                addressesBarAdapter.notifyDataSetChanged();
                addressesBar.setAdapter(addressesBarAdapter);
            }
        });

        addressesBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final AutocompletePrediction mPrediction = addressesBarAdapter.getItem(position);
                if (mPrediction != null) {
                    final String mAddress = mPrediction.getPrimaryText(null).toString();
                    addressesBar.setText(mAddress);

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        userCity = geocoder.getFromLocationName(mAddress, 1).get(0).getLocality();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        addressesBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        addressesBar.dismissDropDown();
                        searchBtn.performClick();
                        return true;
                    }
                }
                return false;
            }
        });

        filterFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent filtersIntent = new Intent(HomepageActivity.this, PlacesFilterActivity.class);
                filtersIntent.putExtra("listPlace", listPlace);
                filtersIntent.putExtra("outState", filterBundle);
                filtersIntent.putExtra("userCity", userCity);
                filtersIntent.putExtra("userLatitude", myLocationListener.latitude);
                filtersIntent.putExtra("userLongitude", myLocationListener.longitude);
                startActivityForResult(filtersIntent, SEARCH_FILTER_REQUEST_CODE);
            }
        });

        listViewPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                nomeGruppo.eathome.actors.Place place = (nomeGruppo.eathome.actors.Place) adapterView.getItemAtPosition(i);
                Intent placeInfoIntent = new Intent(HomepageActivity.this, PlaceInfoActivity.class);
                placeInfoIntent.putExtra(FirebaseConnection.CLIENT, client);
                placeInfoIntent.putExtra(FirebaseConnection.PLACE, place);
                startActivityForResult(placeInfoIntent, PLACE_INFO_REQUEST_CODE);
            }
        });
    }//end initListeners

    private class MyLocationListener extends MyLocationListenerAbstract {

        private double latitude;
        private double longitude;

        public MyLocationListener(final long timer) {
            super(timer);

            //handler eccezione sollevata nel thread
            final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                    if (e instanceof MyExceptions) {
                        if (((MyExceptions) e).getExceptionType() == MyExceptions.TIMEOUT) {
                            //eccezione sollevata nel thread ma la posizione non è stata ancora trovata

                            Log.e(TAG, "timeout: " + timer);
                            Toast.makeText(getApplicationContext(), R.string.locationNotFound, Toast.LENGTH_SHORT).show();

                            mLocationManager.removeUpdates(myLocationListener);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            };
            super.timerThread.setUncaughtExceptionHandler(handler);
        }

        //se posizione individuata
        public void onLocationChanged(Location location) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> list;
            try {

                setLocationFound(); //posizione trovata e timer bloccato
                list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                final String locality = list.get(0).getLocality();
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                addressesBar.setText(locality);
                userCity = locality;
                findPlacesBtn.setVisibility(View.GONE);
                search(userCity);
                progressBar.setVisibility(View.GONE);
                listViewPlace.setVisibility(View.VISIBLE);
                //elimina registrazione listener dal location manager per evitare altra ricerca
                mLocationManager.removeUpdates(this);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //se gps disabilitato durante ricerca
        public void onProviderDisabled(String arg0) {
            Toast.makeText(getApplicationContext(), R.string.gpsBeenDesabled, Toast.LENGTH_SHORT).show();
        }

        //se gps riabilitato durante ricerca
        public void onProviderEnabled(String provider) {
            findPlacesBtn.performClick();
        }

    }
}


