package nomeGruppo.eathome.placeSide;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.DialogListFoodOrder;
import nomeGruppo.eathome.utility.MenuNavigationItemSelected;
import nomeGruppo.eathome.utility.PlaceOrderAdapter;

/**
 * activity per visualizzare le informazioni sulle ordinazioni
 */
public class PlaceOrderInfoActivity extends AppCompatActivity {

    private Place place;
    private MenuNavigationItemSelected menuNavigationItemSelected;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order_info);

        this.place = (Place) getIntent().getSerializableExtra(FirebaseConnection.PLACE);//recupero l'oggetto Place

        final BottomNavigationView bottomMenuPlace = findViewById(R.id.bottom_navigationPlaceOrderInfo);
        tabLayout = findViewById(R.id.tabLayoutPlaceOrderInfo);
        this.viewPager=findViewById(R.id.viewPagerOrder);
        this.pagerAdapter=new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),place);
        viewPager.setAdapter(pagerAdapter);

        this.menuNavigationItemSelected = new MenuNavigationItemSelected();


        //tabLoyout per switchare tra gli ordini odierni e precedenti
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


        bottomMenuPlace.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {//navigazione nel bottom menu
                return menuNavigationItemSelected.menuNavigationPlace(item, place, PlaceOrderInfoActivity.this);
            }
        });

    }
}
