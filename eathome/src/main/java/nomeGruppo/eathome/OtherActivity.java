package nomeGruppo.eathome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.clientSide.MyFeedbackClientActivity;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.clientSide.profile.ClientProfileActivity;
import nomeGruppo.eathome.placeSide.profile.PlaceMyFeedbackActivity;
import nomeGruppo.eathome.placeSide.profile.PlaceProfileActivity;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity dove sono presenti il mio profilo le mie recensioni e il logout
 */
public class OtherActivity extends AppCompatActivity {
    private final MenuNavigationItemSelected menuNavigationItemSelected=new MenuNavigationItemSelected();
    private Place place;
    private Client client;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other);

        final Button btnProfile = findViewById(R.id.btnMyProfile);
        final Button btnFeedback = findViewById(R.id.btnMyFeedback);
        final Button btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationMenu = findViewById(R.id.bottom_navigationOther);

        this.mAuth = FirebaseAuth.getInstance();
        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        if(place!=null){ //se l'activity corrente è stata raggiunta da un Place

            //mostro il menu sottostante
            bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return menuNavigationItemSelected.menuNavigationPlace(item,place,OtherActivity.this);//carico il menu dei Place
                }
            });

            //se il Place clicca su il mio profilo
            btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profilePlaceIntent=new Intent(OtherActivity.this, PlaceProfileActivity.class);
                    profilePlaceIntent.putExtra(FirebaseConnection.PLACE,place);
                    startActivity(profilePlaceIntent);//apro l'activity del profilo di Place
                }
            });

            //se place clicca su le mie recensioni
            btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent feedbackPlaceIntent=new Intent(OtherActivity.this, PlaceMyFeedbackActivity.class);
                    feedbackPlaceIntent.putExtra(FirebaseConnection.PLACE,place);
                    startActivity(feedbackPlaceIntent);//apro l'activity dei feedback di Place
                }
            });
        }

        else{ //se l'activity corrente è stata raggiunta da un Client

            //mostro il menu sottostante
            bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return menuNavigationItemSelected.menuNavigation(item,client,OtherActivity.this);//carico il menu dei CLient
                }
            });

            //se cliente clicca su il mio profilo
            btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileClientIntent=new Intent(OtherActivity.this, ClientProfileActivity.class);
                    profileClientIntent.putExtra(FirebaseConnection.CLIENT,client);
                    startActivity(profileClientIntent);//apro l'activity profilo di Cliente
                }
            });

            //se cliente clicca su le mie recensioni
            btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent feedbackClientIntent=new Intent(OtherActivity.this, MyFeedbackClientActivity.class);
                    feedbackClientIntent.putExtra(FirebaseConnection.CLIENT,client);
                    startActivity(feedbackClientIntent);//apro l'activity dei feedback di Client

                }
            });

        }

        //se place clicca su logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                SharedPreferences.Editor mEditor = getPreferences(Context.MODE_PRIVATE).edit();
                mEditor.clear();
                mEditor.apply();

                //svuoto backstack e ritorna alla Home
                Intent homepageIntent = new Intent(OtherActivity.this, HomepageActivity.class);
                homepageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                homepageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homepageIntent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final MenuItem mItem = bottomNavigationMenu.getMenu().findItem(R.id.action_other);
        mItem.setChecked(true);
    }
}
