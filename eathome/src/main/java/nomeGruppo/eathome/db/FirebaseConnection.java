package nomeGruppo.eathome.db;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nomeGruppo.eathome.HomepageActivity;
import nomeGruppo.eathome.PlaceHomeActivity;
import nomeGruppo.eathome.actors.Client;
import nomeGruppo.eathome.actors.Place;

public class FirebaseConnection{

    private static final String TAG = "FirebaseConnection";

    public static final String PLACE_TABLE = "Places"; //todo rinomina tutti i table in node
    public static final String CLIENT_TABLE = "Clients";
    public static final String ORDER_TABLE = "Orders";
    public static final String FOOD_TABLE="Foods";
    public static final String BOOKING_TABLE="Bookings";
    public static final String FEEDBACK_TABLE="Feedbacks";
    public static final String LOGIN_FLAG = "Login from another activity"; //flag per controllare se l'activity login è stata chiamata da un'altra activity

    //stringhe utilizzate negli intent
    public static final String LOGGED_FLAG = "Logged";
    public static final String PLACE = "Place";
    public static final String CLIENT = "Client";
    public static final String ORDER="Order";

    private static DatabaseReference mDatabase;
    private Object objectFounded;

    private boolean operationSuccess = false;

    public FirebaseConnection() {
        this.mDatabase = FirebaseDatabase.getInstance("https://eathome-bc890.firebaseio.com/").getReference();
    }

    public void write(String table, String column, String value) {
        mDatabase.child(table).child(column).setValue(value);
    }

    public void write(String table, String column, Object value) {
        mDatabase.child(table).child(column).setValue(value);
    }

    public void writeObject(String table, Object obj) {
        mDatabase.child(table).push().setValue(obj);
    }

    public boolean searchUser(String table, String userId) {

        mDatabase.child(table).child(userId).addListenerForSingleValueEvent(searchEventListener());

        if (this.objectFounded == null) {
            return false;
        } else {
            return true;
        }
    }

    public Object getObjectFounded() {
        return this.objectFounded;
    }

    public String getKey(String table) {
        return mDatabase.child(table).push().getKey();
    }

    private ValueEventListener searchEventListener() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            List<Object> objList = null;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    objectFounded = dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        return valueEventListener;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public Query queryEqualTo(String table, String column, String value) {
        return mDatabase.child(table).orderByChild(column).equalTo(value);
    }

    /**
     * metodo per ricerca utente nel database Firebase nei nodi Clients e Places
     * NB: per funzionare correttamente il parametro node passato deve essere Firebase.CLIENT_TABLE
     *
     * @param userId      codice id dell'utente
     * @param node        nodo FirebaseConnection.CLIENT_NODE
     * @param progressBar progressbar presente nell'activity chiamante. null se non presente
     * @param activity    activity chiamante
     * @throws Resources.NotFoundException
     */
    public void searchUserInDb(final String userId, final String node, final ProgressBar progressBar, final Activity activity) {

        mDatabase.child(node).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (progressBar != null) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    //ricerca nel nodo clienti
                    if (node.equals(FirebaseConnection.CLIENT_TABLE)) {
                        Client client = dataSnapshot.getValue(Client.class);
                        Intent intent = new Intent(activity, HomepageActivity.class);
                        intent.putExtra(CLIENT, client);
                        intent.putExtra(LOGGED_FLAG, true);

                        if (activity.getIntent().getBooleanExtra(FirebaseConnection.LOGIN_FLAG, false)){
                            activity.finish();
                        }else {
                            activity.startActivity(intent);
                            activity.finish();
                        }

                    } else { //ricerca nel nodo places
                        Place place = dataSnapshot.getValue(Place.class);
                        Intent intent = new Intent(activity, PlaceHomeActivity.class);
                        intent.putExtra(PLACE, place);
                        intent.putExtra(LOGGED_FLAG, true);
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

    public<T> void updateDate(String table, String key, T object){

    }
    public boolean getOperationSuccess(){
        return operationSuccess;
    }

    public void reauthenticateUser(FirebaseUser user, String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        operationSuccess = false;

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            operationSuccess = true;
                        }else {
                            operationSuccess = false;
                        }
                    }
                });
    }

    public void updateEmail(FirebaseAuth firebaseAuth, final FirebaseUser user, final String email, final Activity activity) {

        //controllo chr la mail non sia già presente
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                Log.d(TAG, "" + task.getResult().getSignInMethods().size());

                if (task.getResult().getSignInMethods().size() == 0) {
                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity, "Email modificata correttamente", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(activity, "Non è stato possibile cambiare la mail", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(activity, "Email già presente", Toast.LENGTH_LONG).show();
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }//fine updateEmail

    public void updatePassword(final FirebaseUser user, final String newPassword, final Activity activity){
        operationSuccess = false;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Password modificata correttamente", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity, "Non è stato possibile cambiare la password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static class DeleteAccount implements Runnable{

        private FirebaseUser user;
        private String uID;
        private String table;

        public DeleteAccount(FirebaseUser user, String uID, String table) {
            this.user = user;
            this.uID = uID;
            this.table = table;
        }

        @Override
        public void run() {
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");

                        mDatabase.child(table).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    dataSnapshot.getRef().removeValue();
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

    public static class DeleteClientInfo implements Runnable{

        private String uID;
        private String table;

        public DeleteClientInfo(String uID, String table) {
            this.uID = uID;
            this.table = table;
        }

        @Override
        public void run() {
            mDatabase.child(table).orderByChild("idClientBooking").equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        //elimina ogni campo restituito
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
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

    public static class DeleteFeedbacks implements Runnable{

        private String uID;

        public DeleteFeedbacks (String uID){
            this.uID = uID;
        }
        @Override
        public void run() {
            mDatabase.child(FEEDBACK_TABLE).orderByChild("idPlaceBooking").equalTo(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        //elimina ogni campo restituito
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
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
