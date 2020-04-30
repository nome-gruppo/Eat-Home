package nomeGruppo.eathome.utility;

import android.util.Patterns;
/*Classe contenente tutti i controlli utili in più activities
 */
public class UtilitiesAndControls {

    private static final int MIN_PASSWORD_LENGTH = 6;

    public UtilitiesAndControls(){

    }

    /**metodo che ontrolla la validità della mail
     * l'input inserito deve aver il pattern di un indirizzo mail
     * @param email
     * @return
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

    /** metodo controlla la validità della password
     * la password non deve essere minore di 6 caratteri
     * @param password
     * @return
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
     *
     * @param phone
     * @return
     */
    public boolean isPhoneValid(String phone){
        return Patterns.PHONE.matcher(phone).matches();
    }

}
