package nomeGruppo.eathome.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.widget.Toast;

import nomeGruppo.eathome.R;

/*Classe contenente tutti i controlli utili in più activities
 */
public class UtilitiesAndControls {

    /**costante che indica la dimensione massima di un Place
     */
    public static final int PICT_SIZE_MAX = 3840;

    private static final int MIN_PASSWORD_LENGTH = 6;


    public UtilitiesAndControls(){

    }

    /**metodo che ontrolla la validità della mail
     * l'input inserito deve aver il pattern di un indirizzo mail
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

    /**il metodo controlla la validità della password
     * la password non deve essere minore di 6 caratteri
     */
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

    /**il metodo controlla che la stringa inserita abbia un pattern simile a quello
     * di un numero di telefono
     *
     * la stringa deve iniziare e finire con un numero, eve contenere numeri (può contenere anche spazi, punti, trattini)
     * opzionalmente può essere preceduta da un +
     * opzionalmente alcni numeri possono essere contenuti tra parentesi
     */
    public boolean isPhoneValid(String phone){
        return Patterns.PHONE.matcher(phone).matches();
    }

    /**metodo che controlla se è disponibile una connessione ad internet
     *
     * @param context contesto dell'app
     * @return true se è disponibile una connessione, altrimenti false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean result;
        if(connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            result = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        }else{
            result = false;
        }

        if(!result){
            Toast.makeText(context, R.string.noInternetConnection, Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
