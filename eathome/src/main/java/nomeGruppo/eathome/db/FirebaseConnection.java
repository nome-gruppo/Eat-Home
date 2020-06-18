package nomeGruppo.eathome.db;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nomeGruppo.eathome.R;
import nomeGruppo.eathome.actions.Feedback;
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;
import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.placeSide.PlaceHomeActivity;
import nomeGruppo.eathome.utility.MyExceptions;

/**
 * FIrebaseConnection contiene costanti e metodi per la gestione del database Firebase Realtime e di FirebaseAuthentication
 */
public class FirebaseConnection {

    private static final String TAG = "FirebaseConnection";
    private static final String DB_ADDRESS = "https://eathome-bc890.firebaseio.com/";

    public static final String PLACE_NODE = "Places";
    public static final String CLIENT_NODE = "Clients";
    public static final String ORDER_NODE = "Orders";
    public static final String FOOD_NODE = "Foods";
    public static final String BOOKING_NODE = "Bookings";
    public static final String FEEDBACK_NODE = "Feedback";

    //stringhe usate negli intent
    public static final String FROM_ANOTHER_ACTIVITY = "fromActivity";
    public static final String PLACE = "Place";
    public static final String CLIENT = "Client";
    public static final String ORDER = "Order";

    private static DatabaseReference mDatabase;

    //variabile usata nei metodi reauthenticateUser e updatePassword
    private boolean operationSuccess = false;

    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance(DB_ADDRESS).getReference();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public boolean getOperationSuccess() {
        return operationSuccess;
    }

    /**
     * Scrive una variabile nel database Firebase
     *
     * @param node   nodo principale contenente variabili tutte dello stesso tipo.
     *               Corrisponde ad una delle costanti *_NODE elencate sopra
     * @param nodeId sotto-nodo di node identificato da un'id assegnato precedentemente da firebase
     * @param obj    variabile da scrivere in corrispondenza di node-nodeId
     */
    public void write(String node, String nodeId, Object obj) {
        mDatabase.child(node).child(nodeId).setValue(obj);
    }

    /**
     * Scrive una variabile nel database Firebase
     *
     * @param node nodo principale contenente variabili tutte dello stesso tipo.
     *             Corrisponde ad una delle costanti *_NODE elencate sopra
     * @param obj  variabile da inserire in node
     */
    public void writeObject(String node, Object obj) {
        mDatabase.child(node).push().setValue(obj);
    }


    /**
     * Il metodo cerca ricorsivamente l'utente identificato da userId nelle tabelle CLIENT_NODE e PLACE_NODE
     * Per funzionare correttamente alla prima chiamata deve essere passato il parametro CLIENT_NODE come parametro node
     *
     * @param userId      cfr. searchUserInDb
     * @param node        nodo in cui effettuare la ricerca nel database
     * @param progressBar cfr. searchUserInDb
     * @param activity    cfr. searchUserInDb
     */
    private void searchUser(final String userId, final String node, final ProgressBar progressBar, final Activity activity) throws MyExceptions {

        final boolean fromAnotherActivity = activity.getIntent().getBooleanExtra(FROM_ANOTHER_ACTIVITY, false);

        mDatabase.child(node).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    operationSuccess = true;
                    if (progressBar != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    //ricerca nel nodo clienti
                    if (node.equals(FirebaseConnection.CLIENT_NODE)) {
                        final Client client = dataSnapshot.getValue(Client.class);

                        if (!fromAnotherActivity) {
                            final Intent intent = new Intent(activity, HomepageActivity.class);
                            intent.putExtra(CLIENT, client);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        } else {
                            final Intent intent = new Intent();
                            final Place place = (Place) activity.getIntent().getSerializableExtra(PLACE);
                            final Order order = (Order) activity.getIntent().getSerializableExtra(ORDER);
                            intent.putExtra(PLACE, place);
                            intent.putExtra(CLIENT, client);
                            intent.putExtra(ORDER, order);
                            activity.setResult(Activity.RESULT_OK, intent);
                        }


                    } else { //ricerca nel nodo places
                        final Place place = dataSnapshot.getValue(Place.class);
                        final Intent intent = new Intent(activity, PlaceHomeActivity.class);
                        intent.putExtra(PLACE, place);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                    activity.finish();
                    //se non è stato trovato l'id nel nodo clienti cerca in places
                } else if (!dataSnapshot.exists() && node.equals(FirebaseConnection.CLIENT_NODE)) {
                    searchUser(userId, PLACE_NODE, progressBar, activity);
                } else {
                    throw new MyExceptions(MyExceptions.FIREBASE_NOT_FOUND, "Not found in Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "searchUserInDb:onCancelled", databaseError.toException());
                throw new MyExceptions(MyExceptions.FIREBASE_NOT_FOUND, "Not found in Firebase");
            }
        });
    }// end searchUserInDb

    /**
     * metodo per ricerca utente nel database Firebase.
     * Chiama il metodo ricorsivo searchUser
     *
     * @param userId      codice id dell'utente da ricercare nek database
     * @param progressBar progressBar, che indica l'operazione di ricerca, presente nell'activity chiamante
     * @param activity    activity chiamante il metodo
     */
    public void searchUserInDb(final String userId, final ProgressBar progressBar, final Activity activity) throws MyExceptions {
        this.searchUser(userId, CLIENT_NODE, progressBar, activity);
    }

