package nomeGruppo.eathome;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.db.FirebaseConnection;
import nomeGruppo.eathome.utility.MyExceptions;
import nomeGruppo.eathome.utility.TimerThread;
import nomeGruppo.eathome.utility.UtilitiesAndControls;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_TIME_INTERVAL = 2000L;        //Tempo minimo di visualizzazione dell'activity
    private static final long MAX_TIME_INTERVAL = 15000L;       //Tempo massimo dopo il quale se non si passa all'activity successiva l'app si chiude
    private static final long NO_CONNECTION_INTERVAL = 4500L;   //Tempo di visualizzazione in caso di nessuna connessione ad internet

    private ProgressBar progressBar;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.activity_launcher_progressBar);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Geocoder.isPresent()) {
            final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                    if (e instanceof MyExceptions) {
                        if (((MyExceptions) e).getExceptionType() == MyExceptions.TIMEOUT) {
                            Log.e("SplashActivity", "Uncaught exception from thread" + MyExceptions.TIMEOUT_MESSAGE);
                            endAll();
                        }
                    }
                }
            };

            if (UtilitiesAndControls.isNetworkAvailable(this)) {

                final TimerThread timerThread = new TimerThread(MAX_TIME_INTERVAL);

                timerThread.setUncaughtExceptionHandler(exceptionHandler);
                timerThread.start();

                if (user == null) {
                    //utente non autenticato
                    //mostra splashscreen per intervallo minimo
                    final Handler noLoginHandler = new Handler();
                    noLoginHandler.postDelayed(new Runnable() {
                        public void run() {
                            //utente non ha effettuato il login
                            Intent homePageIntent = new Intent(SplashActivity.this, HomepageActivity.class);
                            startActivity(homePageIntent);
                            finish();
                            timerThread.stopTimer();    //termina il timer
                        }
                    }, MIN_TIME_INTERVAL);

                } else {

                    final FirebaseConnection firebaseConnection = new FirebaseConnection();

                    firebaseConnection.searchUserInDb(user.getUid(), progressBar, this, timerThread);
                }

            } else {
                progressBar.setVisibility(View.INVISIBLE);

                //chiudi l'app dopo 4.5 secondi se non c'è connessione ad internet
                final Handler noConnectionHandler = new Handler();
                noConnectionHandler.postDelayed(new Runnable() {
                    public void run() {
                        endAll();
                    }
                }, NO_CONNECTION_INTERVAL);
            }
        } else {
            //geocoder non presente
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.noGeocoderDialogTitle)
                    .setMessage(R.string.noGeocoderDialogText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            endAll();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }//fine onStart

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Process.killProcess(Process.myPid());
    }

    //chiude l'activity e impedisce che venga chiamata la home
    private void endAll() {
        moveTaskToBack(true);
        finish();
        Process.killProcess(Process.myPid());
    }
}
