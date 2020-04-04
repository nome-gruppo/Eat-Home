package nomeGruppo.eathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nomeGruppo.eathome.db.DBOpenHelper;
import nomeGruppo.eathome.db.FirebaseConnection;

public class LauncherActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean logged;
    public TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        progressBar = (ProgressBar) findViewById(R.id.activity_launcher_progressBar);
        errorTv = (TextView) findViewById(R.id.activity_launcher_tv_error);
        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();

        if (user == null) {
            logged = false;
        } else {
            logged = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();



        if (!logged) {
            Intent homePageIntent = new Intent(this, HomepageActivity.class);
            homePageIntent.putExtra(FirebaseConnection.LOGGED_FLAG, logged);
            startActivity(homePageIntent);
            finish();
        } else {
            final FirebaseConnection firebaseConnection = new FirebaseConnection();
            final String userId = user.getUid();

            try {
                firebaseConnection.searchUserInDb(userId, FirebaseConnection.CLIENT_TABLE, progressBar, this);
            } catch (Resources.NotFoundException e) {
                errorTv.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

        }
    }
}