    /**
     * metodo che serve per riautenticare l'utente in Firebase Authentication
     * Questa operazione è necessaria per poter eseguire correttamente procedure sensibili quali modifica e eliminazione dell'account
     *
     * @param user     variabile contenente l'account  dell'utente che deve essere riautenticato
     * @param email    email dell'utente user che deve essere riautenticato
     * @param password password dell'utente user che deve essere riautenticato
     */
    public void reauthenticateUser(FirebaseUser user, String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        operationSuccess = true;

        /* il listener è innescato solo quando l'utente non è più autenticato
        questo capita quando l'utente non inserisce le sue credenziali da molto tempo.
        Non viene innescato quando l'utente è ancora autenticato, perciò in questo caso l'operazione ha avuto comunque successo
         */
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "User re-authenticated.");

                } else {
                    Log.d(TAG, "User not re-authenticated.");
                    operationSuccess = false;
                }
            }
        });
    }

    /**
     * metodo per la modifica della mail di un utente.
     * La ricerca avviene dapprima in Firebase Authentication, poi nel database RealTime
     * Controlla che non ci siano mail uguali già esistenti
     *
     * @param firebaseAuth istanza FirebaseAuth usata nell'activity chiamante
     * @param user         istanza di FirebaseUser contenente l'account dell'utente la cui email deve essere aggiornata
     * @param node         nodo di Firebase Realtime in cui sono presenti le informazioni dell'utente user
     *                     equivale a CLIENT_NODE o PLACE_NODE
     * @param email        nuova email da assegnare all'utente user
     * @param activity     activity chiamante
     */
    public void updateEmail(FirebaseAuth firebaseAuth, final FirebaseUser user, final String node, final String email, final Activity activity) {

        //controllo che la mail non sia già presente
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.getResult() != null && task.getResult().getSignInMethods() != null) {
                    //se la lista è vuota non ci sono account con quella email
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //modifica email in Firebase Realtime
                                    if (node.equals(CLIENT_NODE)) {
                                        mDatabase.child(node).child(user.getUid()).child(Client.ID_FIELD).setValue(email);
                                    } else {
                                        mDatabase.child(node).child(user.getUid()).child(Place.ID_FIELD).setValue(email);
                                    }
                                    Toast.makeText(activity, activity.getString(R.string.emailChangedCorrectly), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(activity, activity.getString(R.string.noChangedEmail), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.emailAlreadyExists), Toast.LENGTH_LONG).show();
                    }
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }//fine updateEmail

    /**
     * metodo che aggiorna la password di un utente in Firebase Authentiation
     *
     * @param user        istanza di FirebaseUser contenente l'account dell'utente la cui password deve essere aggiornata
     * @param newPassword nuova password da inserire in Firebase Authentiation
     * @param activity    activity chiamante il metodo
     */
    public void updatePassword(final FirebaseUser user, final String newPassword, final Activity activity) {
        operationSuccess = false;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, R.string.passwordChangedCorrectly, Toast.LENGTH_LONG).show();
                            operationSuccess = true;
                        } else {
                            Toast.makeText(activity, R.string.noChangedPassword, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * metodo per il reset della password di Firebase Authentication nel caso in cui venga eliminata dall'utente.
     * Il metodo invia una mail all'utente che permette il reset della password
     *
     * @param firebaseAuth istanza FirebaseAuth usata nell'activity chiamante
     * @param emailAddress email dell'utente che vuole resettare la password
     * @param activity     activity chiamante il metodo
     */
    public void resetPassword(FirebaseAuth firebaseAuth, String emailAddress, final Activity activity) {
        firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, R.string.emailSent, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * DeleteAccount è la classe che esegue su un thread secondario l'eliminazione dell'account.
     * <p>
     * Vengono cancellate le informazioni personali dell'utente (sia Client che Place) da Firebase Authentication
     * e da Firebase Realtime
     */
    public static class DeleteAccount implements Runnable {

        private final FirebaseUser user;
        private final String uID;
        private final String table;
        private final Activity activity;

        public DeleteAccount(FirebaseUser user, String uID, String table, Activity activity) {
            this.user = user;
            this.uID = uID;
            this.table = table;
            this.activity = activity;
        }

        @Override
        public void run() {

            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        mDatabase.child(table).child(uID).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                Toast.makeText(activity, "account eliminato", Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                }
            });
        }
    }

    /**
     * DeleteAccount è la classe che esegue su un thread secondario l'eliminazione delle recensioni
     * rilasciate dal cliente che decide di eliminare l'account
     */
    public static class DeleteFeedback implements Runnable {

        private final String uID;

        public DeleteFeedback(String uID) {
            this.uID = uID;
        }

        @Override
        public void run() {
            mDatabase.child(FEEDBACK_NODE).orderByChild(Feedback.ID_PLACE_FIELD).equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //elimina ogni campo restituito
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
