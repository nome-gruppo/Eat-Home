package nomeGruppo.eathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.db.FirebaseConnection;

public class LauncherActivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private FirebaseUser user;
    private TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        progressBar =findViewById(R.id.activity_launcher_progressBar);
        errorTv = findViewById(R.id.activity_launcher_tv_error);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkAvailable()) {
            if (user == null) {
                //utente non ha effettuato il login
                Intent homePageIntent = new Intent(this, HomepageActivity.class);
                startActivity(homePageIntent);
                finish();
            } else {

                final FirebaseConnection firebaseConnection = new FirebaseConnection();

                try {
                    firebaseConnection.searchUserInDb(user.getUid(), FirebaseConnection.CLIENT_NODE, progressBar, this);
                } catch (Resources.NotFoundException e) {
                    errorTv.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }

            }
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.noInternetConnection, Toast.LENGTH_LONG).show();

            //chiudi l'app dopo 4 secondi se non c'è connessione ad internet
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    LauncherActivity.this.finish();
                    System.exit(0);
                }
            }, 4000);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }else{
            return false;
        }
    }

}
