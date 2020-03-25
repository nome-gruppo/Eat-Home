package nomeGruppo.eathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.db.FirebaseConnection;

public class LauncherActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean isLogged;
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
            isLogged = false;
        } else {
            isLogged = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (!isLogged) {
            Intent homePageIntent = new Intent(this, MainActivity.class);
            homePageIntent.putExtra(FirebaseConnection.LOGGED_FLAG, isLogged);
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
