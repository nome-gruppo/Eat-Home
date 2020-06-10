package nomeGruppo.eathome.placeSide;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Booking;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;

/*
activity per far visualizzare a Place il riepilogo delle sue prenotazioni
 */

public class PlaceBookingInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_booking_info);

        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceBookingInfo);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);
        this.menuNavigationItemSelected = new MenuNavigationItemSelected();
        this.tabLayout=findViewById(R.id.tabLayoutPlaceBookingInfo);
        this.viewPager=findViewById(R.id.viewPagerBooking);
        this.pagerAdapter=new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),place);
        viewPager.setAdapter(pagerAdapter);

        //mostro il menu sottostante
        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceBookingInfoActivity.this);
            }
        });

        //tabLoyout per switchare tra le prenotazioni odierne e precedenti
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }
}
