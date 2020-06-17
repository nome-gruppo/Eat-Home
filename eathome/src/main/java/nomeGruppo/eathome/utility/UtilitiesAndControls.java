package nomeGruppo.eathome.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.clientSide.HomepageActivity;

/*Classe contenente tutti i controlli utili in più activities
 */
public class UtilitiesAndControls {

    /**
     * costante che indica la dimensione massima di un Place
     */
    public static final int PICT_SIZE_MAX = 3840;

    private static final int MIN_PASSWORD_LENGTH = 6;


    public UtilitiesAndControls() {

    }

    /**
     * metodo che ontrolla la validità della mail
     * l'input inserito deve aver il pattern di un indirizzo mail
     *
     * @param email email da controllare
     */
    public boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    /**
     * il metodo controlla la validità della password
     * la password non deve essere minore di 6 caratteri
     */
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * il metodo controlla che la stringa inserita abbia un pattern simile a quello
     * di un numero di telefono
     * <p>
     * la stringa deve iniziare e finire con un numero, eve contenere numeri (può contenere anche spazi, punti, trattini)
     * opzionalmente può essere preceduta da un +
     * opzionalmente alcni numeri possono essere contenuti tra parentesi
     */
    public boolean isPhoneValid(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * metodo che controlla se è disponibile una connessione ad internet
     *
     * @param context contesto dell'app
     * @return true se è disponibile una connessione, altrimenti false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean result;
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            result = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } else {
            result = false;
        }

        if (!result) {
            Toast.makeText(context, R.string.noInternetConnection, Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static void locationPermissionRequest(final Activity activity, LocationManager mLocationManager, LocationListener myLocationListener,
                                                 final int requestCode, ProgressBar progressBar, RadioButton radioButton) {

        //richiedo i permessi
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permessi non concessi

            //permessi non ancora concessi
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(R.string.positionDialog)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    requestCode);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        } else {
            //permessi concessi
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            } else {
                if (activity instanceof HomepageActivity) {
                    progressBar.setVisibility(View.VISIBLE);

                } else {
                    radioButton.setClickable(true);
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000000000L, 0, myLocationListener);
            }
        }
    }
}
