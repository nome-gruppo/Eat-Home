package nomeGruppo.eathome.utility;

import android.util.Patterns;
/*Classe contenente tutti i controlli utili in più activities
 */
public class UtilitiesAndControls {

    private static final int MIN_PASSWORD_LENGTH = 6;

    public UtilitiesAndControls(){

    }

    // A placeholder username validation check
    public boolean isEmailValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= MIN_PASSWORD_LENGTH;
    }

}
