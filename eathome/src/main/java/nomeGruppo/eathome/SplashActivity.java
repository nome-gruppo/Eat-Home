package nomeGruppo.eathome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.MyExceptions;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_TIME_INTERVAL = 2000L;
    private static final long MAX_TIME_INTERVAL = 10000L;
    private static final long NO_CONNECTION_INTERVAL = 4500L;

    private ProgressBar progressBar;
    private FirebaseUser user;
    private TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.activity_launcher_progressBar);
        errorTv = findViewById(R.id.activity_launcher_tv_error);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (UtilitiesAndControls.isNetworkAvailable(this)) {

            if (user == null) {
                final Handler noLoginHandler = new Handler();
                noLoginHandler.postDelayed(new Runnable() {
                    public void run() {
                        //utente non ha effettuato il login
                        Intent homePageIntent = new Intent(SplashActivity.this, HomepageActivity.class);
                        startActivity(homePageIntent);
                        finish();
                    }
                }, MIN_TIME_INTERVAL);

            } else {
                final ClosingTimerThread timerThread = new ClosingTimerThread();
                final Thread mThread = new Thread(timerThread);
                final FirebaseConnection firebaseConnection = new FirebaseConnection();

                mThread.start();

                try {
                    firebaseConnection.searchUserInDb(user.getUid(), progressBar, this);
                }catch (MyExceptions e){
                    if(e.getExceptionType() == MyExceptions.FIREBASE_NOT_FOUND){
                        errorTv.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);

            //chiudi l'app dopo 4.5 secondi se non c'è connessione ad internet
            final Handler noConnectionHandler = new Handler();
            noConnectionHandler.postDelayed(new Runnable() {
                public void run() {
                    SplashActivity.this.finish();
                    System.exit(0);
                }
            }, NO_CONNECTION_INTERVAL);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
    }

    private class ClosingTimerThread extends Thread {

        @Override
        public void run() {
            super.run();

            try {
                Thread.sleep(MAX_TIME_INTERVAL);

                SplashActivity.this.finish();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
