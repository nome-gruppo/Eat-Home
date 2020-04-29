package nomegruppo.eathome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import nomegruppo.eathome.actors.Client;
import nomegruppo.eathome.actors.Place;
import nomegruppo.eathome.clientSide.HomepageActivity;
import nomegruppo.eathome.db.FirebaseConnection;
import nomegruppo.eathome.clientSide.profile.ClientProfileActivity;
import nomegruppo.eathome.placeSide.profile.PlaceProfileActivity;
import nomegruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity dove sono presenti il mio profilo le mie recensioni e il logout
 */
public class OtherActivity extends AppCompatActivity {
    private MenuNavigationItemSelected menuNavigationItemSelected=new MenuNavigationItemSelected();
    private ImageButton btnProfile,btnFeedback,btnLogout;
    private BottomNavigationView bottomNavigationMenu;
    private Place place;
    private Client client;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other);

        this.mAuth = FirebaseAuth.getInstance();

        this.btnProfile=findViewById(R.id.btnMyProfile);
        this.btnFeedback=findViewById(R.id.btnMyFeedback);
        this.btnLogout=findViewById(R.id.btnLogout);
        this.bottomNavigationMenu=findViewById(R.id.bottom_navigationOther);

        this.place=(Place)getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.client=(Client)getIntent().getSerializableExtra(FirebaseConnection.CLIENT);

        if(place!=null){ //se l'activity corrente è stata raggiunta da un Place

            //mostro il menu sottostante
            this.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return menuNavigationItemSelected.menuNavigationPlace(item,place,OtherActivity.this);//carico il menu dei Place
                }
            });

            //se il Place clicca su il mio profilo
            this.btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profilePlaceIntent=new Intent(OtherActivity.this, PlaceProfileActivity.class);
                    profilePlaceIntent.putExtra(FirebaseConnection.PLACE,place);
                    startActivity(profilePlaceIntent);//apro l'activity del profilo di Place
                }
            });

            //se place clicca su le mie recensioni
            this.btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent feedbackPlaceIntent=new Intent(OtherActivity.this,FeedbackPlaceActivity.class);
                    feedbackPlaceIntent.putExtra(FirebaseConnection.PLACE,place);
                    startActivity(feedbackPlaceIntent);//apro l'activity dei feedback di Place
                }
            });

            //se place clicca su logout
            this.btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    Intent homepageIntent = new Intent(OtherActivity.this, HomepageActivity.class);
                    homepageIntent.putExtra(FirebaseConnection.LOGGED_FLAG, false);
                    startActivity(homepageIntent);//faccio il logout e ritorna alla Home
                    finish();
                }
            });
        }

        else{ //se l'activity corrente è stata raggiunta da un Client

            //mostro il menu sottostante
            this.bottomNavigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return menuNavigationItemSelected.menuNavigation(item,client,OtherActivity.this);//carico il menu dei CLient
                }
            });

            //se cliente clicca su il mio profilo
            this.btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileClientIntent=new Intent(OtherActivity.this, ClientProfileActivity.class);
                    profileClientIntent.putExtra(FirebaseConnection.CLIENT,client);
                    startActivity(profileClientIntent);//apro l'activity profilo di Cliente
                }
            });

            //se cliente clicca su le mie recensioni
            this.btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent feedbackClientIntent=new Intent(OtherActivity.this,MyFeedbackClientActivity.class);
                    feedbackClientIntent.putExtra(FirebaseConnection.CLIENT,client);
                    startActivity(feedbackClientIntent);//apro l'activity dei feedback di Client

                }
            });

            //se cliente clicca su logout
            this.btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    Intent homepageIntent = new Intent(OtherActivity.this, HomepageActivity.class);
                    homepageIntent.putExtra(FirebaseConnection.LOGGED_FLAG, false);
                    startActivity(homepageIntent);//faccio il logout e torno alla Home
                    finish();
                }
            });
        }


    }
}
