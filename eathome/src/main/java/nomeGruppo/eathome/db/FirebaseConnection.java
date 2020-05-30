package nomeGruppo.eathome.db;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import nomeGruppo.eathome.actions.Order;
import nomeGruppo.eathome.clientSide.HomepageActivity;
import nomeGruppo.eathome.placeSide.PlaceHomeActivity;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;

public class FirebaseConnection {

    private static final String TAG = "FirebaseConnection";

    public static final String PLACE_TABLE = "Places"; //todo rinomina tutti i table in node
    public static final String CLIENT_TABLE = "Clients";
    public static final String ORDER_TABLE = "Orders";
    public static final String FOOD_TABLE = "Foods";
    public static final String BOOKING_TABLE = "Bookings";
    public static final String FEEDBACK_TABLE = "Feedback";

    //stringhe usate negli intent
    public final static String FROM_ANOTHER_ACTIVITY  = "fromActivity";
    public static final String PLACE = "Place";
    public static final String CLIENT = "Client";
    public static final String ORDER = "Order";

    private static DatabaseReference mDatabase;

    private boolean operationSuccess = false;

    public FirebaseConnection() {
        mDatabase = FirebaseDatabase.getInstance("https://eathome-bc890.firebaseio.com/").getReference();
    }

    public void write(String table, String column, Object value) {
        mDatabase.child(table).child(column).setValue(value);
    }

    public void writeObject(String table, Object obj) {
        mDatabase.child(table).push().setValue(obj);
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }


    /**
     * metodo per ricerca utente nel database Firebase nei nodi Clients e Places
     * NB: per funzionare correttamente il parametro node passato deve essere Firebase.CLIENT_TABLE
     *
     * @param userId      codice id dell'utente
     * @param node        nodo FirebaseConnection.CLIENT_NODE
     * @param progressBar progressbar presente nell'activity chiamante. null se non presente
     * @param activity    activity chiamante
     */
    public void searchUserInDb(final String userId, final String node, final ProgressBar progressBar, final Activity activity) {

        final boolean fromAnotherActivity = activity.getIntent().getBooleanExtra(FROM_ANOTHER_ACTIVITY ,false);

        mDatabase.child(node).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (progressBar != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    //ricerca nel nodo clienti
                    if (node.equals(FirebaseConnection.CLIENT_TABLE)) {
                        final Client client = dataSnapshot.getValue(Client.class);


                        if(!fromAnotherActivity){
                            final Intent intent = new Intent(activity, HomepageActivity.class);
                            intent.putExtra(CLIENT, client);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        }else{
                            final Intent intent = new Intent();
                            final Place place = (Place) activity.getIntent().getSerializableExtra(PLACE);
                            final Order order = (Order) activity.getIntent().getSerializableExtra(ORDER);
                            intent.putExtra(PLACE, place);
                            intent.putExtra(CLIENT, client);
                            intent.putExtra(ORDER, order);
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
                        }



                    } else { //ricerca nel nodo places
                        final Place place = dataSnapshot.getValue(Place.class);
                        final Intent intent = new Intent(activity, PlaceHomeActivity.class);
                        intent.putExtra(PLACE, place);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                    //se non è stato trovato l'id nel nodo clienti cerca in places
                } else if (!dataSnapshot.exists() && node.equals(FirebaseConnection.CLIENT_TABLE)) {
                    searchUserInDb(userId, PLACE_TABLE, progressBar, activity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "searchUserInDb:onCancelled", databaseError.toException());
            }
        });
    }// end searchUserInDb

    public boolean getOperationSuccess() {
        return operationSuccess;
    }

    /**
     * metodo che serve per riautenticare l'utente in Firebase.
     * Questa operazione è necessaria per poter eseguire correttamente proceure di modifica e eliminazione dell'account
     *
     * @param user     variabile contenente l'account  dell'utente che deve essere riautenticato
     * @param email    email dell'utente user che deve essere riautenticato
     * @param password password dell'utente user che deve essere riautenticato
     */
    public void reauthenticateUser(FirebaseUser user, String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);


        operationSuccess = true;

        // il listener è innescato solo quando l'utente non è più autenticato
        // questo capita quando l'utente non inserisce le sue credenziali da molto tempo
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
     * Controlla che non ci siano mail uguali già esistenti
     *
     * @param firebaseAuth variabile FirebaseAuth usata nell'activity
     * @param user         variabile contenente l'account dell'utente che deve essere riautenticato
     * @param email        email dell'utente user che deve essere riautenticato
     * @param activity     activity chiamante il metodo
     */
    public void updateEmail(FirebaseAuth firebaseAuth, final FirebaseUser user, final String email, final Activity activity) {

        //controllo che la mail non sia già presente
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if(task.getResult() != null && task.getResult().getSignInMethods() != null) {
                    if (task.getResult().getSignInMethods().size() == 0) {
                        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
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

    public void updatePassword(final FirebaseUser user, final String newPassword, final Activity activity) {
        operationSuccess = false;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Password modificata correttamente", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "Non è stato possibile cambiare la password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void resetPassword(FirebaseAuth auth, String emailAddress, final Activity activity) {
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Email inviata", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static class DeleteAccount implements Runnable {

        private FirebaseUser user;
        private String uID;
        private String table;
        private Activity activity;

        public DeleteAccount(FirebaseUser user, String uID, String table, Activity callingActivity) {
            this.user = user;
            this.uID = uID;
            this.table = table;
            this.activity = callingActivity;
        }

        @Override
        public void run() {

            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {


                        mDatabase.child(table).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    dataSnapshot.getRef().removeValue();

                                    Intent homeIntent = new Intent(activity, HomepageActivity.class);
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(homeIntent);
                                    activity.finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }
    }


    public static class DeleteFeedback implements Runnable {

        private String uID;

        public DeleteFeedback(String uID) {
            this.uID = uID;
        }

        @Override
        public void run() {
            mDatabase.child(FEEDBACK_TABLE).orderByChild("idPlaceBooking").equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
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
